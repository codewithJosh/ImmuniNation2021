package com.codewithjosh.ImmuniNation2k21.fragments.users;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.codewithjosh.ImmuniNation2k21.R;
import com.codewithjosh.ImmuniNation2k21.models.RequestModel;
import com.codewithjosh.ImmuniNation2k21.models.SlotModel;
import com.codewithjosh.ImmuniNation2k21.models.UserModel;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileFragment extends Fragment
{

    Button btnLogout;
    CircleImageView civUserImage;
    TextView tvUserName;
    TextView tvUserVaccinationStatus;
    TextView tvVaccineFirstDoseDate;
    TextView tvVaccineFirstDoseStatus;
    TextView tvVaccineSecondDoseDate;
    TextView tvVaccineSecondDoseStatus;
    String userId;
    Activity activity;
    Context context;
    FirebaseFirestore firebaseFirestore;
    SharedPreferences sharedPref;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        initViews(view);
        initInstances();
        initSharedPref();
        load();
        loadUser();
        loadUserSlot();

        return view;

    }

    private void load() {

        userId = sharedPref.getString("user_id", String.valueOf(Context.MODE_PRIVATE));

    }

    private void initViews(final View view) {

        if (getContext() != null) context = getContext();
        if (getActivity() != null) activity = getActivity();
        activity.getWindow().setStatusBarColor(context.getColor(R.color.color_dark_cyan));

        btnLogout = view.findViewById(R.id.btn_logout);
        civUserImage = view.findViewById(R.id.civ_user_image);
        tvUserName = view.findViewById(R.id.tv_user_name);
        tvUserVaccinationStatus = view.findViewById(R.id.tv_user_vaccination_status);
        tvVaccineFirstDoseDate = view.findViewById(R.id.tv_vaccine_first_dose_date);
        tvVaccineFirstDoseStatus = view.findViewById(R.id.tv_vaccine_first_dose_status);
        tvVaccineSecondDoseDate = view.findViewById(R.id.tv_vaccine_second_dose_date);
        tvVaccineSecondDoseStatus = view.findViewById(R.id.tv_vaccine_second_dose_status);

    }

    private void initInstances() {

        firebaseFirestore = FirebaseFirestore.getInstance();

    }

    private void initSharedPref() {

        sharedPref = context.getSharedPreferences("user", Context.MODE_PRIVATE);

    }

    private String getUserVaccinationStatus(final int userVaccinationStatus) {

        switch (userVaccinationStatus) {

            case 1:
                return "First dose complete";

            case 2:
                return "Fully vaccinated";

        }
        return "Not yet vaccinated";

    }

    private void loadUser() {

        firebaseFirestore
                .collection("Users")
                .document(userId)
                .addSnapshotListener((value, error) ->
                {

                    if (value != null) {

                        final UserModel user = value.toObject(UserModel.class);

                        if (user != null) {

                            final String userImage = user.getUser_image();
                            final String userName = user.getUser_first_name() + " " + user.getUser_last_name();
                            final int userVaccinationStatus = user.getUser_vaccination_status();

                            Glide.with(context).load(userImage).into(civUserImage);
                            tvUserName.setText(userName);
                            tvUserVaccinationStatus.setText(getUserVaccinationStatus(userVaccinationStatus));

                            tvVaccineFirstDoseDate.setText(userVaccinationStatus > 0
                                    ? "already taken"
                                    : "Not taken yet"
                            );

                            tvVaccineFirstDoseStatus.setText(userVaccinationStatus > 0
                                    ? "Vaccinated"
                                    : "Not vaccinated yet"
                            );

                            tvVaccineSecondDoseDate.setText(userVaccinationStatus > 1
                                    ? "already taken"
                                    : "Not taken yet"
                            );

                            tvVaccineSecondDoseStatus.setText(userVaccinationStatus > 1
                                    ? "Vaccinated"
                                    : "Not vaccinated yet"
                            );

                        }

                    }

                });

    }

    private void loadUserSlot() {

        firebaseFirestore
                .collection("Requests")
                .whereEqualTo("user_id", userId)
                .get()
                .addOnSuccessListener(documentSnapshot ->
                {

                    if (documentSnapshot != null && !documentSnapshot.isEmpty()) {

                        for (QueryDocumentSnapshot snapshot : documentSnapshot) {

                            final RequestModel request = snapshot.toObject(RequestModel.class);
                            final String slotId = request.getSlot_id();

                            if (slotId != null)

                                firebaseFirestore
                                        .collection("Slots")
                                        .document(slotId)
                                        .get()
                                        .addOnSuccessListener(_documentSnapshot ->
                                        {

                                            if (_documentSnapshot != null) {

                                                final SlotModel slot = _documentSnapshot.toObject(SlotModel.class);

                                                if (slot != null) {

                                                    final Date vaccineFirstDoseDate = slot.getVaccine_first_dose_date();
                                                    final Date vaccineSecondDoseDate = slot.getVaccine_second_dose_date();

                                                    final String vaccineDoseDateFormat = "MMM d, yyyy";

                                                    final DateFormat dateFormat = new SimpleDateFormat(vaccineDoseDateFormat);

                                                    tvVaccineFirstDoseDate.setText(dateFormat.format(vaccineFirstDoseDate));
                                                    tvVaccineSecondDoseDate.setText(dateFormat.format(vaccineSecondDoseDate));

                                                }

                                            }

                                        });

                        }

                    }

                });

    }

}
package com.codewithjosh.ImmuniNation2k21.fragments.users;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.codewithjosh.ImmuniNation2k21.R;
import com.codewithjosh.ImmuniNation2k21.adapters.SlotAdapter;
import com.codewithjosh.ImmuniNation2k21.adapters.VaccineAdapter;
import com.codewithjosh.ImmuniNation2k21.models.SlotModel;
import com.codewithjosh.ImmuniNation2k21.models.UserModel;
import com.codewithjosh.ImmuniNation2k21.models.VaccineModel;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ScheduleFragment extends Fragment {

    CircleImageView civUserImage;
    ImageButton navSchedule;
    RecyclerView recyclerSlots;
    RecyclerView recyclerVaccines;
    TextView tvSubtitle;
    TextView tvTitle;
    TextView tvUserName;
    TextView tvVaccinationStatus;
    String userId;
    Activity activity;
    Context context;
    FirebaseFirestore firebaseFirestore;
    SharedPreferences sharedPref;
    VaccineAdapter vaccineAdapter;
    private SlotAdapter slotAdapter;
    private List<SlotModel> slots;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_schedule, container, false);

        initViews(view);
        initInstances();
        initSharedPref();
        load();
        loadUser();
        loadSlots();

        return view;

    }

    private void initViews(final View view) {

        if (getContext() != null) context = getContext();
        if (getActivity() != null) activity = getActivity();
        activity.getWindow().setStatusBarColor(context.getColor(R.color.color_blue_green));

        civUserImage = view.findViewById(R.id.civ_user_image);
        navSchedule = view.findViewById(R.id.nav_schedule);
        recyclerSlots = view.findViewById(R.id.recycler_slots);
        recyclerVaccines = view.findViewById(R.id.recycler_vaccines);
        tvSubtitle = view.findViewById(R.id.tv_subtitle);
        tvTitle = view.findViewById(R.id.tv_title);
        tvUserName = view.findViewById(R.id.tv_user_name);
        tvVaccinationStatus = view.findViewById(R.id.tv_vaccination_status);

        initRecyclerView(recyclerSlots);

        slots = new ArrayList<>();
        slotAdapter = new SlotAdapter(getContext(), slots, R.layout.item_slot);
        recyclerSlots.setAdapter(slotAdapter);

        initRecyclerView(recyclerVaccines);

        final List<VaccineModel> vaccines = new ArrayList<>();
        vaccines.add(new VaccineModel(R.drawable.img_pfizer, "Pfizer-BioNTech"));
        vaccines.add(new VaccineModel(R.drawable.img_moderna, "Moderna"));
        vaccines.add(new VaccineModel(R.drawable.img_johnson_johnson, "Johnson & Johnson"));
        vaccines.add(new VaccineModel(R.drawable.img_sinovac, "SinoVac"));
        vaccines.add(new VaccineModel(R.drawable.img_astra_zeneca, "AstraZeneca"));
        vaccineAdapter = new VaccineAdapter(getActivity(), vaccines);
        recyclerVaccines.setAdapter(vaccineAdapter);

    }

    private void initInstances() {

        firebaseFirestore = FirebaseFirestore.getInstance();

    }

    private void initSharedPref() {

        sharedPref = context.getSharedPreferences("user", Context.MODE_PRIVATE);

    }

    private void load() {

        userId = sharedPref.getString("user_id", String.valueOf(Context.MODE_PRIVATE));

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
                            tvVaccinationStatus.setText(getVaccinationStatus(userVaccinationStatus));

                        }

                    }

                });

    }

    private String getVaccinationStatus(final int userVaccinationStatus) {

        switch (userVaccinationStatus) {

            case 0:
                return "Not yet vaccinated";

            case 1:
                return "First dose complete";

        }
        return "Fully vaccinated";

    }

    private void initRecyclerView(final RecyclerView recyclerView) {

        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(
                getActivity(),
                LinearLayoutManager.HORIZONTAL,
                false
        );
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setHasFixedSize(true);

    }

    private void loadSlots() {

        firebaseFirestore
                .collection("Slots")
                .orderBy("vaccine_first_dose_date")
                .addSnapshotListener((value, error) ->
                {

                    if (value != null) {

                        if (isConnected()) onLoadSlots(value);

                        else
                            Toast.makeText(context, "No Internet Connection!", Toast.LENGTH_SHORT).show();

                    }

                });

    }

    private boolean isConnected() {

        final ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        final NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnectedOrConnecting();

    }

    private void onLoadSlots(final QuerySnapshot value) {

        slots.clear();
        for (QueryDocumentSnapshot snapshot : value) {

            final SlotModel slot = snapshot.toObject(SlotModel.class);
            final String slotId = slot.getSlot_id();
            final int vaccineSlots = slot.getVaccine_slots();

            firebaseFirestore
                    .collection("Requests")
                    .whereEqualTo("slot_id", slotId)
                    .addSnapshotListener((_value, error) ->
                    {

                        if (_value != null) {

                            final int currentSlots = _value.size();
                            final int availableSlots = vaccineSlots - currentSlots;

                            if (availableSlots != 0) slots.add(slot);
                            slotAdapter.notifyDataSetChanged();

                        }

                    });

        }

    }

}
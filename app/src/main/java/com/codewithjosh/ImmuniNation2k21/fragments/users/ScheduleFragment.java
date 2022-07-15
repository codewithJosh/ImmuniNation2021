package com.codewithjosh.ImmuniNation2k21.fragments.users;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.codewithjosh.ImmuniNation2k21.R;
import com.codewithjosh.ImmuniNation2k21.models.UserModel;
import com.google.firebase.firestore.FirebaseFirestore;

import de.hdodenhof.circleimageview.CircleImageView;

public class ScheduleFragment extends Fragment
{

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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_schedule, container, false);

        initViews(view);
        initInstances();
        initSharedPref();
        load();
        loadUser();

        return view;

    }

    private void initViews(final View view)
    {

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

    }

    private void initInstances()
    {

        firebaseFirestore = FirebaseFirestore.getInstance();

    }

    private void initSharedPref()
    {

        sharedPref = context.getSharedPreferences("user", Context.MODE_PRIVATE);

    }

    private void load()
    {

        userId = sharedPref.getString("user_id", String.valueOf(Context.MODE_PRIVATE));

    }

    private void loadUser()
    {

        firebaseFirestore
                .collection("Users")
                .document(userId)
                .addSnapshotListener((value, error) ->
                {

                    if (value != null)
                    {

                        final UserModel user = value.toObject(UserModel.class);

                        if (user != null)
                        {

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

    private String getVaccinationStatus(final int userVaccinationStatus)
    {

        switch (userVaccinationStatus)
        {

            case 0:
                return "Not yet vaccinated";

            case 1:
                return "First dose complete";

        }
        return "Fully vaccinated";

    }

}
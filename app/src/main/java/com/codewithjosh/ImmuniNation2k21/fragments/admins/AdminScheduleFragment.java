package com.codewithjosh.ImmuniNation2k21.fragments.admins;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.codewithjosh.ImmuniNation2k21.CreateSlotActivity;
import com.codewithjosh.ImmuniNation2k21.MainActivity;
import com.codewithjosh.ImmuniNation2k21.R;
import com.codewithjosh.ImmuniNation2k21.adapters.SlotAdapter;
import com.codewithjosh.ImmuniNation2k21.models.SlotModel;
import com.codewithjosh.ImmuniNation2k21.models.UserModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class AdminScheduleFragment extends Fragment {

    Button btnLogout;
    CircleImageView civUserImage;
    ImageButton navCreateSlot;
    RecyclerView recyclerSlots;
    TextView tvUserName;
    String userId;
    Context context;
    FirebaseAuth firebaseAuth;
    FirebaseFirestore firebaseFirestore;
    ProgressDialog pd;
    SharedPreferences sharedPref;
    SharedPreferences.Editor editor;
    private SlotAdapter slotAdapter;
    private List<SlotModel> slots;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_admin_schedule, container, false);

        initViews(view);
        initInstances();
        initSharedPref();
        load();
        loadUser();
        loadSlots();
        buildButtons();

        return view;

    }

    private void initViews(final View view) {

        if (getContext() != null) context = getContext();

        tvUserName = view.findViewById(R.id.tv_user_name);
        civUserImage = view.findViewById(R.id.civ_user_image);
        btnLogout = view.findViewById(R.id.btn_logout);
        navCreateSlot = view.findViewById(R.id.nav_create_slot);
        recyclerSlots = view.findViewById(R.id.recycler_slots);

        initRecyclerView();

        slots = new ArrayList<>();
        slotAdapter = new SlotAdapter(getContext(), slots, R.layout.item_admin_slot);
        recyclerSlots.setAdapter(slotAdapter);

    }

    private void initRecyclerView() {

        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerSlots.setLayoutManager(linearLayoutManager);
        recyclerSlots.setHasFixedSize(true);

    }

    private void initInstances() {

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();

    }

    private void initSharedPref() {

        sharedPref = context.getSharedPreferences("user", Context.MODE_PRIVATE);
        editor = sharedPref.edit();

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

                    if (value != null && value.exists()) {

                        final UserModel user = value.toObject(UserModel.class);

                        if (user != null) {

                            final String userName = "Hello! ".concat(user.getUser_first_name());
                            final String userImage = user.getUser_image();

                            tvUserName.setText(userName);
                            Glide.with(context).load(userImage).into(civUserImage);

                        }

                    }

                });

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
            slots.add(slot);
            slotAdapter.notifyDataSetChanged();

        }

    }

    private void buildButtons() {

        btnLogout.setOnClickListener(v ->
        {

            pd = new ProgressDialog(context);
            pd.setMessage("Signing out");
            pd.show();

            firebaseAuth.signOut();
            editor.putString("user_id", "");
            editor.apply();
            final Intent i = new Intent(context, MainActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(i);

        });

        navCreateSlot.setOnClickListener(v -> startActivity(new Intent(context, CreateSlotActivity.class)));

    }

}
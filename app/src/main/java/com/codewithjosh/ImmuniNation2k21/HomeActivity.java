package com.codewithjosh.ImmuniNation2k21;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.codewithjosh.ImmuniNation2k21.fragments.AdminScheduleFragment;
import com.codewithjosh.ImmuniNation2k21.fragments.ScheduleFragment;
import com.codewithjosh.ImmuniNation2k21.models.UserModel;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.FirebaseFirestore;

public class HomeActivity extends AppCompatActivity {

    BottomNavigationView bottomNavigation;
    FirebaseFirestore firebaseFirestore;
    SharedPreferences sharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        initViews();
        initInstances();
        load();

    }

    private void initViews()
    {

        bottomNavigation = findViewById(R.id.bottom_navigation);

    }

    private void initInstances()
    {

        firebaseFirestore = FirebaseFirestore.getInstance();

    }

    private void load()
    {

        sharedPref = getSharedPreferences("user", MODE_PRIVATE);

        final String userId = sharedPref.getString("user_id", String.valueOf(MODE_PRIVATE));

        checkUserAdmin(userId);

    }

    private void checkUserAdmin(final String userId)
    {

        firebaseFirestore
                .collection("Users")
                .document(userId)
                .addSnapshotListener((value, error) -> {

                    if (value != null)
                    {

                        final UserModel user = value.toObject(UserModel.class);

                        final boolean userIsAdmin = user != null && user.isUser_is_admin();

                        getSupportFragmentManager().beginTransaction().replace(
                                R.id.frame,
                                userIsAdmin
                                        ? new AdminScheduleFragment()
                                        : new ScheduleFragment()
                        ).commit();

                    }

                });

    }

}
package com.codewithjosh.ImmuniNation2k21;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.codewithjosh.ImmuniNation2k21.fragments.admins.AdminRequestFragment;
import com.codewithjosh.ImmuniNation2k21.fragments.admins.AdminScheduleFragment;
import com.codewithjosh.ImmuniNation2k21.fragments.users.ProfileFragment;
import com.codewithjosh.ImmuniNation2k21.fragments.users.ScheduleFragment;
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
        buildButtons();

    }

    private void initViews() {

        bottomNavigation = findViewById(R.id.bottom_navigation);

    }

    private void initInstances() {

        firebaseFirestore = FirebaseFirestore.getInstance();

    }

    private void load() {

        sharedPref = getSharedPreferences("user", MODE_PRIVATE);

        final String userId = sharedPref.getString("user_id", String.valueOf(MODE_PRIVATE));

        checkUserAdmin(userId);

    }

    private void checkUserAdmin(final String userId) {

        firebaseFirestore
                .collection("Users")
                .document(userId)
                .addSnapshotListener((value, error) -> {

                    if (value != null) {

                        final UserModel user = value.toObject(UserModel.class);

                        final boolean userIsAdmin = user != null && user.isUser_is_admin();

                        getSupportFragmentManager().beginTransaction().replace(
                                R.id.frame,
                                userIsAdmin
                                        ? new AdminScheduleFragment()
                                        : new ScheduleFragment()
                        ).commit();

                        bottomNavigation.getMenu().clear();

                        bottomNavigation.inflateMenu(
                                userIsAdmin
                                        ? R.menu.menu_navigation_admin
                                        : R.menu.menu_navigation_user
                        );

                        bottomNavigation.setSelectedItemId(
                                userIsAdmin
                                        ? R.id.nav_admin_schedule
                                        : R.id.nav_schedule
                        );

                    }

                });

    }

    private void buildButtons() {

        bottomNavigation.setOnItemSelectedListener(item ->
        {

            getSupportFragmentManager().beginTransaction().replace(R.id.frame, getFragment(item)).commit();
            return true;

        });

    }

    private Fragment getFragment(final MenuItem item) {

        final int itemId = item.getItemId();

        if (itemId == R.id.nav_profile) return new ProfileFragment();

        else if (itemId == R.id.nav_admin_schedule) return new AdminScheduleFragment();

        else if (itemId == R.id.nav_request_admin) return new AdminRequestFragment();

        return new ScheduleFragment();

    }

}
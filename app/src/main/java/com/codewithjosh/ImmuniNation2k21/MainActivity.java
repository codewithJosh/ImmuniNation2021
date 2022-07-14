package com.codewithjosh.ImmuniNation2k21;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    FirebaseUser firebaseUser;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();
        initSharedPref();

        final Thread thread = new Thread() {

            @Override
            public void run() {

                try {

                    sleep(3000);

                } catch (Exception e) {

                    e.printStackTrace();

                } finally {

                    if (isConnected()) checkCurrentAuthState();

                    else
                        runOnUiThread(() -> Toast.makeText(MainActivity.this, "No Internet Connection!", Toast.LENGTH_LONG).show());

                }

            }

        };

        thread.start();

    }

    private void initViews() {

        getWindow().setNavigationBarColor(getColor(R.color.color_blue_green));
        getWindow().setStatusBarColor(getColor(R.color.color_dark_cyan));

    }

    private void initSharedPref() {

        editor = getSharedPreferences("user", MODE_PRIVATE).edit();

    }

    private boolean isConnected() {

        final ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        final NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnectedOrConnecting();

    }

    private void checkCurrentAuthState() {

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        if (firebaseUser != null) {

            final String userId = firebaseUser.getUid();

            editor.putString("user_id", userId);
            editor.apply();
            startActivity(new Intent(this, HomeActivity.class));

        } else startActivity(new Intent(this, LoginActivity.class));

        finish();

    }

}
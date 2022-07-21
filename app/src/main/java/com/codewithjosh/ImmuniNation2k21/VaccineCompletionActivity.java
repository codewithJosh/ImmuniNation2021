package com.codewithjosh.ImmuniNation2k21;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;

public class VaccineCompletionActivity extends AppCompatActivity
{

    Button btnPass;
    Button btnFail;
    TextView tvDoseCompletion;
    TextView tvUserName;
    TextView tvUserStreet;
    TextView tvVaccineName;
    int requestStatus;
    String requestId;
    String requestUserId;
    String userName;
    String userStreet;
    String vaccineName;
    DocumentReference documentRef;
    FirebaseFirestore firebaseFirestore;
    SharedPreferences sharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vaccine_completion);

        initViews();
        initInstances();
        initSharedPref();
        load();
        buildButtons();

    }

    private void initViews() {

        btnPass = findViewById(R.id.btn_pass);
        btnFail = findViewById(R.id.btn_fail);
        tvDoseCompletion = findViewById(R.id.tv_dose_completion);
        tvUserName = findViewById(R.id.tv_user_name);
        tvUserStreet = findViewById(R.id.tv_user_street);
        tvVaccineName = findViewById(R.id.tv_vaccine_name);

    }

    private void initInstances() {

        firebaseFirestore = FirebaseFirestore.getInstance();

    }

    private void initSharedPref() {

        sharedPref = getSharedPreferences("user", MODE_PRIVATE);

    }

    private void load() {

        requestStatus = sharedPref.getInt("request_status", MODE_PRIVATE);
        requestId = sharedPref.getString("request_id", String.valueOf(MODE_PRIVATE));
        requestUserId = sharedPref.getString("request_user_id", String.valueOf(MODE_PRIVATE));
        userName = sharedPref.getString("user_name", String.valueOf(MODE_PRIVATE));
        userStreet = sharedPref.getString("user_street", String.valueOf(MODE_PRIVATE));
        vaccineName = sharedPref.getString("vaccine_name", String.valueOf(MODE_PRIVATE));

        final String doseCompletion = "% Dose Completion";
        final String doseComplete = "% Dose Complete";

        btnPass.setText(
                requestStatus == 1
                        ? doseComplete.replace("%", "First")
                        : doseComplete.replace("%", "Second")
        );

        tvDoseCompletion.setText(
                requestStatus == 1
                        ? doseCompletion.replace("%", "First")
                        : doseCompletion.replace("%", "Second")
        );

        tvVaccineName.setText(vaccineName);
        tvUserName.setText(userName);
        tvUserStreet.setText(userStreet);

        documentRef = firebaseFirestore.collection("Requests").document(requestId);

    }

    private void buildButtons() {

        btnPass.setOnClickListener(v ->
        {

            if (isConnected()) onPass();

            else Toast.makeText(this, "No Internet Connection!", Toast.LENGTH_SHORT).show();

        });

        btnFail.setOnClickListener(v -> {

            if (isConnected()) onFail();

            else Toast.makeText(this, "No Internet Connection!", Toast.LENGTH_SHORT).show();

        });

    }

    private boolean isConnected() {

        final ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        final NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnectedOrConnecting();

    }

    private void onPass() {

        final DocumentReference userRef = firebaseFirestore
                .collection("Users")
                .document(requestUserId);

        final HashMap<String, Object> user = new HashMap<>();
        user.put("user_vaccination_status", requestStatus);

        updateUser(userRef, user);

    }

    private void updateUser(final DocumentReference userRef, final HashMap<String, Object> user) {

        userRef
                .get()
                .addOnSuccessListener(documentSnapshot ->
                {

                    if (documentSnapshot != null && documentSnapshot.exists())

                        userRef
                                .update(user)
                                .addOnSuccessListener(unused -> {

                                    final HashMap<String, Object> request = new HashMap<>();
                                    request.put("request_status", requestStatus + 1);

                                    updateUserRequest(documentRef, request);

                                }).addOnFailureListener(e -> Toast.makeText(this, "Please Contact Your Service Provider", Toast.LENGTH_SHORT).show());

                });

    }

    private void updateUserRequest(final DocumentReference requestRef, final HashMap<String, Object> request) {

        final String text = "% Dose Complete!".replace(
                "%",
                requestStatus == 1
                        ? "First"
                        : "Second"
        );

        requestRef
                .get()
                .addOnSuccessListener(documentSnapshot ->
                {

                    if (documentSnapshot != null && documentSnapshot.exists())

                        requestRef
                                .update(request)
                                .addOnSuccessListener(unused -> {

                                    Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(this, HomeActivity.class));
                                    finish();

                                }).addOnFailureListener(e -> Toast.makeText(this, "Please Contact Your Service Provider", Toast.LENGTH_SHORT).show());

                });

    }

    private void onFail() {

        documentRef
                .get()
                .addOnSuccessListener(documentSnapshot ->
                {

                    if (documentSnapshot != null && documentSnapshot.exists())

                        documentRef
                                .delete()
                                .addOnSuccessListener(unused -> {

                                    Toast.makeText(this, "Failed to attend!", Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(this, HomeActivity.class));
                                    finish();

                                }).addOnFailureListener(e -> Toast.makeText(this, "Please Contact Your Service Provider", Toast.LENGTH_SHORT).show());

                });

    }

}
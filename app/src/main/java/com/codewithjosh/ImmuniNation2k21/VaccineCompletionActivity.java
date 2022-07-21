package com.codewithjosh.ImmuniNation2k21;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;

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

    }

}
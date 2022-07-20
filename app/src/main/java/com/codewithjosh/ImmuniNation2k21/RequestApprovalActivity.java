package com.codewithjosh.ImmuniNation2k21;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.codewithjosh.ImmuniNation2k21.models.RequestModel;
import com.codewithjosh.ImmuniNation2k21.models.SlotModel;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

public class RequestApprovalActivity extends AppCompatActivity
{

    Button btnAccept;
    Button btnReject;
    TextView tvUserBirthDate;
    TextView tvUserCategory;
    TextView tvUserContact;
    TextView tvUserName;
    TextView tvUserSelfieWithId;
    TextView tvUserStreet;
    TextView tvUserValidId;
    TextView tvVaccineDoseDate;
    TextView tvVaccineName;
    String requestId;
    String slotId;
    String vaccineDoseDateFormat;
    DateFormat dateFormat;
    DocumentReference documentRef;
    FirebaseFirestore firebaseFirestore;
    SharedPreferences sharedPref;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_approval);

        initViews();
        initInstances();
        initSharedPref();
        load();
        loadUserSlot();
        loadUserRequest();
        buildButtons();

    }

    private void initViews() {

        btnAccept = findViewById(R.id.btn_accept);
        btnReject = findViewById(R.id.btn_reject);
        tvUserBirthDate = findViewById(R.id.tv_user_birth_date);
        tvUserCategory = findViewById(R.id.tv_user_category);
        tvUserContact = findViewById(R.id.tv_user_contact);
        tvUserName = findViewById(R.id.tv_user_name);
        tvUserSelfieWithId = findViewById(R.id.tv_user_selfie_with_id);
        tvUserStreet = findViewById(R.id.tv_user_street);
        tvUserValidId = findViewById(R.id.tv_user_valid_id);
        tvVaccineDoseDate = findViewById(R.id.tv_vaccine_dose_date);
        tvVaccineName = findViewById(R.id.tv_vaccine_name);

        vaccineDoseDateFormat = "MMM d, yyyy";

        dateFormat = new SimpleDateFormat(vaccineDoseDateFormat);

    }

    private void initInstances() {

        firebaseFirestore = FirebaseFirestore.getInstance();

    }

    private void initSharedPref() {

        sharedPref = getSharedPreferences("user", Context.MODE_PRIVATE);
        editor = sharedPref.edit();

    }

    private void load() {

        requestId = sharedPref.getString("request_id", String.valueOf(Context.MODE_PRIVATE));
        slotId = sharedPref.getString("slot_id", String.valueOf(Context.MODE_PRIVATE));

        documentRef = firebaseFirestore.collection("Requests").document(requestId);

    }

    private void loadUserSlot() {

        firebaseFirestore
                .collection("Slots")
                .document(slotId)
                .get()
                .addOnSuccessListener(documentSnapshot ->
                {

                    if (documentSnapshot != null && documentSnapshot.exists()) {

                        final SlotModel slot = documentSnapshot.toObject(SlotModel.class);

                        if (slot != null) {

                            final String vaccineName = slot.getVaccine_name();
                            final Date vaccineFirstDoseDate = slot.getVaccine_first_dose_date();
                            final Date vaccineSecondDoseDate = slot.getVaccine_second_dose_date();
                            final String vaccineFirstDoseDateFormat = dateFormat.format(vaccineFirstDoseDate);
                            final String vaccineSecondDoseDateFormat = dateFormat.format(vaccineSecondDoseDate);
                            final String vaccineDoseDate = vaccineFirstDoseDateFormat + " - " + vaccineSecondDoseDateFormat;

                            tvVaccineName.setText(vaccineName);
                            tvVaccineDoseDate.setText(vaccineDoseDate);

                        }

                    }

                });

    }

    private void loadUserRequest() {

        documentRef
                .get()
                .addOnSuccessListener(documentSnapshot ->
                {

                    if (documentSnapshot != null && documentSnapshot.exists()) {

                        final RequestModel request = documentSnapshot.toObject(RequestModel.class);

                        if (request != null) {

                            final Date userBirthDate = request.getUser_birth_date();
                            final String userBirthDateFormat = dateFormat.format(userBirthDate);
                            final String userCategory = request.getUser_category();
                            final String userContact = request.getUser_contact();
                            final String userName = request.getUser_name();
                            final String userSelfieWithId = request.getUser_selfie_with_id();
                            final String userStreet = request.getUser_street();
                            final String userValidId = request.getUser_valid_id();
                            final String userSelfieWithIdImage = request.getUser_selfie_with_id_image();
                            final String userValidIdImage = request.getUser_valid_id_image();

                            tvUserBirthDate.setText(userBirthDateFormat);
                            tvUserCategory.setText(userCategory);
                            tvUserContact.setText(userContact);
                            tvUserName.setText(userName);
                            tvUserSelfieWithId.setText(userSelfieWithId);
                            tvUserStreet.setText(userStreet);
                            tvUserValidId.setText(userValidId);

                            tvUserValidId.setOnClickListener(v -> onViewImage(userValidIdImage));

                            tvUserSelfieWithId.setOnClickListener(v -> onViewImage(userSelfieWithIdImage));

                        }

                    }

                });

    }

    private void onViewImage(final String image) {

        editor.putString("image", image);
        editor.apply();
        startActivity(new Intent(this, ViewImageActivity.class));

    }

    private void buildButtons() {

        btnReject.setOnClickListener(v ->
        {

            if (isConnected()) onReject();

            else Toast.makeText(this, "No Internet Connection!", Toast.LENGTH_SHORT).show();

        });


        btnAccept.setOnClickListener(v ->
        {

            if (isConnected()) onAccept();

            else Toast.makeText(this, "No Internet Connection!", Toast.LENGTH_SHORT).show();

        });


    }

    private boolean isConnected() {

        final ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        final NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnectedOrConnecting();

    }

    private void onReject() {

        documentRef
                .get()
                .addOnSuccessListener(documentSnapshot ->
                {

                    if (documentSnapshot != null && documentSnapshot.exists())

                        documentRef
                            .delete()
                            .addOnSuccessListener(unused ->
                            {

                                Toast.makeText(this, "Request has been removed!", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(this, HomeActivity.class));
                                finish();

                            }).addOnFailureListener(e -> Toast.makeText(this, "Please Contact Your Service Provider", Toast.LENGTH_SHORT).show());

                });

    }

    private void onAccept() {

        final int requestStatus = 1;

        final HashMap<String, Object> request = new HashMap<>();
        request.put("request_status", requestStatus);

        documentRef
                .get()
                .addOnSuccessListener(documentSnapshot ->
                {

                    if (documentSnapshot != null && documentSnapshot.exists())

                        documentRef
                                .update(request)
                                .addOnSuccessListener(unused ->
                                {

                                    Toast.makeText(this, "Request has been approved!", Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(this, HomeActivity.class));
                                    finish();

                                }).addOnFailureListener(e -> Toast.makeText(this, "Please Contact Your Service Provider", Toast.LENGTH_SHORT).show());

                });

    }

}
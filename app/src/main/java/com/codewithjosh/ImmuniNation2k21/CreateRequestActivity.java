package com.codewithjosh.ImmuniNation2k21;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.codewithjosh.ImmuniNation2k21.models.RequestModel;
import com.codewithjosh.ImmuniNation2k21.models.UserModel;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;

public class CreateRequestActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    public static final int cameraRequest = 99;
    public static final int storageRequest = 100;
    Button btnCreateRequest;
    EditText etUserContact;
    Spinner sUserCategory;
    Spinner sUserStreet;
    TextView tvUserBirthDate;
    TextView tvUserValidId;
    TextView tvUserSelfieWithId;
    boolean hasFocusOnUserValidId;
    Date userBirthDate;
    String[] cameraPermission;
    String[] storagePermission;
    String[] userCategories;
    String[] userStreets;
    String slotId;
    String userCategory;
    String userContact;
    String userId;
    String userName;
    String userSelfieWithIdUrl;
    String userStreet;
    String userValidIdUrl;
    Uri userValidIdUri;
    Uri userSelfieWithIdUri;
    Calendar calendar;
    DateFormat dateFormat;
    DocumentReference documentRef;
    FirebaseFirestore firebaseFirestore;
    FirebaseStorage firebaseStorage;
    ProgressDialog pd;
    SharedPreferences sharedPref;
    StorageReference storageRef;
    UploadTask uTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_request);

        initViews();
        initInstances();
        initSharedPref();
        load();
        loadUser();
        build();

    }

    private void initViews() {

        cameraPermission = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        storagePermission = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};

        btnCreateRequest = findViewById(R.id.btn_create_request);
        etUserContact = findViewById(R.id.et_user_contact);
        sUserCategory = findViewById(R.id.s_user_category);
        sUserStreet = findViewById(R.id.s_user_street);
        tvUserBirthDate = findViewById(R.id.tv_user_birth_date);
        tvUserValidId = findViewById(R.id.tv_user_valid_id);
        tvUserSelfieWithId = findViewById(R.id.tv_user_selfie_with_id);

    }

    private void initInstances() {

        calendar = Calendar.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseStorage = FirebaseStorage.getInstance();

    }

    private void initSharedPref() {

        sharedPref = getSharedPreferences("user", MODE_PRIVATE);

    }

    private void load() {

        slotId = sharedPref.getString("slot_id", String.valueOf(MODE_PRIVATE));
        userId = sharedPref.getString("user_id", String.valueOf(MODE_PRIVATE));

        userCategories = getResources().getStringArray(R.array.UserCategories);
        userStreets = getResources().getStringArray(R.array.UserStreets);

        final String userBirthDateFormat = "dd/MM/yyyy";
        dateFormat = new SimpleDateFormat(userBirthDateFormat);

        storageRef = firebaseStorage.getReference("Requests").child(userId);

    }

    private void loadUser() {

        firebaseFirestore
                .collection("Users")
                .document(userId)
                .get()
                .addOnSuccessListener(documentSnapshot ->
                {

                    if (documentSnapshot != null && documentSnapshot.exists()) {

                        final UserModel user = documentSnapshot.toObject(UserModel.class);
                        if (user != null)
                            userName = user.getUser_first_name() + " " + user.getUser_last_name();

                    }

                });

    }

    private void build() {

        initSpinner(userCategories, sUserCategory);
        initSpinner(userStreets, sUserStreet);

        tvUserBirthDate.setOnClickListener(v -> onDatePickerDialog(onDateSetListener(tvUserBirthDate)));

        tvUserValidId.setOnClickListener(v ->
        {

            hasFocusOnUserValidId = true;
            getImage();

        });

        tvUserSelfieWithId.setOnClickListener(v ->
        {

            hasFocusOnUserValidId = false;
            getImage();

        });

        btnCreateRequest.setOnClickListener(v ->
        {

            get();

            if (validate(v)) onCreateRequest();

            else pd.dismiss();

        });

    }

    private void get() {

        final String userBirthDateFormat = String.valueOf(tvUserBirthDate.getText());

        try {

            userBirthDate = dateFormat.parse(userBirthDateFormat);

        } catch (ParseException e) {

            e.printStackTrace();

        }

        userCategory = String.valueOf(sUserCategory.getSelectedItem());
        userStreet = String.valueOf(sUserStreet.getSelectedItem());
        userContact = String.valueOf(etUserContact.getText());

    }

    private boolean validate(final View v) {

        pd = new ProgressDialog(this);
        pd.setMessage("Creating request");
        pd.show();

        final InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(v.getWindowToken(), 0);
        if (getCurrentFocus() != null) getCurrentFocus().clearFocus();

        if (!isConnected())
            Toast.makeText(this, "No Internet Connection!", Toast.LENGTH_SHORT).show();

        else if (sUserCategory.getSelectedItemPosition() == 0
                || sUserStreet.getSelectedItemPosition() == 0
                || userContact.isEmpty()
                || userBirthDate == null
                || userValidIdUri == null
                || userSelfieWithIdUri == null)
            Toast.makeText(this, "All fields are required!", Toast.LENGTH_SHORT).show();

        else if (!userContact.startsWith("09"))
            Toast.makeText(this, "Provide a valid Phone Number", Toast.LENGTH_SHORT).show();

        else if (userContact.length() < 11)
            Toast.makeText(this, "Phone Number must be at least 11 digits", Toast.LENGTH_SHORT).show();

        else return true;

        return false;

    }

    private boolean isConnected() {

        final ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        final NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnectedOrConnecting();

    }


    private void onCreateRequest() {

        final StorageReference userValidIdRef = storageRef.child(getRef(userValidIdUri));
        final StorageReference userSelfieWithIdRef = storageRef.child(getRef(userSelfieWithIdUri));

        uTask = userValidIdRef.putFile(userValidIdUri);
        uTask.continueWithTask(task ->
        {

            if (!task.isSuccessful()) throw task.getException();

            return userValidIdRef.getDownloadUrl();

        }).addOnSuccessListener(runnable ->
        {

            userValidIdUrl = String.valueOf(runnable);
            uTask = userSelfieWithIdRef.putFile(userSelfieWithIdUri);
            uTask.continueWithTask(task ->
            {

                if (!task.isSuccessful()) throw task.getException();

                return userSelfieWithIdRef.getDownloadUrl();

            }).addOnSuccessListener(_runnable ->
            {

                userSelfieWithIdUrl = String.valueOf(_runnable);

                final String requestId = firebaseFirestore
                        .collection("Requests")
                        .document()
                        .getId();

                final int requestStatus = 0;
                final String userValidId = getFileName(userValidIdRef);
                final String userSelfieWithId = getFileName(userSelfieWithIdRef);

                final RequestModel request = new RequestModel(
                        requestId,
                        requestStatus,
                        slotId,
                        userBirthDate,
                        userCategory,
                        userContact,
                        userId,
                        userName,
                        userSelfieWithId,
                        userSelfieWithIdUrl,
                        userStreet,
                        userValidId,
                        userValidIdUrl
                );

                setRequest(requestId, request);

            });

        });

    }

    private void setRequest(final String requestId, final RequestModel request) {

        documentRef = firebaseFirestore
                .collection("Requests")
                .document(requestId);

        documentRef.addSnapshotListener((value, error) ->
        {

            if (value != null && !value.exists())

                documentRef
                        .set(request)
                        .addOnSuccessListener(unused ->
                        {

                            pd.dismiss();
                            Toast.makeText(this, "Request Successfully added!", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(this, HomeActivity.class));
                            finish();

                        });

        });

    }

    private String getRef(final Uri uri) {

        final Random ran = new Random();

        return ran.nextInt(999999999) + getFileExtension(uri).substring(getFileExtension(uri).lastIndexOf("."));

    }

    private String getFileName(final StorageReference storageRef) {

        final String ref = String.valueOf(storageRef);

        return ref.substring(ref.lastIndexOf("/") + 1);

    }

    private void onDatePickerDialog(final DatePickerDialog.OnDateSetListener dateSetListener) {

        final int year = calendar.get(Calendar.YEAR);
        final int month = calendar.get(Calendar.MONTH);
        final int day = calendar.get(Calendar.DAY_OF_MONTH);

        final DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                dateSetListener,
                year,
                month,
                day
        );
        datePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        datePickerDialog.show();

    }

    private DatePickerDialog.OnDateSetListener onDateSetListener(final TextView textView) {

        return (datePicker, year, month, day) ->
        {

            month += 1;
            final String userBirthDate = day + "/" + month + "/" + year;
            textView.setText(userBirthDate);

        };

    }

    private void initSpinner(final String[] items, final Spinner spinner) {

        spinner.setSelection(0);
        spinner.setOnItemSelectedListener(this);
        final ArrayAdapter<String> itemAdapter = new ArrayAdapter<>(
                this,
                R.layout.item_spinner,
                items
        );
        itemAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(itemAdapter);

    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

        if (i != 0) {

            final String item = String.valueOf(adapterView.getItemAtPosition(i)).concat(" is selected");
            Toast.makeText(this, item, Toast.LENGTH_SHORT).show();

        }

    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    private void getImage() {

        if (!checkCameraPermission()) requestCameraPermission();

        else if (!checkStoragePermission()) requestStoragePermission();

        else CropImage.activity().start(this);

    }

    private boolean checkCameraPermission() {

        final boolean isCameraGranted = checkSelfPermission(Manifest.permission.CAMERA) == (PackageManager.PERMISSION_GRANTED);
        final boolean isStorageGranted = checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED);
        return isCameraGranted && isStorageGranted;

    }

    private boolean checkStoragePermission() {

        return checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED);

    }

    private void requestCameraPermission() {

        requestPermissions(cameraPermission, cameraRequest);

    }

    private void requestStoragePermission() {

        requestPermissions(storagePermission, storageRequest);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {

            case cameraRequest:

                if (grantResults.length > 0) {

                    final boolean cameraAccepted = grantResults[0] == (PackageManager.PERMISSION_GRANTED);
                    final boolean storageAccepted = grantResults[1] == (PackageManager.PERMISSION_GRANTED);

                    if (cameraAccepted && storageAccepted) getImage();

                    else
                        Toast.makeText(this, "Please enable camera and storage permission", Toast.LENGTH_SHORT).show();

                }
                break;

            case storageRequest:

                if (grantResults.length > 0) {

                    final boolean storageAccepted = grantResults[0] == (PackageManager.PERMISSION_GRANTED);

                    if (storageAccepted) getImage();

                    else
                        Toast.makeText(this, "Please enable storage permission", Toast.LENGTH_SHORT).show();

                }
                break;

        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK) {

            final CropImage.ActivityResult result = CropImage.getActivityResult(data);

            if (result != null) {

                if (hasFocusOnUserValidId) {

                    userValidIdUri = result.getUri();
                    tvUserValidId.setText(getFileExtension(userValidIdUri));

                } else {

                    userSelfieWithIdUri = result.getUri();
                    tvUserSelfieWithId.setText(getFileExtension(userSelfieWithIdUri));

                }

            }

        } else onBackPressed();

    }

    private String getFileExtension(final Uri uri) {

        final String result = uri.getPath();
        final int cut = result.lastIndexOf('/');
        return result.substring(cut + 1);

    }

}
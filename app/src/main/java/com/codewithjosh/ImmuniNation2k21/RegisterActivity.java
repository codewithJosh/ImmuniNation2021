package com.codewithjosh.ImmuniNation2k21;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.codewithjosh.ImmuniNation2k21.models.UserModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class RegisterActivity extends AppCompatActivity {

    Button btnRegister;
    EditText etEmail;
    EditText etFirstName;
    EditText etLastName;
    EditText etPassword;
    LinearLayout navLogin;
    String email;
    String firstName;
    String lastName;
    String password;
    DocumentReference documentRef;
    FirebaseAuth firebaseAuth;
    FirebaseFirestore firebaseFirestore;
    FirebaseUser firebaseUser;
    ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        initViews();
        initInstances();
        buildButtons();

    }

    private void initViews() {

        getWindow().setStatusBarColor(getColor(R.color.color_dark_cyan));

        btnRegister = findViewById(R.id.btn_register);
        etFirstName = findViewById(R.id.et_first_name);
        etLastName = findViewById(R.id.et_last_name);
        etEmail = findViewById(R.id.et_email);
        etPassword = findViewById(R.id.et_password);
        navLogin = findViewById(R.id.nav_login);

    }

    private void initInstances()
    {

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();

    }

    private void buildButtons()
    {

        navLogin.setOnClickListener(v ->
        {

            startActivity(new Intent(this, LoginActivity.class));
            finish();

        });

        btnRegister.setOnClickListener(v ->
        {

            getString();

            if (validate(v)) onRegister();

            else pd.dismiss();

        });

    }

    private void getString()
    {

        firstName = etFirstName.getText().toString().trim();
        lastName = etLastName.getText().toString().trim();
        email = etEmail.getText().toString().toLowerCase();
        password = etPassword.getText().toString();

    }

    private boolean validate(final View v)
    {

        pd = new ProgressDialog(this);
        pd.setMessage("Signing up");
        pd.show();

        final InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(v.getWindowToken(), 0);
        if (getCurrentFocus() != null) getCurrentFocus().clearFocus();

        if (!isConnected()) Toast.makeText(this, "No Internet Connection!", Toast.LENGTH_SHORT).show();

        else if (firstName.isEmpty()
                || lastName.isEmpty()
                || email.isEmpty()
                || password.isEmpty())
            Toast.makeText(this, "All fields are required!", Toast.LENGTH_SHORT).show();

        else if (!(email.contains("@") && email.endsWith(".com"))
                || email.startsWith("@")
                || email.contains("@.com")
                || email.length() < 19)
            Toast.makeText(this, "Provide a valid Email Address", Toast.LENGTH_SHORT).show();

        else if (password.length() < 6) Toast.makeText(this, "Password Must be at least 6 characters", Toast.LENGTH_SHORT).show();

        else return true;

        return false;

    }

    private boolean isConnected()
    {

        final ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        final NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnectedOrConnecting();

    }

    private void onRegister()
    {

        firebaseAuth
                .createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener(authResult ->
                {

                    firebaseUser = firebaseAuth.getCurrentUser();

                    if (firebaseUser != null)
                    {

                        final String userId = firebaseUser.getUid();
                        final String userImage = "https://firebasestorage.googleapis.com/v0/b/immuni-nation-2k21.appspot.com/o/Res_20220702%2Fdefault_user_image.png?alt=media&token=ccc826c9-807b-4904-a7fd-5bebb1283f2c";
                        final boolean userIsAdmin = false;
                        final int userVaccinationStatus = 0;

                        final UserModel user = new UserModel(
                                firstName,
                                userId,
                                userImage,
                                userIsAdmin,
                                lastName,
                                userVaccinationStatus
                        );

                        setUser(userId, user);

                    }

                }).addOnFailureListener(e ->
                {

                    pd.dismiss();

                    final String _e = e.toString().toLowerCase();

                    if (_e.contains("the email address is already in use by another account")) Toast.makeText(this, "Email is Already Exist!", Toast.LENGTH_SHORT).show();

                    else if (_e.contains("a network error (such as timeout, interrupted connection or unreachable host) has occurred")) Toast.makeText(this, "No Internet Connection!", Toast.LENGTH_SHORT).show();

                    else Toast.makeText(this, "Please Contact Your Service Provider", Toast.LENGTH_SHORT).show();

                });

    }

    private void setUser(final String userId, final UserModel user)
    {

        documentRef = firebaseFirestore
                .collection("Users")
                .document(userId);

        documentRef
                .get()
                .addOnSuccessListener(documentSnapshot ->
                {

                    if (documentSnapshot != null)

                        if (!documentSnapshot.exists())

                            documentRef
                                    .set(user)
                                    .addOnSuccessListener(unused ->
                                    {

                                        Toast.makeText(this, "You're Successfully Added!", Toast.LENGTH_LONG).show();
                                        startActivity(new Intent(this, LoginActivity.class));
                                        finish();

                                    })
                                    .addOnFailureListener(e ->
                                    {

                                        pd.dismiss();
                                        Toast.makeText(this, "Please Contact Your Service Provider", Toast.LENGTH_SHORT).show();

                                    });

                });

    }

}
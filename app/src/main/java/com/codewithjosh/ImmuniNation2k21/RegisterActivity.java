package com.codewithjosh.ImmuniNation2k21;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

public class RegisterActivity extends AppCompatActivity {

    Button btnRegister;
    EditText etEmail;
    EditText etFirstname;
    EditText etLastname;
    EditText etPassword;
    LinearLayout navLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        initViews();
        buildButtons();

    }

    private void initViews() {

        getWindow().setStatusBarColor(getColor(R.color.color_dark_cyan));

        btnRegister = findViewById(R.id.btn_register);
        etFirstname = findViewById(R.id.et_first_name);
        etLastname = findViewById(R.id.et_last_name);
        etEmail = findViewById(R.id.et_email);
        etPassword = findViewById(R.id.et_password);
        navLogin = findViewById(R.id.nav_login);

    }

    private void buildButtons()
    {

        navLogin.setOnClickListener(v ->
        {

            startActivity(new Intent(this, LoginActivity.class));
            finish();

        });

    }

}
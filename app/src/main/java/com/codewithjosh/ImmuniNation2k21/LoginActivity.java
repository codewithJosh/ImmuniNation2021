package com.codewithjosh.ImmuniNation2k21;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {

    Button btnLogin;
    EditText etEmail;
    EditText etPassword;
    LinearLayout navRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initViews();
        buildButtons();

    }

    private void initViews()
    {

        getWindow().setStatusBarColor(getColor(R.color.color_dark_cyan));

        btnLogin = findViewById(R.id.btn_login);
        etEmail = findViewById(R.id.et_email);
        etPassword = findViewById(R.id.et_password);
        navRegister = findViewById(R.id.nav_register);

    }

    private void buildButtons()
    {

        navRegister.setOnClickListener(v ->
        {

            startActivity(new Intent(this, RegisterActivity.class));
            finish();

        });

    }

}
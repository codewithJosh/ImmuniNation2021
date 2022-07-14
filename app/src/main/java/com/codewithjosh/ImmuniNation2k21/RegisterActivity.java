package com.codewithjosh.ImmuniNation2k21;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class RegisterActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        initViews();

    }

    private void initViews() {

        getWindow().setStatusBarColor(getColor(R.color.color_dark_cyan));

    }

}
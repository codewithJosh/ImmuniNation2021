package com.codewithjosh.ImmuniNation2k21;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();

    }

    private void initViews()
    {

        getWindow().setNavigationBarColor(getColor(R.color.color_blue_green));
        getWindow().setStatusBarColor(getColor(R.color.color_dark_cyan));

    }

}
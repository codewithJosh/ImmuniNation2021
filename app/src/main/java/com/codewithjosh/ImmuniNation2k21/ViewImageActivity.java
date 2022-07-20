package com.codewithjosh.ImmuniNation2k21;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;

public class ViewImageActivity extends AppCompatActivity
{

    ImageView ivImage;
    String image;
    SharedPreferences sharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_image);

        initViews();
        initSharedPref();
        load();
        loadImage();

    }

    private void initViews() {

        ivImage = findViewById(R.id.iv_image);

    }

    private void initSharedPref() {

        sharedPref = getSharedPreferences("user", MODE_PRIVATE);

    }

    private void load() {

        image = sharedPref.getString("image", String.valueOf(MODE_PRIVATE));

    }

    private void loadImage() {

        Glide.with(this).load(image).into(ivImage);

    }

}
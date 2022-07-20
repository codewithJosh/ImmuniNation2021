package com.codewithjosh.ImmuniNation2k21;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class ViewVaccineActivity extends AppCompatActivity {

    ImageView ivVaccineImage;
    TextView tvVaccineArmSideEffects;
    TextView tvVaccineBodySideEffects;
    TextView tvVaccineInformation;
    TextView tvVaccineManufacturer;
    TextView tvVaccineName;
    TextView tvVaccineShots;
    TextView tvVaccineType;
    String[] vaccineArmSideEffects;
    String[] vaccineBodySideEffects;
    String[] vaccineInformation;
    String[] vaccineManufacturers;
    String[] vaccineShots;
    String[] vaccineTypes;
    int vaccineId;
    int vaccineImage;
    String vaccineName;
    SharedPreferences sharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_vaccine);

        initViews();
        initSharedPref();
        load();
        loadVaccine();

    }

    private void initViews() {

        getWindow().setStatusBarColor(this.getColor(R.color.color_blue_green));

        ivVaccineImage = findViewById(R.id.iv_vaccine_image);
        tvVaccineArmSideEffects = findViewById(R.id.tv_vaccine_arm_side_effects);
        tvVaccineBodySideEffects = findViewById(R.id.tv_vaccine_body_side_effects);
        tvVaccineInformation = findViewById(R.id.tv_vaccine_information);
        tvVaccineManufacturer = findViewById(R.id.tv_vaccine_manufacturer);
        tvVaccineName = findViewById(R.id.tv_vaccine_name);
        tvVaccineShots = findViewById(R.id.tv_vaccine_shots);
        tvVaccineType = findViewById(R.id.tv_vaccine_type);

    }

    private void initSharedPref() {

        sharedPref = getSharedPreferences("user", MODE_PRIVATE);

    }

    private void load() {

        vaccineId = sharedPref.getInt("vaccine_id", MODE_PRIVATE);
        vaccineImage = sharedPref.getInt("vaccine_image", MODE_PRIVATE);
        vaccineName = sharedPref.getString("vaccine_name", String.valueOf(MODE_PRIVATE));

        vaccineArmSideEffects = getResources().getStringArray(R.array.VaccineArmSideEffects);
        vaccineBodySideEffects = getResources().getStringArray(R.array.VaccineBodySideEffects);
        vaccineInformation = getResources().getStringArray(R.array.VaccineInformation);
        vaccineManufacturers = getResources().getStringArray(R.array.VaccineManufacturers);
        vaccineShots = getResources().getStringArray(R.array.VaccineShots);
        vaccineTypes = getResources().getStringArray(R.array.VaccineTypes);

    }

    private void loadVaccine() {

        ivVaccineImage.setImageResource(vaccineImage);
        tvVaccineName.setText(vaccineName);
        tvVaccineManufacturer.setText(vaccineManufacturers[vaccineId]);
        tvVaccineType.setText(vaccineTypes[vaccineId]);
        tvVaccineShots.setText(vaccineShots[vaccineId]);
        tvVaccineInformation.setText(vaccineInformation[vaccineId]);
        tvVaccineArmSideEffects.setText(vaccineArmSideEffects[vaccineId]);
        tvVaccineBodySideEffects.setText(vaccineBodySideEffects[vaccineId]);

    }

}
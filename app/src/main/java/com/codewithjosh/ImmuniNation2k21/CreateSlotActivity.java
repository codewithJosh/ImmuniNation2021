package com.codewithjosh.ImmuniNation2k21;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
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

import androidx.appcompat.app.AppCompatActivity;

import com.codewithjosh.ImmuniNation2k21.models.SlotModel;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class CreateSlotActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    Button btnCreateSlot;
    EditText etVaccineSlots;
    Spinner sVaccineName;
    Spinner sVaccineSite;
    TextView tvVaccineFirstDoseDate;
    TextView tvVaccineSecondDoseDate;
    String[] vaccineNames;
    String[] vaccineSites;
    Date vaccineFirstDoseDate;
    Date vaccineSecondDoseDate;
    String vaccineDoseDateFormat;
    String vaccineName;
    String vaccineSite;
    String vaccineSlots;
    Calendar calendar;
    DateFormat dateFormat;
    DocumentReference documentRef;
    FirebaseFirestore firebaseFirestore;
    ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_slot);

        initViews();
        initInstances();
        load();
        build();

    }

    private void initViews() {

        btnCreateSlot = findViewById(R.id.btn_create_slot);
        etVaccineSlots = findViewById(R.id.et_vaccine_slots);
        sVaccineName = findViewById(R.id.s_vaccine_name);
        sVaccineSite = findViewById(R.id.s_vaccine_site);
        tvVaccineFirstDoseDate = findViewById(R.id.tv_vaccine_first_dose_date);
        tvVaccineSecondDoseDate = findViewById(R.id.tv_vaccine_second_dose_date);

    }

    private void initInstances() {

        calendar = Calendar.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();

    }

    private void load() {

        vaccineNames = getResources().getStringArray(R.array.VaccineNames);
        vaccineSites = getResources().getStringArray(R.array.VaccineSites);

        vaccineDoseDateFormat = "dd/MM/yyyy";
        dateFormat = new SimpleDateFormat(vaccineDoseDateFormat);
        tvVaccineFirstDoseDate.setText(dateFormat.format(calendar.getTime()));
        calendar.add(Calendar.MONTH, 1);
        tvVaccineSecondDoseDate.setText(dateFormat.format(calendar.getTime()));
        calendar.add(Calendar.MONTH, -1);

    }

    private void build() {

        initSpinner(vaccineNames, sVaccineName);
        initSpinner(vaccineSites, sVaccineSite);

        tvVaccineFirstDoseDate.setOnClickListener(v -> onDatePickerDialog(onDateSetListener(tvVaccineFirstDoseDate)));

        tvVaccineSecondDoseDate.setOnClickListener(v -> onDatePickerDialog(onDateSetListener(tvVaccineSecondDoseDate)));

        btnCreateSlot.setOnClickListener(v ->
        {

            get();

            if (validate(v)) onCreateSlot();

            else pd.dismiss();

        });

    }

    private void get() {

        final String vaccineFirstDoseDateFormat = String.valueOf(tvVaccineFirstDoseDate.getText());
        final String vaccineSecondDoseDateFormat = String.valueOf(tvVaccineSecondDoseDate.getText());

        try {

            vaccineFirstDoseDate = dateFormat.parse(vaccineFirstDoseDateFormat);
            vaccineSecondDoseDate = dateFormat.parse(vaccineSecondDoseDateFormat);

        } catch (ParseException e) {

            e.printStackTrace();

        }

        vaccineName = String.valueOf(sVaccineName.getSelectedItem());
        vaccineSite = String.valueOf(sVaccineSite.getSelectedItem());
        vaccineSlots = String.valueOf(etVaccineSlots.getText());

    }

    private boolean validate(final View v) {

        pd = new ProgressDialog(this);
        pd.setMessage("Creating slot");
        pd.show();

        final InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(v.getWindowToken(), 0);
        if (getCurrentFocus() != null) getCurrentFocus().clearFocus();

        if (!isConnected())
            Toast.makeText(this, "No Internet Connection!", Toast.LENGTH_SHORT).show();

        else if (sVaccineName.getSelectedItemPosition() == 0
                || sVaccineSite.getSelectedItemPosition() == 0
                || vaccineSlots.isEmpty())
            Toast.makeText(this, "All fields are required!", Toast.LENGTH_SHORT).show();

        else if (Integer.parseInt(vaccineSlots) < 1)
            Toast.makeText(this, "Vaccination capacity cannot be less than 1!", Toast.LENGTH_SHORT).show();

        else return true;

        return false;

    }

    private boolean isConnected() {

        final ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        final NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnectedOrConnecting();

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
            final String vaccineDoseDate = day + "/" + month + "/" + year;
            textView.setText(vaccineDoseDate);

        };

    }

    private void onCreateSlot() {

        final String slotId = firebaseFirestore
                .collection("Slots")
                .document()
                .getId();

        final int _vaccineSlots = Integer.parseInt(vaccineSlots);

        final SlotModel slot = new SlotModel(
                slotId,
                vaccineFirstDoseDate,
                getVaccineImage(vaccineName),
                vaccineName,
                vaccineSecondDoseDate,
                vaccineSite,
                _vaccineSlots
        );

        setSlot(slotId, slot);

    }

    private String getVaccineImage(final String vaccineName) {

        switch (vaccineName) {

            case "Pfizer-BioNTech":
                return "https://firebasestorage.googleapis.com/v0/b/immuni-nation-2k21.appspot.com/o/Res_20220702%2Fimg_pfizer.jpg?alt=media&token=dc0c2f36-e1c5-4cfc-9d21-0cd84ade6440";

            case "Moderna":
                return "https://firebasestorage.googleapis.com/v0/b/immuni-nation-2k21.appspot.com/o/Res_20220702%2Fimg_moderna.jpg?alt=media&token=671d6eb9-2b23-4533-a76d-e016e7a554ca";

            case "Johnson and Johnson":
                return "https://firebasestorage.googleapis.com/v0/b/immuni-nation-2k21.appspot.com/o/Res_20220702%2Fimg_johnson_johnson.jpg?alt=media&token=1c2d7a98-b417-4fff-9986-a8e42a259e21";

            case "SinoVac":
                return "https://firebasestorage.googleapis.com/v0/b/immuni-nation-2k21.appspot.com/o/Res_20220702%2Fimg_sinovac.jpg?alt=media&token=a82af819-af63-4357-aeb8-a09e9030d2a7";

            case "AstraZeneca":
                return "https://firebasestorage.googleapis.com/v0/b/immuni-nation-2k21.appspot.com/o/Res_20220702%2Fimg_astra_zeneca.jpg?alt=media&token=6e5983d6-8e8e-4329-baf4-91068defe638";

        }
        return "";

    }

    private void setSlot(final String slotId, final SlotModel slot) {

        documentRef = firebaseFirestore
                .collection("Slots")
                .document(slotId);

        documentRef.addSnapshotListener((value, error) ->
        {

            if (value != null && !value.exists())

                documentRef
                        .set(slot)
                        .addOnSuccessListener(unused ->
                        {

                            pd.dismiss();
                            onBackPressed();

                        });

        });

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

}
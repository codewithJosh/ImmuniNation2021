package com.codewithjosh.ImmuniNation2k21;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.codewithjosh.ImmuniNation2k21.models.RequestModel;
import com.codewithjosh.ImmuniNation2k21.models.SlotModel;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ViewScheduleActivity extends AppCompatActivity {

    ImageView ivQRCode;
    ImageView ivVaccineImage;
    TextView tvRequestId;
    TextView tvVaccineDoseDate;
    TextView tvVaccineName;
    TextView tvVaccineSite;
    int requestStatus;
    String userId;
    FirebaseFirestore firebaseFirestore;
    SharedPreferences sharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_schedule);

        initViews();
        initInstances();
        initSharedPref();
        load();
        loadUserSlot();

    }

    private void initViews() {

        getWindow().setNavigationBarColor(getColor(R.color.color_blue_green));

        ivQRCode = findViewById(R.id.iv_q_r_code);
        ivVaccineImage = findViewById(R.id.iv_vaccine_image);
        tvRequestId = findViewById(R.id.tv_request_id);
        tvVaccineDoseDate = findViewById(R.id.tv_vaccine_dose_date);
        tvVaccineName = findViewById(R.id.tv_vaccine_name);
        tvVaccineSite = findViewById(R.id.tv_vaccine_site);

    }

    private void initInstances() {

        firebaseFirestore = FirebaseFirestore.getInstance();

    }

    private void initSharedPref() {

        sharedPref = getSharedPreferences("user", MODE_PRIVATE);

    }

    private void load() {

        requestStatus = sharedPref.getInt("request_status", MODE_PRIVATE);
        userId = sharedPref.getString("user_id", String.valueOf(MODE_PRIVATE));

    }

    private void loadUserSlot() {

        firebaseFirestore
                .collection("Requests")
                .whereEqualTo("user_id", userId)
                .get()
                .addOnSuccessListener(documentSnapshot ->
                {

                    if (documentSnapshot != null && !documentSnapshot.isEmpty()) {

                        for (QueryDocumentSnapshot snapshot : documentSnapshot) {

                            final RequestModel request = snapshot.toObject(RequestModel.class);

                            final String requestId = request.getRequest_id();
                            final String slotId = request.getSlot_id();

                            firebaseFirestore
                                    .collection("Slots")
                                    .document(slotId)
                                    .get()
                                    .addOnSuccessListener(_documentSnapshot ->
                                    {

                                        if (_documentSnapshot != null && _documentSnapshot.exists()) {

                                            final SlotModel slot = _documentSnapshot.toObject(SlotModel.class);

                                            if (slot != null) {

                                                final String ref = "Reference Code: " + requestId;
                                                final Date vaccineDoseDate = requestStatus == 1
                                                        ? slot.getVaccine_first_dose_date()
                                                        : slot.getVaccine_second_dose_date();
                                                final String vaccineImage = slot.getVaccine_image();
                                                final String vaccineName = slot.getVaccine_name();
                                                final String vaccineSite = slot.getVaccine_site();

                                                final String date = "MMM d, yyyy";

                                                final DateFormat dateFormat = new SimpleDateFormat(date);

                                                Glide.with(this).load(vaccineImage).into(ivVaccineImage);
                                                tvRequestId.setText(ref);
                                                tvVaccineDoseDate.setText(dateFormat.format(vaccineDoseDate));
                                                tvVaccineName.setText(vaccineName);
                                                tvVaccineSite.setText(vaccineSite);

                                                final String QRCode = vaccineName + " vaccination schedule approved on " + vaccineDoseDate;

                                                final MultiFormatWriter multiFormatWriter = new MultiFormatWriter();

                                                try {

                                                    final BitMatrix bitMatrix = multiFormatWriter.encode(QRCode, BarcodeFormat.QR_CODE, 350, 350);
                                                    final BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
                                                    final Bitmap bitmap = barcodeEncoder.createBitmap(bitMatrix);
                                                    ivQRCode.setImageBitmap(bitmap);

                                                } catch (WriterException e) {

                                                    e.printStackTrace();

                                                }

                                            }

                                        }

                                    });

                        }

                    }

                });

    }

}
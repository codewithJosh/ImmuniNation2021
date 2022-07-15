package com.codewithjosh.ImmuniNation2k21.adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.codewithjosh.ImmuniNation2k21.R;
import com.codewithjosh.ImmuniNation2k21.models.SlotModel;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class SlotAdapter extends RecyclerView.Adapter<SlotAdapter.ViewHolder>
{

    public Context context;
    public List<SlotModel> slots;
    public int itemSlot;
    String userId;
    DateFormat dateFormat;
    FirebaseFirestore firebaseFirestore;
    SharedPreferences sharedPref;
    SharedPreferences.Editor editor;

    public SlotAdapter(final Context context, final List<SlotModel> slots, final int itemSlot)
    {

        this.context = context;
        this.slots = slots;
        this.itemSlot = itemSlot;

    }

    @NonNull
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {

        View view = LayoutInflater.from(context).inflate(itemSlot, parent, false);
        return new ViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position)
    {

        final SlotModel slot = slots.get(position);

//        initViews
        final ImageView ivVaccineImage = holder.ivVaccineImage;
        final TextView tvVaccineName = holder.tvVaccineName;
        final TextView tvVaccineSite = holder.tvVaccineSite;
        final TextView tvVaccineSlots = holder.tvVaccineSlots;
        final TextView tvVaccineFirstDoseDate = holder.tvVaccineFirstDoseDate;

//        load
        final String slotId = slot.getSlot_id();
        final String vaccineImage = slot.getVaccine_image();
        final String vaccineName = slot.getVaccine_name();
        final String vaccineSite = slot.getVaccine_site();
        final int vaccineSlots = slot.getVaccine_slots();
        final Date vaccineFirstDoseDate = slot.getVaccine_first_dose_date();
        final String vaccineFirstDoseDateFormat = "dd/MM/yyyy";

        initInstances();
        initSharedPref();
        load();

        tvVaccineName.setText(vaccineName);
        tvVaccineSite.setText(vaccineSite);

        dateFormat = new SimpleDateFormat(vaccineFirstDoseDateFormat);
        if (vaccineFirstDoseDate != null) tvVaccineFirstDoseDate.setText(dateFormat.format(vaccineFirstDoseDate));

        firebaseFirestore
                .collection("Requests")
                .whereEqualTo("slot_id", slotId)
                .addSnapshotListener((value, error) ->
                {

                    if (value != null)
                    {

                        final int currentSlots = value.size();
                        final int availableSlots = vaccineSlots - currentSlots;
                        final String vaccineSlotsFormat = availableSlots + "/" + vaccineSlots;

                        tvVaccineName.setTextColor(context.getColor(
                                availableSlots == 0
                                        ? R.color.color_tart_orange
                                        : R.color.color_blue_green));

                        tvVaccineSlots.setTextColor(context.getColor(
                                availableSlots == 0
                                        ? R.color.color_tart_orange
                                        : R.color.color_dark_silver));

                        final String vaccineImageUnavailable = "https://firebasestorage.googleapis.com/v0/b/immuni-nation-2k21.appspot.com/o/Res_20220702%2Fimg_unavailable.jpg?alt=media&token=f05f673b-74b9-40e2-95ca-c3b37448fde6";
                        Glide.with(context).load(
                                availableSlots == 0
                                        ? vaccineImageUnavailable
                                        : vaccineImage
                        ).into(ivVaccineImage);

                        tvVaccineSlots.setText(vaccineSlotsFormat);

                    }

                });

    }

    private void initInstances()
    {

        firebaseFirestore = FirebaseFirestore.getInstance();

    }

    private void initSharedPref()
    {

        sharedPref = context.getSharedPreferences("user", Context.MODE_PRIVATE);
        editor = sharedPref.edit();

    }

    private void load()
    {

        userId = sharedPref.getString("user_id", String.valueOf(Context.MODE_PRIVATE));

    }

    @Override
    public int getItemCount()
    {

        return slots.size();

    }

    public static class ViewHolder extends RecyclerView.ViewHolder
    {

        public ImageView ivVaccineImage;
        public TextView tvVaccineName;
        public TextView tvVaccineSite;
        public TextView tvVaccineSlots;
        public TextView tvVaccineFirstDoseDate;

        public ViewHolder(@NonNull View itemView)
        {

            super(itemView);

            ivVaccineImage = itemView.findViewById(R.id.iv_vaccine_image);
            tvVaccineName = itemView.findViewById(R.id.tv_vaccine_name);
            tvVaccineSite = itemView.findViewById(R.id.tv_vaccine_site);
            tvVaccineSlots = itemView.findViewById(R.id.tv_vaccine_slots);
            tvVaccineFirstDoseDate = itemView.findViewById(R.id.tv_vaccine_first_dose_date);

        }

    }

}

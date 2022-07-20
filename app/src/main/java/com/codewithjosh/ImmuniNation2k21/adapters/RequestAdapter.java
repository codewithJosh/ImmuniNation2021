package com.codewithjosh.ImmuniNation2k21.adapters;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.codewithjosh.ImmuniNation2k21.R;
import com.codewithjosh.ImmuniNation2k21.RequestApprovalActivity;
import com.codewithjosh.ImmuniNation2k21.models.RequestModel;
import com.codewithjosh.ImmuniNation2k21.models.SlotModel;
import com.codewithjosh.ImmuniNation2k21.models.UserModel;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class RequestAdapter extends RecyclerView.Adapter<RequestAdapter.ViewHolder> {

    public Context context;
    public List<RequestModel> requests;
    public int requestStatus;
    DateFormat dateFormat;
    FirebaseFirestore firebaseFirestore;
    SharedPreferences.Editor editor;

    public RequestAdapter(final Context context, final List<RequestModel> requests, final int requestStatus) {

        this.context = context;
        this.requests = requests;
        this.requestStatus = requestStatus;

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.item_request, parent, false);
        return new ViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        final RequestModel request = requests.get(position);

//        initViews
        final CircleImageView civUserImage = holder.civUserImage;
        final TextView tvUserName = holder.tvUserName;
        final TextView tvRequestStatus = holder.tvRequestStatus;

//        load
        final String userId = request.getUser_id();
        final String userName = request.getUser_name();
        final String slotId = request.getSlot_id();
        final String requestId = request.getRequest_id();

        initInstances();
        initSharedPref();

        tvUserName.setText(userName);

        loadUserImage(userId, civUserImage);

        loadRequestStatus(slotId, tvRequestStatus);

        holder.itemView.setOnClickListener(v ->
        {

            if (requestStatus == 0) onRequestApproval(requestId, slotId);

        });

    }

    private void initInstances() {

        firebaseFirestore = FirebaseFirestore.getInstance();

    }

    private void initSharedPref() {

        editor = context.getSharedPreferences("user", Context.MODE_PRIVATE).edit();

    }

    private void loadUserImage(final String userId, final CircleImageView civUserImage) {

        firebaseFirestore
                .collection("Users")
                .document(userId)
                .addSnapshotListener((value, error) ->
                {

                    if (value != null && value.exists()) {

                        final UserModel user = value.toObject(UserModel.class);

                        if (user != null) {

                            final String userImage = user.getUser_image();

                            Glide.with(context).load(userImage).into(civUserImage);

                        }

                    }

                });

    }

    private void loadRequestStatus(final String slotId, final TextView tvRequestStatus) {

        firebaseFirestore
                .collection("Slots")
                .document(slotId)
                .addSnapshotListener((value, error) ->
                {

                    if (value != null && value.exists()) {

                        final SlotModel slot = value.toObject(SlotModel.class);

                        if (slot != null) {

                            final String vaccineName = slot.getVaccine_name();
                            final Date vaccineFirstDoseDate = slot.getVaccine_first_dose_date();
                            final Date vaccineSecondDoseDate = slot.getVaccine_second_dose_date();
                            final String vaccineSite = slot.getVaccine_site();

                            tvRequestStatus.setText(getRequestStatus(vaccineName, vaccineSite, vaccineFirstDoseDate, vaccineSecondDoseDate));

                        }

                    }

                });

    }

    private String getRequestStatus(final String vaccineName, final String vaccineSite, final Date vaccineFirstDoseDate, final Date vaccineSecondDoseDate) {

        final String vaccineDoseDateFormat = "MMM d, yyyy";

        dateFormat = new SimpleDateFormat(vaccineDoseDateFormat);

        final String vaccineFirstDoseDateFormat = dateFormat.format(vaccineFirstDoseDate);
        final String vaccineSecondDoseDateFormat = dateFormat.format(vaccineSecondDoseDate);

        switch (requestStatus) {

            case 1:
                return "To be vaccinated on " + vaccineFirstDoseDateFormat + " at " + vaccineSite;

            case 2:
                return "To be vaccinated on " + vaccineSecondDoseDateFormat + " at " + vaccineSite;

        }
        return "Request a slot for " + vaccineName + " on " + vaccineFirstDoseDateFormat + " at " + vaccineSite;

    }

    private void onRequestApproval(final String requestId, final String slotId) {

        editor.putString("request_id", requestId);
        editor.putString("slot_id", slotId);
        editor.apply();
        context.startActivity(new Intent(context, RequestApprovalActivity.class));

    }

    @Override
    public int getItemCount() {

        return requests.size();

    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public CircleImageView civUserImage;
        public TextView tvUserName;
        public TextView tvRequestStatus;

        public ViewHolder(@NonNull View itemView) {

            super(itemView);

            civUserImage = itemView.findViewById(R.id.civ_user_image);
            tvUserName = itemView.findViewById(R.id.tv_user_name);
            tvRequestStatus = itemView.findViewById(R.id.tv_request_status);

        }

    }

}

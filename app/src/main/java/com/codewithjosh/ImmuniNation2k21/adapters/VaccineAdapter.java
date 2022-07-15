package com.codewithjosh.ImmuniNation2k21.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.codewithjosh.ImmuniNation2k21.R;
import com.codewithjosh.ImmuniNation2k21.models.VaccineModel;

import java.util.List;

public class VaccineAdapter extends RecyclerView.Adapter<VaccineAdapter.InfoViewHolder>
{

    public Context context;
    public List<VaccineModel> vaccines;

    public VaccineAdapter(final Context context, final List<VaccineModel> vaccines)
    {

        this.context = context;
        this.vaccines = vaccines;

    }

    @NonNull
    @Override
    public InfoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {

        View view = LayoutInflater.from(context).inflate(R.layout.item_vaccine, parent, false);
        return new InfoViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull InfoViewHolder holder, int position)
    {

        final VaccineModel vaccine = vaccines.get(position);

//        initViews
        final ImageView ivVaccineImage = holder.ivVaccineImage;
        final TextView tvVaccineName = holder.tvVaccineName;

//        load
        final int vaccineImage = vaccine.getVaccine_image();
        final String vaccineName = vaccine.getVaccine_name();

        ivVaccineImage.setImageResource(vaccineImage);
        tvVaccineName.setText(vaccineName);

    }

    @Override
    public int getItemCount()
    {

        return vaccines.size();

    }

    public static class InfoViewHolder extends RecyclerView.ViewHolder
    {

        public ImageView ivVaccineImage;
        public TextView tvVaccineName;

        public InfoViewHolder(@NonNull View itemView)
        {

            super(itemView);

            ivVaccineImage = itemView.findViewById(R.id.iv_vaccine_image);
            tvVaccineName = itemView.findViewById(R.id.tv_vaccine_name);

        }

    }

}

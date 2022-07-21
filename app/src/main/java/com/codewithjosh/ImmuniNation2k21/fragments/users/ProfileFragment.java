package com.codewithjosh.ImmuniNation2k21.fragments.users;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.codewithjosh.ImmuniNation2k21.R;

public class ProfileFragment extends Fragment
{

    Activity activity;
    Context context;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        initViews();

        return view;

    }

    private void initViews() {

        if (getContext() != null) context = getContext();
        if (getActivity() != null) activity = getActivity();
        activity.getWindow().setStatusBarColor(context.getColor(R.color.color_dark_cyan));

    }

}
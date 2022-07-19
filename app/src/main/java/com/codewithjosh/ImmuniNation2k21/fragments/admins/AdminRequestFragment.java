package com.codewithjosh.ImmuniNation2k21.fragments.admins;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager2.widget.ViewPager2;

import com.codewithjosh.ImmuniNation2k21.R;
import com.codewithjosh.ImmuniNation2k21.adapters.TabAdapter;
import com.google.android.material.tabs.TabLayout;

public class AdminRequestFragment extends Fragment {

    TabLayout tabLayout;
    ViewPager2 viewPager2;
    TabAdapter tabAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_admin_request, container, false);

        initViews(view);
        build();

        return view;

    }

    private void initViews(final View view) {

        tabLayout = view.findViewById(R.id.tab_layout);
        viewPager2 = view.findViewById(R.id.view_pager_2);

        if (getActivity() != null) {

            final FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            tabAdapter = new TabAdapter(fragmentManager, getLifecycle());
            viewPager2.setAdapter(tabAdapter);

        }

        final String scheduleRequest = "Schedule\nRequest";
        final String firstDose = "First\nDose";
        final String secondDose = "Second\nDose";

        tabLayout.addTab(tabLayout.newTab().setText(scheduleRequest));
        tabLayout.addTab(tabLayout.newTab().setText(firstDose));
        tabLayout.addTab(tabLayout.newTab().setText(secondDose));

    }

    private void build() {

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {

            @Override
            public void onTabSelected(TabLayout.Tab tab) {

                viewPager2.setCurrentItem(tab.getPosition());

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }

        });

        viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {

            @Override
            public void onPageSelected(int position) {

                tabLayout.selectTab(tabLayout.getTabAt(position));

            }

        });

    }

}
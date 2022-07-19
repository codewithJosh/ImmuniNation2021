package com.codewithjosh.ImmuniNation2k21.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.codewithjosh.ImmuniNation2k21.fragments.admins.tabs.FirstDoseFragment;
import com.codewithjosh.ImmuniNation2k21.fragments.admins.tabs.ScheduleRequestFragment;
import com.codewithjosh.ImmuniNation2k21.fragments.admins.tabs.SecondDoseFragment;

public class TabAdapter extends FragmentStateAdapter
{

    public TabAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle)
    {

        super(fragmentManager, lifecycle);

    }

    @NonNull
    @Override
    public Fragment createFragment(int position)
    {

        switch (position)
        {

            case 1:
                return new FirstDoseFragment();

            case 2:
                return new SecondDoseFragment();

        }
        return new ScheduleRequestFragment();

    }

    @Override
    public int getItemCount()
    {

        return 3;

    }

}

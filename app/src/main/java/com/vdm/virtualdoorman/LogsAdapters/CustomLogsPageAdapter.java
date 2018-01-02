package com.vdm.virtualdoorman.LogsAdapters;


import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.vdm.virtualdoorman.LogsFragments.CallLogs;
import com.vdm.virtualdoorman.LogsFragments.GuestDeliveryLogs;

public class CustomLogsPageAdapter extends FragmentPagerAdapter{

    private static final String TAG = CustomLogsPageAdapter.class.getSimpleName();

    private static final int FRAGMENT_COUNT = 2;

    public CustomLogsPageAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                return new GuestDeliveryLogs();
            case 1:
                return new CallLogs();

        }
        return null;
    }

    @Override
    public int getCount() {
        return FRAGMENT_COUNT;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position){
            case 0:
                return "Activity";
            case 1:
                return "Calls";

        }
        return null;
    }
}

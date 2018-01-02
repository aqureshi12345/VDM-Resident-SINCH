package com.vdm.virtualdoorman.ArrivalsAdapters;


import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.vdm.virtualdoorman.ArrivalsFragments.GuestList;
import com.vdm.virtualdoorman.ArrivalsFragments.deliveryList;

public class CustomFragmentPageAdapter extends FragmentPagerAdapter{

    private static final String TAG = CustomFragmentPageAdapter.class.getSimpleName();

    private static final int FRAGMENT_COUNT = 2;

    public CustomFragmentPageAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                return new GuestList();
            case 1:
                return new deliveryList();

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
                return "Guests";
            case 1:
                return "Deliveres";

        }
        return null;
    }
}

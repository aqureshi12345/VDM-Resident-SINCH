package com.vdm.virtualdoorman.ArrivalsFragments;


import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.vdm.virtualdoorman.R;
import com.vdm.virtualdoorman.ArrivalsAdapters.CustomFragmentPageAdapter;


public class GuestDeliveryListContainerFragment extends Fragment {

    private static final String TAG = GuestDeliveryListContainerFragment.class.getSimpleName();

    private TabLayout tabLayout;
    private ViewPager viewPager;


    public GuestDeliveryListContainerFragment() {
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_arrivals_container, container, false);

        tabLayout = (TabLayout)view.findViewById(R.id.tabs);
        viewPager = (ViewPager)view.findViewById(R.id.view_pager);

        viewPager.setAdapter(new CustomFragmentPageAdapter(getChildFragmentManager()));
        tabLayout.setupWithViewPager(viewPager);

        return view;
    }
}

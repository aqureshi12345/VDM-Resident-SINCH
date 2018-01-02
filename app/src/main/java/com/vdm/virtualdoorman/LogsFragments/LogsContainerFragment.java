package com.vdm.virtualdoorman.LogsFragments;


import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.vdm.virtualdoorman.LogsAdapters.CustomLogsPageAdapter;
import com.vdm.virtualdoorman.R;
import com.vdm.virtualdoorman.ArrivalsAdapters.CustomFragmentPageAdapter;


public class LogsContainerFragment extends Fragment {

    private static final String TAG = LogsContainerFragment.class.getSimpleName();

    private TabLayout tabLayout;
    private ViewPager viewPager;


    public LogsContainerFragment() {
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_logs_container, container, false);

        tabLayout = (TabLayout)view.findViewById(R.id.tabs);
        viewPager = (ViewPager)view.findViewById(R.id.view_pager);

        viewPager.setAdapter(new CustomLogsPageAdapter(getChildFragmentManager()));
        tabLayout.setupWithViewPager(viewPager);

        return view;
    }
}

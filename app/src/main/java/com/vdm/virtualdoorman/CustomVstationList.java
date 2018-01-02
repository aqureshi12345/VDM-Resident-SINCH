package com.vdm.virtualdoorman;



import com.vdm.virtualdoorman.ArrivalsFragments.GuestList;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class CustomVstationList extends ArrayAdapter<String>{
    private final Activity context;
    private final String[] VstationList;
    private final Integer[] imageId;
    private final String[] VstationID;
    //static String[] data = null;
    GuestList guest;
    TextView txtTitle,id;
    ImageView imageView;


   public CustomVstationList(Activity context, String[] VstationList, Integer[] imageId,  String[] VstationID) {
         super(context, R.layout.list_single, VstationList);
        this.context = context;
        this.VstationList = VstationList;
        this.imageId = imageId;
        this.VstationID = VstationID;

    }


    @SuppressLint("ViewHolder")
    @Override
    public View getView(final int position, View view, final ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        final View rowView = inflater.inflate(R.layout.list_single, parent, false);
        txtTitle = (TextView) rowView.findViewById(R.id.txt);
        id = (TextView) rowView.findViewById(R.id.ID);
        imageView = (ImageView) rowView.findViewById(R.id.img);
        txtTitle.setText(VstationList[position]);
        imageView.setImageResource(imageId[position]);
        id.setText(VstationID[position]);

        return rowView;
    }




}

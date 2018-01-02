package com.vdm.virtualdoorman.ArrivalsAdapters;



import com.vdm.virtualdoorman.R;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class customDeliveryList extends ArrayAdapter<String>{
    private final Activity context;
    private final String[] guestlist;
    private final Integer[] imageId;
    private final String[] guestId;
    TextView txtTitle,guestid;

    public customDeliveryList(Activity context, String[] guestlist, Integer[] imageId,  String[] guestId) {
        super(context, R.layout.list_single_delivery, guestlist);
        this.context = context;
        this.guestlist = guestlist;
        this.imageId = imageId;
        this.guestId = guestId;
    }

    @SuppressLint("ViewHolder")
    @Override
    public View getView(final int position, View view, final ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        final View View = inflater.inflate(R.layout.list_single_delivery, parent, false);
        txtTitle = (TextView) View.findViewById(R.id.txt);

        txtTitle.setText(guestlist[position]);
        //guestid.setText(guestId[position]);


        return View;
    }




}

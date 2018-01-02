package com.vdm.virtualdoorman.ArrivalsAdapters;



import com.vdm.virtualdoorman.R;
import com.vdm.virtualdoorman.ArrivalsFragments.GuestList;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class customGuestList extends ArrayAdapter<String>{
	private final Activity context;
	private final String[] guestlist;
	private final Integer[] imageId;
	private final String[] guestId;
	//static String[] data = null;
	GuestList guest;
	TextView txtTitle,guestid;
	ImageView imageView;

	
	
	public customGuestList(Activity context, String[] guestlist, Integer[] imageId,  String[] guestId) {
		super(context, R.layout.list_single_guest, guestlist);
		this.context = context;
		this.guestlist = guestlist;
		this.imageId = imageId;
		this.guestId = guestId;
		
	}

	
	@SuppressLint("ViewHolder")
	@Override
	public View getView(final int position, View view, final ViewGroup parent) {
		LayoutInflater inflater = context.getLayoutInflater();
		 final View View = inflater.inflate(R.layout.list_single_guest, parent, false);
		 txtTitle = (TextView) View.findViewById(R.id.txt);
//		 guestid = (TextView) View.findViewById(R.id.guestid);
		txtTitle.setText(guestlist[position]);
		//imageView.setImageResource(imageId[position]);
//		guestid.setText(guestId[position]);
		

		return View;
	}
	
	

	
}

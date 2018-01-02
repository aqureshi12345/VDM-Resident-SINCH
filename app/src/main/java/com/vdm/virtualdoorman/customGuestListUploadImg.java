package com.vdm.virtualdoorman;



import com.vdm.virtualdoorman.R;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class customGuestListUploadImg extends ArrayAdapter<String>{
	private final Activity context;
	private final String[] guestlist;
	private final Integer[] imageId;
	private final String[] guestId;
	
	public customGuestListUploadImg(Activity context, String[] guestlist, Integer[] imageId,  String[] guestId) {
		super(context, R.layout.list_photo, guestlist);
		this.context = context;
		this.guestlist = guestlist;
		this.imageId = imageId;
		this.guestId = guestId;
	}

	@SuppressLint({ "ViewHolder", "InflateParams" })
	@Override
	public View getView(final int position, View view, ViewGroup parent) {
		LayoutInflater inflater = context.getLayoutInflater();
		View rowView = inflater.inflate(R.layout.list_photo, null, true);
		TextView txtTitle = (TextView) rowView.findViewById(R.id.txt);
		TextView guestid = (TextView) rowView.findViewById(R.id.guestid);
		ImageView imageView = (ImageView) rowView.findViewById(R.id.img);
		txtTitle.setText(guestlist[position]);
		imageView.setImageResource(imageId[position]);
		guestid.setText(guestId[position]);
		
		return rowView;
		
		
	}
}

package com.vdm.virtualdoorman.LogsAdapters;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.vdm.virtualdoorman.R;

public class CustomCallLogList extends ArrayAdapter<String> {
	private final Activity context;
	private String[] dateTime=null;
	private String[] callFrom=null;
	private String[] receivedBy=null;
	private String[] id=null;
	private String[] missedCall=null;

	public CustomCallLogList(Activity context, String[] dateTime, String[] callFrom, String[] receivedBy, String[] id, String[] missedCall) {
		super(context, R.layout.list_single_log_call,dateTime);
		this.context = context;
		this.dateTime = dateTime;
		this.callFrom = callFrom;
		this.receivedBy = receivedBy;
		this.missedCall = missedCall;
		this.id = id;
	}

	@SuppressLint({ "ViewHolder", "InflateParams" })
	@Override
	public View getView(int position, View view, ViewGroup parent) {
		LayoutInflater inflater = context.getLayoutInflater();
		View rowView = inflater.inflate(R.layout.list_single_log_call, null, true);
		TextView date_Time = (TextView) rowView.findViewById(R.id.date_time);
		TextView call_from = (TextView) rowView.findViewById(R.id.Caller_id);
		TextView reciever = (TextView) rowView.findViewById(R.id.operator_name);
		TextView logid = (TextView) rowView.findViewById(R.id.logid);
		
		if(missedCall[position].equalsIgnoreCase("0")){
			date_Time.setTextColor(Color.RED);
			call_from.setTextColor(Color.RED);
			reciever.setTextColor(Color.RED);
		}

		date_Time.setText(dateTime[position]);
		call_from.setText(callFrom[position]);
		reciever.setText(receivedBy[position]);
		logid.setText(id[position]);
		return rowView;
	}
}

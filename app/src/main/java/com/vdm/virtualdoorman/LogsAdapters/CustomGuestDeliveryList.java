package com.vdm.virtualdoorman.LogsAdapters;

import com.vdm.virtualdoorman.R;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class CustomGuestDeliveryList extends ArrayAdapter<String> {
	private final Activity context;
	private String[] dateTime=null;
	private String[] reportType=null;
	private String[] operatorName=null;
	private String[] id=null;
	

	public CustomGuestDeliveryList(Activity context, String[] dateTime, String[] reportType, String[] operatorName, String[] id) {
		super(context, R.layout.list_single_log,dateTime);
		this.context = context;
		this.dateTime = dateTime;
		this.reportType = reportType;
		this.operatorName = operatorName;
		this.id = id;
	}

	@SuppressLint({ "ViewHolder", "InflateParams" })
	@Override
	public View getView(int position, View view, ViewGroup parent) {
		LayoutInflater inflater = context.getLayoutInflater();
		View rowView = inflater.inflate(R.layout.list_single_log, null, true);
		TextView date_Time = (TextView) rowView.findViewById(R.id.date_time);
		TextView report_Time = (TextView) rowView.findViewById(R.id.report_time);
		TextView operator_Name = (TextView) rowView.findViewById(R.id.operator_name);
		TextView logid = (TextView) rowView.findViewById(R.id.logid);
		
		
		date_Time.setText(dateTime[position]);
		report_Time.setText(reportType[position]);
		operator_Name.setText(operatorName[position]);
		logid.setText(id[position]);
		return rowView;
	}
}

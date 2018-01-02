package com.vdm.virtualdoorman;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class CustomListAdapter extends BaseAdapter {
    private ArrayList<ListItem> listData;
    private LayoutInflater layoutInflater;
    public Context adapter_context;
    public CustomListAdapter(Context context, ArrayList<ListItem> listData) {
        this.listData = listData;
        layoutInflater = LayoutInflater.from(context);
        adapter_context=context;
    }

    @Override
    public int getCount() {
        return listData.size();
    }

    @Override
    public Object getItem(int position) {
        return listData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.list_row_layout, null);
            holder = new ViewHolder();

            holder.reportedDateView = (TextView) convertView.findViewById(R.id.date);
            holder.imageView = (ImageView) convertView.findViewById(R.id.thumbImage);
            convertView.setTag(holder);

        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        ListItem newsItem = listData.get(position);
        holder.reportedDateView.setText(newsItem.getDate());

        if (holder.imageView != null) {
            new ImageDownloaderTask(holder.imageView,adapter_context).execute(newsItem.getUrl());
        }

        return convertView;
    }

    static class ViewHolder {
        TextView reportedDateView;
        ImageView imageView;
    }
}

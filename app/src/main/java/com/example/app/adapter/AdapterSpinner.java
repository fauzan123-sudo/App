package com.example.app.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.app.R;
import com.example.app.model.SpinnerModel;

import java.util.List;

public class AdapterSpinner extends BaseAdapter {
    private final Activity activity;
    private LayoutInflater inflater;
    private final List<SpinnerModel> item;

    public AdapterSpinner(Activity activity, List<SpinnerModel> item) {
        this.activity = activity;
        this.item = item;
    }

    @Override
    public int getCount() {
        return item.size();
    }

    @Override
    public Object getItem(int location) {
        return item.get(location);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (inflater == null)
            inflater = (LayoutInflater) activity
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (convertView == null)
            convertView = inflater.inflate(R.layout.spinner_izin, null);

        TextView pendidikan = convertView.findViewById(R.id.jenis_izin);

        SpinnerModel data;
        data = item.get(position);

        pendidikan.setText(data.getNama());

        return convertView;
    }
}


package com.example.app.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.app.R;
import com.example.app.model.Model_Spinner_Izin;

import java.util.List;

class AdapterSpinerIzin extends BaseAdapter {
    private Activity activity;
    private LayoutInflater inflater;
    private List<Model_Spinner_Izin> item;

    public AdapterSpinerIzin(Activity activity,  List<Model_Spinner_Izin> item) {
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
            convertView = inflater.inflate(R.layout.item_spinner_izin, null);

        TextView pendidikan =  convertView.findViewById(R.id.text_view_name);

        Model_Spinner_Izin data;
        data = item.get(position);

        pendidikan.setText(data.getNama());

        return convertView;
    }
}

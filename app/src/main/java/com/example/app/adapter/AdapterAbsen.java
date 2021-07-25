package com.example.app.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.app.R;
import com.example.app.model.ModelAbsen;

import java.util.ArrayList;

public class AdapterAbsen extends RecyclerView.Adapter<AdapterAbsen.AdapterBaru> {
    Context context;
    ArrayList<ModelAbsen> modelAbsens;

    public AdapterAbsen(Context context, ArrayList<ModelAbsen> modelAbsens) {
        this.context = context;
        this.modelAbsens = modelAbsens;
    }

    @Override
    public AdapterBaru onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_absensi,parent,false);
        return new AdapterBaru(view);
    }

    @Override
    public void onBindViewHolder(AdapterBaru holder, int position) {

        ModelAbsen modelAbsen = modelAbsens.get(position);
        String jam = modelAbsen.getmJam();
        String tanggal = modelAbsen.getmTanggal();


        holder.Tanggal.setText(tanggal);
        holder.Jam.setText(jam);
    }

    @Override
    public int getItemCount() {
        return modelAbsens.size();
    }

    public class AdapterBaru extends RecyclerView.ViewHolder {
        public TextView Jam,Tanggal;
        public AdapterBaru(View itemView) {
            super(itemView);
            Jam = itemView.findViewById(R.id.jam);
            Tanggal = itemView.findViewById(R.id.tanggal);
        }
    }
}

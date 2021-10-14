package com.example.app.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.app.R;
import com.example.app.helper.Constans;
import com.example.app.model.Model_Berita_Acara;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class AdapterBeritaAcara extends RecyclerView.Adapter<AdapterBeritaAcara.ExampleViewHolder> {
    private final Context mContext;
    public String BERITA_ACARA = Constans.urlImageBerita;
    private final ArrayList<Model_Berita_Acara> listBerita;

    public AdapterBeritaAcara(Context context, ArrayList<Model_Berita_Acara> exampleList) {
        mContext    = context;
        listBerita  = exampleList;
    }
    @NotNull
    @Override
    public ExampleViewHolder onCreateViewHolder(@NotNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.item_berita_acara, parent, false);
        return new ExampleViewHolder(v);
    }
    @Override
    public void onBindViewHolder(ExampleViewHolder holder, int position) {
        Model_Berita_Acara currentItem = listBerita.get(position);
        String gambar       = currentItem.getGambar();
        String judul        = currentItem.getJudul();
        String keterangan   = currentItem.getKeterangan();

        Picasso.with(mContext).load(BERITA_ACARA+gambar).into(holder.Gambar);
        holder.Judul.setText(judul);
        holder.Keterangan.setText(keterangan);
    }

    @Override
    public int getItemCount() {
        return listBerita.size();
    }
    public static class ExampleViewHolder extends RecyclerView.ViewHolder {
        public ImageView Gambar;
        public TextView Judul;
        public TextView Keterangan;

        public ExampleViewHolder(View itemView) {
            super(itemView);
            Gambar      = itemView.findViewById(R.id.gambar);
            Judul       = itemView.findViewById(R.id.judul);
            Keterangan  = itemView.findViewById(R.id.keterangan);
        }
    }
}

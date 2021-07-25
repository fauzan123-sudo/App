package com.example.app.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.app.R;
import com.example.app.model.Model_Gaji;

import java.util.ArrayList;

public class AdapterGaji extends RecyclerView.Adapter<AdapterGaji.ExampleViewHolder> {
    private Context mContext;
    private ArrayList<Model_Gaji> listGaji;

    public AdapterGaji(Context context, ArrayList<Model_Gaji> exampleList) {
        mContext = context;
        listGaji = exampleList;
    }
    @Override
    public ExampleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.item_gaji, parent, false);
        return new ExampleViewHolder(v);
    }
    @Override
    public void onBindViewHolder(ExampleViewHolder holder, int position) {
        Model_Gaji currentItem = listGaji.get(position);
        String creatorName = currentItem.getGaji_Pokok();
//        String likeCount   = currentItem.getLikeCount();
//        String potongan    = currentItem.getPotongan();
        holder.mTextViewCreator.setText("gaji pokok: " +creatorName);
//        holder.mTextViewLikes.setText("asuransi: " + likeCount);
//        holder.mPotongan.setText("potongan: " + potongan);
    }





    @Override
    public int getItemCount() {
        return listGaji.size();
    }
    public class ExampleViewHolder extends RecyclerView.ViewHolder {
        public TextView mTextViewCreator;
        public TextView mTextViewLikes;
        public TextView mPotongan;

        public void addItem(Model_Gaji item){
//        ArrayList<String> exampleItems = new ArrayList<>();
            listGaji.add(0,item);
            notifyDataSetChanged();
        }

        public ExampleViewHolder(View itemView) {
            super(itemView);
            mTextViewCreator    = itemView.findViewById(R.id.gaji_pokok);
//            mTextViewLikes      = itemView.findViewById(R.id.text_view_likes);
//            mPotongan           = itemView.findViewById(R.id.text_view_potongan);
        }
    }
}

package com.example.app.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.fragment.app.Fragment;

import com.example.app.R;

public class Gagal_Scan extends Fragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_gagal__scan, container, false);
        Button Scanlagi = view.findViewById(R.id.scanLagi);
        Scanlagi.setOnClickListener(view1 -> {
            Scan scan = new Scan();
            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, scan)
                    .commit();
        });
        return  view;
    }
}
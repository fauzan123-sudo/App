package com.example.app.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.fragment.app.Fragment;

import com.example.app.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Konfirmasi_Scan#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Konfirmasi_Scan extends Fragment {

    Button Konfirmasi;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public Konfirmasi_Scan() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Konfirmasi_Scan.
     */
    // TODO: Rename and change types and number of parameters
    public static Konfirmasi_Scan newInstance(String param1, String param2) {
        Konfirmasi_Scan fragment = new Konfirmasi_Scan();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_konfirmasi__scan, container, false);

            Konfirmasi     = view.findViewById(R.id.konfirmasi);
            Konfirmasi.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Bundle bundle = new Bundle();
                    Scan fragment2 = new Scan();
                    fragment2.setArguments(bundle);

                    getFragmentManager()
                            .beginTransaction()
                            .replace(R.id.fragment_container, fragment2)
                            .commit();
                }
            });
        return view;
    }
}
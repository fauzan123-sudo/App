package com.example.app.fragment;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.toolbox.JsonArrayRequest;
import com.example.app.R;
import com.example.app.adapter.AdapterBeritaAcara;
import com.example.app.helper.AppController;
import com.example.app.model.Model_Berita_Acara;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static com.example.app.helper.Constans.TAG_JSON_OBJECT;
import static com.example.app.helper.Constans.berita;

public class BeritaAcara extends Fragment {
    private RecyclerView mRecyclerView;
    private ArrayList<Model_Berita_Acara> modelBeritaAcara;
    private AdapterBeritaAcara mExampleAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_berita_acara, container, false);

        ProgressDialog pd = new ProgressDialog(requireActivity());
        pd.setMessage("loading");
        mRecyclerView = view.findViewById(R.id.recycler);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        modelBeritaAcara    = new ArrayList<>();
        parseJSON();
        return view;
    }

    private void parseJSON() {
        modelBeritaAcara.clear();
        JsonArrayRequest jArr = new JsonArrayRequest(berita,
        response -> {
            for (int i = 0; i < response.length(); i++) {
                try {
                JSONObject hit     = response.getJSONObject(i);
                String Gambar      = hit.getString("image");
                String Judul       = hit.getString("title");
                String Keterangan  = hit.getString("isi");
                modelBeritaAcara.add(new Model_Berita_Acara(Gambar, Judul, Keterangan));

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            mExampleAdapter = new AdapterBeritaAcara(getActivity(), modelBeritaAcara);
            mRecyclerView.setAdapter(mExampleAdapter);
            mExampleAdapter.notifyDataSetChanged();

        }, Throwable::printStackTrace);
        AppController.getInstance().addToRequestQueue(jArr, TAG_JSON_OBJECT);
    }

    @Override
    public void onResume() {
        ((AppCompatActivity)getActivity()).getSupportActionBar().hide();
        super.onResume();
    }

    @Override
    public void onStop() {
        ((AppCompatActivity)getActivity()).getSupportActionBar().show();
        super.onStop();
    }
}
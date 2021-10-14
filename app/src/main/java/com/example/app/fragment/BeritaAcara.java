package com.example.app.fragment;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.example.app.R;
import com.example.app.adapter.AdapterBeritaAcara;
import com.example.app.helper.AppController;
import com.example.app.helper.SessionManager;
import com.example.app.model.Model_Berita_Acara;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import static com.example.app.helper.Constans.berita;

public class BeritaAcara extends Fragment {
    private ProgressDialog pd;
    private RecyclerView mRecyclerView;
    String tag_json_obj = "json_obj_req";
    private ArrayList<Model_Berita_Acara> modelBeritaAcara;
    SessionManager sessionManager;
    String getId;
    private AdapterBeritaAcara mExampleAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_berita_acara, container, false);

        pd = new ProgressDialog(requireActivity());
        pd.setMessage("loading");
        mRecyclerView = view.findViewById(R.id.recycler);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        modelBeritaAcara    = new ArrayList<>();
        sessionManager      = new SessionManager(requireActivity());
        HashMap<String, String> user = sessionManager.getUserDetail();
        getId = user.get(SessionManager.ID);
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                parseJSON();
                handler.postDelayed(this, 1000);
            }
        }, 1000);
        return view;
    }

    private void parseJSON() {

        StringRequest stringRequest = new StringRequest(Request.Method.POST, berita,
                response -> {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        JSONArray jsonArray = jsonObject.getJSONArray("hasil");
                        modelBeritaAcara.clear();
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject hit     = jsonArray.getJSONObject(i);
                            String Gambar      = hit.getString("image");
                            String Judul       = hit.getString("title");
                            String Keterangan  = hit.getString("isi");
                            modelBeritaAcara.add(new Model_Berita_Acara(Gambar, Judul, Keterangan));

                        }
                        mExampleAdapter = new AdapterBeritaAcara(getActivity(), modelBeritaAcara);
                        mRecyclerView.setAdapter(mExampleAdapter);
                        mExampleAdapter.notifyDataSetChanged();

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }, Throwable::printStackTrace);
        AppController.getInstance().addToRequestQueue(stringRequest, tag_json_obj);
    }
}
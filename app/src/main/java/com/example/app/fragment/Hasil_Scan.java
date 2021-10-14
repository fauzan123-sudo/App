package com.example.app.fragment;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.example.app.R;
import com.example.app.helper.AppController;
import com.example.app.helper.SessionManager;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import static com.example.app.helper.Constans.CekScanning;
import static com.example.app.helper.Constans.TAG_JSON_OBJECT;
import static com.example.app.helper.Constans.TAG_MESSAGE;
import static com.example.app.helper.Constans.TAG_QR;
import static com.example.app.helper.Constans.TAG_SUCCESS;
import static com.example.app.helper.Constans.URL_InserScan;

public class Hasil_Scan extends Fragment {
    SessionManager sessionManager;
    String getId, data, Lokasi;
    Button Berhasil,Gagal;
    int success;
    TextView Tanggal, Jam;
    ProgressDialog pDialog;

    FusedLocationProviderClient client;
    int Request_codes = 1;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view       = inflater.inflate(R.layout.fragment_hasil_scan, container, false);
        Bundle bundle   = this.getArguments();
        data            = bundle.getString("key");
        Berhasil        = view.findViewById(R.id.btn_berhasil);
        Gagal           = view.findViewById(R.id.btn_gagal);
        Tanggal         = view.findViewById(R.id.tanggal);
        Jam             = view.findViewById(R.id.jam);
        Calendar calendar = Calendar.getInstance();
        String currentDate = DateFormat.getDateInstance(DateFormat.FULL).format(calendar.getTime());
        Tanggal.setText(currentDate);

        String currentTime = new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date());
        Jam.setText(currentTime);
        //SharePreference
        sessionManager      = new SessionManager(requireActivity());
        HashMap<String, String> user = sessionManager.getUserDetail();
        getId = user.get(SessionManager.ID);

        if (ActivityCompat.checkSelfPermission(requireActivity(),
            Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(requireActivity(),
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, Request_codes);
            } else {
                getLocations();
            }
            cek_hasil_scan();
        //        Selesai Scan dan berhasil
        Berhasil.setOnClickListener(view1 -> {
            pDialog = new ProgressDialog(requireActivity());
            pDialog.setCancelable(false);
            pDialog.setMessage("Logging in ...");
            showDialog();
            StringRequest strReq = new StringRequest(Request.Method.POST, URL_InserScan, response -> {
                hideDialog();
                try {
                    JSONObject jObj = new JSONObject(response);
                    success = jObj.getInt(TAG_SUCCESS);
                    // Check for error node in json
                    if (success == 1) {
                        Absensi fragment2   = new Absensi();
                        requireActivity().getSupportFragmentManager()
                                .beginTransaction()
                                .replace(R.id.fragment_container, fragment2)
                                .commit();

                    } else {
                        Toast.makeText(requireActivity(), "error", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    // JSON error
                    e.printStackTrace();
                    Toast.makeText(requireActivity(), "ada error", Toast.LENGTH_SHORT).show();
                    hideDialog();
                }
            }, error ->{
                hideDialog();
                Toast.makeText(requireActivity(), "error Volley"+error, Toast.LENGTH_LONG).show();
            })

            {

                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<>();
                    params.put("id_pegawai", getId);
                    return params;
                }
            };
            AppController.getInstance().addToRequestQueue(strReq, TAG_JSON_OBJECT);
        });

//        Jika gagal
        Gagal.setOnClickListener(view12 -> {
            Bundle bundle12 = new Bundle();
            Scan fragment2  = new Scan();
            fragment2.setArguments(bundle12);

            requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment2)
                .commit();
        });

        return view;
    }

    private void showDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hideDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }

    private void getLocations() {

        LocationRequest locationRequest = new LocationRequest();
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(3000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        if (ActivityCompat.checkSelfPermission(requireActivity(),
            Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(requireActivity(),
            Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        LocationServices.getFusedLocationProviderClient(requireActivity())
                .requestLocationUpdates(locationRequest, new LocationCallback() {
                    @Override
                    public void onLocationResult(@NotNull LocationResult locationResult) {
                        super.onLocationResult(locationResult);

                        LocationServices.getFusedLocationProviderClient(requireActivity())
                                .removeLocationUpdates(this);
                        if (locationResult.getLocations().size()>0){
                            int lastLocationIndex   = locationResult.getLocations().size() -1;
                            double latitude         = locationResult.getLocations().get(lastLocationIndex).getLatitude();
                            double longetitude      = locationResult.getLocations().get(lastLocationIndex).getLongitude();
                            Lokasi = latitude+","+longetitude;

                        }
                    }
                }, Looper.getMainLooper());
    }

    private void cek_hasil_scan() {
        StringRequest strReq = new StringRequest(Request.Method.POST, CekScanning, response -> {
            try {
                JSONObject jObj = new JSONObject(response);
                success = jObj.getInt(TAG_SUCCESS);
                if (success == 1) {
                    Berhasil.setVisibility(View.VISIBLE);
                    Gagal.setVisibility(View.INVISIBLE);
                } else {
                    Toast.makeText(getActivity(),
                            jObj.getString(TAG_MESSAGE), Toast.LENGTH_LONG).show();
                    Berhasil.setVisibility(View.INVISIBLE);
                    Gagal.setVisibility(View.VISIBLE);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> Toast.makeText(getActivity(), "error Volley"+error, Toast.LENGTH_LONG).show()) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("id_pegawai", getId);
                params.put("string_qr_code", data);
                return params;
            }
        };
        AppController.getInstance().addToRequestQueue(strReq, TAG_JSON_OBJECT);
    }

    @Override
    public void onStart() {
        super.onStart();
        client = LocationServices.getFusedLocationProviderClient(requireActivity());
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == Request_codes &&  grantResults.length>0){
            if (grantResults[0]==PackageManager.PERMISSION_GRANTED){
                getLocations();
            }else{
                Toast.makeText(requireActivity(), "Permission denied!!", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
package com.example.app.fragment;

import android.Manifest;
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
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.app.R;
import com.example.app.helper.AppController;
import com.example.app.helper.Constans;
import com.example.app.helper.SessionManager;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class Hasil_Scan extends Fragment {
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_MESSAGE = "message";
    private static final String TAG_QR      = "qr";
    String tag_json_obj = "json_obj_req";
    SessionManager sessionManager;
    String getId;
    Button Berhasil,Gagal;
    int success;
    private static String URL_TAMPIL_QR = Constans.BaseUrl2 + "jajal/tampil_qr.php";
    private static String URL_SCAN      = Constans.BaseUrl2 + "jajal/scan.php";
    private static String URL_InserScan = Constans.BaseUrl2 + "jajal/insertScan.php";
    FusedLocationProviderClient client;
    TextView NIP, TampilQR, Lokasi;
    int Request_codes = 1;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view       = inflater.inflate(R.layout.fragment_hasil_scan, container, false);
        NIP             = view.findViewById(R.id.nip);
        Lokasi          = view.findViewById(R.id.lokasi);
        TampilQR        = view.findViewById(R.id.tampil_qr);
        Bundle bundle   = this.getArguments();
        String data     = bundle.getString("key");
        String qre      = TampilQR.getText().toString().trim();
        Berhasil        = view.findViewById(R.id.btn_berhasil);
        Gagal           = view.findViewById(R.id.btn_gagal);

        //SharePreference
        sessionManager      = new SessionManager(getActivity());
        HashMap<String, String> user = sessionManager.getUserDetail();
        getId = user.get(sessionManager.ID);

        NIP.setText(data);
        String nip = NIP.getText().toString().trim();

        if (ActivityCompat.checkSelfPermission(requireActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(),
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, Request_codes);
        } else {
            getLocations();
        }

        Berhasil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Bundle bundle = new Bundle();
                Absensi fragment2 = new Absensi();
                fragment2.setArguments(bundle);

                getFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragment_container, fragment2)
                        .commit();

                StringRequest strReq = new StringRequest(Request.Method.POST, URL_InserScan, new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {

                        try {
                            JSONObject jObj = new JSONObject(response);
                            success = jObj.getInt(TAG_SUCCESS);

                            // Check for error node in json
                            if (success == 1) {
                                String qrnya = jObj.getString(TAG_QR);

                                TampilQR.setText(qrnya);


                                Log.e("Successfully Login!", jObj.toString());

                                Toast.makeText(getActivity(), jObj.getString(TAG_MESSAGE), Toast.LENGTH_LONG).show();


                            } else {
                                Toast.makeText(getActivity(),
                                        jObj.getString(TAG_MESSAGE), Toast.LENGTH_LONG).show();

                            }
                        } catch (JSONException e) {
                            // JSON error
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getActivity(), "error Volley"+error, Toast.LENGTH_LONG).show();

                    }
                }) {

                    @Override
                    protected Map<String, String> getParams() {
                        Map<String, String> params = new HashMap<String, String>();
                        params.put("id_karyawan", getId);
                        params.put("lokasi", Lokasi.getText().toString());


                        return params;
                    }

                };

                // Adding request to request queue
                AppController.getInstance().addToRequestQueue(strReq, tag_json_obj);
            }
        });

        Gagal.setOnClickListener(new View.OnClickListener() {
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


        //Tampil Hasil QR
        if (qre.isEmpty()){
            Toast.makeText(getActivity(), "Kosong", Toast.LENGTH_SHORT).show();
        }else{
            cek_input();
        }


//          cek QR Pegawai
        if (nip.isEmpty()){
            Toast.makeText(getActivity(), "Maaf QR kosong", Toast.LENGTH_SHORT).show();
        }else{
            cek_hasil_scan(nip);
        }
        return view;
    }

    private void getLocations() {

        LocationRequest locationRequest = new LocationRequest();
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(3000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        if (ActivityCompat.checkSelfPermission(requireActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(requireActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        LocationServices.getFusedLocationProviderClient(requireActivity())
                .requestLocationUpdates(locationRequest, new LocationCallback() {
                    @Override
                    public void onLocationResult(LocationResult locationResult) {
                        super.onLocationResult(locationResult);

                        LocationServices.getFusedLocationProviderClient(requireActivity())
                                .removeLocationUpdates(this);
                        if (locationResult != null && locationResult.getLocations().size()>0){
                            int lastLocationIndex = locationResult.getLocations().size() -1;
                            double latitude = locationResult.getLocations().get(lastLocationIndex).getLatitude();
                            double longetitude = locationResult.getLocations().get(lastLocationIndex).getLongitude();
                            Lokasi.setText((latitude+","+longetitude));
                        }
                    }
                }, Looper.getMainLooper());
    }

    private void cek_hasil_scan(String nip) {
        StringRequest strReq = new StringRequest(Request.Method.POST, URL_SCAN, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {

                try {
                    JSONObject jObj = new JSONObject(response);
                    success = jObj.getInt(TAG_SUCCESS);

                    if (success == 1) {
                        String qrnya = jObj.getString(TAG_QR);

                        Toast.makeText(getActivity(), "Sama", Toast.LENGTH_LONG).show();
                        Berhasil.setVisibility(View.VISIBLE);

                        Gagal.setVisibility(View.INVISIBLE);
//                        InsertScan();
                    } else {
                        Toast.makeText(getActivity(),
                                jObj.getString(TAG_MESSAGE), Toast.LENGTH_LONG).show();
                        Berhasil.setVisibility(View.INVISIBLE);
                        Gagal.setVisibility(View.VISIBLE);


                    }
                } catch (JSONException e) {
                    // JSON error
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getActivity(), "error Volley"+error, Toast.LENGTH_LONG).show();

            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("id_pegawai", getId);
                params.put("string_qr_code", nip);


                return params;
            }

        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_json_obj);
    }

    private void InsertScan() {

    }

    private void cek_input() {
        StringRequest strReq = new StringRequest(Request.Method.POST, URL_TAMPIL_QR, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {

                try {
                    JSONObject jObj = new JSONObject(response);
                    success = jObj.getInt(TAG_SUCCESS);

                    // Check for error node in json
                    if (success == 1) {
                        String qrnya = jObj.getString(TAG_QR);

                        TampilQR.setText(qrnya);


                        Log.e("Successfully Login!", jObj.toString());

                        Toast.makeText(getActivity(), jObj.getString(TAG_MESSAGE), Toast.LENGTH_LONG).show();


                    } else {
                        Toast.makeText(getActivity(),
                                jObj.getString(TAG_MESSAGE), Toast.LENGTH_LONG).show();

                    }
                } catch (JSONException e) {
                    // JSON error
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getActivity(), "error Volley"+error, Toast.LENGTH_LONG).show();

            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("id_pegawai", getId);

                return params;
            }

        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_json_obj);
    }

//    private void requesPermission() {
//        ActivityCompat.requestPermissions(requireActivity(), new String[]{ACCESS_FINE_LOCATION},1);
//    }

    @Override
    public void onStart() {
        super.onStart();
        client = LocationServices.getFusedLocationProviderClient(requireActivity());

//        requesPermission();
//        if (ActivityCompat.checkSelfPermission(requireActivity(),ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
//            return;
//        }

//        client.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
//            @Override
//            public void onSuccess(Location location) {
//                if (location != null){
//                    Lokasi.setText(location.toString());
//                }
//
//            }
//        });
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
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
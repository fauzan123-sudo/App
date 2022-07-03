package com.example.app.fragment;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.app.Profil;
import com.example.app.R;
import com.example.app.adapter.AdapterAbsen;
import com.example.app.adapter.AdapterSpinner;
import com.example.app.helper.AppController;
import com.example.app.helper.SessionManager;
import com.example.app.model.ModelAbsen;
import com.example.app.model.SpinnerModel;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.example.app.helper.Constans.CekScanning;
import static com.example.app.helper.Constans.TAG_JSON_OBJECT;
import static com.example.app.helper.Constans.TAG_SUCCESS;
import static com.example.app.helper.Constans.Total_Absensi;
import static com.example.app.helper.Constans.URL_Spinner;
import static com.example.app.helper.Constans.izin;
import static com.example.app.helper.Constans.read_absensi;
import static com.example.app.helper.Constans.urlImagePegawai;

public class Absensi extends Fragment implements AdapterView.OnItemSelectedListener{
    RadioGroup radioGroup;
    int success,angka;
    TextView Tr;
    RadioButton rb1,rb2;
    TextView AksiBottomSheet, Dari, Sampai, Nama, Jabatan, Hadir, Izin;
    public String getId, getNama, getImage, getJabatan, Status, JenisIjin, ID;
    private BottomSheetBehavior bottomSheetBehavior;
    SessionManager sessionManager;
    EditText Alasan;
    Button KirimDataIzin;
    ImageView arrow_right;
    Spinner spinnerCountries;
    private TimePickerDialog timePickerDialog;
    private int mYear, mMonth, mDay;
    CircleImageView profile_image;
    AdapterSpinner adapter;
    List<SpinnerModel> spinnerList = new ArrayList<>();
    HashMap<String, String> user;
    String tipe;

//    Absen
    RecyclerView mRecyclerView;
    private AdapterAbsen mExampleAdapter;
    ArrayList<ModelAbsen> modelBeritaAcara = new ArrayList<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_absensi, container, false);

        //Find ID
        tipe = String.valueOf(angka);
        ProgressDialog pd = new ProgressDialog(requireActivity());
        pd.setMessage("loading");
        Hadir               = view.findViewById(R.id.hadir);
        Izin                = view.findViewById(R.id.izin);
        Nama                = view.findViewById(R.id.nama);
        Jabatan             = view.findViewById(R.id.jabatan);
        spinnerCountries    = view.findViewById(R.id.jenis_izin);
        AksiBottomSheet     = view.findViewById(R.id.ajukanIzin);
        arrow_right         = view.findViewById(R.id.arrow_right);
        View bottomSheet    = view.findViewById(R.id.bottom_sheet);
        radioGroup          = view.findViewById(R.id.Radio_Group);
        rb1                 = view.findViewById(R.id.rbJam);
        rb2                 = view.findViewById(R.id.rbHari);
        Dari                = view.findViewById(R.id.dari);
        Sampai              = view.findViewById(R.id.sampai);
        KirimDataIzin       = view.findViewById(R.id.kirimDataIzin);
        Alasan              = view.findViewById(R.id.alasan);
        profile_image       = view.findViewById(R.id.profile_image);
        Tr                  = view.findViewById(R.id.tr);

        Status              = "masuk";
        callData();

//        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(RadioGroup radioGroup, int i) {
//
//            }
//        });

        adapter = new AdapterSpinner(requireActivity(), spinnerList);

        spinnerCountries.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    JenisIjin = spinnerList.get(i).getNama();
                Toast.makeText(requireActivity(), spinnerList.get(i).getNama() , Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        spinnerCountries.setAdapter(adapter);

        // Declare
//        SimpleDateFormat dateFormatter = new SimpleDateFormat("dd-MM-yyyy", Locale.US);
        sessionManager          = new SessionManager(requireActivity());
        sessionManager.checkLogin();
        user = sessionManager.getUserDetail();
        getId                   = user.get(SessionManager.ID);
        tampilDataJumlah(getId);

        mRecyclerView = view.findViewById(R.id.rec);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(requireActivity()));
        mExampleAdapter = new AdapterAbsen(getActivity(), modelBeritaAcara);
        mRecyclerView.setAdapter(mExampleAdapter);

        parseJson(getId);
        //Bottom Sheet
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);

        //Sharepreference

        getNama                      = user.get(SessionManager.NAME);
        getImage                     = user.get(SessionManager.IMAGE);
        getJabatan                   = user.get(SessionManager.JABATAN);

//        Nama.setText(getNama);
//        Jabatan.setText(getJabatan);
//        Picasso.with(requireActivity())
//                .load(urlImagePegawai + getImage)
//                .into(profile_image);

//        initList();

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(),
                R.array.numbers, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        //When click id

        KirimDataIzin.setOnClickListener(view12 -> kirimDataIzin());

//        profile_image.setOnClickListener(view1 -> ProfilPegawai());

        radioGroup.setOnCheckedChangeListener((radioGroup, i) -> {
            int radioButtonID = radioGroup.getCheckedRadioButtonId();
            View radioButton = radioGroup.findViewById(radioButtonID);
            int position = radioGroup.indexOfChild(radioButton);
            switch (i) {
                case R.id.rbHari://hari
                    showCalendar();
                    angka = position;
                    Log.d("angka", "onCreateView: "+angka);
//                    tipe = "1";
                    break;
                case R.id.rbJam://jam
                    showClock();
                    angka = position+2;
                    Log.d("angka", "onCreateView: "+angka);
//                    tipe = "2";
                    break;
            }
        });
        arrow_right.setOnClickListener(view13 -> bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED));
        AksiBottomSheet.setOnClickListener(view14 -> bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED));

        return view;
    }

    private void parseJson(String GETID) {
                Log.d("parseJSON"+ GETID, "parseJson: ");
        StringRequest jArr = new StringRequest(Request.Method.POST,read_absensi,
            response -> {
                JSONArray jsonArray;
                try {
                    jsonArray = new JSONArray(response);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonobject = jsonArray.getJSONObject(i);
                        String Judul          = jsonobject.getString("tanggal");
                        String Keterangan     = jsonobject.getString("jam");
                        modelBeritaAcara.add(new ModelAbsen(Judul, Keterangan));
                    }
                    mExampleAdapter.notifyDataSetChanged();
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.d("parseJson catch"+ e, "parseJson: ");
                }
            }, error -> {
            Log.d("volley error"+error, "onErrorResponse: ");
            Log.d("TAG", "onErrorResponse: ");
        }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("id_pegawai", GETID);

                return params;
            }};
        AppController.getInstance().addToRequestQueue(jArr, "tag_json_obj");
    }

    private void tampilDataJumlah(String GETID) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Total_Absensi, response -> {
            try {
                JSONObject jObj = new JSONObject(response);
                success         = jObj.getInt(TAG_SUCCESS);
                if (success == 1) {
                    String jumlah_hadir = jObj.getString("hadir");
                    String jumlah_izin  = jObj.getString("izin");
                    Hadir.setText(jumlah_hadir);
                    Izin.setText(jumlah_izin);
                } else {
                    Toast.makeText(requireActivity(), "Sukses 0" , Toast.LENGTH_SHORT).show();
                    Log.d("suksesnya 0", "tampilDataJumlah: ");
                }

            } catch (JSONException e) {
                e.printStackTrace();
                Log.d("TAG"+e, "tampilDataJumlah: ");
            }
        },
        error -> {
//            Toast.makeText(requireActivity(), "Error " + error.toString(), Toast.LENGTH_SHORT).show();
            Log.d("volley error jumlah"+ error, "tampilDataJumlah: ");
        }) {
        @Override
        protected Map<String, String> getParams() {
            Map<String, String> params = new HashMap<>();
            params.put("id_pegawai", GETID);
            Log.d("id_pegawai", "getParams: " + GETID);
            Log.i("id_pegawai", "getParams: " + GETID);
            return params;
        }
        };
        AppController.getInstance().addToRequestQueue(stringRequest, TAG_JSON_OBJECT);
    }

//    Spinner
    private void callData() {
        spinnerList.clear();
        JsonArrayRequest jArr = new JsonArrayRequest(URL_Spinner,
            response -> {
                Log.e("response", response.toString());
                for (int i = 0; i < response.length(); i++) {
                    try {
                        JSONObject obj = response.getJSONObject(i);
                        SpinnerModel item = new SpinnerModel();
                        item.setNama(obj.getString("nama"));
                        spinnerList.add(item);
                    } catch (JSONException e) {
                        Log.e("catch", "callData: ", e );
                        e.printStackTrace();
                    }
                }

                adapter.notifyDataSetChanged();

            }, error -> {
                VolleyLog.e("terjadi error spinner ", "Error: " + error.getMessage());
                Log.d("volley error"+error, "callData: ");

        });
        RequestQueue requestQueue = Volley.newRequestQueue(requireActivity());
        requestQueue.add(jArr);

    }
    private void ProfilPegawai() {
        Intent intent = new Intent(requireActivity(), Profil.class);
        startActivity(intent);
    }

    private void kirimDataIzin() {
        String dari     = Dari.getText().toString().trim();
        String sampai   = Sampai.getText().toString().trim();
        String Alasnya  = Alasan.getText().toString().trim();

//        Absensi absensi = new Absensi();
//        requireActivity().getSupportFragmentManager()
//                .beginTransaction()
//                .replace(R.id.fragment_container, absensi)
//                .commit();



        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, izin,
            response -> {
                try {
                    JSONObject jObj = new JSONObject(response);
                    success         = jObj.getInt(TAG_SUCCESS);
                    if (success == 1) {
                        requireActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new Absensi()).commit();
                        Log.i("success", "kirimDataIzin: ");
                        ((BottomNavigationView)requireActivity().findViewById(R.id.bottomNavigationView)).setSelectedItemId(R.id.absen);
//                        ((BottomNavigationView) requireActivity().findViewById(R.id.bottomNavigation)).setSelectedItemId(R.id.absen);
                    }else{
                        Toast.makeText(requireActivity(), "Maaf sepertianya ada masalah!!", Toast.LENGTH_SHORT).show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.d("error catch izin", "kirimDataIzin: ");
                }
            }, error -> {
                Log.d("error volley izin", "onErrorResponse: ");
                Log.e("error volley izin", "onErrorResponse: ");
            }){

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("id_pegawai", getId);
                params.put("start_izin", dari);
                params.put("end_izin", sampai);
                params.put("jenis_izin", JenisIjin);
                params.put("keterangan", Alasnya);
                params.put("tipe", tipe); // disini tambahkan tipe
                Log.d("aa", "getParams: "+ tipe);
                return params;
            }};
        AppController.getInstance().addToRequestQueue(stringRequest, TAG_JSON_OBJECT);
    }

    private void showCalendar() {
        Dari.setEnabled(true);
        Sampai.setEnabled(true);
        Dari.setOnClickListener(view -> {
            Calendar calendar = Calendar.getInstance();
            mYear   = calendar.get(Calendar.YEAR);
            mMonth  = calendar.get(Calendar.MONTH);
            mDay    = calendar.get(Calendar.DAY_OF_MONTH);
            @SuppressLint("SetTextI18n") DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), (view1, year, monthOfYear, dayOfMonth) ->
                Dari.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year), mYear, mMonth, mDay);
            datePickerDialog.show();
        });

        Sampai.setOnClickListener(view -> {
            Calendar calendar = Calendar.getInstance();
            mYear   = calendar.get(Calendar.YEAR);
            mMonth  = calendar.get(Calendar.MONTH);
            mDay    = calendar.get(Calendar.DAY_OF_MONTH);
            @SuppressLint("SetTextI18n") DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), (view1, year, monthOfYear, dayOfMonth) ->
                    Sampai.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year), mYear, mMonth, mDay);
            datePickerDialog.show();
        });
    }

    @SuppressLint("SetTextI18n")
    private void showClock() {
        Dari.setEnabled(true);
        Sampai.setEnabled(true);
        Dari.setOnClickListener(view -> {
            Calendar calendar = Calendar.getInstance();
            timePickerDialog = new TimePickerDialog(getActivity(), (view1, hourOfDay, minute) ->
                Dari.setText(hourOfDay + ":" + minute),
                calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE),

                DateFormat.is24HourFormat(getActivity()));

            timePickerDialog.show();


        });
        Sampai.setOnClickListener(view -> {
            Calendar calendar = Calendar.getInstance();
            timePickerDialog = new TimePickerDialog(getActivity(), (view12, hourOfDay, minute) ->
                    Sampai.setText(hourOfDay + ":" + minute),
                    calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE),

                    DateFormat.is24HourFormat(getActivity()));

            timePickerDialog.show();
        });
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int i, long l) {

        String text = parent.getItemAtPosition(i).toString();
        Log.d("select" + text, "onItemSelected: ");
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }



}
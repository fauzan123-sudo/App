package com.example.app.fragment;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
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
import androidx.transition.ChangeBounds;
import androidx.transition.Fade;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.app.Login;
import com.example.app.R;
import com.example.app.adapter.AdapterAbsen;
import com.example.app.adapter.AdapterSpinner;
import com.example.app.helper.AppController;
import com.example.app.helper.Constans;
import com.example.app.helper.SessionManager;
import com.example.app.model.ModelAbsen;
import com.example.app.model.SpinnerModel;
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

import static com.example.app.helper.Constans.Jumlah_data;
import static com.example.app.helper.Constans.TAG_JSON_OBJECT;
import static com.example.app.helper.Constans.TAG_MESSAGE;
import static com.example.app.helper.Constans.TAG_SUCCESS;
import static com.example.app.helper.Constans.Total_Absensi;
import static com.example.app.helper.Constans.URL_Spinner;
import static com.example.app.helper.Constans.izin;
import static com.example.app.helper.Constans.read_absensi;
import static com.example.app.helper.Constans.urlImagePegawai;

public class Absensi extends Fragment implements AdapterView.OnItemSelectedListener{
    private ProgressDialog pd;
    RadioGroup radioGroup;
    int success;
    RadioButton rb1,rb2;
    TextView AksiBottomSheet, Dari, Sampai, Nama, Jabatan, Hadir, Izin;
    String getId, getNama, getImage, getJabatan, Status, JenisIjin;
    private RecyclerView mRecyclerView;
    private BottomSheetBehavior bottomSheetBehavior;
    private ArrayList<ModelAbsen> modelAbsens;
    SessionManager sessionManager;
    EditText Alasan;
    Button KirimDataIzin;
    ImageView arrow_right;
    private AdapterAbsen mExampleAdapter;
    Spinner spinnerCountries;
    private TimePickerDialog timePickerDialog;
    private int mYear, mMonth, mDay;
    CircleImageView profile_image;
    AdapterSpinner adapter;
    List<SpinnerModel> spinnerList = new ArrayList<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_absensi, container, false);

        //Find ID
        pd = new ProgressDialog(requireActivity());
        pd.setMessage("loading");
        Hadir               = view.findViewById(R.id.hadir);
        Izin                = view.findViewById(R.id.izin);
        Nama                = view.findViewById(R.id.nama);
        Jabatan             = view.findViewById(R.id.jabatan);
        spinnerCountries    = view.findViewById(R.id.jenis_izin);
        AksiBottomSheet     = view.findViewById(R.id.ajukanIzin);
        arrow_right         = view.findViewById(R.id.arrow_right);
        View bottomSheet    = view.findViewById(R.id.bottom_sheet);
        mRecyclerView       = view.findViewById(R.id.recycler);
        radioGroup          = view.findViewById(R.id.Radio_Group);
        rb1                 = view.findViewById(R.id.rbJam);
        rb2                 = view.findViewById(R.id.rbHari);
        Dari                = view.findViewById(R.id.dari);
        Sampai              = view.findViewById(R.id.sampai);
        KirimDataIzin       = view.findViewById(R.id.kirimDataIzin);
        Alasan              = view.findViewById(R.id.alasan);
        profile_image       = view.findViewById(R.id.profile_image);
        Status              = "masuk";
        callData();
        tampilDataJumlah();


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
        modelAbsens         = new ArrayList<>();
        sessionManager      = new SessionManager(requireActivity());
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        //Bottom Sheet
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);

        //Sharepreference
        HashMap<String, String> user = sessionManager.getUserDetail();
        getNama             = user.get(SessionManager.NAME);
        getImage            = user.get(SessionManager.IMAGE);
        getJabatan          = user.get(SessionManager.JABATAN);
        getId = user.get(SessionManager.ID);

        //Call Functions
//        final Handler handler = new Handler(Looper.getMainLooper());
//        handler.post(() -> {

//                    currentApiVersion = Build.VERSION.SDK_INT;
//                    if (currentApiVersion >= Build.VERSION_CODES.M) {
//                        if (checkPermission()) {
//                            Log.e("SCAN QR CODE", "Permission already granted!");
//                        } else {
//                            requestPermission();
//                        }
//                    }
//                });
        parseJSON();
        Nama.setText(getNama);
        Jabatan.setText(getJabatan);
        Picasso.with(requireActivity())
                .load(urlImagePegawai + getImage)
                .into(profile_image);

//        initList();

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(),
                R.array.numbers, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        //When click id

        KirimDataIzin.setOnClickListener(view12 -> kirimDataIzin());

        profile_image.setOnClickListener(view1 -> ProfilPegawai());

        radioGroup.setOnCheckedChangeListener((radioGroup, i) -> {
            switch (i) {
                case R.id.rbJam://jam
                    showClock();
                    break;
                case R.id.rbHari://hari
                    showCalendar();
                    break;
            }
        });
        arrow_right.setOnClickListener(view13 -> bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED));
        AksiBottomSheet.setOnClickListener(view14 -> bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED));

        return view;
    }

    private void tampilDataJumlah() {
        pd.show();
        StringRequest strReq = new StringRequest(Request.Method.POST, Total_Absensi, response -> {
            try {
                pd.hide();
                JSONObject jObj = new JSONObject(response);
                    String jumlah_hadir = jObj.getString("hadir");
                    String jumlah_izin = jObj.getString("izin");
                    Log.d("jumlah", "tampilDataJumlah: "+ jumlah_hadir+jumlah_izin);
                    Log.i("jumlah", "tampilDataJumlah: "+ jumlah_hadir+jumlah_izin);
                      Hadir.setText(jumlah_hadir);
                      Izin.setText(jumlah_izin);

            } catch (JSONException e) {
                pd.hide();
                e.printStackTrace();
            }

        }, error -> Toast.makeText(requireActivity(), "Hi . . ", Toast.LENGTH_SHORT).show()) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("id_pegawai", getId);
                return params;
            }
        };
        AppController.getInstance().addToRequestQueue(strReq, TAG_JSON_OBJECT);
    }

    private void callData() {
        spinnerList.clear();
        JsonArrayRequest jArr = new JsonArrayRequest(URL_Spinner,
                response -> {
                    Log.e("response", response.toString());

                    // Parsing json
                    for (int i = 0; i < response.length(); i++) {
                        try {
                            JSONObject obj = response.getJSONObject(i);
                            SpinnerModel item = new SpinnerModel();
                            item.setNama(obj.getString("nama"));
                            spinnerList.add(item);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    adapter.notifyDataSetChanged();

                }, (Response.ErrorListener) error -> {
            VolleyLog.e("terjadi error", "Error: " + error.getMessage());

        });
        RequestQueue requestQueue = Volley.newRequestQueue(requireActivity());
        requestQueue.add(jArr);

    }


    private void ProfilPegawai() {

        Profile_Absensi sharedElementFragment2 = new Profile_Absensi();

        Fade slideTransition = new Fade(Fade.MODE_IN);
        slideTransition.setDuration(1000);

        ChangeBounds changeBoundsTransition = new ChangeBounds();
        changeBoundsTransition.setDuration(1000);

        sharedElementFragment2.setEnterTransition(slideTransition);
        sharedElementFragment2.setAllowEnterTransitionOverlap(false);
        sharedElementFragment2.setAllowReturnTransitionOverlap(false);
        sharedElementFragment2.setSharedElementEnterTransition(changeBoundsTransition);

        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, sharedElementFragment2)
                .addToBackStack(null)
                .addSharedElement(profile_image, getString(R.string.square_blue_name))
                .commit();
    }

    private void kirimDataIzin() {
        String dari     = Dari.getText().toString().trim();
        String sampai   = Sampai.getText().toString().trim();
        String Alasnya  = Alasan.getText().toString().trim();

        Absensi absensi = new Absensi();
        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, absensi)
                .commit();

        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, izin,
                response -> {
                    try {
                        JSONObject jsonObject   = new JSONObject(response);
                        JSONArray jsonArray     = jsonObject.getJSONArray("hasil");
                        modelAbsens.clear();
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject hit      = jsonArray.getJSONObject(i);
                            String Tanggal      = hit.getString("tanggal");
                            String Jam          = hit.getString("jam");
                            modelAbsens.add(new ModelAbsen(Tanggal, Jam));
                        }
                        mExampleAdapter = new AdapterAbsen(requireActivity(), modelAbsens);
                        mRecyclerView.setAdapter(mExampleAdapter);
                        mExampleAdapter.notifyDataSetChanged();

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }, Throwable::printStackTrace){

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("id_pegawai", getId);
                params.put("start_izin", dari);
                params.put("end_izin", sampai);
                params.put("jenis_izin", JenisIjin);
                params.put("keterangan", Alasnya);
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
            DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), (view1, year, monthOfYear, dayOfMonth) ->
                Dari.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year), mYear, mMonth, mDay);
            datePickerDialog.show();
        });

        Sampai.setOnClickListener(view -> {
            Calendar calendar = Calendar.getInstance();
            mYear   = calendar.get(Calendar.YEAR);
            mMonth  = calendar.get(Calendar.MONTH);
            mDay    = calendar.get(Calendar.DAY_OF_MONTH);
            DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), (view1, year, monthOfYear, dayOfMonth) ->
                    Sampai.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year), mYear, mMonth, mDay);
            datePickerDialog.show();
        });
    }

    private void showClock() {
        Dari.setEnabled(true);
        Sampai.setEnabled(true);
        Dari.setOnClickListener(view -> {
            Calendar calendar = Calendar.getInstance();
            timePickerDialog = new TimePickerDialog(getActivity(), (view1, hourOfDay, minute) -> Dari.setText(hourOfDay + ":" + minute),
                    calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE),

                    DateFormat.is24HourFormat(getActivity()));

            timePickerDialog.show();
        });
        Sampai.setOnClickListener(view -> {
            Calendar calendar = Calendar.getInstance();
            timePickerDialog = new TimePickerDialog(getActivity(), (view12, hourOfDay, minute) -> Sampai.setText(hourOfDay + ":" + minute),
                    calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE),

                    DateFormat.is24HourFormat(getActivity()));

            timePickerDialog.show();
        });
    }

    private void parseJSON() {

        StringRequest stringRequest = new StringRequest(Request.Method.POST, read_absensi,
                response -> {

                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        JSONArray jsonArray   = jsonObject.getJSONArray("hasil");
                        modelAbsens.clear();
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject hit      = jsonArray.getJSONObject(i);
                            String Tanggal      = hit.getString("tanggal");
                            String Jam          = hit.getString("jam");
                            modelAbsens.add(new ModelAbsen(Tanggal, Jam));

                        }
                        mExampleAdapter = new AdapterAbsen(getActivity(), modelAbsens);
                        mRecyclerView.setAdapter(mExampleAdapter);
                        mExampleAdapter.notifyDataSetChanged();

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }, Throwable::printStackTrace){

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("id_pegawai", getId);
                return params;
            }};
        AppController.getInstance().addToRequestQueue(stringRequest, TAG_JSON_OBJECT);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int i, long l) {
        String text = parent.getItemAtPosition(i).toString();
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

}
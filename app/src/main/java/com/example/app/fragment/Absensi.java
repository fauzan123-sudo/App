package com.example.app.fragment;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.format.DateFormat;
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

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.transition.ChangeBounds;
import androidx.transition.Fade;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.example.app.R;
import com.example.app.adapter.AdapterAbsen;
import com.example.app.helper.AppController;
import com.example.app.helper.Constans;
import com.example.app.helper.SessionManager;
import com.example.app.model.ModelAbsen;
import com.google.android.material.bottomsheet.BottomSheetBehavior;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class Absensi extends Fragment implements AdapterView.OnItemSelectedListener{
    RadioGroup radioGroup;
    RadioButton rb1,rb2;
    TextView AksiBottomSheet, Dari, Sampai;
    private RecyclerView mRecyclerView;
    private BottomSheetBehavior bottomSheetBehavior;
    String tag_json_obj = "json_obj_req";
    private ArrayList<ModelAbsen> modelAbsens;
    SessionManager sessionManager;
    String getId;
    EditText Alasan;
    Button KirimDataIzin;
    ImageView arrow_right;
    private AdapterAbsen mExampleAdapter;
    Spinner spinnerCountries;
    private TimePickerDialog timePickerDialog;
    private SimpleDateFormat dateFormatter;
    private int mYear, mMonth, mDay;
    CircleImageView profile_image;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_absensi, container, false);

        //Find ID
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

        // Declare
        dateFormatter = new SimpleDateFormat("dd-MM-yyyy", Locale.US);
        modelAbsens         = new ArrayList<>();
        sessionManager      = new SessionManager(getActivity());
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

//        mAdapter = new CountryAdapter(getActivity(), mCountryList);
//        spinnerCountries.setAdapter(mAdapter);

        //Bottom Sheet
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);

        //Sharepreference
        HashMap<String, String> user = sessionManager.getUserDetail();
        getId = user.get(sessionManager.ID);

        //Call Functions
        parseJSON();

//        initList();

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(),
                R.array.numbers, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCountries.setAdapter(adapter);
        spinnerCountries.setOnItemSelectedListener(this);


        //When click id

        KirimDataIzin.setOnClickListener(view12 -> kirimDataIzin());

        profile_image.setOnClickListener(view1 -> ProfilPegawai(false));

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

//        spinnerCountries.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                CountryItem clickedItem = (CountryItem) parent.getItemAtPosition(position);
//                String clickedCountryName = clickedItem.getCountryName();
//                Toast.makeText(getActivity(), clickedCountryName + " selected", Toast.LENGTH_SHORT).show();
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> adapterView) {
//
//            }
//        });

        return view;
    }

    private void ProfilPegawai(boolean overlap) {
//        Profile fragment = new Profile();
////        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
////            fragment.setSharedElementEnterTransition(new Profile());
////            fragment.setEnterTransition(new Fade());
////            setExitTransition(new Fade());
////            fragment.setSharedElementReturnTransition(new Profile());
////        }
//        getActivity().getSupportFragmentManager()
//                .beginTransaction()
//
//                .addSharedElement(profile, profile.getTransitionName())
//                .replace(R.id.container_fragment, fragment)
//                .addToBackStack(null)
//                .commit();
        Profile_Absensi sharedElementFragment2 = new Profile_Absensi();

        Fade slideTransition = new Fade(Fade.MODE_IN);
        slideTransition.setDuration(1000);

        ChangeBounds changeBoundsTransition = new ChangeBounds();
        changeBoundsTransition.setDuration(1000);

        sharedElementFragment2.setEnterTransition(slideTransition);
        sharedElementFragment2.setAllowEnterTransitionOverlap(overlap);
        sharedElementFragment2.setAllowReturnTransitionOverlap(overlap);
        sharedElementFragment2.setSharedElementEnterTransition(changeBoundsTransition);

        getFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, sharedElementFragment2)
                .addToBackStack(null)
                .addSharedElement(profile_image, getString(R.string.square_blue_name))
                .commit();
    }

    private void kirimDataIzin() {
        String dari     = Dari.getText().toString().trim();
        String sampai   = Sampai.getText().toString().trim();

        String url = Constans.BaseUrl +"read_absensi_personal.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                response -> {

                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        JSONArray jsonArray = jsonObject.getJSONArray("hasil");
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
                // Posting parameters ke post url
                Map<String, String> params = new HashMap<>();
                params.put("id_karyawan", getId);
                params.put("dari", dari);
                params.put("sampai", sampai);
                return params;
            }};
        AppController.getInstance().addToRequestQueue(stringRequest, tag_json_obj);
    }

    private void showCalendar() {
        Dari.setEnabled(true);
        Sampai.setEnabled(true);
        Dari.setOnClickListener(view -> {
            Calendar calendar = Calendar.getInstance();
            mYear   = calendar.get(Calendar.YEAR);
            mMonth  = calendar.get(Calendar.MONTH);
            mDay    = calendar.get(Calendar.DAY_OF_MONTH);
            DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), (DatePickerDialog.OnDateSetListener) (view1, year, monthOfYear, dayOfMonth) ->
                Dari.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year), mYear, mMonth, mDay);
            datePickerDialog.show();
        });
//                DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
//                    @Override
//                    public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
//
//
//                    }
//                });
//                    @Override
//                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
//                        Dari.setText(hourOfDay+":"+minute);
//                    }
//                },
//                        calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE),
//
//                        DateFormat.is24HourFormat(getActivity()));
//
//                timePickerDialog.show();
//            }
//        });


        Sampai.setOnClickListener(view -> {
            Calendar calendar = Calendar.getInstance();
            mYear   = calendar.get(Calendar.YEAR);
            mMonth  = calendar.get(Calendar.MONTH);
            mDay    = calendar.get(Calendar.DAY_OF_MONTH);
            DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), (DatePickerDialog.OnDateSetListener) (view1, year, monthOfYear, dayOfMonth) ->
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

        String url = Constans.BaseUrl +"read_absensi_personal.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                response -> {

                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        JSONArray jsonArray = jsonObject.getJSONArray("hasil");
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
                // Posting parameters ke post url
                Map<String, String> params = new HashMap<>();
                params.put("id_karyawan", getId);
                return params;
            }};
        AppController.getInstance().addToRequestQueue(stringRequest, tag_json_obj);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int i, long l) {
        String text = parent.getItemAtPosition(i).toString();
//        Toast.makeText(parent.getContext(), text, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

//    private void initList() {
//        mCountryList = new ArrayList<>();
//        mCountryList.add(new CountryItem("Izin", R.drawable.ic_home));
//        mCountryList.add(new CountryItem("Sakit", R.drawable.ic_home));
//        mCountryList.add(new CountryItem("Tugas", R.drawable.ic_home));
//
//    }
}
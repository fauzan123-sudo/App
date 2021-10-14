package com.example.app.fragment;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.transition.ChangeBounds;
import androidx.transition.Fade;
import androidx.viewpager.widget.ViewPager;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.example.app.MainActivity;
import com.example.app.R;
import com.example.app.adapter.ViewPagerAdapter;
import com.example.app.helper.AppController;
import com.example.app.helper.CustomVolleyRequest;
import com.example.app.helper.SessionManager;
import com.example.app.model.SliderUtils;
//import com.github.mikephil.charting.charts.LineChart;
//import com.github.mikephil.charting.components.XAxis;
//import com.github.mikephil.charting.components.YAxis;
//import com.github.mikephil.charting.data.Entry;
//import com.github.mikephil.charting.data.LineData;
//import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.google.firebase.messaging.FirebaseMessaging;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.example.app.helper.Constans.TAG_JSON_OBJECT;
import static com.example.app.helper.Constans.Total_Absensi2;
import static com.example.app.helper.Constans.inputToken;
import static com.example.app.helper.Constans.request_url;
import static com.example.app.helper.Constans.urlImagePegawai;

public class Home extends Fragment {

    ProgressDialog pd;
    int i,a;
//    LineChart lineChart;
ArrayList yAxis;
    ArrayList yValues;
    ArrayList xAxis1;
    BarEntry values ;
    BarChart chart;
    BarData data;
    LineChart lineChart;
    private static final int TAG_SIMPLE_NOTIFICATION    = 1;
    private static final int TAG_BIG_TEXT_NOTIFICATION  = 2;
    private static final String TAG_SUCCESS             = "success";
    String CHANNEL_NAME = "MESSAGE";
    String Token;
    //    ViewPager
    ViewPager viewPager;
    int success;
    String message;
    LinearLayout sliderDotspanel;
    private int dotscount;
    private ImageView[] dots;
    List<SliderUtils> sliderImg; //model
    ViewPagerAdapter viewPagerAdapter;//adapter
    Timer timer;
    int currentApiVersion;

    TextView Nama,Jabatan;
    String CHANNEL_ID = "my_channel_01";
    String getId, getNama, getImage, getJabatan;
    SessionManager sessionManager;
    CircleImageView profile_image;
    private NotificationManagerCompat notificationManager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        sessionManager          = new SessionManager(requireActivity());
        sessionManager.checkLogin();
        notificationManager     = NotificationManagerCompat.from(requireActivity());
        Nama                    = view.findViewById(R.id.nama);
        Jabatan                 = view.findViewById(R.id.jabatan);

//        yAxis   = new ArrayList<Entry>();
//        yValues = new ArrayList<String>();
//        lineChart = view.findViewById(R.id.chart);

        chart               = view.findViewById(R.id.chart1);
//        LineDataSet lineDataSet = new LineDataSet(dataValues1(), "statistik bulan ini");
//        ArrayList<ILineDataSet> dataSets = new ArrayList<>();
//        dataSets.add(lineDataSet);
//
//        LineData data           = new LineData(dataSets);
//        lineChart.setData(data);
//        lineChart.invalidate();
//        String ana = "absensi anda";
//        lineChart.getDescription(ana);
//        lineChart.setDescription("ana");

        pd = new ProgressDialog(requireActivity());
        pd.setMessage("loading");
        profile_image           = view.findViewById(R.id.profile_image);
        HashMap<String, String> user = sessionManager.getUserDetail();
        getId                   = user.get(SessionManager.ID);
        getNama                 = user.get(SessionManager.NAME);
        getJabatan              = user.get(SessionManager.JABATAN);
        getImage                = user.get(SessionManager.IMAGE);
        load_data_from_server();

        final Handler handler   = new Handler(Looper.getMainLooper());
        Nama.setText(getNama);
        Jabatan.setText(getJabatan);
        Picasso.with(requireActivity())
                .load(urlImagePegawai + getImage)
                .into(profile_image);
        handler.post(() -> {
            currentApiVersion   = Build.VERSION.SDK_INT;
            if (ActivityCompat.checkSelfPermission(requireActivity(),
                    Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(requireActivity(),
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            } else {
                checkRunTimePermission();
            }
        });

        profile_image.setOnClickListener(view1 -> ProfilPegawai());
        Nama.setText(getNama);
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        Log.w("eror", "Fetching FCM registration token failed",
                                task.getException());
                        return;
                    }

                    // Get new FCM registration token
                    String token = task.getResult();
                    Token = token;
                    cekToken(token);
                    Log.d("token","tokenya"+""+token);
                });

//        List<Entry> lineEntries = getDataSet();
//        LineDataSet lineDataSet = new LineDataSet(lineEntries, "Statistik kehadiran");
//        lineDataSet.setAxisDependency(YAxis.AxisDependency.LEFT);
//        lineDataSet.setHighlightEnabled(true);
//        lineDataSet.setLineWidth(2);
//        lineDataSet.setColor(Color.RED);
//        lineDataSet.setCircleColor(Color.YELLOW);
//        lineDataSet.setCircleRadius(6);
//        lineDataSet.setCircleHoleRadius(3);
//        lineDataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);
//        lineDataSet.setDrawHighlightIndicators(true);
//        lineDataSet.setHighLightColor(Color.RED);
//        lineDataSet.setValueTextSize(12);
//        lineDataSet.setValueTextColor(Color.DKGRAY);
//
//        LineData lineData = new LineData(lineDataSet);
//        lineChart.getDescription().setText("Bulan ini");
//        lineChart.getDescription().setTextSize(12);
//        lineChart.setDrawMarkers(true);
//        lineChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTH_SIDED);
//        lineChart.animateY(1000);
//        lineChart.getXAxis().setGranularityEnabled(true);
//        lineChart.getXAxis().setGranularity(2.0f);
//        lineChart.getXAxis().setLabelCount(lineDataSet.getEntryCount());
//        lineChart.setData(lineData);

        sliderImg           = new ArrayList<>();
        viewPager           = view.findViewById(R.id.viewPager);
        sliderDotspanel     = view.findViewById(R.id.SliderDots);
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                viewPager.post(() -> viewPager.setCurrentItem((viewPager.getCurrentItem() + 1) % dots.length));
            }
        };
        timer = new Timer();
        timer.schedule(timerTask, 5000, 5000);

        sendRequest();
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }
            @Override
            public void onPageSelected(int position) {
                for(int i = 0; i< dotscount; i++){
                    dots[i].setImageDrawable(ContextCompat.getDrawable(requireActivity(), R.drawable.non_active_dot));
                }
                dots[position].setImageDrawable(ContextCompat.getDrawable(requireActivity(), R.drawable.active_dot));
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        return view;
    }

    private void load_data_from_server() {
        pd.show();
        xAxis1 = new ArrayList<>();
        yAxis = null;
        yValues = new ArrayList<>();

        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                Total_Absensi2,
                new Response.Listener() {
                    @Override
                    public void onResponse(Object response) {
                        try {
                            JSONArray jsonarray = new JSONArray(response);
                            for(int i=0; i < jsonarray.length(); i++) {

                                JSONObject jsonobject = jsonarray.getJSONObject(i);

                                String score = jsonobject.getString("hadir").trim();
                                String name = jsonobject.getString("izin").trim();

                                xAxis1.add(name);
                                values = new BarEntry(Float.valueOf(score),i);
                                yValues.add(values);

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        BarDataSet barDataSet1 = new BarDataSet(yValues, "Statistic");
                        barDataSet1.setColor(Color.rgb(0, 82, 159));

                        yAxis = new ArrayList<>();
                        yAxis.add(barDataSet1);
                        String names[]= (String[]) xAxis1.toArray(new String[xAxis1.size()]);
                        data = new BarData(names,yAxis);
                        chart.setData(data);
                        chart.setDescription("");
                        chart.animateXY(2000, 2000);
                        chart.invalidate();
                        pd.hide();
                    }
                },
                error -> {
                    if(error != null){

                        Toast.makeText(requireActivity(), "Something went wrong.", Toast.LENGTH_LONG).show();
                        pd.hide();
                    }
                }

        );
        AppController.getInstance().addToRequestQueue(stringRequest, TAG_JSON_OBJECT);
    }


    private List<Entry> getDataSet() {
        List<Entry> lineEntries = new ArrayList<>();
        float[] ys1 = new float[]{80f, 90f, 80f, 90f, 80f, 80f, 100f};
        float[] yx1 = new float[]{1f, 5f, 6f, 9f, 3f, 2f, 5f};
        for (a = 0; a < yx1.length; a++) {
            lineEntries.add(new Entry(i, a));
        }

        return lineEntries;
    }


    private void checkRunTimePermission() {
    }

    private ArrayList<Entry> dataValues1(){
        ArrayList<Entry> dataVals = new ArrayList<>();
        float[] x = new float[]{0, 10, 20,30};
        int[] y = new int[]{};
        dataVals.add(new Entry(4,10));
        dataVals.add(new Entry(8,20));
        return dataVals;
    }

    private void cekToken(String token) {
        StringRequest strReq = new StringRequest(Request.Method.POST, inputToken, response -> {
//            Log.e("login respons", "Login Response: " + response);

            try {
                JSONObject jObj = new JSONObject(response);
                success = jObj.getInt(TAG_SUCCESS);
                message = jObj.getString("message");

                if (success == 1) {
//                    Log.e("Token berhasil diupdate", jObj.toString());
                    Log.d("success 1 ", "cekToken: " + message);

                } else if(success == 2){
                    Log.d("success=2", "token sudah ada :" + message);
                } else{
                    Log.d("success", "dialog: error" + message);

                }
            } catch (JSONException e) {
                // JSON error
                e.printStackTrace();
            }

        }, error -> Log.e("error", "Login Error: " + error.getMessage())) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("id_pegawai", getId);
                params.put("token", token);
                return params;
            }

        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, TAG_JSON_OBJECT);
    }

    private void dialog() {
        AlertDialog.Builder alert = new AlertDialog.Builder(requireContext());
        alert.setMessage("Hay"+ getNama);
        alert.setNeutralButton("OK", (dialogInterface, i) -> {
            StringRequest strReq = new StringRequest(Request.Method.POST, inputToken, response -> {
                Log.e("login respons", "Login Response: " + response);

                try {
                    JSONObject jObj = new JSONObject(response);
                    success = jObj.getInt(TAG_SUCCESS);

                    if (success == 1) {
                        Log.e("Successfully Login!", jObj.toString());

                    } else {
                        Log.d("G", "dialog: error");

                    }
                } catch (JSONException e) {
                    // JSON error
                    e.printStackTrace();
                }

            }, error -> Log.e("error", "Login Error: " + error.getMessage())) {

                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<>();
                    params.put("id_pegawai", getId);
                    params.put("token", Token);

                    return params;
                }

            };

            // Adding request to request queue
            AppController.getInstance().addToRequestQueue(strReq, TAG_JSON_OBJECT);
        });
        alert.show();
    }

    private PendingIntent pendingIntentForNotification() {

        Intent intent = new Intent(getActivity(), MainActivity.class);

        return PendingIntent.getActivity(getActivity(), 1, intent, 0);
    }

    private void showSimpleNotification() {
        //Use the NotificationCompat compatibility library in order to get gingerbread support.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Notification notification = new NotificationCompat.Builder(requireActivity(), CHANNEL_ID)
                    .setContentTitle(getString(R.string.nf_simple_title))
                    .setContentText(getString(R.string.nf_simple_message))
                    .setTicker(getString(R.string.nf_simple_ticker))
                    .setSmallIcon(R.drawable.ic_absensi)
                    .setLargeIcon(BitmapFactory.decodeResource(requireActivity().getResources(), R.drawable.ic_absensi))
                    //Set the intent
                    .setContentIntent(pendingIntentForNotification())
                    .setChannelId(CHANNEL_ID)
                    .build();


            //Grab the NotificationManager and post the notification
            NotificationManager notificationManager = (NotificationManager)
                    requireActivity().getSystemService(Context.NOTIFICATION_SERVICE);

            //Set a tag so that the same notification doesn't get reposted over and over again and
            //you can grab it again later if you need to.
            notificationManager.notify(TAG_SIMPLE_NOTIFICATION, notification);
        }
    }

    private void showBigTextNotification() {
        //Use the NotificationCompat compatibility library in order to not have this barf on < 4.1 devices.
        NotificationManagerCompat manager = NotificationManagerCompat.from(requireActivity());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_DEFAULT);
            manager.createNotificationChannel(channel);
        }
        Notification notification = new NotificationCompat.Builder(requireActivity(), CHANNEL_ID)
                .setContentTitle("fauzan")
                //This will show on devices that don't support the big text and if further notifications
                //come in after the big text notification.
                .setContentText("gg")
                .setTicker("fauzan")
                .setSmallIcon(R.drawable.ic_absensi)
                .setLargeIcon(BitmapFactory.decodeResource(requireActivity().getResources(), R.drawable.ic_absensi))
                .setContentIntent(pendingIntentForNotification())
                .setChannelId(CHANNEL_ID)
                .build();

        //Same deal as the simple notification.
        NotificationManager notificationManager = (NotificationManager)
                requireActivity().getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(TAG_BIG_TEXT_NOTIFICATION, notification);
    }


    public void sendRequest() {
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, request_url,
                null, response -> {

            for (int i = 0; i < response.length(); i++) {
                SliderUtils sliderUtils = new SliderUtils();
                try {
                    JSONObject jsonObject = response.getJSONObject(i);
                    sliderUtils.setSliderImageUrl(jsonObject.getString("image"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                sliderImg.add(sliderUtils);
            }
            viewPagerAdapter = new ViewPagerAdapter(sliderImg, getActivity());
            viewPager.setAdapter(viewPagerAdapter);
            dotscount        = viewPagerAdapter.getCount();
            dots             = new ImageView[dotscount];

            for (int i = 0; i < dotscount; i++) {

                dots[i] = new ImageView(getActivity());
                dots[i].setImageDrawable(ContextCompat.getDrawable(requireActivity(), R.drawable.non_active_dot));

                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT);

                params.setMargins(8, 0, 8, 0);
                sliderDotspanel.addView(dots[i], params);

            }

            dots[0].setImageDrawable(ContextCompat.getDrawable(requireActivity(), R.drawable.active_dot));

        }, error -> Toast.makeText(getActivity(), "Error" + error.toString(), Toast.LENGTH_LONG).show());

        CustomVolleyRequest.getInstance(getActivity()).addToRequestQueue(jsonArrayRequest);

    }

    private void ProfilPegawai() {
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
        Profile sharedElementFragment2 = new Profile();

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

}

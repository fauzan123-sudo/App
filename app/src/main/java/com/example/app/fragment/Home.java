package com.example.app.fragment;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.app.Login;
import com.example.app.MainActivity;
import com.example.app.Profil;
import com.example.app.R;
import com.example.app.adapter.SliderAdapterExample;
import com.example.app.helper.AppController;
import com.example.app.helper.SessionManager;
import com.example.app.model.SliderItem;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.firebase.messaging.FirebaseMessaging;
import com.smarteist.autoimageslider.IndicatorView.animation.type.IndicatorAnimationType;
import com.smarteist.autoimageslider.SliderAnimations;
import com.smarteist.autoimageslider.SliderView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import de.hdodenhof.circleimageview.CircleImageView;

import static com.example.app.Login.TAG_ID;
import static com.example.app.helper.Constans.TAG_JSON_OBJECT;
import static com.example.app.helper.Constans.berita;
import static com.example.app.helper.Constans.inputToken;
import static com.example.app.helper.Constans.urlImageBerita;

public class Home extends Fragment {
    ArrayList<Entry> x = new ArrayList<>();
    ArrayList<String> y = new ArrayList<>();
    private LineChart mChart;
    String Token;
    SliderView sliderView;
    ArrayList<SliderItem> sliderItems = new ArrayList<>();
    private SliderAdapterExample adapter;
    ProgressDialog pd;
    private static final int TAG_SIMPLE_NOTIFICATION    = 1;
    private static final int TAG_BIG_TEXT_NOTIFICATION  = 2;
    private static final String TAG_SUCCESS             = "success";
    String CHANNEL_NAME = "MESSAGE";
    int success;
    SharedPreferences sharedpreferences;
    String message;
    int currentApiVersion;
    TextView Nama,Jabatan;
    String CHANNEL_ID = "my_channel_01";
    String getId, getNama, getImage, getJabatan;
    SessionManager sessionManager;
    CircleImageView profile_image;
    private NotificationManagerCompat notificationManager;
//    ImageView Keluar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        sharedpreferences       = this.getActivity().getSharedPreferences(Login.my_shared_preferences, Context.MODE_PRIVATE);
        sessionManager          = new SessionManager(requireActivity());
        sessionManager.checkLogin();
        notificationManager     = NotificationManagerCompat.from(requireActivity());
        pd = new ProgressDialog(requireActivity());
        pd.setMessage("loading");
        HashMap<String, String> user = sessionManager.getUserDetail();
        getId                   = user.get(SessionManager.ID);
        Nama                    = view.findViewById(R.id.nama);
        Jabatan                 = view.findViewById(R.id.jabatan);
        profile_image           = view.findViewById(R.id.profile_image);
        getId                   = user.get(SessionManager.ID);
        getNama                 = user.get(SessionManager.NAME);
        getJabatan              = user.get(SessionManager.JABATAN);
        getImage                = user.get(SessionManager.IMAGE);

//        Keluar                  = view.findViewById(R.id.logOout);

//        Chart
        mChart = view.findViewById(R.id.chart1);
        mChart.setDrawGridBackground(false);
        mChart.setTouchEnabled(true);
        mChart.setDragEnabled(true);
        mChart.setScaleEnabled(true);
        mChart.setPinchZoom(true);
        mChart.setDrawBorders(true);

        mChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTH_SIDED);
        YAxis xAxis = mChart.getAxisRight();
        xAxis.setEnabled(false);
        YAxis leftAxis = mChart.getAxisLeft();
        leftAxis.setAxisMinValue(0f);
        leftAxis.setAxisMaxValue(40f);
        leftAxis.setDrawLimitLinesBehindData(true);
        LimitLine limitLine = new LimitLine(14, "Jarang Hadir");
        limitLine.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_BOTTOM);
        Legend l = mChart.getLegend();
        l.setForm(Legend.LegendForm.LINE);
        Graph_List();
        
//        Slider
        loadData();
        adapter    = new SliderAdapterExample(requireActivity(),sliderItems );
        sliderView = view.findViewById(R.id.imageSlider);

        sliderView.setSliderAdapter(adapter);
        sliderView.setIndicatorAnimation(IndicatorAnimationType.WORM);
        sliderView.setSliderTransformAnimation(SliderAnimations.SIMPLETRANSFORMATION);
        sliderView.setAutoCycleDirection(SliderView.AUTO_CYCLE_DIRECTION_BACK_AND_FORTH);
        sliderView.setIndicatorSelectedColor(Color.WHITE);
        sliderView.setIndicatorUnselectedColor(Color.GRAY);
        sliderView.setScrollTimeInSec(3);
        sliderView.setAutoCycle(true);
        sliderView.startAutoCycle();

//        Keluar.setOnClickListener(view1 -> Logout());

//        Nama.setText(getNama);
//        Jabatan.setText(getJabatan);
//        Picasso.with(requireActivity())
//                .load(urlImagePegawai + getImage)
//                .into(profile_image);
//        profile_image.setOnClickListener(view1 -> ProfilPegawai());
//        Nama.setText(getNama);
        final Handler handler   = new Handler(Looper.getMainLooper());
        handler.post(() -> {
            currentApiVersion   = Build.VERSION.SDK_INT;
            if (ActivityCompat.checkSelfPermission(requireActivity(),
                    Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(requireActivity(),
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            }
        });

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
//                    cekToken(token);
                    Log.d("token","tokenya"+""+token);
                });
        return view;
    }

    private void Logout() {
        Toast.makeText(requireActivity(), "ini home", Toast.LENGTH_SHORT).show();
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                requireActivity());

        // set title dialog
        alertDialogBuilder.setTitle("Keluar dari aplikasi?");

        // set pesan dari dialog
        alertDialogBuilder
                .setMessage("Klik Ya untuk keluar!")
                .setIcon(R.mipmap.ic_wengky)
                .setCancelable(false)
                .setPositiveButton("Ya", (dialog, id) -> {
                    // TODO jika tombol diklik, maka akan menutup activity ini
                    SharedPreferences.Editor editor = sharedpreferences.edit();
                    editor.putBoolean(Login.session_status, false);
                    editor.putString(TAG_ID, null);
                    editor.apply();

                    Intent intent = new Intent(requireActivity(), Login.class);
//                    finish();
                    startActivity(intent);
                })
                .setNegativeButton("Tidak", (dialog, id) -> {
                    // TODO jika tombol ini diklik, akan menutup dialog
                    // TODO dan tidak terjadi apa2
                    dialog.cancel();
                });

        // membuat alert dialog dari builder
        AlertDialog alertDialog = alertDialogBuilder.create();

        // menampilkan alert dialog
        alertDialog.show();
    }


    private void Graph_List() {
//        final ProgressDialog progressDialog = new ProgressDialog(requireActivity());
        pd.setMessage("Loading...");
        pd.show();
        String url = "https://damha.000webhostapp.com/api/Data.json";
        JsonArrayRequest jArr = new JsonArrayRequest(url, response -> {
            pd.hide();
            for (int i = 0; i < response.length(); i++) {
                try {
                    pd.hide();
                    JSONObject obj = response.getJSONObject(i);
                    int score      = obj.getInt("jumlah");
                    String date    = obj.getString("bulan");
                    x.add(new Entry(score, i));
                    y.add(date);
                } catch (JSONException e) {
                    pd.hide();
                    e.printStackTrace();
                }
            }
            pd.hide();
            LineDataSet set1 = new LineDataSet(x, Token);
            set1.setCircleColor(Color.BLACK);
            set1.setCircleColorHole(Color.BLACK);
            set1.setColors(ColorTemplate.COLORFUL_COLORS);
            set1.setMode(LineDataSet.Mode.CUBIC_BEZIER);
            set1.setLineWidth(3f);
            set1.setCircleRadius(4f);
            LineData data = new LineData(y, set1);
            mChart.setData(data);
            mChart.invalidate();

        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                pd.hide();
                VolleyLog.d("error"+error, "Error: " + error.getMessage());
                Toast.makeText(requireActivity(), "error", Toast.LENGTH_SHORT).show();
            }
        });

        AppController.getInstance().addToRequestQueue(jArr);
    }

    private void loadData() {
//        final ProgressDialog progressDialog = new ProgressDialog(requireActivity());
        pd.setMessage("Loading...");
        pd.show();
        sliderItems.clear();
        JsonArrayRequest jArr = new JsonArrayRequest(berita,
            response -> {
                pd.hide();
                for (int i = 0; i < response.length(); i++) {
                    try {
                        pd.hide();
                        JSONObject obj = response.getJSONObject(i);
                        SliderItem item = new SliderItem();
                        String Title = obj.getString("title");
                        String Image = obj.getString("image");
                        item.setImageUrl(urlImageBerita+Image);
                        item.setDescription(Title);
                        sliderItems.add(item);

                    } catch (JSONException e) {
                        e.printStackTrace();
                        pd.hide();
                        Log.d("catch"+e, "loadData: ");
                    }
                }
                adapter.notifyDataSetChanged();

            }, error -> {
            pd.hide();
            VolleyLog.e("terjadi error berita"+error, "Error: " + error.getMessage());
            Toast.makeText(requireActivity(), "Error"+ error, Toast.LENGTH_LONG).show();

        });
        RequestQueue requestQueue = Volley.newRequestQueue(requireActivity());
        requestQueue.add(jArr);

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

    private void ProfilPegawai() {
//        Profile sharedElementFragment2 = new Profile();
//
//        Fade slideTransition = new Fade(Fade.MODE_IN);
//        slideTransition.setDuration(1000);
//
//        ChangeBounds changeBoundsTransition = new ChangeBounds();
//        changeBoundsTransition.setDuration(1000);
//
//        sharedElementFragment2.setEnterTransition(slideTransition);
//        sharedElementFragment2.setAllowEnterTransitionOverlap(false);
//        sharedElementFragment2.setAllowReturnTransitionOverlap(false);
//        sharedElementFragment2.setSharedElementEnterTransition(changeBoundsTransition);
//
//        requireActivity().getSupportFragmentManager()
//                .beginTransaction()
//                .replace(R.id.fragment_container, sharedElementFragment2)
//                .addToBackStack(null)
//                .addSharedElement(profile_image, getString(R.string.square_blue_name))
//                .commit();
        Intent intent = new Intent(requireActivity(), Profil.class);
        startActivity(intent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if ( pd!=null && pd.isShowing() )
        {
            pd.cancel();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if ( pd!=null && pd.isShowing() )
        {
            pd.cancel();
        }
    }
}

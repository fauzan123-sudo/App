package com.example.app.fragment;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
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
import com.example.app.helper.Constans;
import com.example.app.helper.CustomVolleyRequest;
import com.example.app.helper.SessionManager;
import com.example.app.model.SliderUtils;
import com.google.firebase.messaging.FirebaseMessaging;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.example.app.helper.Constans.BaseUrl;
import static com.example.app.helper.Constans.URL;

public class Home extends Fragment {

    private static final int TAG_SIMPLE_NOTIFICATION = 1;
    private static final int TAG_BIG_TEXT_NOTIFICATION = 2;
    private static final String TAG_SUCCESS = "success";
    public static final String inputToken = URL+"jajal/cekToken.php";
    String CHANNEL_NAME = "MESSAGE";
    String Token;
    String tag_json_obj = "json_obj_req";

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

    String request_url = BaseUrl + "gambarBerita.php";
    TextView Nama;
    String CHANNEL_ID = "my_channel_01";// The id of the channel.
    String getId, getNama;
    SessionManager sessionManager;
    CircleImageView profile_image;
    private NotificationManagerCompat notificationManager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);


        sessionManager = new SessionManager(requireActivity());
        sessionManager.checkLogin();
        notificationManager = NotificationManagerCompat.from(requireActivity());
        Nama = view.findViewById(R.id.nama);
        profile_image = view.findViewById(R.id.profile_image);
        HashMap<String, String> user = sessionManager.getUserDetail();
        getId = user.get(SessionManager.ID);
        getNama = user.get(SessionManager.NAME);
//        Notification = view.findViewById(R.id.ic_centang);

//        getImage = user.get(sessionManager.IMAGE);
//        Picasso.get().load(getImage).into(profile_image);
//        satu = view.findViewById(R.id.txt1);
//        dua = view.findViewById(R.id.txt2);
//        satu.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                showSimpleNotification();
//            }
//        });
//        dua.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                showBigTextNotification();
//            }
//        });
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
        AppController.getInstance().addToRequestQueue(strReq, tag_json_obj);
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
            AppController.getInstance().addToRequestQueue(strReq, tag_json_obj);
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
            Notification notification = new NotificationCompat.Builder(requireActivity())
                    //Title of the notification
                    .setContentTitle(getString(R.string.nf_simple_title))
                    //Content of the notification once opened
                    .setContentText(getString(R.string.nf_simple_message))
                    //This bit will show up in the notification area in devices that support that
                    .setTicker(getString(R.string.nf_simple_ticker))
                    //Icon that shows up in the notification area
                    .setSmallIcon(R.drawable.ic_absensi)
                    //Icon that shows up in the drawer
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
        Notification notification = new NotificationCompat.Builder(requireActivity())
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
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, request_url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {

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
                dotscount = viewPagerAdapter.getCount();
                dots = new ImageView[dotscount];

                for (int i = 0; i < dotscount; i++) {

                    dots[i] = new ImageView(getActivity());
                    dots[i].setImageDrawable(ContextCompat.getDrawable(requireActivity(), R.drawable.non_active_dot));

                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT);

                    params.setMargins(8, 0, 8, 0);

                    sliderDotspanel.addView(dots[i], params);

                }

                dots[0].setImageDrawable(ContextCompat.getDrawable(requireActivity(), R.drawable.active_dot));

            }
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

        assert getFragmentManager() != null;
        getFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, sharedElementFragment2)
                .addToBackStack(null)
                .addSharedElement(profile_image, getString(R.string.square_blue_name))
                .commit();
    }
}

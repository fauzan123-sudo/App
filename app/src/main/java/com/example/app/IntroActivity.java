package com.example.app;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;

import com.example.app.adapter.IntroViewPagerAdapter;
import com.example.app.model.ScreenItem;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

import static android.Manifest.permission.CAMERA;

public class IntroActivity extends AppCompatActivity {
    private long backPressedTime;
    private Toast backToast;
    private ViewPager screenPager;
    IntroViewPagerAdapter introViewPagerAdapter ;
    TabLayout tabIndicator;
    Button btnNext;
    int position = 0 ;
    Button btnGetStarted;
    Animation btnAnim ;
    TextView tvSkip;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        int currentApiVersion = Build.VERSION.SDK_INT;
        if (currentApiVersion >= Build.VERSION_CODES.M) {
            if (checkPermission()) {
                Log.e("SCAN QR CODE", "Permission already granted!");
            } else {
                requestPermission();
            }
        }
        if (restorePrefData()) {

            Intent login = new Intent(getApplicationContext(),Login.class );
            startActivity(login);
            finish();

        }

        setContentView(R.layout.activity_intro);

        // ini views
        btnNext         = findViewById(R.id.btn_next);
        btnGetStarted   = findViewById(R.id.btn_get_started);
        tabIndicator    = findViewById(R.id.tab_indicator);
        btnAnim         = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.button_animation);
        tvSkip          = findViewById(R.id.tv_skip);

        // fill list screen
        final List<ScreenItem> mList = new ArrayList<>();
        mList.add(new ScreenItem("Aplikasi Data Pegawai",
                "Selamat datang di aplikasi data pegawai, yuk nikmati berbagai fitur yang akan membantumu",
                R.drawable.intro_perkenalan));
        mList.add(new ScreenItem("Absensi Berbasis Scan",
                "Nikmati fitur absensi dengan perangkat anda, sekarang absensi hanya dengan scan, anda juga dapat mengajukkan izin dari mana saja dan kapan saja dengan menyertakan alasan",
                R.drawable.intro_qr));
        mList.add(new ScreenItem("Fitur Berita Acara",
                "Informasi berita acara kini telah hadir, dapatkan informasi terbaru anda jangan sampai ketinggalan ",
                R.drawable.intro_berita_acara));


        // setup viewpager
        screenPager             =findViewById(R.id.screen_viewpager);
        introViewPagerAdapter   = new IntroViewPagerAdapter(this,mList);
        screenPager.setAdapter(introViewPagerAdapter);

        // setup tablayout with viewpager

        tabIndicator.setupWithViewPager(screenPager);

        btnNext.setOnClickListener(v -> {

            position = screenPager.getCurrentItem();
            if (position < mList.size()) {
                position++;
                screenPager.setCurrentItem(position);
            }
            if (position == mList.size()-1) { // when we rech to the last screen
                // TODO : show the GETSTARTED Button and hide the indicator and the next button
                loaddLastScreen();
            }
        });

        // tablayout add change listener
        tabIndicator.addOnTabSelectedListener(new TabLayout.BaseOnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {

                if (tab.getPosition() == mList.size()-1) {

                    loaddLastScreen();

                }
            }
            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }
            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });

        // Get Started button click listener

        btnGetStarted.setOnClickListener(v -> {
            //open Login activity
            Intent login = new Intent(getApplicationContext(),Login.class);
            startActivity(login);
            savePrefsData();
            finish();



        });

        // skip button click listener
        tvSkip.setOnClickListener(v -> screenPager.setCurrentItem(mList.size()));

    }

    private boolean checkPermission() {
        return (ContextCompat.checkSelfPermission(this,
                CAMERA) == PackageManager.PERMISSION_GRANTED);
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(this,
                new String[]{CAMERA}, 200);
    }


    private boolean restorePrefData() {
        SharedPreferences pref = getApplicationContext().getSharedPreferences("mYpref3",MODE_PRIVATE);
        return pref.getBoolean("isIntroOpnend3",false);
    }

    private void savePrefsData() {
        SharedPreferences pref = getApplicationContext().getSharedPreferences("mYpref3",MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean("isIntroOpnend3",true);
        editor.apply();
    }

    private void loaddLastScreen() {
        btnNext.setVisibility(View.INVISIBLE);
        btnGetStarted.setVisibility(View.VISIBLE);
        tvSkip.setVisibility(View.INVISIBLE);
        tabIndicator.setVisibility(View.INVISIBLE);

        // setup animation
        btnGetStarted.setAnimation(btnAnim);

    }

    @Override
    public void onBackPressed() {
        if (backPressedTime + 2000 > System.currentTimeMillis()) {
            backToast.cancel();
            super.onBackPressed();
            return;
        } else {
            backToast = Toast.makeText(getBaseContext(), "tekan lagi untuk keluar", Toast.LENGTH_SHORT);
            backToast.show();
        }
        backPressedTime = System.currentTimeMillis();
    }
}
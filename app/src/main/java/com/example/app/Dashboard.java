package com.example.app;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.transition.ChangeBounds;
import androidx.transition.Fade;

import com.etebarian.meowbottomnavigation.MeowBottomNavigation;
import com.example.app.fragment.Absensi;
import com.example.app.fragment.BeritaAcara;
import com.example.app.fragment.Gaji;
import com.example.app.fragment.Home;
import com.example.app.fragment.Profile;
import com.example.app.fragment.Scan;
import com.example.app.fragment.Scanning;
import com.example.app.helper.InternetCheckService;
import com.example.app.helper.SessionManager;
import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.example.app.Login.TAG_ID;
import static com.example.app.helper.Constans.CHANNEL_DESC;
import static com.example.app.helper.Constans.CHANNEL_ID;
import static com.example.app.helper.Constans.CHANNEL_NAME;
import static com.example.app.helper.Constans.urlImagePegawai;

public class Dashboard extends AppCompatActivity {
    public String TAG = "Dashboard";
    ConnectivityManager conMgr;
    private static FloatingActionButton Fab;
    private static BottomNavigationView bottomNav;
    private static BottomAppBar bottomAppBar;
    CircleImageView profile_image;
    MeowBottomNavigation bottomNavigation;
    BroadcastReceiver broadcastReceiver = null;
    SessionManager sessionManager;
    String id, getImage, getName;
    SharedPreferences sharedpreferences;
    private long backPressedTime;
    private Toast backToast;
    ImageView berita_acara;
    TextView Nama;
    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        setContentView(R.layout.activity_dashboard);

        sharedpreferences       = getSharedPreferences(Login.my_shared_preferences, Context.MODE_PRIVATE);
        sessionManager          = new SessionManager(Dashboard.this);
        sessionManager.checkLogin();

//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//            Window w = getWindow(); // in Activity's onCreate() for instance
//            w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
//        }
        Fab = findViewById(R.id.fab);
        Fab.setOnClickListener(view -> {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                    new Scanning()).commit();
//            BottomNavigationView mBottomNavigationView = findViewById(R.id.bottomNavigationView);

            bottomNav.setSelectedItemId(R.id.scan);
        });
        broadcastReceiver = new InternetCheckService();
        checkInternet();
        bottomAppBar = findViewById(R.id.bottomAppBar);
        bottomNav    = findViewById(R.id.bottomNavigationView);
        bottomNav.setOnNavigationItemSelectedListener(navListener);
        bottomNav.setBackground(null);
        bottomNav.getMenu().getItem(2).setEnabled(false);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        profile_image           = toolbar.findViewById(R.id.profile_image);
        berita_acara            = toolbar.findViewById(R.id.beritaAcara);
        Nama                    = toolbar.findViewById(R.id.namas);
        berita_acara.setOnClickListener(view -> FunBeritaAcara());
        HashMap<String, String> user = sessionManager.getUserDetail();
        getImage                = user.get(SessionManager.IMAGE);
        getName                 = user.get(SessionManager.NAME);

        Nama.setText(getName);

        Picasso.with(Dashboard.this)
                .load(urlImagePegawai + getImage)
                .into(profile_image);
        profile_image.setOnClickListener(view1 -> Toast.makeText(this, "Hi..", Toast.LENGTH_SHORT).show());



        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                    new Home()).commit();
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription(CHANNEL_DESC);
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }
        conMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        {
            if (conMgr.getActiveNetworkInfo() != null
                    && conMgr.getActiveNetworkInfo().isAvailable()
                    && conMgr.getActiveNetworkInfo().isConnected()) {
                Log.d(TAG, "onCreate: ");
            } else {
                Toast.makeText(getApplicationContext(), "No Internet Connection", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(Dashboard.this, NoConnection.class);
                startActivity(intent);
            }
        }
        sessionManager = new SessionManager(this);
        sessionManager.checkLogin();

//        Logout = findViewById(R.id.logout);

//        bottomNavigation = findViewById(R.id.bottomNavigation);
//        bottomNavigation.add(new MeowBottomNavigation.Model(1, R.drawable.ic_ring));
//        bottomNavigation.add(new MeowBottomNavigation.Model(2, R.drawable.ic_readme));
//        bottomNavigation.add(new MeowBottomNavigation.Model(3, R.drawable.ic_qr));
//        bottomNavigation.add(new MeowBottomNavigation.Model(4, R.drawable.ic_dollar));
//        bottomNavigation.add(new MeowBottomNavigation.Model(5, R.drawable.ic_history));

        sharedpreferences = getSharedPreferences(Login.my_shared_preferences, Context.MODE_PRIVATE);
        id = getIntent().getStringExtra(TAG_ID);
//        bottomNavigation.show(1, true);
//        replace(new Home());
//        bottomNavigation.setOnClickMenuListener(model -> {
//            switch (model.getId()) {
//                case 1:
//                    replace(new Home());
//                    break;
//
//                case 2:
//                    replace(new Absensi());
//                    break;
//
//                case 3:
//                    replace(new Scanning());
//                    break;
//
//                case 4:
//                    replace(new Gaji());
//                    break;
//                case 5:
//                    replace(new BeritaAcara());
//                    break;
//            }
//            return null;
//        });

//        Logout.setOnClickListener(view -> Keluar());
    }

    private void FunBeritaAcara() {
        BeritaAcara fragment2 = new BeritaAcara();

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


        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment2)
                .commit();
    }


    public  void LoginOut() {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                    Dashboard.this);

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

                        Intent intent = new Intent(Dashboard.this, Login.class);
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

    private BottomNavigationView.OnNavigationItemSelectedListener navListener =
            item -> {
                Fragment selectedFragment = null;

                switch (item.getItemId()) {
                    case R.id.home:
                        selectedFragment = new Home();
                        break;
                    case R.id.absen:
                        selectedFragment = new Absensi();
                        break;
                    case R.id.scan:
                        selectedFragment = new Scanning();
                        break;
                    case R.id.gaji:
                        selectedFragment = new Gaji();
                        break;
                    case R.id.profile:
                        selectedFragment = new Profile();
                        break;
                }

                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        selectedFragment).commit();

                return true;
            };

    private void checkInternet() {
        registerReceiver(broadcastReceiver,
                new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
    }

    public static void hideBottomNav(){
        bottomAppBar.setVisibility(View.GONE);
        Fab.setVisibility(View.GONE);
        bottomNav.setVisibility(View.GONE);
    }

    public static void showBottomNav(){
        bottomAppBar.setVisibility(View.VISIBLE);
        Fab.setVisibility(View.VISIBLE);
        bottomNav.setVisibility(View.VISIBLE);
    }

//    @Override
//    protected void onPause() {
//        super.onPause();
//        unregisterReceiver(broadcastReceiver);
//    }

    private void Keluar() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                this);

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

                    Intent intent = new Intent(Dashboard.this, Login.class);
                    finish();
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

    private void replace (Fragment fragment){
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_container, fragment);
            transaction.commit();
        }

    @Override
    public void onBackPressed() {
//        Home home = new Home();
//        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
//        transaction.replace(R.id.fragment_container, home);
//        transaction.addToBackStack(null);
//        transaction.commit();
//        if (bottomNavigation.isShowing(1)){
//
//            if (backPressedTime + 3000 > System.currentTimeMillis()) {
//                backToast.cancel();
//                super.onBackPressed();
//                return;
//            } else {
//                dialog();
//
//            }
//            backPressedTime = System.currentTimeMillis();
//        }else{
//
//        }
//
//        backPressedTime = System.currentTimeMillis();
        BottomNavigationView mBottomNavigationView = findViewById(R.id.bottomNavigationView);
        if (mBottomNavigationView.getSelectedItemId() == R.id.home){
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                    this);

            // set title dialog
            alertDialogBuilder.setTitle("Konfirmasi");

            // set pesan dari dialog
            alertDialogBuilder
                    .setMessage("Klik Ya untuk keluar!")
                    .setCancelable(false)
                    .setPositiveButton("Ya", (dialog, id) -> {
                        // jika tombol diklik, maka akan menutup activity ini
                        Dashboard.this.finish();
                        super.onBackPressed();
                    })
                    .setNegativeButton("Tidak", (dialog, id) -> {
                        // jika tombol ini diklik, akan menutup dialog
                        // dan tidak terjadi apa2
                        dialog.cancel();
                    });

            // membuat alert dialog dari builder
            AlertDialog alertDialog = alertDialogBuilder.create();

            // menampilkan alert dialog
            alertDialog.show();

        }else{
            bottomNav.setSelectedItemId(R.id.home);
        }
    }

    private void dialog() {

    }


}

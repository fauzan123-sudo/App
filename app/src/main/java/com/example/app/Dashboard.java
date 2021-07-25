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
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.etebarian.meowbottomnavigation.MeowBottomNavigation;
import com.example.app.fragment.Absensi;
import com.example.app.fragment.BeritaAcara;
import com.example.app.fragment.Gaji;
import com.example.app.fragment.Home;
import com.example.app.fragment.Scan;
import com.example.app.helper.InternetCheckService;
import com.example.app.helper.SessionManager;

import static com.example.app.Login.TAG_ID;
import static com.example.app.helper.Constans.CHANNEL_DESC;
import static com.example.app.helper.Constans.CHANNEL_ID;
import static com.example.app.helper.Constans.CHANNEL_NAME;

public class Dashboard extends AppCompatActivity {
    public String TAG = "Dashboard";
    ConnectivityManager conMgr;
    MeowBottomNavigation bottomNavigation;
    ImageView Logout;
    TextView idnya;
    BroadcastReceiver broadcastReceiver = null;
    SessionManager sessionManager;
    String id;
    SharedPreferences sharedpreferences;
    private long backPressedTime;
    private Toast backToast;
    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        setContentView(R.layout.activity_dashboard);

        broadcastReceiver = new InternetCheckService();
        checkInternet();

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
//        Pie pie = AnyChart.pie();
//
//        List<DataEntry> data = new ArrayList<>();
//        data.add(new ValueDataEntry("John", 10000));
//        data.add(new ValueDataEntry("Jake", 12000));
//        data.add(new ValueDataEntry("Peter", 18000));
//
//        AnyChartView anyChartView = (AnyChartView) findViewById(R.id.any_chart_view);
//        anyChartView.setChart(pie);

        sessionManager = new SessionManager(this);
        sessionManager.checkLogin();

        Logout = findViewById(R.id.logout);

        bottomNavigation = findViewById(R.id.bottomNavigation);
        bottomNavigation.add(new MeowBottomNavigation.Model(1, R.drawable.ic_ring));
        bottomNavigation.add(new MeowBottomNavigation.Model(2, R.drawable.ic_readme));
        bottomNavigation.add(new MeowBottomNavigation.Model(3, R.drawable.ic_qr));
        bottomNavigation.add(new MeowBottomNavigation.Model(4, R.drawable.ic_dollar));
        bottomNavigation.add(new MeowBottomNavigation.Model(5, R.drawable.ic_history));

        bottomNavigation.setCount(5, "2");



        sharedpreferences = getSharedPreferences(Login.my_shared_preferences, Context.MODE_PRIVATE);
        id = getIntent().getStringExtra(TAG_ID);
        idnya = findViewById(R.id.id);

        idnya.setText("ID : " + id);

        bottomNavigation.show(1, true);
        replace(new Home());
        bottomNavigation.setOnClickMenuListener(model -> {
            switch (model.getId()) {
                case 1:
                    replace(new Home());
                    break;
                    
                case 2:
                    replace(new Absensi());
                    break;

                case 3:
                    replace(new Scan());
                    break;

                case 4:
                    replace(new Gaji());
                    break;
                case 5:
                    replace(new BeritaAcara());
                    break;
            }
            return null;
        });

        Logout.setOnClickListener(view -> {
            Keluar();
//                boolean permission;
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//                    permission = Settings.System.canWrite(Dashboard.this);
//                } else {
//                    permission = ContextCompat.checkSelfPermission(Dashboard.this, Manifest.permission.WRITE_SETTINGS) == PackageManager.PERMISSION_GRANTED;
//                }
//                if (!permission) {
//                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
//                        Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS);
//                        intent.setData(Uri.parse("package:" + getPackageName()));
//                        startActivityForResult(intent, 200);
//
//                    }
//                    sessionManager.logout();
//                }
//
//                if (ActivityCompat.checkSelfPermission(Dashboard.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
//                    Log.v("Permission", Manifest.permission.WRITE_EXTERNAL_STORAGE+"  Permission is revoked");
//                    ActivityCompat.requestPermissions(Dashboard.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 200);
//                    sessionManager.logout();
//                    return;
//                }
        });
    }

    private void checkInternet() {
        registerReceiver(broadcastReceiver,
                new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(broadcastReceiver);
    }

    private void Keluar() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                this);

        // set title dialog
        alertDialogBuilder.setTitle("Keluar dari aplikasi?");

        // set pesan dari dialog
        alertDialogBuilder
                .setMessage("Klik Ya untuk keluar!")
                .setIcon(R.mipmap.ic_launcher)
                .setCancelable(false)
                .setPositiveButton("Ya", (dialog, id) -> {
                    // jika tombol diklik, maka akan menutup activity ini
                    SharedPreferences.Editor editor = sharedpreferences.edit();
                    editor.putBoolean(Login.session_status, false);
                    editor.putString(TAG_ID, null);
                    editor.apply();

                    Intent intent = new Intent(Dashboard.this, Login.class);
                    finish();
                    startActivity(intent);
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

    }

    private void replace (Fragment fragment){
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_container, fragment);
            transaction.commit();
        }

    @Override
    public void onBackPressed() {

        Home home = new Home();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, home);
        transaction.addToBackStack(null);
        transaction.commit();
        if (bottomNavigation.isShowing(1)){

            if (backPressedTime + 3000 > System.currentTimeMillis()) {
                backToast.cancel();
                super.onBackPressed();
                return;
            } else {
                dialog();

            }
            backPressedTime = System.currentTimeMillis();
        }else{
            bottomNavigation.show(1,true);
        }

        backPressedTime = System.currentTimeMillis();
    }

    private void dialog() {
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
    }



}

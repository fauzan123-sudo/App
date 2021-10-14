package com.example.app;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.example.app.helper.AppController;
import com.example.app.helper.Constans;
import com.example.app.helper.SessionManager;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.messaging.FirebaseMessaging;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static com.example.app.helper.Constans.TAG_JSON_OBJECT;
import static com.example.app.helper.Constans.URL_LOGIN;

public class Login extends AppCompatActivity {
    private static final String TAG_SUCCESS = "success";
    private ProgressBar loading;
    private long backPressedTime;
    private Toast backToast;
    Boolean session = false;
    String id, email;
    int success;
    final String TAG = Login.class.getSimpleName();
    public static final String my_shared_preferences = "my_shared_preferences1";
    public static final String session_status = "session_status1";
    ConnectivityManager conMgr;
    SessionManager sessionManager;
    public final static String TAG_ID       = "id";
    public final static String TAG_IMAGE    = "image";
    public final static String TAG_USERNAME = "username";
    public final static String TAG_JABATAN = "username";
    SharedPreferences sharedpreferences;
    TextInputEditText Username,Password;
    Button Login ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        setContentView(R.layout.activity_login);

        sessionManager = new SessionManager(this);
        Username = findViewById(R.id.username);
        Password = findViewById(R.id.password);
        Login    = findViewById(R.id.login);
        loading  = findViewById(R.id.loading);

        conMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        {
            if (conMgr.getActiveNetworkInfo() != null
                    && conMgr.getActiveNetworkInfo().isAvailable()
                    && conMgr.getActiveNetworkInfo().isConnected()) {
                Log.d(TAG, "onCreate: ");
            } else {
                Toast.makeText(getApplicationContext(), "No Internet Connection",
                        Toast.LENGTH_LONG).show();
            }
        }

// Cek session login jika TRUE maka langsung buka Beranda
        sharedpreferences = getSharedPreferences(my_shared_preferences, Context.MODE_PRIVATE);
        session           = sharedpreferences.getBoolean(session_status, false);
        id                = sharedpreferences.getString(TAG_ID, null);
        email             = sharedpreferences.getString(TAG_USERNAME, null);

        if (session) {
            Intent intent = new Intent(Login.this, Dashboard.class);
            intent.putExtra(TAG_ID, id);
            finish();
            startActivity(intent);
        }

        Login.setOnClickListener(view -> {
            String UserName = Username.getText().toString().trim();
            String PassWOrd = Password.getText().toString().trim();
            if (UserName.isEmpty()){
                Username.setError("harap isi username");
            }
            if (PassWOrd.isEmpty()){
                Password.setError("harap isi password");
            }
            if (!UserName.isEmpty() || !PassWOrd.isEmpty()) {
                if (conMgr.getActiveNetworkInfo() != null
                        && conMgr.getActiveNetworkInfo().isAvailable()
                        && conMgr.getActiveNetworkInfo().isConnected()) {
                    LoginUser(UserName, PassWOrd);
                } else {
                    Toast.makeText(Login.this, "tidak ada koneksi", Toast.LENGTH_SHORT).show();
                }
            }else{
                Toast.makeText(this, "Harap isi Username dan password dulu!!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void LoginUser(final String email, final String password) {
        loading.setVisibility(View.VISIBLE);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_LOGIN,
                response -> {
                    try {
                        JSONObject jObj = new JSONObject(response);
                        success         = jObj.getInt(TAG_SUCCESS);

                        if (success == 1) {
                            String name     = jObj.getString("nama").trim();
                            String email1   = jObj.getString("username").trim();
                            String id       = jObj.getString("id").trim();
                            String image    = jObj.getString("image").trim();
                            String jabatan  = jObj.getString("jabatan").trim();
                            sessionManager.createSession(name, email1, id, image, jabatan);

                            // menyimpan login ke session
                            SharedPreferences.Editor editor = sharedpreferences.edit();
                            editor.putBoolean(session_status, true);
                            editor.putString(TAG_ID, id);
                            editor.putString(TAG_IMAGE, image);
                            editor.putString(TAG_JABATAN, jabatan);
                            editor.apply();

                            Intent intent = new Intent(Login.this, Dashboard.class);
                            startActivity(intent);
                            finish();

                        } else {
                            Toast.makeText(Login.this, "periksa kembali username atau password anda", Toast.LENGTH_SHORT).show();
                        }
                        loading.setVisibility(View.GONE);

                    } catch (JSONException e) {
                        e.printStackTrace();
                        loading.setVisibility(View.GONE);
                        Toast.makeText(Login.this, "Error " + e.toString(), Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    loading.setVisibility(View.GONE);
                    Toast.makeText(Login.this, "Error " + error.toString(), Toast.LENGTH_SHORT).show();
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("username", email);
                params.put("password", password);
                return params;
            }
        };
        AppController.getInstance().addToRequestQueue(stringRequest, TAG_JSON_OBJECT);
    }

    @Override
    public void onBackPressed() {
        if (backPressedTime + 2000 > System.currentTimeMillis()) {
            backToast.cancel();
            super.onBackPressed();
            return;
        } else {
            backToast = Toast.makeText(getBaseContext(), "Press back again to exit", Toast.LENGTH_SHORT);
            backToast.show();
        }
        backPressedTime = System.currentTimeMillis();
    }
}
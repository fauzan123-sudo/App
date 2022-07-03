package com.example.app.fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.example.app.Dashboard;
import com.example.app.Login;
import com.example.app.R;
import com.example.app.helper.AppController;
import com.example.app.helper.Constans;
import com.example.app.helper.SessionManager;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.example.app.Login.TAG_ID;
import static com.example.app.helper.Constans.PROFILE;
import static com.example.app.helper.Constans.TAG_JSON_OBJECT;


public class Profile extends Fragment {
    private static final String TAG = Profile.class.getSimpleName(); //getting the info
    TextView name, email, nip,jabatan, tgl_lahir,tempat_lahir, telepon, alamat, back2;
    String getId;
    SessionManager sessionManager;
    SharedPreferences sharedpreferences;
    CircleImageView profile_image;
    private final String urlImagePegawai = Constans.urlImagePegawai;
    ImageView back1;
    Button KelolaAkun, Logout;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_profile, container, false);

        sharedpreferences       = requireActivity().getSharedPreferences(Login.my_shared_preferences, Context.MODE_PRIVATE);
        sessionManager = new SessionManager(requireActivity());
        sessionManager.checkLogin();
        HashMap<String, String> user = sessionManager.getUserDetail();
        getId = user.get(SessionManager.ID);

        KelolaAkun      = v.findViewById(R.id.kelolaAkun);
        profile_image   = v.findViewById(R.id.profile_image3);
        name            = v.findViewById(R.id.nama);
        email           = v.findViewById(R.id.email);
        nip             = v.findViewById(R.id.nip);
        jabatan         = v.findViewById(R.id.jabatan);
        tgl_lahir       = v.findViewById(R.id.tgl_lahir);
        tempat_lahir    = v.findViewById(R.id.tempat_lhr);
        telepon         = v.findViewById(R.id.no_tlp);
        alamat          = v.findViewById(R.id.alamat);
        back1           = v.findViewById(R.id.back1);
        back2           = v.findViewById(R.id.back2);
        Logout          = v.findViewById(R.id.logout);
        Logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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

                            Intent intent = new Intent(requireContext(), Login.class);
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
        });

        readProfile();
        back1.setOnClickListener(view -> kembali());
        back2.setOnClickListener(view -> kembali());
        KelolaAkun.setOnClickListener(view -> kelolaAkun());
        return  v;
    }

    private void kelolaAkun() {
        UbahSandi fragment2 = new UbahSandi();
        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment2)
                .commit();
    }

    private void kembali() {
       requireActivity().onBackPressed();
    }

    private void readProfile() {
            final ProgressDialog progressDialog = new ProgressDialog(getActivity());
            progressDialog.setMessage("Loading...");
            progressDialog.show();

            StringRequest stringRequest = new StringRequest(Request.Method.POST, PROFILE,
                    response -> {
                        progressDialog.dismiss();
                        Log.i(TAG, response);

                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String success = jsonObject.getString("success");
                            JSONArray jsonArray = jsonObject.getJSONArray("read");

                            if (success.equals("1")) {

                                for (int i = 0; i < jsonArray.length(); i++) {

                                    JSONObject object = jsonArray.getJSONObject(i);

                                    String strName          = object.getString("nama").trim();
                                    String strJabatan       = object.getString("jabatan").trim();
                                    String strEmail         = object.getString("email").trim();
                                    String strImage         = object.getString("image").trim();
                                    String strNip           = object.getString("nip").trim();
                                    String strTempatLahir   = object.getString("tempat_lahir").trim();
                                    String strTglLahir      = object.getString("tgl_lahir").trim();
                                    String strTelepon       = object.getString("no_tlp").trim();
                                    String strAlamat        = object.getString("alamat").trim();

                                    name.setText(strName);
                                    jabatan.setText(strJabatan);
                                    email.setText(strEmail);
                                    //display image from string url
                                        Picasso.with(getActivity())
                                                .load(urlImagePegawai + strImage)
                                                .into(profile_image);
                                    nip.setText(strNip);
                                    tempat_lahir.setText(strTempatLahir);
                                    tgl_lahir.setText(strTglLahir);
                                    telepon.setText(strTelepon);
                                    alamat.setText(strAlamat);

                                }

                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            progressDialog.dismiss();
                            Toast.makeText(getActivity(), "Error 1 " + e.toString(), Toast.LENGTH_LONG).show();
                        }

                    },
                    error -> {
                        progressDialog.dismiss();
                        Toast.makeText(getActivity(), "Error2 " + error.toString(), Toast.LENGTH_LONG).show();
                    }) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<>();
                    params.put("id_pegawai", getId);
                    return params;
                }
            };

            AppController.getInstance().addToRequestQueue(stringRequest, TAG_JSON_OBJECT);
    }

    @Override
    public void onResume() {
        ((AppCompatActivity)requireActivity()).getSupportActionBar().hide();
        super.onResume();
    }

    @Override
    public void onStop() {
        ((AppCompatActivity)requireActivity()).getSupportActionBar().show();
        super.onStop();
    }
}
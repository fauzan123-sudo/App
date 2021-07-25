package com.example.app.fragment;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
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


public class Profile extends Fragment {
    private static final String TAG = Profile.class.getSimpleName(); //getting the info
    TextView name, email, nip,jabatan, tgl_lahir,tempat_lahir, telepon, alamat, back2;
    private static String URL_READ = Constans.BaseUrl +"read.php";
    String getId;
    Fragment fragment;
    SessionManager sessionManager;
    CircleImageView profile_image;
    private String urlImagePegawai = Constans.urlImagePegawai;
    String tag_json_obj = "json_obj_req";
    ImageView back1;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_profile, container, false);

        sessionManager = new SessionManager(getActivity());
        sessionManager.checkLogin();
        HashMap<String, String> user = sessionManager.getUserDetail();
        getId = user.get(sessionManager.ID);

        profile_image   = v.findViewById(R.id.profile_image);
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

        readProfile();
        back1.setOnClickListener(view -> kembali(new Home()));
        back2.setOnClickListener(view -> kembali(new Home()));
        return  v;
    }


    private void kembali(Fragment fragment) {
        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();



    }

    private void readProfile() {
            final ProgressDialog progressDialog = new ProgressDialog(getActivity());
            progressDialog.setMessage("Loading...");
            progressDialog.show();

            StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_READ,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            progressDialog.dismiss();
                            Log.i(TAG, response.toString());

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

                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            progressDialog.dismiss();
                            Toast.makeText(getActivity(), "Error2 " + error.toString(), Toast.LENGTH_LONG).show();
                        }
                    }) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> params = new HashMap<>();
                    params.put("id_pegawai", getId);
                    return params;
                }
            };

            AppController.getInstance().addToRequestQueue(stringRequest, tag_json_obj);
    }
}
package com.example.app.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.transition.ChangeBounds;
import androidx.transition.Fade;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.example.app.R;
import com.example.app.helper.AppController;
import com.example.app.helper.SessionManager;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.example.app.helper.Constans.TAG_JSON_OBJECT;
import static com.example.app.helper.Constans.gaji;
import static com.example.app.helper.Constans.urlImagePegawai;

public class Gaji extends Fragment {
    private static final String TAG = Gaji.class.getSimpleName();
    TextView Gaji_Bersih, Nama, Jabatan, Potongan;
    SessionManager sessionManager;
    String getId, getNama, getImage, getJabatan;
    CircleImageView profile_image;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_gaji, container, false);
        sessionManager = new SessionManager(requireActivity());
        HashMap<String, String> user = sessionManager.getUserDetail();
        getId = user.get(SessionManager.ID);
        parseJSON();
        Nama        = rootView.findViewById(R.id.nama);
        Jabatan     = rootView.findViewById(R.id.jabatan);
        Potongan    = rootView.findViewById(R.id.tidak_hadir);
        Gaji_Bersih = rootView.findViewById(R.id.gaji);
        getNama     = user.get(SessionManager.NAME);
        getImage    = user.get(SessionManager.IMAGE);
        getJabatan  = user.get(SessionManager.JABATAN);
        getId = user.get(SessionManager.ID);
        profile_image       = rootView.findViewById(R.id.profile_image);
        profile_image.setOnClickListener(view -> ProfilPegawai());
        Nama.setText(getNama);
        Jabatan.setText(getJabatan);
        Picasso.with(requireActivity())
                .load(urlImagePegawai + getImage)
                .into(profile_image);
        return  rootView;
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
        Profile_Gaji sharedElementFragment2 = new Profile_Gaji();

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

    private void parseJSON() {

            StringRequest stringRequest = new StringRequest(Request.Method.POST, gaji,
                    response -> {
                        Log.i(TAG, response);
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            JSONArray jsonArray = jsonObject.getJSONArray("hasil");
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject hit     = jsonArray.getJSONObject(i);
                                String gaji_bersih = hit.getString("gaji_bersih");
//                                    String asuransi    = hit.getString("asuransi");
                                    String potongan    = hit.getString("potongan");

                                Gaji_Bersih.setText("Rp." + gaji_bersih);
                                Potongan.setText("Rp. "+ potongan );
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }, Throwable::printStackTrace){

                @Override
                protected Map<String, String> getParams() {
                    // Posting parameters ke post url
                    Map<String, String> params = new HashMap<>();
                    params.put("id_pegawai", getId);
                    return params;
                }};
            AppController.getInstance().addToRequestQueue(stringRequest, TAG_JSON_OBJECT);

    }
}
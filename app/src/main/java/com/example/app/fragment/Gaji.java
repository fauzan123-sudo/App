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
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.app.R;
import com.example.app.helper.AppController;
import com.example.app.helper.Constans;
import com.example.app.helper.SessionManager;
import com.example.app.model.Model_Gaji;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class Gaji extends Fragment {
    private static final String TAG = Gaji.class.getSimpleName();
    String tag_json_obj = "json_obj_req";
    private ArrayList<Model_Gaji> model_gajis;
    TextView Gaji_Bersih;
    SessionManager sessionManager;
    String getId;
    CircleImageView profile_image;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_gaji, container, false);
        sessionManager = new SessionManager(getActivity());
        HashMap<String, String> user = sessionManager.getUserDetail();
        getId = user.get(sessionManager.ID);
        parseJSON();
        Gaji_Bersih = rootView.findViewById(R.id.gaji);
        profile_image       = rootView.findViewById(R.id.profile_image);
        profile_image.setOnClickListener(view -> ProfilPegawai(false));
        return  rootView;
    }

    private void ProfilPegawai(boolean overlap) {
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
        sharedElementFragment2.setAllowEnterTransitionOverlap(overlap);
        sharedElementFragment2.setAllowReturnTransitionOverlap(overlap);
        sharedElementFragment2.setSharedElementEnterTransition(changeBoundsTransition);

        getFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, sharedElementFragment2)
                .addToBackStack(null)
                .addSharedElement(profile_image, getString(R.string.square_blue_name))
                .commit();
    }

    private void parseJSON() {
            String url = Constans.BaseUrl +"gaji.php";
            StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            Log.i(TAG, response.toString());
                            try {
                                JSONObject jsonObject = new JSONObject(response);
                                JSONArray jsonArray = jsonObject.getJSONArray("hasil");
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject hit     = jsonArray.getJSONObject(i);
                                    String gaji_bersih = hit.getString("gaji_pokok");
//                                    String asuransi    = hit.getString("asuransi");
//                                    String potongan    = hit.getString("potongan");

                                    Gaji_Bersih.setText(gaji_bersih);


                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }



                        }

                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    error.printStackTrace();
                }
            }){

                @Override
                protected Map<String, String> getParams() {
                    // Posting parameters ke post url
                    Map<String, String> params = new HashMap<>();
                    params.put("id_pegawai", getId);
                    return params;
                }};
            AppController.getInstance().addToRequestQueue(stringRequest, tag_json_obj);

    }
}
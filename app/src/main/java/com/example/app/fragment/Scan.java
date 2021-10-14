package com.example.app.fragment;

import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.app.R;
import com.example.app.helper.SessionManager;
import com.google.zxing.Result;

import org.jetbrains.annotations.NotNull;
import java.util.HashMap;
import me.dm7.barcodescanner.zxing.ZXingScannerView;
import static android.Manifest.permission.CAMERA;

public class Scan extends Fragment implements ZXingScannerView.ResultHandler {
    private ZXingScannerView mScannerView;
    String getId;
    SessionManager sessionManager;
    String HasilScan;
    private final String  TAG = Scan.class.getSimpleName();
@Override
public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    requireActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    sessionManager      = new SessionManager(requireActivity());
    HashMap<String, String> user = sessionManager.getUserDetail();
    getId = user.get(SessionManager.ID);

    int currentApiVersion = Build.VERSION.SDK_INT;
    if (currentApiVersion >= Build.VERSION_CODES.M) {
      if (checkPermission()) {
      Log.e("SCAN QR CODE", "Permission already granted!");
    } else {
      requestPermission();
      }
    }
    mScannerView = new ZXingScannerView(requireActivity());
    return mScannerView;
    }
    @Override
    public void onResume() {
    super.onResume();
    int currentapiVersion = Build.VERSION.SDK_INT;
    if (currentapiVersion >= Build.VERSION_CODES.M) {
      if (checkPermission()) {
        if (mScannerView == null) {
          mScannerView = new ZXingScannerView( getActivity() );
          requireActivity().setContentView( mScannerView );
        }
        mScannerView.setResultHandler( this );
        mScannerView.startCamera();
      } else {
         requestPermission();
      }
    }
    mScannerView.setResultHandler(this);
    mScannerView.startCamera();
    }

    private boolean checkPermission() {
      return (ContextCompat.checkSelfPermission(requireActivity(),
            CAMERA) == PackageManager.PERMISSION_GRANTED);
    }

    private void requestPermission() {
      ActivityCompat.requestPermissions(requireActivity(),
            new String[]{CAMERA}, 200);
    }

    @Override
    public void handleResult(Result rawResult) {
        HasilScan = String.valueOf(rawResult);

        Log.d(TAG, "handleResult: ");
        Bundle bundle = new Bundle();
        bundle.putString("key", String.valueOf(rawResult));

        Hasil_Scan fragment2 = new Hasil_Scan();
        fragment2.setArguments(bundle);

        requireActivity().getSupportFragmentManager()
           .beginTransaction()
           .replace(R.id.fragment_container, fragment2)
           .commit();
        Handler handler = new Handler();
        handler.postDelayed(() -> mScannerView.resumeCameraPreview(Scan.this), 2000);
    }

    @Override
    public void onPause() {
    super.onPause();
    mScannerView.stopCamera();
    }
}
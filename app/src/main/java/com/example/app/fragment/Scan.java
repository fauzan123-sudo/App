package com.example.app.fragment;

import android.content.pm.PackageManager;
import android.hardware.Camera;
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
import com.google.zxing.Result;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

import static android.Manifest.permission.CAMERA;

public class Scan extends Fragment implements ZXingScannerView.ResultHandler {
        private ZXingScannerView mScannerView;
        private static final int REQUEST_CAMERA = 1;
        private ZXingScannerView scannerView;
        private static int camId = Camera.CameraInfo.CAMERA_FACING_BACK;
@Override
public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        int currentApiVersion = Build.VERSION.SDK_INT;
        if (currentApiVersion >= Build.VERSION_CODES.M) {
        if (checkPermission()) {
        //Toast.makeText(getApplicationContext(), "Permission already granted!", Toast.LENGTH_LONG).show();
        Log.e("SCAN QR CODE", "Permission already granted!");
        } else {
        requestPermission();
        }
        }
        mScannerView = new ZXingScannerView(getActivity());
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
        getActivity().setContentView( mScannerView );
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
        return (ContextCompat.checkSelfPermission(getActivity(), CAMERA) == PackageManager.PERMISSION_GRANTED);
        }

        private void requestPermission() {
        ActivityCompat.requestPermissions(getActivity(), new String[]{CAMERA}, 200);
        }

        @Override
        public void handleResult(Result rawResult) {
                Bundle bundle = new Bundle();
                bundle.putString("key", String.valueOf(rawResult)); // Put anything what you want

                Hasil_Scan fragment2 = new Hasil_Scan();
                fragment2.setArguments(bundle);

                getFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragment_container, fragment2)
                        .commit();
//        Toast.makeText(getActivity(), "Contents = " + rawResult.getText() +
//        ", Format = " + rawResult.getBarcodeFormat().toString(), Toast.LENGTH_SHORT).show();


        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
        @Override
        public void run() {
        mScannerView.resumeCameraPreview(Scan.this);

        }
        }, 2000);
        }

        @Override
        public void onPause() {
        super.onPause();
        mScannerView.stopCamera();
        }
}
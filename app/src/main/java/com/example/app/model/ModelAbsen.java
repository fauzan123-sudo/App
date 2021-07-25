package com.example.app.model;

public class ModelAbsen {
    private String mTanggal;
    private String mJam;

    public ModelAbsen(String mTanggal, String mJam) {
        this.mTanggal = mTanggal;
        this.mJam = mJam;
    }

    public String getmTanggal() {
        return mTanggal;
    }

    public void setmTanggal(String mTanggal) {
        this.mTanggal = mTanggal;
    }

    public String getmJam() {
        return mJam;
    }

    public void setmJam(String mJam) {
        this.mJam = mJam;
    }
}

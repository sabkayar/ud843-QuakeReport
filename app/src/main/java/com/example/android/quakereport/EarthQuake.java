package com.example.android.quakereport;

public class EarthQuake {
    private double mMagnitudeOfEarthQuake;
    private String mPlaceOfEarthQuake;
    private long mDateOfEarthQuake;
    private String mEarthQuakeUrl;

    public EarthQuake(double magnitudeOfEarthQuake, String placeOfEarthQuake, long dateOfEarthQuake, String url) {
        mMagnitudeOfEarthQuake = magnitudeOfEarthQuake;
        mPlaceOfEarthQuake = placeOfEarthQuake;
        mDateOfEarthQuake = dateOfEarthQuake;
        mEarthQuakeUrl=url;
    }

    public double getMagnitudeOfEarthQuake() {
        return mMagnitudeOfEarthQuake;
    }

    public String getPlaceOfEarthQuake() {
        return mPlaceOfEarthQuake;
    }

    public long getDateOfEarthQuake() {
        return mDateOfEarthQuake;
    }

    public String getEarthQuakeUrl() {
        return mEarthQuakeUrl;
    }
}

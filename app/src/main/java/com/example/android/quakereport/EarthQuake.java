package com.example.android.quakereport;

public class EarthQuake {

    private double mMagnitude;
    private String mLocation;
    private long mTimeInMilliseconds;
    private String mUrl;

    public EarthQuake(double magnitudeOfEarthQuake, String locationOfEarthQuake, long timeInMilliseconds, String url){

        mMagnitude = magnitudeOfEarthQuake;
        mLocation = locationOfEarthQuake;
        mTimeInMilliseconds = timeInMilliseconds;
        mUrl = url;
    }

    public EarthQuake(){

    }

    public double getMagnitude(){
        return mMagnitude;
    }

    public String getLocation(){
        return mLocation;
    }


    public long getTime(){
        return mTimeInMilliseconds;
    }

    public String getUrl(){ return mUrl;}
}

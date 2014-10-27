package com.irisa.unpourcent.location;

import java.io.Serializable;

import android.location.Location;

public class LocationStruct implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private Double mLatitude;
	private Double mLongitude;
	
	public LocationStruct() {
		mLatitude = 0.0;
		mLongitude = 0.0;
	}
	
	public LocationStruct( LocationStruct loc ) {
		mLatitude = loc.getLatitude();
		mLongitude = loc.getLongitude();
	}
	
	public LocationStruct( Location loc ) {
		mLatitude = loc.getLatitude();
		mLongitude = loc.getLongitude();
	}
	
	public Double getLatitude() {
		return mLatitude;
	}
	public void setLatitude(Double l) {
		mLatitude = l;
	}
	public Double getLongitude() {
		return mLongitude;
	}
	public void setLongitude(Double l) {
		mLongitude = l;
	}
}


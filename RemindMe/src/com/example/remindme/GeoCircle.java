package com.example.remindme;

import com.google.android.maps.GeoPoint;

public class GeoCircle extends GeoPoint{
	private static final int DEFAULT_RADIUS = 500;
	private int radius;
	
	public GeoCircle(int lat, int lon){
		super(lat, lon);
		setRadius(DEFAULT_RADIUS);
	}
	
	public GeoCircle(int lat, int lon, int radius){
		super(lat, lon);
		setRadius(radius);
	}
	
	public GeoCircle(GeoPoint point, int radius) {
		super(point.getLatitudeE6(), point.getLongitudeE6());
		this.radius = radius;
	}

	public int getRadius() {
		return radius;
	}
	
	public void setRadius(int radius) {
		this.radius = radius;
	}

}

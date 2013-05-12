package com.example.remindme;

import android.location.Location;

import com.google.android.maps.GeoPoint;

public class GeoCircle extends GeoPoint{
	private static final int DEFAULT_RADIUS = 50;
	private int radius;
	private boolean notifyOnEnter = true;
	private boolean notifyOnExit = false;
	
	

	/**
	 * 
	 * @param lat in microdegrees ex. 59473256
	 * @param lon in microdegrees ex. 18123456
	 */
	public GeoCircle(int lat, int lon){
		super(lat, lon);
		setRadius(DEFAULT_RADIUS);
	}
	
	/**
	 * 
	 * @param lat in microdegrees ex. 59473256
	 * @param lon in microdegrees ex. 18123456
	 * @param radius radius in meters
	 */
	public GeoCircle(int lat, int lon, int radius){
		super(lat, lon);
		setRadius(radius);
	}
	
	/**
	 * 
	 * @param lat in degrees ex. 59,473256
	 * @param lon in degrees ex. 18,123456
	 * @param radius in meters
	 */
	public GeoCircle(double lat, double lon, int radius){
		super((int) (lat * 1E6), (int) (lon* 1E6));
		setRadius(radius);
	}
	
	public GeoCircle(GeoPoint point, int radius) {
		super(point.getLatitudeE6(), point.getLongitudeE6());
		setRadius(radius);
	}
	
	public GeoCircle(Location location) {
		super((int) (location.getLatitude() * 1E6), (int) (location.getLongitude() * 1E6));
		setRadius(DEFAULT_RADIUS);
	}
	public GeoCircle(Location location, int radius) {
		super((int) (location.getLatitude() * 1E6), (int) (location.getLongitude() * 1E6));
		setRadius(radius);
	}

	public boolean isNotifyOnEnter() {
		return notifyOnEnter;
	}

	public void setNotifyOnEnter(boolean notifyOnEnter) {
		this.notifyOnEnter = notifyOnEnter;
	}

	public boolean isNotifyOnExit() {
		return notifyOnExit;
	}

	public void setNotifyOnExit(boolean notifyOnExit) {
		this.notifyOnExit = notifyOnExit;
	}

	public int getRadius() {
		return radius;
	}
	
	public void setRadius(int radius) {
		this.radius = radius;
	}

	/**
	 * Determines whether the provided geoPoint is inside this geoCircle
	 * @param newGeoPoint
	 * @return true if point is inside
	 */
	public boolean isPointInside(GeoPoint newGeoPoint) {
		 return getEuclideanDistance(newGeoPoint) < this.radius;
	}

	/**
	 * Gets the distance from the edge of this GeoCircle to the edge of provided GeoCircle
	 * @param geoCircle the GeoCircle to measure distance to 
	 * @return distance in meters
	 */
	public int distanceTo(GeoCircle geoCircle) {
		int distance = getEuclideanDistance(geoCircle) - geoCircle.getRadius() - this.getRadius();
		return distance < 0 ? 0 : distance;
	}

	/**
	 * Gets the distance from the middle of this GeoCircle to the middle of provided GeoCircle
	 * @param geoCircle the GeoCircle to measure distance to 
	 * @return distance in meters
	 */
	private int getEuclideanDistance(GeoCircle geoCircle) {
		int x1 =this.getLatitudeE6();
		int y1 =this.getLongitudeE6();
		int x2 =geoCircle.getLatitudeE6();
		int y2 =geoCircle.getLongitudeE6();
		double distance = Math.sqrt((x2-x1)*(x2-x1)+(y2-y1)*(y2-y1));
		return microDegreesToMeters(distance);
	}
	
	private int microDegreesToMeters(double distance) {
		// TODO Auto-generated method stub
		return (int) (distance * 0.111325D);
	}

	/**
	 * Gets the distance from center point in GeoCircle to GeoPoint
	 * @param geoPoint 
	 * @return distance in meters
	 */
	private int getEuclideanDistance(GeoPoint geoPoint) {
		int x1 =this.getLatitudeE6();
		int y1 =this.getLongitudeE6();
		int x2 =geoPoint.getLatitudeE6();
		int y2 =geoPoint.getLongitudeE6();
		double distance = Math.sqrt((x2-x1)*(x2-x1)+(y2-y1)*(y2-y1));
		return (int)distance;
	}

	public double getLatitude() {
		return (double)( (double)getLatitudeE6() / 1E6 );
	}

	public double getLongitude() {
		return (double)( (double)getLongitudeE6() / 1E6 );
	}
	
//	/**
//	 * Defines what happens when this geoCircle is entered
//	 */
//	public void onEnter(Context packageContext) {
//		if (notifyOnEnter){
//			AlarmManager.alarm(this, packageContext);
//		}
//	}
	
//	/**
//	 * Defines what happens when this geoCircle is exited
//	 */
//	public void onExit(Context packageContext) {
//		if (notifyOnExit){
//			AlarmManager.alarm(this, packageContext);
//		}
//	}

}

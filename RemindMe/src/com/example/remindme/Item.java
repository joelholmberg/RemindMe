package com.example.remindme;

import java.util.Date;
import java.util.HashSet;

import android.view.View.OnClickListener;

import com.google.android.maps.GeoPoint;

/*
 * Items for RemindMe app. Each Item is a to-do with alarm and locations where you want too be reminded.
 */
public interface Item {
	int getId();
	boolean addGeoCircle(GeoCircle gc);
	boolean addAlarm(Date alarm);
	String getMessage();
	void setMessage(String message);
	HashSet<GeoCircle> getGeoCircles();
	HashSet<Date> getAlarms();
	String toString();
	OnClickListener getListener();
	
}

package com.example.remindme;

import java.util.Date;
import java.util.HashSet;

import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;


public class SimpleItem implements Item{
	
	private final int id;
	private String message;
	private HashSet<Date> alarms;
	private HashSet<GeoCircle> geoCircles;
	private OnClickListener editListener; //listener for edit button
	
	public SimpleItem(String message, int id){
		this.id = id;
		this.message = message;
		alarms = new HashSet<Date>();
		geoCircles = new HashSet<GeoCircle>();
		editListener = new OnClickListener(){
	        //@Override
	        public void onClick(View v) {
	            // the default action for all lines
	            Log.v("Item", "onClick() running");
	            
	        }
	    };
	}

	public int getId() {
		return id;
	}

	public HashSet<GeoCircle> getGeoCircles() {
		return geoCircles;
	}

	public HashSet<Date> getAlarms() {
		return alarms;
	}

	public boolean addGeoCircle(GeoCircle gc) {
			return geoCircles.add(gc);
	}

	public boolean addAlarm(Date alarm) {
		return alarms.add(alarm);
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
		
	}

	public OnClickListener getListener() {
		return editListener;
	}

}

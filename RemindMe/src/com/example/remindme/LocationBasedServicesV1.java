package com.example.remindme;

import java.util.List;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;
import com.google.android.maps.Projection;

public class LocationBasedServicesV1 extends MapActivity {

	private MapView mapView;
	private LocationManager locManager;
	private LocationListener locListener;
	private Model model;


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		//set this mapActivity to be the current view
		setContentView(R.layout.activity_location_based_services_v1);
		
		//get a reference to the model
		model = Model.getInstance(this);

		//Create map and location manager
		initMap();
		initLocationManager();
		
		
	}

	@Override
	public void onStart(){
		super.onStart();
		//move to current position when entering this view (create or resume)
		moveToMyLocation(locManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)!=null
				?locManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
				:locManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER));
	}
	
	@Override
	public void onResume(){
		super.onResume();
		//let model read from file and do some updating on the overlays to make sure they show on resume
		model.update();
	}
	
	/**
	 * Initializes the MyLocationOverlay and adds it to the overlays of the map
	 */
	private void initLocationManager() {
		locManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

		locListener = new LocationListener() {

			public void onLocationChanged(Location newLocation) {
				
			}

			public void onProviderDisabled(String arg0) {
			}

			public void onProviderEnabled(String arg0) {
			}

			public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
			}
		};
		locManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0,
				locListener);
		locManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0,
				locListener);
		
		
	}

	/**
	 * Initialize the map and adds the zoom controls to the LinearLayout.
	 */
	private void initMap() {
		mapView = (MapView) findViewById(R.id.mymap);
		mapView.setBuiltInZoomControls(true);
		mapView.displayZoomControls(true);

		//Get all overlays from mapView
		List<Overlay> overlays = mapView.getOverlays();

		// first remove old overlays
		overlays.clear();

		// initialize icon
		Drawable icon = getResources().getDrawable(R.drawable.pin);
		icon.setBounds(0, 0, icon.getIntrinsicWidth(), icon
				.getIntrinsicHeight());

		// create new overlay
		MyItemizedOverlay overlay = new MyItemizedOverlay(icon);
		
		//Add newly created overlay to overlays list
		overlays.add(overlay);

		// redraw map
		mapView.postInvalidate();

	}


	/**
	 * This method will be called whenever a change of the current position
	 * is submitted via the GPS.
	 * @param newLocation
	 */
	protected void createAndShowMyItemizedOverlay(Location newLocation) {
		List<Overlay> overlays = mapView.getOverlays();

		// first remove old overlay
		//overlays.clear();

		// transform the current location to a geopoint
		GeoPoint geopoint = new GeoPoint(
				(int) (newLocation.getLatitude() * 1E6), (int) (newLocation
						.getLongitude() * 1E6));

		// initialize icon
		Drawable icon = getResources().getDrawable(R.drawable.pin);
		icon.setBounds(0, 0, icon.getIntrinsicWidth(), icon
				.getIntrinsicHeight());

		// create my overlay and show it
		MyItemizedOverlay overlay = new MyItemizedOverlay(icon);
		OverlayItem item = new OverlayItem(geopoint, "My Location", null);
		overlay.addItem(item);
		overlays.add(overlay);

		// move to location
		mapView.getController().animateTo(geopoint);
		
		//ask model to update its observers
		model.update();

		// redraw map
		mapView.postInvalidate();
	}
	
	/**
	 * This method will be called whenever a change of the current position
	 * is submitted via the GPS.
	 * @param newLocation
	 */
	protected void createAndShowMyLocationOverlay(Location newLocation) {
		
		List<Overlay> overlays = mapView.getOverlays();

		// first remove old overlay
		//overlays.clear();

		// transform the current location to a geopoint
		GeoPoint geopoint = new GeoPoint(
				(int) (newLocation.getLatitude() * 1E6), (int) (newLocation
						.getLongitude() * 1E6));

		// initialize icon
		Drawable icon = getResources().getDrawable(R.drawable.pin);
		icon.setBounds(0, 0, icon.getIntrinsicWidth(), icon
				.getIntrinsicHeight());

		// create my overlay and show it
		MyItemizedOverlay overlay = new MyItemizedOverlay(icon);
		OverlayItem item = new OverlayItem(geopoint, "My Location", null);
		overlay.addItem(item);
		overlays.add(overlay);

		// move to location
		mapView.getController().animateTo(geopoint);
		
		//ask model to update its observers
		model.update();

		// redraw map
		mapView.postInvalidate();
	}
	
	public void moveToMyLocation(Location newLocation){
		GeoPoint geopoint = new GeoPoint((int) (newLocation.getLatitude() * 1E6), (int) (newLocation.getLongitude() * 1E6));
		mapView.getController().animateTo(geopoint);
	}

	public GeoPoint getGeoPoint(MotionEvent ev){
		Projection p = mapView.getProjection();
		return p.fromPixels((int) ev.getX(), (int) ev.getY());
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_location_based_services_v1, menu);
		return true;
	}

	@Override  
	public void onBackPressed() {
		super.onBackPressed();
		//Add the locations from the overlay to the model.
		locManager.removeUpdates(locListener);
		
		//createReminder()
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			NavUtils.navigateUpFromSameTask(this);
			locManager.removeUpdates(locListener);
			finish(); //TODO: save state?
			return true;

		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	protected boolean isRouteDisplayed() {
		// TODO Auto-generated method stub
		return false;
	}
	


}

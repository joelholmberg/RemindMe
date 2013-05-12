package com.example.remindme;

import java.util.ArrayList;
import java.util.List;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.text.format.Time;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;

import com.example.remindme.R;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapView;
import com.google.android.maps.MyLocationOverlay;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;
import com.google.android.maps.Projection;
//import android.support.v4.app.NavUtils;

public class PositionService extends MapActivity {

	public static final String DB_PROVIDER = "db";
	public static final String KEY_ROW_ID = "rowId";
	private static final int LATITUDE_COLUMN = 1;
	private static final int LONGITUDE_COLUMN = 2;
	private static final int RADIUS_COLUMN = 3;
	protected static final long EXPIRATION_TIME = 300000; //5 minutes
	protected static final int SYNC_TIME_INTERVAL = 300; //5 minutes
	
	private MapView mapView;
	private LocationManager locManager;
	private LocationListener locListener;
	private NotesDbAdapter mDbHelper;
	private Long mRowId;
	private MyItemizedOverlay pinOverlay;
	private MyLocationOverlay myLocationOverlay;

	protected List<GeoCircle> mSavedLocations;
	private GeoCircle mCurrentPosition;
	private int mCurrentVelocity;

	private Time mTimeOfLastSync;
	public static boolean savedLocationsListSynched = false;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		//set this mapActivity to be the current view
		setContentView(R.layout.activity_position_service);

		//Create dbHelper
		mDbHelper = new NotesDbAdapter(this);

		//Fetch row Id from saved instance or bundle
		mRowId = (savedInstanceState == null) ? null :
			(Long) savedInstanceState.getSerializable(NotesDbAdapter.KEY_ID);
		if (mRowId == null) {
			Bundle extras = getIntent().getExtras();
			mRowId = extras != null ? extras.getLong(NotesDbAdapter.KEY_ID) : null;
		}

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
		
		
		//Start listening to location changes
		myLocationOverlay.enableMyLocation();
		
		//Add existing items from db if there are any
		addItemsFromDB();

		// redraw map
		mapView.postInvalidate();
	}
	
	@Override
	public void onPause(){
		super.onPause();
		//Start listening to location changes
			myLocationOverlay.disableMyLocation();
	}

	/**
	 * Initializes the LocationManager and adds a locationListener to GPS and Network events.
	 */
	private void initLocationManager() {
		locManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

		locListener = new LocationListener() {

			public void onLocationChanged(Location newLocation) {
//				GeoPoint newGeoPoint = new GeoPoint((int)newLocation.getLatitude(), (int)newLocation.getLongitude());
				
				//Sync the list of what saved locations are in reach of the device within the sync time frame
				if (!synchronizeSavedLocationsList()){
					return;
				}

				//Create reminder intent
				Intent intent = new Intent(getApplicationContext(), ReminderActivity.class);
				intent.putExtra(KEY_ROW_ID, mRowId);
				PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
				
				//Add proximity alerts to all saved locations that are within reach of the device within the sync time frame
				for (GeoCircle savedLocation : mSavedLocations){
					locManager.addProximityAlert(savedLocation.getLatitude(), savedLocation.getLongitude(), 
							savedLocation.getRadius(), EXPIRATION_TIME, pendingIntent);
//					if (savedLocation.isPointInside(newGeoPoint)){
//						if (verifyEntered(savedLocation)){
//							onEnter(savedLocation);
//						}
//					}
				}
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
	 * Synchronize the list of saved location. These locations are ones that can be reached before the next sync is performed.
	 * The algorithm for determining if a position is reachable is shortly:
	 *  1. determine the estimated distance from current location to saved position
	 *  2. reduce with the radius of saved position
	 *  3. calculate time needed to reach position with average velocity of last 5 minutes
	 *  4. if time needed is less them the interval of syncs, add to list 
	 *  
	 *  @return true if sync was performed, false if no locatins exists, or time since last sync is to 
	 *  small and no sync therefore was performed
	 */
	protected boolean synchronizeSavedLocationsList() {
		
		Time now = new Time();
		now.setToNow();
		
		if (mTimeOfLastSync != null && now.toMillis(true) < (mTimeOfLastSync.toMillis(true) + SYNC_TIME_INTERVAL*1000)) return false;
		
		mCurrentVelocity = getCurrentVelocity(); //Must be called before getCurrentPosition
		mCurrentPosition = getCurrentPosition();
		mSavedLocations = new ArrayList<GeoCircle>();
		Cursor cursor = mDbHelper.fetchAllPositions();
		if (cursor == null) return false;
		int distance, minTimeToArrival = 0;
		while(!cursor.isAfterLast()){
			int lat = Integer.parseInt(cursor.getString(LATITUDE_COLUMN));
			int lon = Integer.parseInt(cursor.getString(LONGITUDE_COLUMN));
			int radius = Integer.parseInt(cursor.getString(RADIUS_COLUMN));
			GeoCircle geoCircle = new GeoCircle(lat, lon, radius);
			distance = geoCircle.distanceTo(mCurrentPosition);
			minTimeToArrival = mCurrentVelocity == 0 ? SYNC_TIME_INTERVAL+1 : distance / mCurrentVelocity; //SYNC_TIME_INTERVAL+1 acts as infinity here
			if (minTimeToArrival < SYNC_TIME_INTERVAL){
				mSavedLocations.add(geoCircle);
			}
			cursor.moveToNext();
		}
		savedLocationsListSynched = true;
		mTimeOfLastSync = now;
		return true;
	}

	private GeoCircle getCurrentPosition() {
		GeoPoint geoPoint = myLocationOverlay.getMyLocation();
		if (geoPoint!=null){
			return new GeoCircle(geoPoint, 0);
		}
		//If LocationOverlay fails, do it manually
		Location location = locManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
		if (location == null){
			location = locManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
		}
		return new GeoCircle(location, 0);
	}

	private int getCurrentVelocity() {
		if (mCurrentPosition == null) return 0;
		return (int) getCurrentPosition().distanceTo(mCurrentPosition) / SYNC_TIME_INTERVAL;
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

		// create new pin overlay
		pinOverlay = new MyItemizedOverlay(icon, mDbHelper, mRowId);
		
		// create new circle-around-my-location overlay
		myLocationOverlay = new MyLocationOverlay(this, mapView);

		//Add newly created overlay to overlays list
		overlays.add(pinOverlay);
		overlays.add(myLocationOverlay);

		//Add existing items from db if there are any
		addItemsFromDB();

		// redraw map
		mapView.postInvalidate();

	}


	private void addItemsFromDB() {
		Cursor cursor = mDbHelper.fetchPositions(mRowId);
		if (cursor == null) return;
		while(!cursor.isAfterLast()){
			pinOverlay.addItem(cursor.getString(1), cursor.getString(2), cursor.getString(3));
			cursor.moveToNext();
		}
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
		MyItemizedOverlay overlay = new MyItemizedOverlay(icon, mDbHelper, mRowId);
		OverlayItem item = new OverlayItem(geopoint, "My Location", null);
		overlay.addItem(item);
		overlays.add(overlay);

		// move to location
		mapView.getController().animateTo(geopoint);

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
		MyItemizedOverlay overlay = new MyItemizedOverlay(icon, mDbHelper, mRowId);
		OverlayItem item = new OverlayItem(geopoint, "My Location", null);
		overlay.addItem(item);
		overlays.add(overlay);

		// move to location
		mapView.getController().animateTo(geopoint);

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
		//TODO: keep listening to changes even after this view is closed, otherwise reminders won't work properly.
		locManager.removeUpdates(locListener);

	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			NavUtils.navigateUpFromSameTask(this);
			//TODO: keep listening to changes even after this view is closed, otherwise reminders won't work properly.
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

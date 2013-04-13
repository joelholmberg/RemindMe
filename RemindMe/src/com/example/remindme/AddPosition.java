package com.example.remindme;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class AddPosition  extends Activity {
//	private NotesDbAdapter mDbHelper;
//	private MapView mapView;
//	private LocationManager locManager;
//	private LocationListener locListener;
//	
//	@Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        mDbHelper = new NotesDbAdapter(this);
//        mDbHelper.open();
//        
//        setContentView(R.layout.add_position);
//        
//        setTitle(R.string.add_position);
//
//        mTitleText = (EditText) findViewById(R.id.title);
//        mBodyText = (EditText) findViewById(R.id.body);
//
//        Button confirmButton = (Button) findViewById(R.id.confirm);
//        Button addPositionButton = (Button) findViewById(R.id.add_position);
//
//        mRowId = (savedInstanceState == null) ? null :
//            (Long) savedInstanceState.getSerializable(NotesDbAdapter.KEY_ROWID);
//        if (mRowId == null) {
//            Bundle extras = getIntent().getExtras();
//            mRowId = extras != null ? extras.getLong(NotesDbAdapter.KEY_ROWID)
//                                    : null;
//        }
//        
//    }
//	
//	/**
//	 * Initializes the MyLocationOverlay and adds it to the overlays of the map
//	 */
//	private void initLocationManager() {
//		locManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
//
//		locListener = new LocationListener() {
//
//			public void onLocationChanged(Location newLocation) {
//				
//			}
//
//			public void onProviderDisabled(String arg0) {
//			}
//
//			public void onProviderEnabled(String arg0) {
//			}
//
//			public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
//			}
//		};
//		locManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0,
//				locListener);
//		locManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0,
//				locListener);
//		
//		
//	}
//	
//	/**
//	 * Initialize the map and adds the zoom controls to the LinearLayout.
//	 */
//	private void initMap() {
//		mapView = (MapView) findViewById(R.id.mymap);
//		mapView.setBuiltInZoomControls(true);
//		mapView.displayZoomControls(true);
//
//		//Get all overlays from mapView
//		List<Overlay> overlays = mapView.getOverlays();
//
//		// first remove old overlays
//		overlays.clear();
//
//		// initialize icon
//		Drawable icon = getResources().getDrawable(R.drawable.pin);
//		icon.setBounds(0, 0, icon.getIntrinsicWidth(), icon
//				.getIntrinsicHeight());
//
//		// create new overlay
//		MyItemizedOverlay overlay = new MyItemizedOverlay(icon);
//		
//		//Add newly created overlay to overlays list
//		overlays.add(overlay);
//
//		// redraw map
//		mapView.postInvalidate();
//
//	}
}

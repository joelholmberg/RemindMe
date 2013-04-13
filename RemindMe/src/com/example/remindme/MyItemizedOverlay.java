package com.example.remindme;

import java.io.DataInputStream;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.logging.Logger;
 
import android.app.AlertDialog;
import android.content.*;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.util.Log;
 
import com.google.android.maps.GeoPoint;
import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.MapView;
import com.google.android.maps.OverlayItem;

//TODO: Låt denna klass sköta skapandet av overlayItems genom att läsa positioner alternativt geoCircles från modellen
 
public class MyItemizedOverlay extends ItemizedOverlay<OverlayItem>{
 
	private List<OverlayItem> items;
	private Drawable marker;
	private Context context;
 
	public MyItemizedOverlay(Drawable defaultMarker) {
		super(defaultMarker);
		items = new ArrayList<OverlayItem>();
		populate();
		marker = defaultMarker;

		
	}

 
	@Override
	public boolean onTap(GeoPoint p, MapView mapView){
		
		//Show dialog, ask for radius, then call model to add geocircle to current item.
		Log.d("MyItemizedOverlay", "tapped on map");
		//show dialogue first?
		//OverlayItem item = new OverlayItem(p, "Title", "message");
		GeoCircle geoCircle = new GeoCircle(p.getLatitudeE6(), p.getLongitudeE6());
//		GeoCircle mockupGeoCircle = new GeoCircle(p.getLatitudeE6(), p.getLongitudeE6());
//		model.addMockupGeoCircle(mockupGeoCircle);
		//model.addOverlayItem(item);
		
        return true;
	}
	
	
	/*
	 * (non-Javadoc)
	 * @see com.google.android.maps.ItemizedOverlay#onTap(int)
	 * What should happen when user taps an existing marker?
	 */
	@Override
    protected boolean onTap(int index) {
		Log.d("MyItemizedOverlay", "tapped at index" + index);
        OverlayItem itemClicked = items.get(index);
        AlertDialog.Builder dialog = new AlertDialog.Builder(context);
        dialog.setTitle(itemClicked.getTitle());
        dialog.setMessage("Use this location?");
        dialog.setCancelable(true);
        dialog.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
//                Log.i(this.getClass().getName(), "Selected Yes To use Location");
//                //What to do when user wants use selected location
//                Item testItem = SimpleItemFactory.createItem("test");
//                GeoCircle testCircle = new GeoCircle(id, id);
//                testItem.addGeoCircle(testCircle);
                
            }
        });
        dialog.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                Log.i(this.getClass().getName(), "Selected No To Add Location");
                dialog.cancel();
            }
        });
        dialog.show();
        return true;
    }
	 
	
	@Override
	protected OverlayItem createItem(int index) {
		return (OverlayItem)items.get(index);
	}
 
	
	@Override
	public int size() {
		return items.size();
	}
 
	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * com.google.android.maps.ItemizedOverlay#draw(android.graphics.Canvas,
	 * com.google.android.maps.MapView, boolean)
	 */
	@Override
	public void draw(Canvas canvas, MapView mapView, boolean shadow) {
		super.draw(canvas, mapView, shadow);
		boundCenterBottom(marker);
	}
 
	public void addItem(OverlayItem overlay) {
	    items.add(overlay);
	    populate();
	}
	
	public void addItemsFromFile(String filename) {
		
	}

 
}
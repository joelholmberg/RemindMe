package se.joelholmberg.android.remindme;

import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.util.Log;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.MapView;
import com.google.android.maps.OverlayItem;
import com.google.android.maps.Projection;



public class MyItemizedOverlay extends ItemizedOverlay<OverlayItem>{
 
	private List<OverlayItem> mItems;
	private Context context;
	private NotesDbAdapter mDbHelper;
	private Long mRowId;
	private Paint mStrokePaint;
	private Paint mFillPaint; 
	private static final Integer DEFAULT_RADIUS = 500; 
 
	public MyItemizedOverlay(Drawable defaultMarker, NotesDbAdapter notesDbAdapter, Long rowId) {
		super(boundCenterBottom(defaultMarker));
		mItems = new ArrayList<OverlayItem>();
		populate();

		//Save the ID of the reminder we're currently editing
		mRowId = rowId;
		
		initDb(notesDbAdapter);
		initPaint();
	}

	private void initDb(NotesDbAdapter notesDbAdapter) {
		mDbHelper = notesDbAdapter;
		mDbHelper.open();
	}
	
	private void initPaint() {
		// Setup the stroke paint
        mStrokePaint = new Paint();
        mStrokePaint.setAntiAlias(true);
        mStrokePaint.setStrokeWidth(2.0f);
        mStrokePaint.setColor(0xff6666ff);
        mStrokePaint.setStyle(Style.STROKE);
        
        // Setup the fill paint
        mFillPaint = new Paint();
        mFillPaint.setAntiAlias(true);
        mFillPaint.setStrokeWidth(2.0f);
        mFillPaint.setColor(0x186666ff);
        mFillPaint.setStyle(Style.FILL);
        mFillPaint.setAlpha(75);
	}

	@Override
	public boolean onTap(GeoPoint p, MapView mapView){
		Log.d("MyItemizedOverlay", "tapped on map");

		//TODO: show dialogue first?
		OverlayItem overlayItem = new OverlayItem(p, "Title", "message");
		
		//Add a pin to the tapped position on the overlay
		addItem(overlayItem);
		populate();
		mapView.postInvalidate();
		mDbHelper.createPosition(mRowId, Integer.toString(p.getLatitudeE6()), Integer.toString(p.getLongitudeE6()), 
				Integer.toString(DEFAULT_RADIUS));
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
        OverlayItem itemClicked = mItems.get(index);
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
		return mItems.get(index);
	}
 
	@Override
	public int size() {
		return mItems.size();
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
//		boundCenterBottom(marker);
		
		if (!shadow) drawRadius(canvas, mapView);
	}
 
	private void drawRadius(Canvas canvas, MapView mapView) {
		Projection projection = mapView.getProjection();
		
	    for (OverlayItem overlayItem: mItems){
	    	GeoPoint geoPoint = overlayItem.getPoint();
	    	Point myPoint = new Point();
		    projection.toPixels(geoPoint, myPoint);
		    int radiusPixel = (int) projection.metersToEquatorPixels(50.0f);
		    
		    //Draw circle
		    canvas.drawCircle(myPoint.x, myPoint.y, radiusPixel, mStrokePaint);
		    canvas.drawCircle(myPoint.x, myPoint.y, radiusPixel, mFillPaint);
	    }
	}

	public void addItem(OverlayItem overlayItem) {
	    mItems.add(overlayItem);
	    populate();
	}
	
	public void addItem(String lat, String lon, String radius) {
		OverlayItem item = new OverlayItem(new GeoPoint(Integer.parseInt(lat), Integer.parseInt(lon)), "title", "msg");
		this.addItem(item);
	}
	
	
	

 
}

package com.example.remindme;

import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Scanner;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.OverlayItem;

public class Model extends Observable{
	private static Model model;
	private ArrayList<Item> items;
	private List<OverlayItem> overlayItems;
	private List<GeoPoint> locations;
	private int currentItemIndex;
	private Activity activity;
	private int currentItemId;


	public static Model getInstance(Activity a){
		if (model == null){
			model = new Model(a);
		}
		return model;
	}

	//Returns null if model has not been initialized with an activity yet.
	public static Model getInstance(){
		return model;
	}

	private Model(Activity a){
		activity = a;
		initItems();
		initLocations();
		initOverlayItems();
//		currentItemId = items.size()-1;
	}
	
	private void initItems(){
		items = new ArrayList<Item>();
	}
	private void initLocations(){
		locations = new ArrayList<GeoPoint>();
	}
	private void initOverlayItems() {
		//Add saved locations to overlayItem list
		overlayItems = new ArrayList<OverlayItem>();
		FileInputStream fis;

		try {
			fis = activity.openFileInput(activity.getString(R.string.savefile));
			DataInputStream dataIO = new DataInputStream(fis);
			Scanner scanner = new Scanner(fis);

			while(scanner.hasNext()){
				int lat = scanner.hasNextInt()?scanner.nextInt():-1;
				int lon = scanner.hasNextInt()?scanner.nextInt():-1;
				String message = scanner.hasNext()?scanner.next():null;
				if (lat != -1 && lon != -1 && message != null){
					//TODO: Should be GeoCircles
					GeoPoint point = new GeoPoint(lat, lon);
					OverlayItem item = new OverlayItem(point, message, null);
					overlayItems.add(item);
				}
				if (scanner.hasNextLine()){
					scanner.nextLine();
				}else{
					break;
				}
			}
			Log.v("initMap()", "No more strings");
			scanner.close();
			dataIO.close();
			fis.close();
		}
		catch  (Exception e) {  
			Log.v("initMap()", "error in reading file");
			e.printStackTrace();
		}
		setChanged();
		notifyObservers();

	}




	public void addItem(Item item){
		items.add(item);
		currentItemIndex = items.size()-1;
		setChanged();
		notifyObservers();
	}

	public Item getItem(int index){
		return items.get(index);
	}

	public ArrayList<Item> getItems(){
		return items;
	}

	public Item getCurrentItem(){
		return items.get(currentItemIndex);
	}
	
	public int getCurrentItemId(){
		return items.get(currentItemIndex).getId();
	}
	
	public Item getLastItem(){
		return items.get(items.size()-1);
	}


	/**
	 * Adds an overlayItem to the current items list of overlayItems
	 * @param item
	 */
	public void addOverlayItem(OverlayItem item){
		
		//Create a geocircle from the overlayitem and add to the current items list
		addGeoCircle(new GeoCircle(item.getPoint(), 500));
		
		overlayItems.add(item);
		try{
	        FileOutputStream fos = activity.openFileOutput(activity.getString(R.string.savefile), Context.MODE_APPEND);
	        
	        //Create string on format: "12863812 18763213 My House\n"
	        StringBuilder message = new StringBuilder(30);
	        message.append(item.getPoint().getLatitudeE6()).append(" ");
	        message.append(item.getPoint().getLongitudeE6()).append(" ");
	        message.append(item.getTitle()).append("\n");
	        Log.d("Model", "wrote " + message.toString() + " to file " + R.string.savefile);
	        fos.write(message.toString().getBytes());
	        fos.close();
        }catch (IOException e){
        	//No problem, doesn't exist until we run once.
        }
		Log.d("Model", "Added OverlayItem");
		setChanged();
		notifyObservers();
	}

	private void addGeoCircle(GeoCircle geoCircle) {
		getCurrentItem().addGeoCircle(geoCircle);
		setChanged();
		notifyAll();
		
	}

	public void update(){
		//update the overlayItems list to the current state of the savefile.
		
		//Clear old values
		overlayItems.clear();
		
		//read values from file
		FileInputStream fis;

		try {
			fis = activity.openFileInput(activity.getString(R.string.savefile));
			DataInputStream dataIO = new DataInputStream(fis);
			Scanner scanner = new Scanner(fis);

			while(scanner.hasNext()){
				int lat = scanner.hasNextInt()?scanner.nextInt():-1;
				int lon = scanner.hasNextInt()?scanner.nextInt():-1;
				String message = scanner.hasNext()?scanner.next():null;
				if (lat != -1 && lon != -1 && message != null){
					//TODO: Should be GeoCircles
					GeoPoint point = new GeoPoint(lat, lon);
					OverlayItem item = new OverlayItem(point, message, null);
					overlayItems.add(item);
				}
				if (scanner.hasNextLine()){
					scanner.nextLine();
				}else{
					break;
				}
			}
			Log.v("update()", "No more strings");
			scanner.close();
			dataIO.close();
			fis.close();
		}
		catch  (Exception e) {  
			Log.v("update()", "error in reading file");
			e.printStackTrace();
		}
		setChanged();
		notifyObservers();
	}
 	public List<GeoPoint> getLocations() {
		List<GeoPoint> temp_locations = new ArrayList<GeoPoint>();
		for (OverlayItem item: overlayItems){
			temp_locations.add(item.getPoint());
		}
		return temp_locations;
	}

	public List<OverlayItem> getOverlayItems() {
		List<OverlayItem> tempList = new ArrayList<OverlayItem>();
		for( GeoCircle gc : getCurrentItem().getGeoCircles() ){
			OverlayItem overlayItem = new OverlayItem(
					new GeoPoint(gc.getLatitudeE6(),  gc.getLongitudeE6()), 
					getCurrentItem().getMessage(), 
					null);
			tempList.add(overlayItem);
		}
		return tempList;
	}

	public void addMockupGeoCircle(GeoCircle mockupGeoCircle) {
		getCurrentItem().getGeoCircles().add(mockupGeoCircle);
		Log.v("Model", "Added mockup geoCircle to item with id: " + getCurrentItemId());
		setChanged();
		notifyObservers();
	}

	public void setCurrentItemId(int id) {
		//TODO: detta är fel: (inte nödvändigtvis i samma ordning som ID på knapparna.
		currentItemId = id;
		
	}
	

	public void addGeoCircleToCurrentItem(GeoCircle geoCircle) {
		getCurrentItem().addGeoCircle(geoCircle);
		setChanged();
		notifyObservers();
		
	}
}

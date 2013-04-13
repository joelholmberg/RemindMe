package com.example.remindme;

public class SimpleItemFactory {
	private static int count;
	public static Item createItem(String message){
		return new SimpleItem(message, ++count);
	}
	
	public static int getCount(){
		return count;
	}
}

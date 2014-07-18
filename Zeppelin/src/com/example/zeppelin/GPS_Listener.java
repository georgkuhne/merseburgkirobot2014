package com.example.zeppelin;

import java.util.ArrayList;
import java.util.HashMap;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

public class GPS_Listener implements LocationListener{
	private static GPS_Listener instance;
	private ArrayList<Double> longitude;
	private ArrayList<Double> latitude;
	private ArrayList<Double> altitude;
	private ArrayList<Float> speed;
	private ArrayList<Float> accuracy;
	
	public static GPS_Listener getInstance(Context context){
		if(instance == null){
			instance = new GPS_Listener(context);
		}
		return instance;
	}
	
	public GPS_Listener(Context context){
		System.out.println("new gps listener");
		LocationManager locationManager = (LocationManager)context.getSystemService(Context.LOCATION_SERVICE);
		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
		
		longitude = new ArrayList<Double>();
		latitude = new ArrayList<Double>();
		altitude = new ArrayList<Double>();
		speed = new ArrayList<Float>();
		accuracy = new ArrayList<Float>();
	}
	
	public HashMap<String, Double> getLocation(){
		double average_longitude = 0;
		double average_latitude = 0;
		
		for(double d : longitude){
			average_longitude += d;
		}
		
		for(double d : latitude){
			average_latitude += d;
		}
		
		average_longitude = average_longitude / longitude.size();
		average_latitude = average_latitude / latitude.size();
		
		longitude = new ArrayList<Double>();
		latitude = new ArrayList<Double>();
		
		HashMap<String, Double> location = new HashMap<String, Double>();
		location.put("Latitude", average_latitude);
		location.put("Longitude", average_longitude);
		
		return location;
	}
	
	public double getAltitude(){
		double average_altitude = 0;
		
		for(double d : altitude){
			average_altitude += d;
		}
		
		average_altitude = average_altitude / altitude.size();
		altitude = new ArrayList<Double>();
		
		return average_altitude;
	}
	
	public float getSpeed(){
		float average_speed = 0;
		
		for(float f : speed){
			average_speed += f;
		}
		
		average_speed = average_speed / speed.size();
		speed = new ArrayList<Float>();
		
		return average_speed;
	}

	public float getAccuracy(){
		float average_accuracy = 0;
		
		
		for(float f : accuracy){
			average_accuracy += f;
		}
		
		average_accuracy = average_accuracy / accuracy.size();
		accuracy = new ArrayList<Float>();
		
		return average_accuracy;
	}
	
	@Override
	public void onLocationChanged(Location location) {
		System.out.println("new Location");
		longitude.add(location.getLongitude());
		latitude.add(location.getLatitude());
		altitude.add(location.getAltitude());
		speed.add(location.getSpeed());
		accuracy.add(location.getAccuracy());
	}

	@Override
	public void onProviderDisabled(String provider) {
		System.out.println("Provider Disabled");
		
	}

	@Override
	public void onProviderEnabled(String provider) {
		System.out.println("Provider Enabled");
		
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		System.out.println("Status Changed");
		
	}

}

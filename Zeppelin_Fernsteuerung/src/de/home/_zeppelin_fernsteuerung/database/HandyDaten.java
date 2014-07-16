package de.home._zeppelin_fernsteuerung.database;

public class HandyDaten {
	//private variables
    int _id;
    String _pressure;
    String _latitude;
    String _longitude;
    String _altitude;
    String _speed;
    String _accuracy;
    String _roll;
    String _pitch;
    String _azimuth;
    String _battery;
	
	// Empty constructor
    public HandyDaten(){
         
    }
    // constructor
    public HandyDaten(int id, String dru, String lat, String lon, String alt, String spe, String acc, 
    		String rol, String roll,String pit,String azi, String bat){
        this._id = id;
        this._pressure = dru;
    	this._latitude = lat;
    	this._longitude = lon;
    	this._altitude = alt;
    	this._speed = spe;
    	this._accuracy = acc;
    	this._roll = roll;
    	this._pitch = pit;
    	this._azimuth = azi;
    	this._battery = bat;
    }
    
    // constructor
    public HandyDaten(String dru, String lat, String lon, String alt, String spe, String acc, 
    		String rol, String roll,String pit,String azi, String bat){
        this._pressure = dru;
    	this._latitude = lat;
    	this._longitude = lon;
    	this._altitude = alt;
    	this._speed = spe;
    	this._accuracy = acc;
    	this._roll = roll;
    	this._pitch = pit;
    	this._azimuth = azi;
    	this._battery = bat;
    }
    
    public int getID(){
    	return this._id;
    }

    public void setID(int id){
        this._id = id;
    }
    
    public String getpressure(){
    	return this._pressure;
    }
    
    public void setpressure(String pressure){
        this._pressure = pressure;
    }
    
    public String getlatitude(){
    	return this._pressure;
    }
    
    public void setlatitude(String latitude){
        this._latitude = latitude;
    }
    
    public String getlongitude(){
    	return this._pressure;
    }
    
    public void setlongitude(String longitude){
        this._longitude = longitude;
    }
    
    public String getaltitude(){
    	return this._pressure;
    }
  
    public void setaltitude(String altitude){
        this._altitude = altitude;
    }
    
    public String getspeed(){
    	return this._pressure;
    }
  
    public void setspeed(String speed){
        this._speed = speed;
    }
    
    public String getaccuracy(){
    	return this._accuracy;
    }
  
    public void setaccuracy(String accuracy){
       this._accuracy = accuracy;
    }
    
    public String getroll(){
    	return this._accuracy;
    }
  
    public void setroll(String roll){
       this._roll = roll;
    }
    
    public String getpitch(){
    	return this._accuracy;
    }
  
    public void setpitch(String pitch){
       this._pitch = pitch;
    }
    
    public String getazimuth(){
    	return this._azimuth;
    }
  
    public void setazimuth(String azimuth){
       this._azimuth = azimuth;
    }
    
    public String getbattery(){
    	return this._azimuth;
    }
  
    public void setbattery(String battery){
       this._battery = battery;
    }
    
}

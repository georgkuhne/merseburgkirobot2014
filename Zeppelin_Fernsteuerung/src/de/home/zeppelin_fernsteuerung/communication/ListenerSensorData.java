package de.home.zeppelin_fernsteuerung.communication;

public interface ListenerSensorData {
	
public void updateSensorData(float pressure, double latitude, double longitude, double altitude, float speed, float accuracy, int roll, int pitch, int azimuth, int battery);
}

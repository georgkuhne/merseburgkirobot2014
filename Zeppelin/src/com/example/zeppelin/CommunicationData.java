package com.example.zeppelin;

import java.nio.ByteBuffer;
import java.util.concurrent.ConcurrentLinkedQueue;

public class CommunicationData {
	public static final ConcurrentLinkedQueue<FunkMessage> sendFunkMessageQueue = new ConcurrentLinkedQueue<FunkMessage>();
	public static CommunicationData instance;

	public static CommunicationData getInstance() {
		if (instance == null) {
			instance = new CommunicationData();
		}
		return instance;
	}

	public void sendSensorData(float pressure, double latitude,double longitude, double altitude, float speed, float accuracy,int battery, int azimuth, int pitch, int roll) {

		FunkMessage message = new FunkMessage();
		message.setAdress(FunkMessage.ADRESSE_TABLET);
		message.setType(FunkMessage.TYPE_OHNERÜCKANTWORT);

		byte[] pressureBA = floatToByteArray(pressure);
		byte[] latitudeBA = doubleToByteArray(latitude);
		byte[] longitudeBA = doubleToByteArray(longitude);
		byte[] altitudeBA = doubleToByteArray(altitude);
		byte[] speedBA = floatToByteArray(speed);
		byte[] accuracyBA = floatToByteArray(accuracy);

		byte[] combined = new byte[4 + pressureBA.length + latitudeBA.length+ longitudeBA.length + altitudeBA.length + speedBA.length+ accuracyBA.length];

		System.arraycopy(pressureBA, 0, combined, 0, pressureBA.length);
		System.arraycopy(latitudeBA, 0, combined, pressureBA.length,latitudeBA.length);
		System.arraycopy(longitudeBA, 0, combined, pressureBA.length+ latitudeBA.length, longitudeBA.length);
		System.arraycopy(altitudeBA, 0, combined, pressureBA.length+ latitudeBA.length + longitudeBA.length, altitudeBA.length);
		System.arraycopy(speedBA, 0, combined, pressureBA.length+ latitudeBA.length + longitudeBA.length + altitudeBA.length,speedBA.length);
		System.arraycopy(accuracyBA, 0, combined, pressureBA.length+ latitudeBA.length + longitudeBA.length + altitudeBA.length+ speedBA.length, accuracyBA.length);
		combined[combined.length - 4] = (byte) roll;
		combined[combined.length - 3] = (byte) pitch;
		combined[combined.length - 2] = (byte) azimuth;
		combined[combined.length - 1] = (byte) battery;

		 message.setData(combined);
		 sendFunkMessageQueue.add(message);
	}

	private byte[] doubleToByteArray(double value) {
		byte[] bytes = new byte[8];
		ByteBuffer.wrap(bytes).putDouble(value);
		return bytes;
	}

	private byte[] floatToByteArray(float value) {
		byte[] bytes = new byte[4];
		ByteBuffer.wrap(bytes).putFloat(value);
		return bytes;
	}

}

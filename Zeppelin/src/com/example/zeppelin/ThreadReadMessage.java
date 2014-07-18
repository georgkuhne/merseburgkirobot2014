package com.example.zeppelin;

import java.nio.ByteBuffer;

import android.app.Activity;
import android.app.FragmentManager;
import android.util.Log;
import android.widget.TextView;

public class ThreadReadMessage extends Thread {
	private static final String TAG = "ThreadReadMessage";
	private FTDriver ftDriver;
	private boolean stop;
	private Activity activity;
	private TextView text;
	private FragmentManager manager;

	public ThreadReadMessage(FTDriver ftDriver, Activity activity, FragmentManager manager) {
		Log.i(TAG, "Thread gestartet");
		this.ftDriver = ftDriver;
		this.activity = activity;
		text = (TextView) activity.findViewById(R.id.text_temp);
		this.manager = manager;
	}

	@Override
	public void run() {
		stop = false;
		while (!stop) {

			try {
				int l;
				byte[] recievedMessage = new byte[40];
				
				l = ftDriver.read(recievedMessage);
		
				byte[] recievedData = new byte[recievedMessage.length - 4];
				
				System.arraycopy(recievedMessage, 3, recievedData, 0, recievedData.length);
				
				if(Integer.toHexString(recievedMessage[0] & 0xFF).equalsIgnoreCase("A5")){
					if(String.format("%8s",Integer.toBinaryString(recievedMessage[1] & 0xFF)).replace(' ', '0').equalsIgnoreCase(String.format("%8s",Integer.toBinaryString(FunkMessage.ADRESSE_TABLET)).replace(' ', '0'))){
						if(recievedMessage[recievedMessage.length-1] == CRC8.calc(recievedData, recievedData.length)){
							//recievedMessage[0] = Startbyte
							//recievedMessage[1] = Adresse
							//recievedMessage[2] = Länge

							float pressure = getFloat(recievedData, 0);
							double latitude = getDouble(recievedData, 4);
							double longitude = getDouble(recievedData, 12);
							double altitude = getDouble(recievedData, 20);
							float speed = getFloat(recievedData, 28);
							float accuracy = getFloat(recievedData, 32);
							int roll = getInt(recievedData, 36);
							int pitch = getInt(recievedData, 37);
							int azimuth = getInt(recievedData, 38);
							int battery = getInt(recievedData, 39);
							
							/*
							System.out.println("Pressure: " + pressure +
									"\nLatitude: " + latitude + 
									"\nLongitude: " + longitude +
									"\nAltitude: " + altitude + 
									"\nSpeed: " + speed + 
									"\nAccuracy: " + accuracy + 
									"\nRoll: " + roll +
									"\nPitch: " + pitch +
									"\nAzimuth: " + azimuth + 
									"\nBattery: " + battery + 
									"\n###################################");
							*/

						} else {
							Log.e(TAG, "CRC8 Fehler: " + String.format("%8s",Integer.toBinaryString(CRC8.calc(recievedData, recievedData.length) & 0xFF)).replace(' ', '0'));
						}
					} else {
						Log.e(TAG, "Falsche Zieladresse");
					}
				} else {
					Log.e(TAG, "Auf Startbyte warten");
				}

			} catch (Exception f) {
				stop = true;
				MainActivity.sending = false;
				new RestartDialog().show(manager, " ");
			}
		}
	}
	
	private float getFloat(byte[] src, int start){
		byte[] temp = new byte[4];
		System.arraycopy(src, start, temp, 0, temp.length);
		return ByteBuffer.wrap(temp).getFloat();
	}
	
	private float getDouble(byte[] src, int start){
		byte[] temp = new byte[8];
		System.arraycopy(src, start, temp, 0, temp.length);
		return ByteBuffer.wrap(temp).getFloat();
	}
	
	private int getInt(byte[] src, int pos){
		byte b = Byte.valueOf(src[pos]);
		int value = 0;
	    value = (value << 8) | b;   
		return value;
	}

}

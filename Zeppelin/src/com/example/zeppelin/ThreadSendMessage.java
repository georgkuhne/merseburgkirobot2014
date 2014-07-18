package com.example.zeppelin;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.concurrent.ConcurrentLinkedQueue;

import android.app.FragmentManager;
import android.util.Log;

public class ThreadSendMessage extends Thread {
	private FTDriver ftDriver;
	private FragmentManager manager;
	private boolean stop;

	public ThreadSendMessage(FTDriver ftDriver,FragmentManager manager) {
		this.ftDriver = ftDriver;
		this.manager = manager;
	}


	@Override
	public void run() {
		stop = false;
		while (!stop) {
			ConcurrentLinkedQueue<FunkMessage> sendque = CommunicationData.sendFunkMessageQueue;

			FunkMessage funkMessage = sendque.poll();
			if (funkMessage != null) {
				ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
				try {
					byte[] header = funkMessage.getHeader();
					byte[] data = funkMessage.getData();
					String sheader = "";
					String sdata = "";

					for (int i = 0; i < header.length; i++) {
						sheader = sheader+ "|"+ String.format("%8s",Integer.toBinaryString(header[i] & 0xFF)).replace(' ', '0');
					}
					for (int i = 0; i < data.length; i++) {
						sdata = sdata+ "|"+ String.format("%8s",Integer.toBinaryString(data[i] & 0xFF)).replace(' ', '0');
					}

//					System.err.println("HEADER:" + sheader);
//					System.err.println("DATA:" + sdata);

					outputStream.write(funkMessage.getHeader());
					outputStream.write(funkMessage.getData());
					outputStream.write(funkMessage.getPruefbyte());
					byte c[] = outputStream.toByteArray();
					
//					Log.e("Vergleich", String.format("%8s",Integer.toBinaryString(funkMessage.getPruefbyte() & 0xFF)).replace(' ', '0') + " --- " + String.format("%8s",Integer.toBinaryString(c[c.length-1] & 0xFF)).replace(' ', '0'));
					
					try{
						ftDriver.write(c);
					} catch(Exception f){
						stop = true;
						MainActivity.sending = false;
						new RestartDialog().show(manager, " ");
					}
					
					
					
//					byte[] recievedData = new byte[c.length - 4];
//					System.arraycopy(c, 3, recievedData, 0, recievedData.length);
//					
//					if(Integer.toHexString(c[0] & 0xFF).equalsIgnoreCase("A5")){
//						if(String.format("%8s",Integer.toBinaryString(c[1] & 0xFF)).replace(' ', '0').equalsIgnoreCase(String.format("%8s",Integer.toBinaryString(FunkMessage.ADRESSE_TABLET)).replace(' ', '0'))){
//							if(c[c.length-1] == CRC8.calc(recievedData, recievedData.length)){
//								//c[0] = Startbyte
//								//c[1] = Adresse
//								//c[2] = Länge
////								String pressure = Integer.toBinaryString(c[3] & 0xFF);
//								
//								
//								float pressure = getFloat(recievedData, 0);
//								double latitude = getDouble(recievedData, 4);
//								double longitude = getDouble(recievedData, 12);
//								double altitude = getDouble(recievedData, 20);
//								float speed = getFloat(recievedData, 28);
//								float accuracy = getFloat(recievedData, 32);
//								int roll = getInt(recievedData, 36);
//								int pitch = getInt(recievedData, 37);
//								int azimuth = getInt(recievedData, 38);
//								int battery = getInt(recievedData, 39);
//								
//								/*
//								
//								System.out.println("Pressure: " + pressure +
//										"\nLatitude: " + latitude + 
//										"\nLongitude: " + longitude +
//										"\nAltitude: " + altitude + 
//										"\nSpeed: " + speed + 
//										"\nAccuracy: " + accuracy + 
//										"\nRoll: " + roll +
//										"\nPitch: " + pitch +
//										"\nAzimuth: " + azimuth + 
//										"\nBattery: " + battery + 
//										"\n###################################");
//								*/
//
//
//							} else {
//								Log.e("READ", "CRC8 Fehler: " + String.format("%8s",Integer.toBinaryString(CRC8.calc(recievedData, recievedData.length) & 0xFF)).replace(' ', '0'));
//							}
//						} else {
//							Log.e("READ", "Falsche Zieladresse");
//						}
//					} else {
//						Log.e("READ", "Auf Startbyte warten");
//					}
					
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
//	private float getFloat(byte[] src, int start){
//		byte[] temp = new byte[4];
//		System.arraycopy(src, start, temp, 0, temp.length);
//		return ByteBuffer.wrap(temp).getFloat();
//	}
	
//	private float getDouble(byte[] src, int start){
//		byte[] temp = new byte[8];
//		System.arraycopy(src, start, temp, 0, temp.length);
//		return ByteBuffer.wrap(temp).getFloat();
//	}
	
//	private int getInt(byte[] src, int pos){
//		byte b = Byte.valueOf(src[pos]);
//		int value = 0;
//	    value = (value << 8) | b;   
//		return value;
//	}
}

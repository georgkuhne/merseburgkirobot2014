package de.home.zeppelin_fernsteuerung.communication;

import java.nio.ByteBuffer;
import java.util.ArrayList;

import org.apache.http.util.ByteArrayBuffer;

import android.util.Log;
import android.widget.Toast;
import de.home.zeppelin_fernsteuerung.MainActivity;

public class ThreadReadMessage extends Thread {
	private static final String TAG = "ThreadReadMessage";
	private FTDriver ftDriver;
	private boolean stop;
	private ArrayList<ListenerSensorData> SensorObserver = new ArrayList<ListenerSensorData>();
	private static ThreadReadMessage instance;
	MainActivity mainactivity;

	public ThreadReadMessage(FTDriver ftDriver, MainActivity mainactivity) {
		Log.i(TAG, "Thread gestartet");
		this.ftDriver = ftDriver;
		instance = this;
		this.mainactivity = mainactivity;
	}

	public static ThreadReadMessage getInstance() {
		return instance;
	}

	@Override
	public void run() {
		stop = false;
		ByteArrayBuffer buffer = new ByteArrayBuffer(500);
		byte[] startbyteblock = null;
		int l;
		while (!stop) {
			try {
				byte[] messageBlock = new byte[44];
				if (startbyteblock == null) {
					l = ftDriver.read(messageBlock);
					startbyteblock = getstartByteBlock(messageBlock, l);
					if (startbyteblock != null) {
						buffer.append(startbyteblock, 0, startbyteblock.length);
					} else
						continue;

				}

				startbyteblock = readuntilMessageBlockComplete(buffer);
				messageBlock = buffer.toByteArray();
				buffer.clear();
				byte[] recievedData = new byte[messageBlock.length - 4];

				toastErrorMessage(bytesToHex(messageBlock));
				System.arraycopy(messageBlock, 3, recievedData, 0,
						recievedData.length);
				if (Integer.toHexString(messageBlock[0] & 0xFF)
						.equalsIgnoreCase("A5")) {
					if (String
							.format("%8s",
									Integer.toBinaryString(messageBlock[1] & 0xFF))
							.replace(' ', '0')
							.equalsIgnoreCase(
									String.format(
											"%8s",
											Integer.toBinaryString(FunkMessage.ADRESSE_TABLET))
											.replace(' ', '0'))) {
						// if (messageBlock[messageBlock.length - 1] ==
						// CRC8.calc(
						// recievedData, recievedData.length)) {
						if (true) {
							// recievedMessage[0] = Startbyte
							// recievedMessage[1] = Adresse
							// recievedMessage[2] = L�nge

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

							updateSensorListener(pressure, latitude, longitude,
									altitude, speed, accuracy, roll, pitch,
									azimuth, battery);
							/*
							 * System.out.println("Pressure: " + pressure +
							 * "\nLatitude: " + latitude + "\nLongitude: " +
							 * longitude + "\nAltitude: " + altitude +
							 * "\nSpeed: " + speed + "\nAccuracy: " + accuracy +
							 * "\nRoll: " + roll + "\nPitch: " + pitch +
							 * "\nAzimuth: " + azimuth + "\nBattery: " + battery
							 * + "\n###################################");
							 */

						} else {

							Log.e(TAG,
									"CRC8 Fehler: "
											+ String.format(
													"%8s",
													Integer.toBinaryString(CRC8
															.calc(recievedData,
																	recievedData.length) & 0xFF))
													.replace(' ', '0'));
						}
					} else {

					}
				} else {
					Log.e(TAG, "Auf Startbyte warten");
					// toastErrorMessage("StartBytewaiting");

				}

			} catch (Exception f) {
				Log.wtf(TAG, f);
				f.printStackTrace();
				toastErrorMessage(f.toString());
			}
		}

	}

	private byte[] readuntilMessageBlockComplete(ByteArrayBuffer buffer) {
		int l = buffer.length();
		boolean complete = false;
		byte[] block = new byte[44];
		ByteArrayBuffer startbyte = new ByteArrayBuffer(44);
		while (!stop) {
			int readed = ftDriver.read(block);
			l += readed;
			if (l >= 44) {
				if (l == 44) {
					buffer.append(block, 0, readed);
					return null;
				}
				if (l > 44) {
					buffer.append(block, 0, readed - (l - 44));
					startbyte.append(block, readed - (l - 44), readed);
					return startbyte.toByteArray();
				}

			}

			buffer.append(block, 0, readed);
		}
		return null;

	}

	private byte[] getstartByteBlock(byte[] messageBlock, int l) {
		for (int i = 0; i < l; i++)
			if (Integer.toHexString(messageBlock[i] & 0xFF).equalsIgnoreCase(
					"A5")) {
				ByteArrayBuffer b = new ByteArrayBuffer(40);
				b.append(messageBlock, i, l - i);
				return b.toByteArray();
			}

		return null;
	}

	private void toastErrorMessage(final String messages) {
		mainactivity.runOnUiThread(new Runnable() {
			public void run() {
				Toast.makeText(mainactivity, messages, Toast.LENGTH_SHORT)
						.show();
			}
		});
	}

	private float getFloat(byte[] src, int start) {
		byte[] temp = new byte[4];
		System.arraycopy(src, start, temp, 0, temp.length);
		return ByteBuffer.wrap(temp).getFloat();
	}

	private float getDouble(byte[] src, int start) {
		byte[] temp = new byte[8];
		System.arraycopy(src, start, temp, 0, temp.length);
		return ByteBuffer.wrap(temp).getFloat();
	}

	private int getInt(byte[] src, int pos) {
		byte b = Byte.valueOf(src[pos]);
		int value = 0;
		value = (value << 8) | b;
		return value;
	}

	public void addSensorListener(ListenerSensorData lsd) {
		SensorObserver.add(lsd);
	}

	public void removeSensorListener(ListenerSensorData lsd) {
		SensorObserver.remove(lsd);
	}

	private void updateSensorListener(final float pressure,
			final double latitude,

			final double longitude, final double altitude, final float speed,
			final float accuracy, final int roll, final int pitch,
			final int azimuth, final int battery) {
		mainactivity.runOnUiThread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				for (int i = 0; i < SensorObserver.size(); i++) {
					SensorObserver.get(i).updateSensorData(pressure, latitude,
							longitude, altitude, speed, accuracy, roll, pitch,
							azimuth, battery);
				}

			}
		});
	}

	final protected static char[] hexArray = "0123456789ABCDEF".toCharArray();

	public static String bytesToHex(byte[] bytes) {
		char[] hexChars = new char[bytes.length * 2];
		for (int j = 0; j < bytes.length; j++) {
			int v = bytes[j] & 0xFF;
			hexChars[j * 2] = hexArray[v >>> 4];
			hexChars[j * 2 + 1] = hexArray[v & 0x0F];
		}
		return new String(hexChars);
	}

	public void end() {
		stop = true;

	}

}

package de.home.zeppelin_fernsteuerung.communication;

import java.nio.ByteBuffer;
import java.util.ArrayList;

import org.apache.http.util.ByteArrayBuffer;

import android.util.Log;
import android.widget.Toast;
import de.home.zeppelin_fernsteuerung.MainActivity;

public class ThreadReadMessage extends Thread {

	static byte[] array1 = { (byte) 0x28, (byte) 0xFF, (byte) 0xC0,
			(byte) 0x00, (byte) 0x00, (byte) 0xFF, (byte) 0xF8, (byte) 0x00,
			(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
			(byte) 0xFF, (byte) 0xF8, (byte) 0x00, (byte) 0x00, (byte) 0x00,
			(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0xFF, (byte) 0xF8,
			(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
			(byte) 0x00, (byte) 0xFF, (byte) 0xC0, (byte) 0x00, (byte) 0x00,
			(byte) 0xFF, (byte) 0xC0, (byte) 0x00, (byte) 0x00, (byte) 0xEE,
			(byte) 0xD3, (byte) 0xEC, (byte) 0x11, (byte) 0x52, (byte) 0xA5 };
	static byte[] array0 = { (byte) 0xA5, (byte) 0x10, };
	private static final String TAG = "ThreadReadMessage";
	private FTDriver ftDriver;
	private boolean stop, bildUebertragungGestartet = false;
	private ArrayList<ListenerSensorData> SensorObserver = new ArrayList<ListenerSensorData>();
	private static ThreadReadMessage instance;
	MainActivity mainactivity;
	private ByteArrayBuffer bildBuffer = new ByteArrayBuffer(9999999);
	private String debug = "debug";
	private byte[] messageblockasd;

	public ThreadReadMessage(FTDriver ftDriver, MainActivity mainactivity) {
		Log.i(TAG, "Thread gestartet");
		this.ftDriver = ftDriver;
		instance = this;
		this.mainactivity = mainactivity;
	}

	public static ThreadReadMessage getInstance() {
		return instance;
	}

	public void run() {
		stop = false;
		ByteArrayBuffer buffer = new ByteArrayBuffer(500);
		byte[] startbyteblock = null;
		int l;
		debug = "";
		while (!stop) {
			try {
				byte[] messageBlock = new byte[500];
				if (startbyteblock == null) {
					// messageBlock = array0;
					l = ftDriver.read(messageBlock);
					// l = messageBlock.length;
					startbyteblock = getstartByteBlock(messageBlock, l);

				} else {
					startbyteblock = getstartByteBlock(startbyteblock,
							startbyteblock.length);

				}
				if (startbyteblock == null)
					continue;

				startbyteblock = readuntilMessageBlockComplete(buffer,
						startbyteblock);
				debug = "messagecomplete";

				messageBlock = buffer.toByteArray();
				debug = "buffer.tobytearray";

				buffer.clear();
				messageblockasd = messageBlock;

				messageAuswerten(messageBlock);

			} catch (Exception f) {
				Log.wtf(TAG, f);
				f.printStackTrace();
				buffer.clear();
				toastErrorMessage(f.toString() + " " + debug + "\n");
			}
		}

	}

	private void messageAuswerten(byte[] messageBlock) {
		debug = "messageAuswerten 1+messageBlock";
		byte[] recievedData = new byte[messageBlock.length - 3];
		int length = messageBlock[2] & 0xFF;
		if ((messageBlock.length - 4) > 0) {
			debug = "messageAuswerten 2" + "messageblocklength "
					+ messageBlock.length + " recieDatalength "
					+ recievedData.length + " messageblog"
					+ bytesToHex(messageBlock);

			System.arraycopy(messageBlock, 3, recievedData, 0,
					recievedData.length - 1);

		}
		// toastErrorMessage(debug + bytesToHex(messageBlock));
		if (Integer.toHexString(messageBlock[0] & 0xFF).equalsIgnoreCase("A5")) {
			if (String
					.format("%8s",
							Integer.toBinaryString(messageBlock[1] & 0xFF))
					.replace(' ', '0')
					.equalsIgnoreCase(
							String.format(
									"%8s",
									Integer.toBinaryString(FunkMessage.ADRESSE_TABLET))
									.replace(' ', '0'))) {

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

				updateSensorListener(pressure, latitude, longitude, altitude,
						speed, accuracy, roll, pitch, azimuth, battery);
			}

			debug = "messageAuswerten 3";

			// if (messageBlock[messageBlock.length - 1] ==
			// CRC8.calc(
			// recievedData, recievedData.length)) {

			if (String
					.format("%8s",
							Integer.toBinaryString(messageBlock[1] & 0xFF))
					.replace(' ', '0')
					.equalsIgnoreCase(
							String.format(
									"%8s",
									Integer.toBinaryString(FunkMessage.ADRESSE_TABLET_BILD))
									.replace(' ', '0'))) {
				debug = "messageAuswerten 5";

				addBildData(recievedData, length);
			}

		}

	}

	private void addBildData(byte[] recievedData, int length) {

		debug = "addbilddata";
		if (length == 0) {
			toastErrorMessage("length==0");
			bildUebertragungGestartet = !bildUebertragungGestartet;

			if (!bildUebertragungGestartet && bildBuffer.length() != 0) {
				updateBild(bildBuffer.toByteArray());
				bildBuffer.clear();
			}

		} else {
			debug = "addbilddata buildbuffer append"
					+ bildBuffer.toByteArray().length;

			bildBuffer.append(recievedData, 0, recievedData.length);
		}
	}

	private void updateBild(byte[] recievedData) {
		toastErrorMessage("bildübertragung fertig länge " + recievedData.length);
	}

	private byte[] readuntilMessageBlockComplete(ByteArrayBuffer buffer,
			byte[] startbyteblock) {
		int l = startbyteblock.length;
		boolean lengthreaded = false;
		int length = 0;
		ByteArrayBuffer breaded = new ByteArrayBuffer(500);

		ByteArrayBuffer returnStartbyte = new ByteArrayBuffer(500);
		// falls startbytblock ist das gesammte packet

		if (l >= 4) {
			length = startbyteblock[2] & 0xFF;
			lengthreaded = true;

			if (l >= (length + 4)) {
				if (l == (length + 4)) {
					buffer.append(startbyteblock, 0, startbyteblock.length);
					return null;
				}
				if (l > (length + 4)) {
					buffer.append(startbyteblock, 0, length + 4);
					returnStartbyte.append(startbyteblock, length + 4,
							startbyteblock.length - length - 4);
					return returnStartbyte.toByteArray();
				}
			}
		}
		buffer.append(startbyteblock, 0, startbyteblock.length);
		breaded.append(startbyteblock, 0, startbyteblock.length);
		while (!stop) {
			byte[] block = new byte[20];
			int readed = ftDriver.read(block);
			// int readed = array1.length;
			l += readed;

			breaded.append(block, 0, block.length);
			if (l >= 4 && !lengthreaded) {
				length = breaded.toByteArray()[2] & 0xFF;
				lengthreaded = true;
			}
			if (lengthreaded) {
				if (l >= (length + 4)) {
					if (l == (length + 4)) {
						buffer.append(block, 0, readed);
						return null;
					}
					debug = debug + "\n\nreadmessage01 leghth " + length
							+ " readed - (l - length - 4) "
							+ (l - (length - 4)) + "\nbuffer: "
							+ bytesToHex(buffer.toByteArray()) + "\nblock: "
							+ bytesToHex(block) + "\nstartblock: "
							+ bytesToHex(startbyteblock) + "\nl: ";

					if (l > (length + 4)) {
						buffer.append(block, 0, readed - (l - (length + 4)));
						debug = "readmessage1";
						returnStartbyte.append(block,
								readed - (l - length - 4), (l - (length + 4)));

						debug = "readmessage2s";
						return returnStartbyte.toByteArray();
					}

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
				ByteArrayBuffer b = new ByteArrayBuffer(1000);
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

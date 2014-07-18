package com.example.zeppelin;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.hardware.usb.UsbManager;
import android.os.BatteryManager;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends Activity implements SensorEventListener {
	private static final int SENDING_INTERVALL = 1000;
	private static final String ACTION_USB_PERMISSION = "jp.ksksue.tutorial.USB_PERMISSION";
	
	private SensorManager sensorManager;
	private Sensor sensor_pressure;
	private Sensor sensor_Accelerometer;
	private Sensor sensor_MagneticField;
	
	private float[] values_Accelerometer;
	private float[] values_MagneticField;
	private float[] matrix_R;
	private float[] matrix_I;
	private float[] matrix_Values;
	
	private int azimuth;
	private int pitch;
	private int roll;

	private TextView text_pressure;
	private TextView text_longitude;
	private TextView text_latitude;
	private TextView text_altitude;
	private TextView text_speed;
	private TextView text_accuracy;
	private TextView text_battery;
	private TextView text_azimuth;
	private TextView text_pitch;
	private TextView text_roll;
	
	private int batteryLevel = -1;

	private ArrayList<Float> average_pressure;

	private NumberFormat numberFormatPressure;
	private NumberFormat numberFormatGPS;

	private GPS_Listener gps_listener;

	public static boolean sending = true;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		FTDriver mSerial = new FTDriver((UsbManager) getSystemService(Context.USB_SERVICE));

		PendingIntent permissionIntent = PendingIntent.getBroadcast(this, 0, new Intent(ACTION_USB_PERMISSION), 0);
		mSerial.setPermissionIntent(permissionIntent);
		mSerial.begin(FTDriver.BAUD57600);

		sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
		sensor_pressure = sensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE);
		sensor_Accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		sensor_MagneticField = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
		
		values_Accelerometer = new float[3];
		values_MagneticField = new float[3];

		matrix_R = new float[9];
		matrix_I = new float[9];
		matrix_Values = new float[3];

		text_pressure = (TextView) findViewById(R.id.text_pressure);
		text_longitude = (TextView) findViewById(R.id.text_longitude);
		text_latitude = (TextView) findViewById(R.id.text_latitude);
		text_altitude = (TextView) findViewById(R.id.text_altitude);
		text_speed = (TextView) findViewById(R.id.text_speed);
		text_accuracy = (TextView) findViewById(R.id.text_accuracy);
		text_battery = (TextView) findViewById(R.id.text_battery);
		text_azimuth = (TextView) findViewById(R.id.text_azimuth);
		text_pitch = (TextView)findViewById(R.id.text_pitch);
		text_roll = (TextView)findViewById(R.id.text_roll);

		average_pressure = new ArrayList<Float>();

		numberFormatPressure = NumberFormat.getInstance();
		numberFormatPressure.setMinimumFractionDigits(2);
		numberFormatPressure.setMaximumFractionDigits(2);

		numberFormatGPS = NumberFormat.getInstance();
		numberFormatGPS.setMinimumFractionDigits(5);
		numberFormatGPS.setMaximumFractionDigits(5);

		gps_listener = GPS_Listener.getInstance(this);
		
		new ReadSensorDataThread().start();
		
		new ThreadSendMessage(mSerial, getFragmentManager()).start();
		new ThreadReadMessage(mSerial, this, getFragmentManager()).start();
	}
	
	private BroadcastReceiver batteryInfoReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			batteryLevel = intent.getIntExtra(BatteryManager.EXTRA_LEVEL,0);
			text_battery.setText("Battery: " + batteryLevel + "%");
		}
	};

	@Override
	public void onResume() {
		sensorManager.registerListener(this, sensor_pressure,SensorManager.SENSOR_DELAY_NORMAL);
		sensorManager.registerListener(this, sensor_Accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
		sensorManager.registerListener(this, sensor_MagneticField, SensorManager.SENSOR_DELAY_NORMAL);
		registerReceiver(this.batteryInfoReceiver,	new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
		super.onResume();
	}

	public void onPause() {
		sensorManager.unregisterListener(this, sensor_pressure);
		sensorManager.unregisterListener(this, sensor_Accelerometer);
		sensorManager.unregisterListener(this, sensor_MagneticField);
		unregisterReceiver(batteryInfoReceiver);
		super.onPause();
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {

	}

	@Override
	public void onSensorChanged(SensorEvent event) {

		switch (event.sensor.getType()) {
		case Sensor.TYPE_PRESSURE:
			average_pressure.add(event.values[0]);
			break;
		case Sensor.TYPE_ACCELEROMETER:
			for (int i = 0; i < 3; i++) {
				values_Accelerometer[i] = event.values[i];
			}
			break;
		case Sensor.TYPE_MAGNETIC_FIELD:
			for (int i = 0; i < 3; i++) {
				values_MagneticField[i] = event.values[i];
			}
			break;
		}
		
		boolean success = SensorManager.getRotationMatrix(matrix_R, matrix_I,values_Accelerometer, values_MagneticField);
		
		if (success) {
			SensorManager.getOrientation(matrix_R, matrix_Values);
			
//			System.out.println("Azi ohne: " + matrix_Values[0] + ", Azi mit: " + (int)matrix_Values[0] + ", Azi Deg: " + Math.toDegrees(matrix_Values[0]));

			azimuth = (int)Math.toDegrees(matrix_Values[0]) - 180;
			pitch = (int)Math.toDegrees(matrix_Values[1]);
			roll = (int)Math.toDegrees(matrix_Values[2]);

		}
	}
	
	public void restartApp(View view){
		Intent mStartActivity = new Intent(this, MainActivity.class);
		int mPendingIntentId = 123456;
		PendingIntent mPendingIntent = PendingIntent.getActivity(this, mPendingIntentId,    mStartActivity, PendingIntent.FLAG_CANCEL_CURRENT);
		AlarmManager mgr = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
		mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 100, mPendingIntent);
		System.exit(0);
	}

	protected class ReadSensorDataThread extends Thread {
		public void run() {
			while (sending) {
				
				HashMap<String, Double> location = gps_listener.getLocation();
				final double av_latitude = location.get("Latitude");
				final double av_longitude = location.get("Longitude");
				
				final float av_speed = gps_listener.getSpeed();
				final double av_altitude = gps_listener.getAltitude();
				final float av_accuracy = gps_listener.getAccuracy();
				
				final float av_pressure = getAveragePressure();
				
				CommunicationData.getInstance().sendSensorData(av_pressure, av_latitude, av_longitude, av_altitude, av_speed, av_accuracy, batteryLevel, azimuth, pitch, roll);
				
				runOnUiThread(new Runnable() {

					@Override
					public void run() {
						text_pressure.setText("Pressure: "+ numberFormatPressure.format(av_pressure)+ " hPa");
						text_latitude.setText("Latitude: "+ numberFormatGPS.format(av_latitude));
						text_longitude.setText("Longitude: "+ numberFormatGPS.format(av_longitude));
						text_speed.setText("Speed: "+ numberFormatPressure.format(av_speed)+ " m/s");
						text_accuracy.setText("Accuracy: auf " + av_accuracy+ " m genau");
						text_altitude.setText("Altitude: "+ numberFormatPressure.format(av_altitude)+ " m über NN");
						text_azimuth.setText("Azimuth: " + String.valueOf(azimuth)+"°");
						text_pitch.setText("Pitch: " + String.valueOf(pitch)+"°");
						text_roll.setText("Roll: " + String.valueOf(roll)+"°");
					}
				});

				try {
					Thread.sleep(SENDING_INTERVALL);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}

		private float getAveragePressure() {
			float average = 0;

			for (float f : average_pressure) {
				average += f;
			}
			average = average / average_pressure.size();
			average_pressure = new ArrayList<Float>();

			return average;
		}
	}

}

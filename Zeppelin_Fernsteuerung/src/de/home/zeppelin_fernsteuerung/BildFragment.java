package de.home.zeppelin_fernsteuerung;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.nio.ByteBuffer;
import java.util.UUID;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class BildFragment extends Fragment {
	/** Called when the activity is first created. */
	private BluetoothAdapter mBluetoothAdapter = null;
	private static final UUID MY_UUID = UUID
			.fromString("fa87c0d0-afac-11de-8a39-0800200c9a66");

	private static final String ADDRESS = "98:52:B1:10:1F:D2"; // Justus' Handy
	// private static final String ADDRESS = "10:68:3F:E1:11:77"; //Nexus 4
	private static String MY_MAC;

	private byte[] buffer = new byte[256];
	private ImageView image;
	private ProgressBar progressBar;

	private TextView txt_percentage;
	private TextView txt_timeEllapsed;

	private Button getPictureButton;
	private static final String TAG = "RecievePictureTask";
	MainActivity mainActivity;

	public BildFragment(MainActivity mainActivity) {
		this.mainActivity = mainActivity;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View rootView = inflater.inflate(R.layout.fragment_bild, container,
				false);

		return rootView;

	}

	public void init() {
		getPictureButton = (Button) mainActivity
				.findViewById(R.id.buttonActivate);
		initBluetoothAdapter();
		getPictureButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				sendMac();
			}
		});
		/*
		 * Hier ist die Image-View
		 */
		image = (ImageView) mainActivity.findViewById(R.id.imageView1);

		txt_percentage = (TextView) mainActivity
				.findViewById(R.id.percentageREAD);
		txt_percentage.setText("Bereit");

		txt_timeEllapsed = (TextView) mainActivity
				.findViewById(R.id.time_ellapsedREAD);
		txt_timeEllapsed.setText("0:00");

		progressBar = (ProgressBar) mainActivity
				.findViewById(R.id.progressBarREAD);

	}

	private void initBluetoothAdapter() {
		mBluetoothAdapter = null;
		mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		if (mBluetoothAdapter == null) {
			Toast.makeText(mainActivity, "Bluetooth is not available.",
					Toast.LENGTH_LONG).show();
			finish();
			return;
		}

		if (!mBluetoothAdapter.isEnabled()) {
			Toast.makeText(mainActivity,
					"Please enable your BT and re-run this program.",
					Toast.LENGTH_LONG).show();
			finish();
			return;
		}

		if ((mBluetoothAdapter != null) && (mBluetoothAdapter.isEnabled())) {
			MY_MAC = BluetoothAdapter.getDefaultAdapter().getAddress();
		}

		getPictureButton.setEnabled(true);
	}

	public void sendMac() {
		getPictureButton.setEnabled(false);
		new SendMacTask().execute();
	}

	private void finish() {
		// TODO Auto-generated method stub

	};

	private class RecievePictureTask extends AsyncTask<URL, Integer, Long> {
		private BluetoothServerSocket mmServerSocket = null;
		private BluetoothSocket socket = null;
		private InputStream mmInStream = null;
		private String device = null;
		private byte[] picture = null;
		private boolean countTime = true;
		private int time_ellapsed = 0;

		private static final String TAG = "RecievePictureTask";

		@Override
		protected Long doInBackground(URL... params) {
			Log.i(TAG, "Start Listening for Picture");
			BluetoothServerSocket tmp = null;
			try {
				Log.i(TAG, "1");
				tmp = mBluetoothAdapter.listenUsingRfcommWithServiceRecord(
						"Bluetooth", MY_UUID);
				Log.i(TAG, "2");
			} catch (IOException e) {
				System.out.println("Accept Data Failure 1");
			}
			mmServerSocket = tmp;
			try {
				Log.i(TAG, "3");
				socket = mmServerSocket.accept();
				Log.i(TAG, "4");
			} catch (IOException e) {
				System.out.println("Accept Data Failure 2");
			}
			Log.i(TAG, "5");
			device = socket.getRemoteDevice().getName();
			System.out.println("Device: " + device);
			InputStream tmpIn = null;
			Log.i(TAG, "6");
			try {
				tmpIn = socket.getInputStream();
			} catch (IOException e) {
				System.out.println("Accept Data Failure 3");
			}
			Log.i(TAG, "7");
			mmInStream = tmpIn;
			try {
				new TimerThread().start();
				int i = 0;
				ByteArrayOutputStream stream = new ByteArrayOutputStream();
				while (true) {
					try {
						mmInStream.read(buffer);
						stream.write(buffer);
						publishProgress(stream.size());

						if (i == 0) {
							byte[] temp = stream.toByteArray();
							final int size = ByteBuffer.wrap(temp).getInt();
							Log.e("LÄNGE", "Dateigröße: " + size);

							progressBar.setMax(size);
							Log.e("ProgressBar", "Max: " + progressBar.getMax());

							stream = new ByteArrayOutputStream();
						}

						i++;
					} catch (IOException e) {
						Log.e("READ", "Übertragung beendet");
						break;
					}

				}
				Log.e("Stream", "Umwandlung wird gestartet");

				picture = stream.toByteArray();
				stream.close();

				if (socket != null) {
					try {
						System.out.println("close");
						mmServerSocket.close();
						System.out.println("closed");

					} catch (IOException e) {
						System.out.println("Accept Data Failure 4");
					}
				}
			} catch (Exception e) {
				countTime = false;
				System.out.println("Accept Data Failure 5");
				e.printStackTrace();
			}
			return (long) picture.length;
		}

		protected void onProgressUpdate(Integer... progress) {
			progressBar.setProgress(progress[0]);

			int p = ((int) ((progress[0] / (float) progressBar.getMax()) * 100));
			txt_percentage.setText(p + "%");
		}

		protected void onPostExecute(Long result) {
			countTime = false;
			txt_percentage.setText("Übertragung erfolgreich beendet");
			Bitmap bmp = BitmapFactory.decodeByteArray(picture, 0,
					picture.length);
			image.setImageBitmap(bmp);
			getPictureButton.setEnabled(true);
		}

		protected class TimerThread extends Thread {
			public void run() {
				while (countTime) {
					time_ellapsed++;

					mainActivity.runOnUiThread(new Runnable() {

						@Override
						public void run() {
							if (time_ellapsed < 10) {
								txt_timeEllapsed.setText("0:0" + time_ellapsed);
							} else if (time_ellapsed > 9 && time_ellapsed < 60) {
								txt_timeEllapsed.setText("0:" + time_ellapsed);
							}

						}
					});

					try {
						Thread.sleep(1000);
					} catch (Exception e) {

					}

				}
			}
		}

	}

	private class SendMacTask extends AsyncTask<URL, Integer, Long> {
		private BluetoothDevice device = null;
		private BluetoothSocket btSocket = null;
		private OutputStream outStream = null;
		private static final String TAG = "SendMacTask";

		@Override
		protected Long doInBackground(URL... params) {
			Log.i(TAG, "Send MAC");
			mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
			device = mBluetoothAdapter.getRemoteDevice(ADDRESS);
			Log.e(TAG, "Device: " + device.getName());
			try {
				btSocket = device.createRfcommSocketToServiceRecord(MY_UUID);
				Log.e(TAG, "Socket created");
			} catch (Exception e) {
				Log.e(TAG, "CreateSocket with UUID Failure");
			}
			mBluetoothAdapter.cancelDiscovery();
			Log.e(TAG, "Cancel Discovery");
			try {
				btSocket.connect();
				Log.e(TAG, "Connected");
			} catch (IOException e) {
				e.printStackTrace();
				try {
					btSocket.close();
				} catch (IOException e2) {
					e.printStackTrace();
				}
			}

			try {
				outStream = btSocket.getOutputStream();
			} catch (IOException e) {
			}

			Log.i(TAG, "PreExecute Finished");

			try {
				Log.e(TAG, "Sende MAC: " + MY_MAC);
				byte[] macByte = MY_MAC.getBytes();
				Log.e(TAG, "MAC Byte length: " + macByte.length);

				outStream.write(macByte);
				outStream.flush();
				outStream.close();
			} catch (Exception e) {
				e.printStackTrace();
				try {
					outStream.close();
				} catch (Exception f) {
					f.printStackTrace();
				}
			}

			device = null;
			btSocket = null;
			outStream = null;

			return 1l;
		}

		protected void onPostExecute(Long result) {
			Log.e(TAG, "MAC ADRESSE GESENDET");
			//
			// try{
			// Thread.sleep(2000);
			// } catch(Exception e){
			// e.printStackTrace();
			// }
			new RecievePictureTask().execute();
		}

	}

}
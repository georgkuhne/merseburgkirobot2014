package de.home._zeppelin_fernsteuerung.pictures;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.annotation.SuppressLint;
import android.os.Environment;
import android.widget.Toast;
import de.home.zeppelin_fernsteuerung.MainActivity;
import de.home.zeppelin_fernsteuerung.communication.ListenerPictureData;
import de.home.zeppelin_fernsteuerung.communication.ThreadReadMessage;

public class PictureManager implements ListenerPictureData {
	private static final int MEDIA_TYPE_IMAGE = 1;
	private static final int MEDIA_TYPE_VIDEO = 2;

	private MainActivity mainactivity;
	private static final String SAVING_DIRECTORY = "Zeppelin-Controller";

	public PictureManager(MainActivity main) {
		mainactivity = main;
		ThreadReadMessage.getInstance().addPictureDataListener(this);
	}

	String debug = "";

	@Override
	public void udatePicture(byte[] data) {
		toastErrorMessage("updatepicture");

		try {
			debug = "update1";
			File pictureFileDir = getOutputMediaFile(MEDIA_TYPE_IMAGE);
			debug = "update2";

			toastErrorMessage("image" + pictureFileDir.getPath());

			if (!pictureFileDir.exists() && !pictureFileDir.mkdirs()) {

				toastErrorMessage("Can't create directory to save image.");

				return;

			}
			debug = "update3";

			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyymmddhhmmss");
			String date = dateFormat.format(new Date());
			debug = "update4";

			String photoFile = "Picture_" + date + ".jpg";
			String filename = pictureFileDir.getPath() + File.separator
					+ photoFile;

			debug = "update5";

			File pictureFile = new File(filename);

			FileOutputStream fos = new FileOutputStream(pictureFile);
			fos.write(data);
			debug = "update6";
			fos.flush();
			fos.close();

			toastErrorMessage("New Image saved:" + photoFile);
		} catch (Exception e) {
			toastErrorMessage(e.toString() + "1 " + debug);

		}
	}

	@SuppressLint("SimpleDateFormat")
	private File getOutputMediaFile(int type) {
		try {

			// To be safe, you should check that the SDCard is mounted
			// using Environment.getExternalStorageState() before doing this.
			String asd = Environment.getExternalStorageState();
			File mediaStorageDir = new File(
					Environment
							.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
					SAVING_DIRECTORY);
			// This location works best if you want the created images to be
			// shared
			// between applications and persist after your app has been
			// uninstalled.

			// Create the storage directory if it does not exist
			if (!mediaStorageDir.exists()) {
				if (!mediaStorageDir.mkdirs()) {
					toastErrorMessage("failed to make dir");
					return null;
				}
			}

			// Create a media file name
			String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss")
					.format(new Date());
			File mediaFile;
			if (type == MEDIA_TYPE_IMAGE) {
				mediaFile = new File(mediaStorageDir.getPath() + File.separator
						+ "IMG_" + timeStamp + ".jpg");

				// Toast.makeText(context, "IMG_"+ timeStamp +
				// ".jpg gepeichert",
				// Toast.LENGTH_SHORT).show();

			} else if (type == MEDIA_TYPE_VIDEO) {
				mediaFile = new File(mediaStorageDir.getPath() + File.separator
						+ "VID_" + timeStamp + ".mp4");
			} else {
				return null;
			}

			return mediaFile;
		} catch (Exception e) {
			toastErrorMessage(e.toString() + "1 " + debug);
			return null;
		}
	}

	private void toastErrorMessage(final String messages) {
		mainactivity.runOnUiThread(new Runnable() {
			public void run() {
				Toast.makeText(mainactivity, messages, Toast.LENGTH_LONG)
						.show();
			}
		});
	}
}

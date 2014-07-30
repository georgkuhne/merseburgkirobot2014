package de.home.zeppelin_fernsteuerung;

import java.io.File;
import java.text.DecimalFormat;

import org.mapsforge.core.model.LatLong;
import org.mapsforge.map.android.graphics.AndroidGraphicFactory;
import org.mapsforge.map.android.util.AndroidUtil;
import org.mapsforge.map.android.view.MapView;
import org.mapsforge.map.layer.cache.TileCache;
import org.mapsforge.map.layer.renderer.TileRendererLayer;
import org.mapsforge.map.rendertheme.InternalRenderTheme;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.ActionBar.TabListener;
import android.app.AlertDialog;
import android.app.FragmentTransaction;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.hardware.usb.UsbManager;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;
import android.widget.ToggleButton;
import de.home._zeppelin_fernsteuerung.pictures.PictureManager;
import de.home.zeppelin_fernsteuerung.adapter.TabsPagerAdapter;
import de.home.zeppelin_fernsteuerung.communication.FTDriver;
import de.home.zeppelin_fernsteuerung.communication.ThreadReadAndSendMessage;
import de.home.zeppelin_fernsteuerung.communication.ThreadReadMessage;
import de.home.zeppelin_fernsteuerung.controler.Controler;
import de.home.zeppelin_fernsteuerung.widgets.joystick.JoystickView;
import de.home.zeppelin_fernsteuerung.widgets.verticalseekbar.VerticalSeekBar;

@SuppressLint("NewApi")
public class MainActivity extends FragmentActivity implements TabListener {
	private TileCache tileCache;
	private TileRendererLayer tileRendererLayer;// [FTDriver] Permission String
	private static final String ACTION_USB_PERMISSION = "jp.ksksue.tutorial.USB_PERMISSION";
	private static final String MAPFILE = "germany.map";
	ToggleButton tb_connect;
	ThreadReadMessage TRM;
	private ViewPager viewPager;
	private TabsPagerAdapter mAdapter;
	private ActionBar actionBar;
	public VerticalSeekBar seekbar1, seekbar2;
	private Button b_reset, b_fix;
	public JoystickView joystick;
	// Tab titles
	private String[] tabs = { "Karte", "Bild", "Statistik" };

	DecimalFormat df = new DecimalFormat("0.00");
	private PendingIntent permissionIntent;

	private FTDriver ftDriver;

	private Controler controler;
	private MapView mapView;
	private ThreadReadAndSendMessage threadReadAndSendMessage;
	private PictureManager picManager;
	private boolean mapIsInitialized = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.layout_main);
		if (savedInstanceState == null) {
			getSupportFragmentManager().beginTransaction()
					.add(R.id.container, new PlaceholderFragment()).commit();
		}
		AndroidGraphicFactory.createInstance(this.getApplication());

		// Initialization
		viewPager = (ViewPager) findViewById(R.id.pager);
		actionBar = getActionBar();
		mAdapter = new TabsPagerAdapter(getSupportFragmentManager(), this);
		seekbar1 = (VerticalSeekBar) findViewById(R.id.ProgressBar01);
		seekbar2 = (VerticalSeekBar) findViewById(R.id.ProgressBar02);
		b_reset = (Button) findViewById(R.id.button_reset);
		b_fix = (Button) findViewById(R.id.buttonfix);
		viewPager.setAdapter(mAdapter);
		actionBar.setHomeButtonEnabled(false);
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		tb_connect = (ToggleButton) findViewById(R.id.ButtonConnect);
		tb_connect.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (tb_connect.isChecked())
					if (connect()) {
						runthreads();
					} else {
						tb_connect.setChecked(false);
					}
				else {
					disconnect();

				}
			}
		});
		joystick = (JoystickView) findViewById(R.id.joystick);

		// Adding Tabs
		for (String tab_name : tabs) {
			actionBar.addTab(actionBar.newTab().setText(tab_name)
					.setTabListener(this));
		}

		/**
		 * on swiping the viewpager make respective tab selected
		 * */
		viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

			@Override
			public void onPageSelected(int position) {
				actionBar.setSelectedNavigationItem(position);
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
			}

			@Override
			public void onPageScrollStateChanged(int arg0) {
			}
		});

		b_reset.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				seekbar1.setProgress(127);
				seekbar2.setProgress(127);

			}
		});

		b_fix.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				init();
				if (joystick.isAutoReturnToCenter() == true) {
					b_fix.setText("handle fix");
					joystick.setAutoReturnToCenter(false);
				} else {
					b_fix.setText("handle not fix");
					joystick.setAutoReturnToCenter(true);
				}

			}
		});

		// Start einer Asynchronen Task

		controler = new Controler(seekbar1, seekbar2, joystick);

		permissionIntent = PendingIntent.getBroadcast(this, 0, new Intent(
				ACTION_USB_PERMISSION), 0);

		ftDriver = new FTDriver(
				(UsbManager) getSystemService(Context.USB_SERVICE));

		ftDriver.setPermissionIntent(permissionIntent);
		threadReadAndSendMessage = new ThreadReadAndSendMessage(ftDriver);
		// controler.start();
		TRM = new ThreadReadMessage(ftDriver, this);
		// TRM.start();
		picManager = new PictureManager(this);
	}

	protected void runthreads() {
		// controler.start();
		// threadReadAndSendMessage.start();
		TRM.start();

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is
		// present.4
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class PlaceholderFragment extends Fragment {

		public PlaceholderFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_main, container,
					false);
			return rootView;
		}
	}

	@Override
	public void onTabReselected(Tab tab, FragmentTransaction ft) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onTabSelected(Tab tab, FragmentTransaction ft) {
		viewPager.setCurrentItem(tab.getPosition());
	}

	@Override
	public void onTabUnselected(Tab tab, FragmentTransaction ft) {
		// TODO Auto-generated method stub

	}

	protected class ThreadConnection extends Thread {
		boolean connected = false;
		MainActivity mainActivity;

		public ThreadConnection(MainActivity mainActivity1) {
			mainActivity = mainActivity1;
		}

		@Override
		public void run() {
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			while (!connected) {

			}
			super.run();
		}

	}

	protected boolean connect() {
		if (ftDriver.begin(FTDriver.BAUD57600)) {

			Toast.makeText(this, "connected", Toast.LENGTH_SHORT).show();
			return true;
		} else {
			// Put up the Yes/No message box
			AlertDialog ad = new AlertDialog.Builder(this).create();
			ad.setCancelable(false); // This blocks the 'BACK' button
			ad.setMessage("Connection failed, please try again.");
			ad.setButton("OK", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
				}
			});
			ad.show();
		}
		return false;

	}

	@Override
	protected void onDestroy() {
		this.tileCache.destroy();
		controler.end();
		threadReadAndSendMessage.end();
		TRM.end();
		ftDriver.end();

		super.onDestroy();
	}

	private void disconnect() {
		controler.end();
		threadReadAndSendMessage.end();
		TRM.end();

		ftDriver.end();
	}

	public void init() {
		if (mapIsInitialized)
			return;
		mAdapter.init();
		// initMap();
		mapIsInitialized = true;

	}

	public void initMap() {

		mapView = (MapView) findViewById(R.id.mapView);
		File file = getMapFile();

		this.mapView.setClickable(true);
		this.mapView.getMapScaleBar().setVisible(true);
		this.mapView.setBuiltInZoomControls(true);
		this.mapView.getMapZoomControls().setZoomLevelMin((byte) 10);
		this.mapView.getMapZoomControls().setZoomLevelMax((byte) 20);

		// create a tile cache of suitable size
		this.tileCache = AndroidUtil.createTileCache(this, "mapcache",
				mapView.getModel().displayModel.getTileSize(), 1f,
				this.mapView.getModel().frameBufferModel.getOverdrawFactor());

		this.mapView.getModel().mapViewPosition.setCenter(new LatLong(
				52.517037, 13.38886));
		this.mapView.getModel().mapViewPosition.setZoomLevel((byte) 12);
		// tile renderer layer using internal render theme
		this.tileRendererLayer = new TileRendererLayer(tileCache,
				this.mapView.getModel().mapViewPosition, false,
				AndroidGraphicFactory.INSTANCE);
		tileRendererLayer.setMapFile(getMapFile());
		tileRendererLayer.setXmlRenderTheme(InternalRenderTheme.OSMARENDER);
		// only once a layer is associated with a mapView the rendering starts
		this.mapView.getLayerManager().getLayers().add(tileRendererLayer);
	}

	private File getMapFile() {
		File file = new File(Environment.getExternalStorageDirectory(), MAPFILE);
		Toast.makeText(getApplicationContext(),
				file.getPath() + " " + file.exists(), Toast.LENGTH_LONG).show();
		return file;
	}

	@Override
	protected void onStop() {
		super.onStop();
		this.mapView.getLayerManager().getLayers()
				.remove(this.tileRendererLayer);
		this.tileRendererLayer.onDestroy();
	}

}

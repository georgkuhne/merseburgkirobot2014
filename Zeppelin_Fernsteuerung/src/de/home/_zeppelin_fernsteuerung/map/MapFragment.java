package de.home._zeppelin_fernsteuerung.map;

import java.io.File;

import org.mapsforge.core.graphics.Paint;
import org.mapsforge.core.graphics.Style;
import org.mapsforge.core.model.LatLong;
import org.mapsforge.core.model.MapPosition;
import org.mapsforge.core.model.Point;
import org.mapsforge.map.android.graphics.AndroidGraphicFactory;
import org.mapsforge.map.android.util.AndroidUtil;
import org.mapsforge.map.android.view.MapView;
import org.mapsforge.map.layer.cache.TileCache;
import org.mapsforge.map.layer.overlay.Marker;
import org.mapsforge.map.layer.renderer.TileRendererLayer;
import org.mapsforge.map.rendertheme.InternalRenderTheme;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import de.home.zeppelin_fernsteuerung.MainActivity;
import de.home.zeppelin_fernsteuerung.R;
import de.home.zeppelin_fernsteuerung.communication.ListenerSensorData;
import de.home.zeppelin_fernsteuerung.communication.ThreadReadMessage;

public class MapFragment extends Fragment implements ListenerSensorData {
	MainActivity mainActivity;
	MapView mapView;
	private TileCache tileCache;
	private TileRendererLayer tileRendererLayer;// [FTDriver] Permission String
	private static final String MAPFILE = "germany.map";
	private boolean isInitialized = false;
	private Marker marker;

	public MapFragment(MainActivity mainActivity) {
		this.mainActivity = mainActivity;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View rootView = inflater.inflate(R.layout.fragment_map, container,
				false);

		return rootView;
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		return super.onContextItemSelected(item);
	}

	public void init() {

		if (isInitialized) {
			updateSensorData(3, 51.3428358, 11.972070555555556, 3, 3, 3, 3, 3,
					3, 3);
			return;
		}

		ThreadReadMessage.getInstance().addSensorListener(this);
		mapView = (MapView) mainActivity.findViewById(R.id.mapView);
		File file = getMapFile();
		if (!file.exists()) {
			Toast.makeText(mainActivity, "Cant find germany.map",
					Toast.LENGTH_LONG);
			return;
		}
		this.mapView.setClickable(true);
		this.mapView.getMapScaleBar().setVisible(true);
		this.mapView.setBuiltInZoomControls(true);
		this.mapView.getMapZoomControls().setZoomLevelMin((byte) 10);
		this.mapView.getMapZoomControls().setZoomLevelMax((byte) 20);

		// create a tile cache of suitable size
		this.tileCache = AndroidUtil.createTileCache(mainActivity, "mapcache",
				mapView.getModel().displayModel.getTileSize(), 1f,
				this.mapView.getModel().frameBufferModel.getOverdrawFactor());
		this.mapView.getModel().mapViewPosition.setCenter(new LatLong(
				11.972070555555556, 51.3428358));

		// tile renderer layer using internal render theme
		this.tileRendererLayer = new TileRendererLayer(tileCache,
				this.mapView.getModel().mapViewPosition, false,
				AndroidGraphicFactory.INSTANCE);
		tileRendererLayer.setMapFile(getMapFile());
		tileRendererLayer.setXmlRenderTheme(InternalRenderTheme.OSMARENDER);
		// only once a layer is associated with a mapView the rendering starts
		this.mapView.getLayerManager().getLayers().add(tileRendererLayer);
		mapView.refreshDrawableState();
		this.mapView.getModel().mapViewPosition.setZoomLevel((byte) 12);
		tileRendererLayer.requestRedraw();
		LatLong latLong1 = new LatLong(11.972070555555556, 51.3428358);
		marker = createTappableMarker(mainActivity, R.drawable.marker_red,
				latLong1);
		mapView.getLayerManager().getLayers().add(marker);

		isInitialized = true;
	}

	private File getMapFile() {
		File file = new File(Environment.getExternalStorageDirectory(), MAPFILE);
		Toast.makeText(mainActivity.getApplicationContext(),
				file.getPath() + " " + file.exists(), Toast.LENGTH_LONG).show();
		return file;
	};

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	@Override
	public void onStop() {

		super.onStop();
	}

	@Override
	public void updateSensorData(float pressure, double latitude,

	double longitude, double altitude, float speed, float accuracy, int roll,
			int pitch, int azimuth, int battery) {

		try {
			if (!isInitialized) {
				return;
			}
			if (longitude == Double.NaN || latitude == Double.NaN)
				return;
			LatLong latlong = new LatLong(latitude, longitude);
			mapView.getModel().mapViewPosition.setMapPosition(new MapPosition(
					latlong, (byte) 19));

			marker.setLatLong(latlong);

			mapView.getLayerManager().getLayers().get(0).requestRedraw();
			tileRendererLayer.requestRedraw();

		} catch (Exception e) {
			Toast.makeText(mainActivity, e.toString(), Toast.LENGTH_LONG)
					.show();
		}
	}

	static Paint createPaint(int color, int strokeWidth, Style style) {
		Paint paint = AndroidGraphicFactory.INSTANCE.createPaint();
		paint.setColor(color);
		paint.setStrokeWidth(strokeWidth);
		paint.setStyle(style);
		return paint;
	}

	static Marker createTappableMarker(Context c, int resourceIdentifier,
			LatLong latLong) {
		Drawable drawable = c.getResources().getDrawable(resourceIdentifier);
		org.mapsforge.core.graphics.Bitmap bitmap = AndroidGraphicFactory
				.convertToBitmap(drawable);
		return new Marker(latLong, bitmap, 0, -bitmap.getHeight() / 2) {
			@Override
			public boolean onTap(LatLong geoPoint, Point viewPosition,
					Point tapPoint) {
				if (contains(viewPosition, tapPoint)) {
					Log.w("Tapp", "The Marker was touched with onTap: "
							+ this.getLatLong().toString());
					return true;
				}
				return false;
			}
		};
	}
}
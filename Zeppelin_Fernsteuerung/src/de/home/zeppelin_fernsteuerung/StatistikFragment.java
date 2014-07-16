package de.home.zeppelin_fernsteuerung;

import de.home.zeppelin_fernsteuerung.communication.ListenerSensorData;
import de.home.zeppelin_fernsteuerung.communication.ThreadReadMessage;
import de.home.zeppelin_fernsteuerung.widgets.verticalseekbar.VerticalSeekBar;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class StatistikFragment extends Fragment implements ListenerSensorData {
	 
	private TextView tvdruck;
	private TextView tvlatitude;
	private TextView tvlongitude;
	private Object rootView;
	private TextView tvaltitude;
	private TextView tvspeed;
	private TextView tvacc;
	private TextView tvroll;
	private TextView tvpitch;
	private TextView tvazi;
	private TextView tvbattery;
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
 
        View rootView = inflater.inflate(R.layout.fragment_statistik, container, false);
               
        tvdruck = (TextView) rootView.findViewById(R.id.tvDruck);
        tvlatitude =   (TextView) rootView.findViewById(R.id.tvlatitude);
        tvlongitude =   (TextView) rootView.findViewById(R.id.tvlongitude);
        tvaltitude =   (TextView) rootView.findViewById(R.id.tvaltitude);
        tvspeed =   (TextView) rootView.findViewById(R.id.tvspeed);
        tvacc=   (TextView) rootView.findViewById(R.id.tvacc);
        tvroll=   (TextView) rootView.findViewById(R.id.tvroll);
        tvpitch=   (TextView) rootView.findViewById(R.id.tvpitch);
        tvazi=   (TextView) rootView.findViewById(R.id.tvazi);
        tvbattery =  (TextView) rootView.findViewById(R.id.tvbattery);
        return rootView;
        
        
    }




	@Override
	public void updateSensorData(final float pressure, final double latitude,
			final double longitude, final double altitude, final float speed, final float accuracy,
			final int roll, final int pitch, final int azimuth, final int battery) {
		
		
		
		getActivity().runOnUiThread(new Runnable() {
			
			
			public void run() {
				tvdruck.setText(""+pressure);
				tvlatitude.setText(""+latitude);
				tvlongitude.setText(""+longitude);
				tvaltitude.setText(""+altitude);
				tvspeed.setText(""+speed);
				tvacc.setText(""+accuracy);
				tvroll.setText(""+roll);
				tvpitch.setText(""+pitch);
				tvazi.setText(""+azimuth);
				tvbattery.setText(""+battery);
			}
		});
	}
    
}
 
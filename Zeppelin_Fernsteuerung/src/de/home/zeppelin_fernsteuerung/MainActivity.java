package de.home.zeppelin_fernsteuerung;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;


public class MainActivity extends ActionBarActivity {
	
	Button ButtonMotorToggle;
	Button ButtonLinks;
	Button ButtonRechts;
	Button ButtonVor;
	Button ButtonRueck;
	Button ButtonRunter;
	Button ButtonHoch;
	VideoView Video1;
	VideoView Video2;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		if (savedInstanceState == null) {
			getSupportFragmentManager().beginTransaction()
					.add(R.id.container, new PlaceholderFragment()).commit();
		}
		
		//Aktivieren der Buttons
		ButtonMotorToggle = (Button) findViewById(R.id.bt_Motor);
		ButtonLinks = (Button) findViewById(R.id.bt_links);
		ButtonRechts = (Button) findViewById(R.id.bt_rechts);
		ButtonVor = (Button) findViewById(R.id.bt_vorschub);
		ButtonRueck =(Button) findViewById(R.id.bt_rueckschub);;
		ButtonRunter = (Button) findViewById(R.id.bt_runter);;
		ButtonHoch = (Button) findViewById(R.id.bt_hoch);;
		Video1 = (VideoView) findViewById(R.id.videoView1);
		Video2 = (VideoView) findViewById(R.id.videoView2);
		
		//Nutzung von Buttons zum Programmstart
		
			ButtonMotorToggle.setOnClickListener(new OnClickListener() {
						
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
					if (ButtonMotorToggle.getText().toString().contains("AUS")) {
						System.out.println(ButtonMotorToggle.getText());
						Toast.makeText(getApplicationContext(), "Motoren starten", Toast.LENGTH_SHORT).show();
						ButtonMotorToggle.setText("Motoren AN");
					} else if (ButtonMotorToggle.getText().toString().contains("AN")) {
						System.out.println(ButtonMotorToggle.getText());
						Toast.makeText(getApplicationContext(), "Motoren stoppen", Toast.LENGTH_SHORT).show();
						ButtonMotorToggle.setText("Motoren AUS");
					}
					
							
			}
			
		}
		);
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.4
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


	
	


}

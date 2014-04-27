package de.home.zeppelin_fernsteuerung;

import java.text.DecimalFormat;

import android.graphics.Canvas;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;




public class MainActivity extends ActionBarActivity {
	
	Button ButtonMotorToggle;
	Button ButtonRunter;
	Button ButtonHoch;
	VideoView Video1;
	VideoView Video2;

	RelativeLayout layout_joystick;
	JoyStickClass js;
	
	DecimalFormat df = new DecimalFormat("0.00");
	
	TextView textView1;
	TextView textView2;
	TextView textView3;
	TextView textView4;
	TextView textView5;
	
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
		ButtonRunter = (Button) findViewById(R.id.bt_runter);;
		ButtonHoch = (Button) findViewById(R.id.bt_hoch);;
		Video1 = (VideoView) findViewById(R.id.videoView1);
		Video2 = (VideoView) findViewById(R.id.videoView2);
		textView1 = (TextView) findViewById(R.id.textView1);
		textView2 = (TextView) findViewById(R.id.textView2);
		textView3 = (TextView) findViewById(R.id.textView3);
		textView4 = (TextView) findViewById(R.id.textView4);
		textView5 = (TextView) findViewById(R.id.textView5);
		
		layout_joystick = (RelativeLayout)findViewById(R.id.layout_joystick);

        js = new JoyStickClass(getApplicationContext()
        		, layout_joystick, R.drawable.image_button);
	    js.setStickSize(150, 150);
	    js.setLayoutSize(400, 400);
	    js.setLayoutAlpha(150);
	    js.setStickAlpha(100);
	    js.setOffset(90);
	    js.setMinimumDistance(50);
		
	    
		textView1.setText("X :");
		textView2.setText("Y :");
		textView3.setText("Angle :");
		textView4.setText("Distance :");
		textView5.setText("Direction :");
	    
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
		
			js.drawStickcreate();
			
			
			
			layout_joystick.setOnTouchListener(new OnTouchListener() {
				public boolean onTouch(View arg0, MotionEvent arg1) {
					
					
					if(arg1.getAction() == MotionEvent.ACTION_DOWN
							|| arg1.getAction() == MotionEvent.ACTION_MOVE) {
						
						js.drawStick(arg1);
						textView1.setText("X : " + String.valueOf(js.getX()));
						textView2.setText("Y : " + String.valueOf(js.getY()));
						textView3.setText("Angle : " + String.valueOf (df.format( js.getAngle())));
						textView4.setText("Distance : " + String.valueOf(df.format(js.getDistance())));
						
						int direction = js.get8Direction();
						if(direction == JoyStickClass.STICK_UP) {
							textView5.setText("Direction : Up");
						} else if(direction == JoyStickClass.STICK_UPRIGHT) {
							textView5.setText("Direction : Up Right");
						} else if(direction == JoyStickClass.STICK_RIGHT) {
							textView5.setText("Direction : Right");
						} else if(direction == JoyStickClass.STICK_DOWNRIGHT) {
							textView5.setText("Direction : Down Right");
						} else if(direction == JoyStickClass.STICK_DOWN) {
							textView5.setText("Direction : Down");
						} else if(direction == JoyStickClass.STICK_DOWNLEFT) {
							textView5.setText("Direction : Down Left");
						} else if(direction == JoyStickClass.STICK_LEFT) {
							textView5.setText("Direction : Left");
						} else if(direction == JoyStickClass.STICK_UPLEFT) {
							textView5.setText("Direction : Up Left");
						} else if(direction == JoyStickClass.STICK_NONE) {
							textView5.setText("Direction : Center");
						}
					} else if(arg1.getAction() == MotionEvent.ACTION_UP) {

						js.drawStick(arg1);
						
						textView1.setText("X : " + String.valueOf(js.getX()));
						textView2.setText("Y : " + String.valueOf(js.getY()));
						textView3.setText("Angle : " + String.valueOf(js.getAngle()));
						textView4.setText("Distance : " + String.valueOf(js.getDistance()));
						
						int direction = js.get8Direction();
						if(direction == JoyStickClass.STICK_UP) {
							textView5.setText("Direction : Up");
						} else if(direction == JoyStickClass.STICK_UPRIGHT) {
							textView5.setText("Direction : Up Right");
						} else if(direction == JoyStickClass.STICK_RIGHT) {
							textView5.setText("Direction : Right");
						} else if(direction == JoyStickClass.STICK_DOWNRIGHT) {
							textView5.setText("Direction : Down Right");
						} else if(direction == JoyStickClass.STICK_DOWN) {
							textView5.setText("Direction : Down");
						} else if(direction == JoyStickClass.STICK_DOWNLEFT) {
							textView5.setText("Direction : Down Left");
						} else if(direction == JoyStickClass.STICK_LEFT) {
							textView5.setText("Direction : Left");
						} else if(direction == JoyStickClass.STICK_UPLEFT) {
							textView5.setText("Direction : Up Left");
						} else if(direction == JoyStickClass.STICK_NONE) {
							textView5.setText("Direction : Center");
						}
						
					}
					return true;
				}
	        });
		
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

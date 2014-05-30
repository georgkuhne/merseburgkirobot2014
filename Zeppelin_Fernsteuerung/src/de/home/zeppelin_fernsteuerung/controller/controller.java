package de.home.zeppelin_fernsteuerung.controller;


import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.view.View;
import android.view.ViewStub;
import android.widget.SeekBar;
import de.home.zeppelin_fernsteuerung.MainActivity;
import de.home.zeppelin_fernsteuerung.R;
import de.home.zeppelin_fernsteuerung.widgets.joystick.JoystickView;
import de.home.zeppelin_fernsteuerung.widgets.verticalseekbar.VerticalSeekBar;

public class controller extends AsyncTask<Void, Integer, Void>{


private VerticalSeekBar SeekBar1;
private VerticalSeekBar SeekBar2;
private JoystickView Joystick;

public controller(VerticalSeekBar seekbar1, VerticalSeekBar seekbar2, JoystickView joystick) {
	// TODO Auto-generated constructor stub
  
    SeekBar1 = seekbar1;
    SeekBar2 = seekbar2;
    Joystick = joystick;
}

@Override
protected Void doInBackground(Void... params) {
	// TODO Auto-generated method stub
	while (true) {
		try {
			Thread.sleep(1000);
			int wert = SeekBar1.getProgress();
			System.err.println(wert);
			int wert2 = SeekBar2.getProgress();
			System.err.println(wert2);
			float wertx = Joystick.gethandleX();
			System.err.println(wertx);
			float werty = Joystick.gethandleY();
			System.err.println(werty);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
}





}	
	 
		

	

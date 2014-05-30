package de.home.zeppelin_fernsteuerung.controller;


import android.os.AsyncTask;
import de.home.zeppelin_fernbedienung.FTDIDriver.FTDriver;
import de.home.zeppelin_fernsteuerung.widgets.joystick.JoystickView;
import de.home.zeppelin_fernsteuerung.widgets.verticalseekbar.VerticalSeekBar;

public class controller extends Thread{


private VerticalSeekBar SeekBar1;
private VerticalSeekBar SeekBar2;
private JoystickView Joystick;
FTDriver USBDriver;

public controller(VerticalSeekBar seekbar1, VerticalSeekBar seekbar2, JoystickView joystick) {
	// TODO Auto-generated constructor stub
  
    SeekBar1 = seekbar1;
    SeekBar2 = seekbar2;
    Joystick = joystick;
    
}

@Override
	public void run() {
		// TODO Auto-generated method stub
	while (true) {
		try {
			Thread.sleep(1000);
			int wert = SeekBar1.getProgress();
			System.err.println(wert);
			int wert2 = SeekBar2.getProgress();
			System.err.println(wert2);
			float wertx = Joystick.gethandleX();
			wertx = wertx - 150;
			System.err.println(wertx);
			float werty = Joystick.gethandleY();
			werty = werty - 150;
			System.err.println(werty);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
	}

}	
	 
		

	

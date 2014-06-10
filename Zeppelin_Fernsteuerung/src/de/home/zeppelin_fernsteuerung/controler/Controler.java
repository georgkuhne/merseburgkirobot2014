package de.home.zeppelin_fernsteuerung.controler;

import de.home.zeppelin_fernsteuerung.communication.Communication;
import de.home.zeppelin_fernsteuerung.widgets.joystick.JoystickView;
import de.home.zeppelin_fernsteuerung.widgets.verticalseekbar.VerticalSeekBar;

public class Controler extends Thread {

	private VerticalSeekBar seekbardrehung;
	private VerticalSeekBar seekbarheck;
	private JoystickView Joystick;
	private boolean stopp;

	public Controler(VerticalSeekBar seekbar1, VerticalSeekBar seekbar2,
			JoystickView joystick) {
		// TODO Auto-generated constructor stub

		seekbardrehung = seekbar1;
		seekbarheck = seekbar2;
		this.Joystick = joystick;
	}

	private float g1(float x, float y) {
		return -2f * x - y;
	}

	private float g2(float x, float y) {
		return 2f * x - y;
	}

	private float g3(float x, float y) {
		return 0.5f * x - y;
	}

	private float g4(float x, float y) {
		return -0.5f * x - y;
	}

	@Override
	public void run() {
		stopp = false;
		while (!stopp) {
			try {
				float fl = 0, fr = 0, betrag;
				byte motorlinks, motorrechts;
				Thread.sleep(1000);
				int drehung = seekbardrehung.getProgress() - 127;
				int heck = seekbarheck.getProgress() - 127;
				if (drehung < -127)
					drehung = -127;
				if (heck < -127)
					heck = -127;
				float wertx = (Joystick.gethandleX() - 100);
				float werty = -(Joystick.gethandleY() - 100);

				if (g1(wertx, werty) <= 0 & g2(wertx, werty) <= 0) {
					fl = 1;
					fr = 1;
				}
				if (g2(wertx, werty) >= 0 & g3(wertx, werty) <= 0) {
					fl = 1;
					fr = 0.5f;
				}

				if (g3(wertx, werty) >= 0 & g4(wertx, werty) <= 0) {
					fl = 1;
					fr = 0.0f;
				}

				if (g1(wertx, werty) <= 0 & g4(wertx, werty) >= 0) {
					fl = -1;
					fr = -0.5f;
				}

				if (g1(wertx, werty) >= 0 & g2(wertx, werty) >= 0) {
					fl = -1;
					fr = -1;
				}
				if (g2(wertx, werty) <= 0 & g3(wertx, werty) >= 0) {
					fl = -0.5f;
					fr = -1;
				}
				if (g3(wertx, werty) <= 0 & g4(wertx, werty) >= 0) {
					fl = 0;
					fr = 1;
				}
				if (g1(wertx, werty) >= 0 & g4(wertx, werty) <= 0) {
					fl = 0.5f;
					fr = 1f;
				}
				float g1 = g1(wertx, werty);
				float g2 = g2(wertx, werty);
				float g3 = g3(wertx, werty);
				float g4 = g4(wertx, werty);

				if (Math.abs(wertx) < Math.abs(werty))
					betrag = Math.abs(werty);
				else
					betrag = Math.abs(wertx);
				motorlinks = (byte) Math.round((127 * fl * (betrag / 50)));
				motorrechts = (byte) Math.round((127 * fr * (betrag / 50)));

				Communication.sendMotorDaten(motorlinks, motorrechts, drehung,
						heck);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

		}
	}

	public void end() {
		stopp = true;

	}

}

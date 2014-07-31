package de.home.zeppelin_fernsteuerung.communication;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.concurrent.ConcurrentLinkedQueue;

public class ThreadSendMessage extends Thread {
	FTDriver ftDriver;
	private boolean stopp;

	public ThreadSendMessage(FTDriver ftDriver) {
		this.ftDriver = ftDriver;
	}

	@Override
	public void run() {
		stopp = false;
		long t0 = System.currentTimeMillis();

		while (!stopp) {
			ConcurrentLinkedQueue<FunkMessage> sendqeue = Communication.sendFunkMessageQueue;

			FunkMessage funkMessage = sendqeue.poll();
			if (funkMessage != null) {
				ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
				try {
					byte[] header = funkMessage.getHeader();
					byte[] data = funkMessage.getData();
					String sheader = "";
					String sdata = "";

					outputStream.write(funkMessage.getHeader());
					outputStream.write(funkMessage.getData());
					outputStream.write(funkMessage.getPruefbyte());
					byte c[] = outputStream.toByteArray();
					ftDriver.write(c);
					if (System.currentTimeMillis() - t0 >= 1050) {
						t0 = System.currentTimeMillis();
						sendqeue.clear();
					}
					;
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		}

	}

	public void end() {
		stopp = true;

	}

	public void setDriver(FTDriver ftDriver2) {
		ftDriver = ftDriver2;

	}
}

package de.home.zeppelin_fernsteuerung.communication;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.concurrent.ConcurrentLinkedQueue;

public class ThreadReadAndSendMessage extends Thread {
	FTDriver ftDriver;
	private boolean stopp;

	public ThreadReadAndSendMessage(FTDriver ftDriver) {
		this.ftDriver = ftDriver;
	}

	@Override
	public void run() {
		stopp = false;
		while (!stopp) {
			ConcurrentLinkedQueue<FunkMessage> sendque = Communication.sendFunkMessageQueue;

			FunkMessage funkMessage = sendque.poll();
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
					// String wbuf = "FTDriver Test.";
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

}

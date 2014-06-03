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

					for (int i = 0; i < header.length; i++) {
						sheader = sheader
								+ "|"
								+ String.format(
										"%8s",
										Integer.toBinaryString(header[i] & 0xFF))
										.replace(' ', '0');
					}
					for (int i = 0; i < data.length; i++) {
						sdata = sdata
								+ "|"
								+ String.format("%8s",
										Integer.toBinaryString(data[i] & 0xFF))
										.replace(' ', '0');
					}

					System.err.println("HEADER:" + sheader);
					System.err.println("DATA:" + sdata);

					outputStream.write(funkMessage.getHeader());
					outputStream.write(funkMessage.getData());
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				byte c[] = outputStream.toByteArray();
				ftDriver.write(c);

			}
		}

	}

	public void end() {
		stopp = true;

	}
}

package de.home.zeppelin_fernsteuerung.communication;

import java.util.concurrent.ConcurrentLinkedQueue;

public class Communication {

	public static final ConcurrentLinkedQueue<FunkMessage> sendFunkMessageQueue = new ConcurrentLinkedQueue<FunkMessage>();
	public static final ConcurrentLinkedQueue<FunkMessage> readFunkMessageQueue = new ConcurrentLinkedQueue<FunkMessage>();

	public static void sendMotorDaten(int motorlinks, int motorrechts,
			int drehungvorn, int motorheck) {
		FunkMessage message = new FunkMessage();
		message.setAdress(FunkMessage.ADRESSE_MC);
		message.setType(FunkMessage.TYPE_OHNERÜCKANTWORT);
		byte[] data = new byte[4];
		data[0] = (byte) motorheck;
		data[1] = (byte) drehungvorn;
		data[2] = (byte) motorlinks;
		data[3] = (byte) motorrechts;
		message.setData(data);
		sendFunkMessageQueue.add(message);

	}

}

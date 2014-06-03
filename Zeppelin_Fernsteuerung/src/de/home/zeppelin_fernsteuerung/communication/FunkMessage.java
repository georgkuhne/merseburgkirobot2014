package de.home.zeppelin_fernsteuerung.communication;

public class FunkMessage {
	public static final byte STARTBYTE = (byte) 0xA5;
	public static final byte ADRESSE_TABLET = (byte) 0x10;
	public static final byte ADRESSE_HANDY = (byte) 0x30;
	public static final byte ADRESSE_MC = (byte) 0x50;
	public static final byte TYPE_OHNERÜCKANTWORT = (byte) 0x00;
	public static final byte TYPE_MITRÜCKANTWORT = (byte) 0x01;

	private byte header[] = new byte[3];
	private byte data[];

	public FunkMessage() {
		header[0] = STARTBYTE;
	}

	public byte[] getData() {
		return data;
	}

	public void setData(byte[] data) {
		header[2] = (byte) data.length;
		this.data = data;
	}

	public byte[] getHeader() {
		return header;
	}

	public void setAdress(byte adresse) {
		header[1] = (byte) ((header[2] & 0x0F) | adresse);
	}

	public void setType(byte type) {
		header[1] = (byte) ((header[2] & 0xF0) | type);
	}

}

package de.home.zeppelin_fernsteuerung.communication;

public class FunkMessage {
	public static final byte STARTBYTE = (byte) 0xA5;
	public static final byte ADRESSE_TABLET = (byte) 0x10;
	public static final byte ADRESSE_HANDY = (byte) 0x30;
	public static final byte ADRESSE_MC = (byte) 0x50;
	public static final byte TYPE_OHNERÜCKANTWORT = (byte) 0x00;
	public static final byte TYPE_MITRÜCKANTWORT = (byte) 0x01;

	private byte pruefbyte;
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
		pruefbyte = CRC8.calc(data, data.length);
		this.data = data;
	}

	public byte getPruefbyte() {
		return pruefbyte;
	}

	public byte[] getHeader() {
		return header;
	}

	public void setAdress(byte adresse) {
		System.err.println();
		header[1] = (byte) ((header[1] & 0xFF) | adresse);
	}

	public void setType(byte type) {
		header[1] = (byte) ((header[1] & 0xFF) | type);
	}

}

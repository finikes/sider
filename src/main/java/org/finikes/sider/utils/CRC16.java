package org.finikes.sider.utils;

/**
 * 
 * @author Finikes.Wu
 *
 */
public class CRC16 {
	private short[] crcTable = new short[256];
	private static final int POLY = 0x1021;

	private static final CRC16 INSTANCE = new CRC16();

	public static final CRC16 getInstance() {
		return INSTANCE;
	}

	private CRC16() {
		computeCrcTable();
	}

	private short getCrcOfByte(int aByte) {
		int value = aByte << 8;

		for (int count = 7; count >= 0; count--) {
			if ((value & 0x8000) != 0) {
				value = (value << 1) ^ POLY;
			} else {
				value = value << 1;
			}

		}
		value = value & 0xFFFF;
		return (short) value;
	}

	private void computeCrcTable() {
		for (int i = 0; i < 256; i++) {
			crcTable[i] = getCrcOfByte(i);
		}
	}

	public short getCrc(byte[] data) {
		int crc = 0;
		int length = data.length;
		for (int i = 0; i < length; i++) {
			crc = ((crc & 0xFF) << 8) ^ crcTable[(((crc & 0xFF00) >> 8) ^ data[i]) & 0xFF];
		}
		crc = crc & 0xFFFF;
		return (short) crc;
	}
}

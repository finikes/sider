package org.finikes.sider.base;

import org.finikes.sider.utils.CRC16;

/**
 * 
 * @author Finikes.Wu
 *
 */
public class RedisClusterSlotQuerier {
	static CRC16 crc16 = CRC16.getInstance();

	public static int keyHashSlot(String key) {
		String pk = getPureKey(key);
		if ("".equals(pk)) {
			pk = key;
		}

		return crc16.getCrc(pk.getBytes()) & 0x3FFF;
	}

	private static String getPureKey(String origKey) {
		int i0 = origKey.indexOf("}");
		if (-1 == i0) {
			return origKey;
		}
		int i1 = origKey.indexOf("{");
		if (-1 == i1) {
			return origKey;
		}

		return origKey.substring(i1 + 1, i0);
	}
}

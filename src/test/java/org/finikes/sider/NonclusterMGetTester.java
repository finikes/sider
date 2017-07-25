package org.finikes.sider;

import redis.clients.jedis.Jedis;

/**
 * 
 * @author Finikes.Wu
 *
 */
public class NonclusterMGetTester {
	static int PREPARE_DATA_SIZE = 100;

	/**
	 * 非集群环境下测试mget方式批量读取性能
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		Jedis jedis = new Jedis("127.0.0.1", 6383);
		String[] keys = ClusterMGetTesting.GET_TEST_PREPARE_DATA(PREPARE_DATA_SIZE);
		long start = System.currentTimeMillis();
		int cycle = 100000;
		for (int i = 0; i < cycle; i++)
			jedis.mget(keys);
		long consume = System.currentTimeMillis() - start;
		System.out.println("非集群环境mget方法批量处理" + PREPARE_DATA_SIZE + "个get指令" + cycle + "次耗时: " + consume + " 毫秒.");
		jedis.close();
	}

}

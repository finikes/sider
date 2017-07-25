package org.finikes.sider;

import redis.clients.jedis.Jedis;

/**
 * 
 * @author Finikes.Wu
 *
 */
public class NonclusterDataPreparer {

	/**
	 * 非集群环境准备测试基准数据
	 * @param args
	 */
	public static void main(String[] args) {
		Jedis jedis = new Jedis("127.0.0.1", 6383);
		for (int i = 0; i < 100000; i++) {
			jedis.set("key" + i, i+"-"+ClusterDataPreparer.TEST_VALUE);
		}
		jedis.close();
	}

}

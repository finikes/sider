package org.finikes.sider;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.finikes.sider.base.RedisClusterTopology;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.Pipeline;

/**
 * 集群环境下通过pipeline方式批量插入数据对象(未经严格测试)
 * @author Finikes.Wu
 *
 */
public class UnsafeSider extends Sider {

	public UnsafeSider(RedisClusterTopology redisClusterTopology) {
		super(redisClusterTopology);
	}

	public List<String> mset(Map<String, String> kvs) {
		int kvsLenth = kvs.size();
		List<String> results = new ArrayList<String>(kvsLenth);
		String[] keys = new String[kvsLenth];

		int pointer = 0;
		for (String key : kvs.keySet()) {
			keys[pointer] = key;
			pointer++;
		}

		Map<String, List<String>> keysSection = pair(keys);
		Set<java.util.Map.Entry<String, List<String>>> ts0 = keysSection.entrySet();

		for (java.util.Map.Entry<String, List<String>> e : ts0) {
			work(kvs, e);
		}

		return results;
	}

	private void work(Map<String, String> kvs, java.util.Map.Entry<String, List<String>> e) {
		Jedis jedis = null;
		try {
			JedisPool pool = redisClusterTopology.getClusterMap().get(e.getKey()).getPool();
			jedis = pool.getResource();
			Pipeline pl = jedis.pipelined();
			List<String> subKeys = e.getValue();
			for (String k : subKeys) {
				pl.set(k, kvs.get(k));
			}

			pl.sync();
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			if (null != jedis) {
				jedis.close();
			}
		}
	}
}

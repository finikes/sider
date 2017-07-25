package org.finikes.sider;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.finikes.sider.base.RedisClusterNode;
import org.finikes.sider.base.RedisClusterSlotQuerier;
import org.finikes.sider.base.RedisClusterTopology;
import org.finikes.sider.exception.NotFoundNodeException;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.Pipeline;

/**
 * 
 * @author Finikes.Wu
 *
 */
public class Sider {
	public Sider(RedisClusterTopology redisClusterTopology) {
		this.redisClusterTopology = redisClusterTopology;
	}

	protected RedisClusterTopology redisClusterTopology;

	public RedisClusterTopology getRedisClusterTopology() {
		return redisClusterTopology;
	}

	public void setRedisClusterTopology(RedisClusterTopology redisClusterTopology) {
		this.redisClusterTopology = redisClusterTopology;
	}

	public List<String> mget(String... keys) {
		List<String> results = new ArrayList<String>(keys.length);
		Map<String, List<String>> keysSection = pair(keys);
		Set<java.util.Map.Entry<String, List<String>>> ts0 = keysSection.entrySet();

		for (java.util.Map.Entry<String, List<String>> e : ts0) {
			work(results, e);
		}

		return results;
	}

	private void work(List<String> results, java.util.Map.Entry<String, List<String>> e) {
		Jedis jedis = null;
		try {
			JedisPool pool = redisClusterTopology.getClusterMap().get(e.getKey()).getPool();
			jedis = pool.getResource();
			Pipeline pl = jedis.pipelined();
			List<String> subKeys = e.getValue();
			for (String k : subKeys) {
				pl.get(k);
			}

			List<Object> response = pl.syncAndReturnAll();
			for (Object r : response) {
				// if (null == r) {
				// continue;
				// }
				results.add(r.toString());
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			if (null != jedis) {
				jedis.close();
			}
		}
	}

	private final long getSlot(String key) {
		return RedisClusterSlotQuerier.keyHashSlot(key);
	}

	public final Map<String, List<String>> pair(String... keys) {
		Map<String, List<String>> results = new HashMap<String, List<String>>();
		Map<String, RedisClusterNode> clusterMap = redisClusterTopology.getClusterMap();
		Set<String> idSet = clusterMap.keySet();
		for (String id : idSet) {
			results.put(id, new ArrayList<String>(keys.length));
		}

		for (String k : keys) {
			long slot = getSlot(k);
			RedisClusterNode node = redisClusterTopology.getSlotMap().get(slot);
			if (null == node) {
				throw new NotFoundNodeException(slot);
			}

			List<String> keysSub = results.get(node.getId());
			keysSub.add(k);
		}

		return results;
	}
}

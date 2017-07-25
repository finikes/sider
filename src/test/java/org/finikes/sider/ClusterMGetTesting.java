package org.finikes.sider;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import org.finikes.sider.Sider;
import org.finikes.sider.async.AsyncMGetSider;
import org.finikes.sider.async.AsyncRedisPipelindHandler;
import org.finikes.sider.base.RedisClusterTopology;
import org.finikes.sider.exception.ClusterTopologyInitException;
import org.finikes.sider.utils.ProcessWorkerThreadPool;

import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.JedisPoolConfig;

/**
 * 
 * @author Finikes.Wu
 *
 */
public class ClusterMGetTesting {

	static String NODE_BUILDER_PATH = "org.finikes.sider.node.impl.V30504_NodeBuilder";

	/**
	 * 集群环境下测试pipeline方式批量读取性能
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		JedisPoolConfig poolConfig = new JedisPoolConfig();
		poolConfig.setMaxTotal(2);
		poolConfig.setMaxIdle(2);
		poolConfig.setMaxWaitMillis(1000);
		Set<HostAndPort> nodes = new LinkedHashSet<HostAndPort>();
		nodes.add(new HostAndPort("127.0.0.1", 6380));
		nodes.add(new HostAndPort("127.0.0.1", 6381));
		nodes.add(new HostAndPort("127.0.0.1", 6382));
		JedisCluster cluster = new JedisCluster(nodes, poolConfig);

		RedisClusterTopology topology = null;
		try {
			topology = new RedisClusterTopology(NODE_BUILDER_PATH).init(cluster);
		} catch (ClusterTopologyInitException e) {
			e.printStackTrace();
		}

		final Sider sider = new Sider(topology);

		int PREPARE_DATA_SIZE = 100;

		final String[] keys = GET_TEST_PREPARE_DATA(PREPARE_DATA_SIZE);
		int cycle = 100000;
		// syncPipelinedTest(sider, PREPARE_DATA_SIZE, keys, cycle);

		asyncPipelinedTest(topology, PREPARE_DATA_SIZE, keys, cycle);
	}

	private static void asyncPipelinedTest(RedisClusterTopology topology, int PREPARE_DATA_SIZE, final String[] keys,
			int cycle) {
		// AsyncRedisPipelindHandler ah = new AsyncMGetSider(topology,
		// org.finikes.sider.strategy.SectionMaxCapacityStrategy.class, "10");
		// AsyncRedisPipelindHandler ah = new AsyncMGetSider(topology,
		// org.finikes.sider.strategy.SectionQuantityStrategy.class, "20");
		AsyncRedisPipelindHandler ah = new AsyncMGetSider(topology);
		long start = System.currentTimeMillis();
		for (int i = 0; i < cycle; i++) {
			Map<String, String> resultMap = ah.mget(keys);
			// asyncResultsPrint(resultMap);
		}
		long consume = System.currentTimeMillis() - start;
		System.out.println("集群环境pipeline异步批量处理" + PREPARE_DATA_SIZE + "个get指令" + cycle + "次耗时: " + consume + " 毫秒.");
		ProcessWorkerThreadPool.shutdown();
	}

	private static void syncPipelinedTest(final Sider sider, int PREPARE_DATA_SIZE, final String[] keys, int cycle) {
		long start = System.currentTimeMillis();
		for (int i = 0; i < cycle; i++) {
			List<String> results = sider.mget(keys);
			// syncResultsPrint(results);
		}
		long consume = System.currentTimeMillis() - start;
		System.out.println("集群环境pipeline同步批量处理" + PREPARE_DATA_SIZE + "个get指令" + cycle + "次耗时: " + consume + " 毫秒.");
	}

	private static void syncResultsPrint(List<String> results) {
		for (String s : results) {
			System.out.println(s);
		}
	}

	private static void asyncResultsPrint(Map<String, String> resultMap) {
		Set<java.util.Map.Entry<String, String>> es = resultMap.entrySet();
		for (java.util.Map.Entry<String, String> e : es) {
			String keyIndex = e.getKey().substring(3);
			String valueIndex = e.getValue().split("-")[0].trim();
			// System.out.println(keyIndex + " : " + valueIndex);
			if (!keyIndex.equals(valueIndex)) {
				System.err.println(keyIndex + " : " + valueIndex);
			}
		}
	}

	static Random r = new Random();

	/**
	 * 准备测试请求数据
	 * 
	 * @param size
	 * @return
	 */
	public static String[] GET_TEST_PREPARE_DATA(int size) {
		int[] _is = new int[size];
		for (int i = 0; i < size; i++) {
			_is[i] = i;
		}

		String[] _ss = new String[size];
		for (int i = 0; i < size; i++) {
			int ri = r.nextInt(size - i);
			_ss[i] = "key" + _is[ri];
			_is[ri] = _is[size - i - 1];
		}
		return _ss;
	}
}

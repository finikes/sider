package org.finikes.sider;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.finikes.sider.base.RedisClusterTopology;
import org.finikes.sider.exception.ClusterTopologyInitException;

import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.JedisPoolConfig;

/**
 * 
 * @author Finikes.Wu
 *
 */
public class ClusterDataPreparer {
	static String NODE_BUILDER_PATH = "org.finikes.sider.node.impl.V30504_NodeBuilder";

	/**
	 * 集群环境下pipeline批量写入功能性测试
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

		final UnsafeSider sider = new UnsafeSider(topology);

		final Map<String, String> kvs = INIT_DATA_TAG();

		for (int i = 0; i < 1; i++)
			new Thread(new Runnable() {
				@Override
				public void run() {
					List<String> response = sider.mset(kvs);
					for (String s : response) {
						System.out.println(s + Thread.currentThread().getName());
					}
				}
			}).start();
	}

	/**
	 * 测试用值,约100字节左右
	 */
	public static String TEST_VALUE = "aaabbbcccdddeeefffggghhhiiijjjkkklllmmmnnnooopppqqqrrrssstttuuuvvvwwwxxxyyyzzz000111222333444555666777888999;;;,,,===!!!";

	private static Map<String, String> INIT_DATA_TAG() {
		Map<String, String> dataTag = new HashMap<String, String>(100000);
		for (int i = 0; i < 100000; i++) {
			dataTag.put("key" + i, i+"-"+TEST_VALUE);
		}
		return dataTag;
	}
}

package org.finikes.sider.base;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.finikes.sider.NodeBuilder;
import org.finikes.sider.exception.ClusterTopologyInitException;

import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.JedisPool;

/**
 * 
 * @author Finikes.Wu
 *
 */
public class RedisClusterTopology {

	public RedisClusterTopology(String nodeBuilderPath) {
		try {
			this.nodeBuilder = (NodeBuilder) Class.forName(nodeBuilderPath).newInstance();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	public RedisClusterTopology(NodeBuilder nodeBuilder) {
		this.nodeBuilder = nodeBuilder;
	}

	private NodeBuilder nodeBuilder;

	public NodeBuilder getNodeBuilder() {
		return nodeBuilder;
	}

	public void setNodeBuilder(NodeBuilder nodeBuilder) {
		this.nodeBuilder = nodeBuilder;
	}

	private boolean IS_INIT = false;

	private final Map<String, RedisClusterNode> CLUSTER_MAP = new HashMap<String, RedisClusterNode>();
	private final Map<Long, RedisClusterNode> SLOT_MAP = new HashMap<Long, RedisClusterNode>(JedisCluster.HASHSLOTS);

	public RedisClusterTopology init(JedisCluster cluster) throws ClusterTopologyInitException {
		if (IS_INIT) {
			return this;
		}

		Map<String, JedisPool> nodeMap = cluster.getClusterNodes();
		Set<String> nodeKey = nodeMap.keySet();
		for (String k : nodeKey) {
			JedisPool pool = nodeMap.get(k);
			RedisClusterNode node = nodeBuilder.build(pool);
			CLUSTER_MAP.put(node.getId(), node);
		}

		injectMaster();

		slotMapping();

		return this;
	}

	private void injectMaster() {
		Collection<RedisClusterNode> c0 = CLUSTER_MAP.values();
		for (RedisClusterNode n : c0) {
			if (!n.isMaster()) {
				n.setMaster(CLUSTER_MAP.get(n.toString().split(" ")[3]));
			}
		}
	}

	public Map<String, RedisClusterNode> getClusterMap() {
		return CLUSTER_MAP;
	}

	public int nodeSize() {
		return CLUSTER_MAP.size();
	}

	public Map<Long, RedisClusterNode> getSlotMap() {
		return SLOT_MAP;
	}

	private void slotMapping() {
		Collection<RedisClusterNode> c = CLUSTER_MAP.values();
		for (long i = 0; i < JedisCluster.HASHSLOTS; i++) {
			for (RedisClusterNode n : c) {
				if (i >= n.getSlotStartPointer() && i <= n.getSlotEndPointer()) {
					SLOT_MAP.put(i, n);
					break;
				}
			}
		}
	}

	public void refresh() {
		IS_INIT = false;
	}
}

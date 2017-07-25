package org.finikes.sider.node.impl;

import org.finikes.sider.NodeBuilder;
import org.finikes.sider.base.RedisClusterNode;
import org.finikes.sider.exception.ClusterTopologyInitException;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

/**
 * 
 * @author Finikes.Wu
 *
 */
public class V30504_NodeBuilder implements NodeBuilder {

	@Override
	public RedisClusterNode build(JedisPool pool) {
		RedisClusterNode node = new RedisClusterNode(pool, this);
		Jedis jedis = null;
		String info = null;
		try {
			jedis = pool.getResource();
			info = jedis.clusterNodes();
		} catch (Exception e) {
			e.printStackTrace();
			throw new ClusterTopologyInitException(e.getMessage(), e);
		} finally {
			if (null != jedis) {
				jedis.close();
			}
		}

		String[] t0 = info.split("\n");
		for (String s0 : t0) {
			if (s0.contains("myself")) {
				info = s0;
				break;
			}
		}

		node.setInfo(info);
		t0 = info.split(" ");

		node.setId(t0[0]);
		String[] t1 = t0[1].split(":");
		node.setIp(t1[0]);
		node.setPort(Integer.parseInt(t1[1]));
		t1 = t0[2].split(",");
		if (info.contains("master")) {
			node.setMaster(true);
		}

		if (node.isMaster()) {
			t1 = t0[8].split("-");
			node.setSlotStartPointer(Long.valueOf(t1[0]));
			node.setSlotEndPointer(Long.valueOf(t1[1]));
		}

		return node;
	}

	@Override
	public void unserialize(RedisClusterNode node) {
		String info = node.toString();
		String[] t0 = info.split("\n");
		for (String s0 : t0) {
			if (s0.contains("myself")) {
				info = s0;
				node.setInfo(info);
				break;
			}
		}

		t0 = info.split(" ");

		node.setId(t0[0]);
		String[] t1 = t0[1].split(":");
		node.setIp(t1[0]);
		node.setPort(Integer.parseInt(t1[1]));
		t1 = t0[2].split(",");
		if ("master".equals(t1[1])) {
			node.setMaster(true);
		}
		t1 = t0[8].split("-");
		node.setSlotStartPointer(Long.valueOf(t1[0]));
		node.setSlotEndPointer(Long.valueOf(t1[1]));
	}
}

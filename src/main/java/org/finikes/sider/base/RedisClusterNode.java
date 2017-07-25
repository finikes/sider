package org.finikes.sider.base;

import org.finikes.sider.NodeBuilder;
import org.finikes.sider.exception.ClusterTopologyInitException;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

/**
 * 
 * @author Finikes.Wu
 *
 */
public class RedisClusterNode {
	private long slotStartPointer;
	private long slotEndPointer;
	private String id;
	private String ip;
	private int port;
	private boolean isMaster;
	private JedisPool pool;
	private RedisClusterNode master;
	private String info;

	public String toString() {
		return info;
	}

	public RedisClusterNode getMaster() {
		return master;
	}

	public void setMaster(RedisClusterNode master) {
		this.master = master;
	}

	public RedisClusterNode(JedisPool pool, NodeBuilder nodeBuilder) throws ClusterTopologyInitException {
		this.pool = pool;
		Jedis jedis = null;
		try {
			jedis = pool.getResource();
			info = jedis.clusterNodes();
		} catch (Exception e) {
			throw new ClusterTopologyInitException(e.getMessage(), e);
		} finally {
			if (null != jedis) {
				jedis.close();
			}
		}

		nodeBuilder.unserialize(this);
	}

	public JedisPool getPool() {
		return pool;
	}

	public void setPool(JedisPool pool) {
		this.pool = pool;
	}

	public long getSlotStartPointer() {
		if (!isMaster) {
			return master.getSlotStartPointer();
		}
		return slotStartPointer;
	}

	public void setSlotStartPointer(long slotStartPointer) {
		this.slotStartPointer = slotStartPointer;
	}

	public long getSlotEndPointer() {
		if (!isMaster) {
			return master.getSlotEndPointer();
		}
		return slotEndPointer;
	}

	public void setSlotEndPointer(long slotEndPointer) {
		this.slotEndPointer = slotEndPointer;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public boolean isMaster() {
		return isMaster;
	}

	public void setMaster(boolean isMaster) {
		this.isMaster = isMaster;
	}

	public void setInfo(String info) {
		this.info = info;
	}
}

package org.finikes.sider;

import org.finikes.sider.base.RedisClusterNode;

import redis.clients.jedis.JedisPool;

/**
 * 
 * @author Finikes.Wu
 *
 */
public interface NodeBuilder {
	RedisClusterNode build(JedisPool pool);

	void unserialize(RedisClusterNode node);
}

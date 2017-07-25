package org.finikes.sider.async;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.finikes.sider.Sider;
import org.finikes.sider.base.RedisClusterTopology;
import org.finikes.sider.strategy.Strategy;
import org.finikes.sider.utils.ProcessWorkerThreadPool;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.Pipeline;

public class AsyncMGetSider extends AsyncRedisPipelindMGetHandler {
	private final Sider sider;
	private Strategy strategy;

	public Strategy getStrategy() {
		return strategy;
	}

	public void setStrategy(Strategy strategy) {
		this.strategy = strategy;
	}

	public void setStrategy(Class<? extends Strategy> strategyClass, String... args) {
		try {
			this.strategy = strategyClass.getConstructor(Integer.class).newInstance(Integer.valueOf(args[0]));
		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		}
	}

	public AsyncMGetSider(RedisClusterTopology redisClusterTopology) {
		sider = new Sider(redisClusterTopology);
	}

	public AsyncMGetSider(RedisClusterTopology redisClusterTopology, Class<? extends Strategy> strategyClass,
			String... args) {
		sider = new Sider(redisClusterTopology);
		this.setStrategy(strategyClass, args);
	}

	@Override
	public Map<String, String> mget(String... keys) {
		Map<String, List<String>> keysSection = sider.pair(keys);
		if (null != strategy) {
			keysSection = strategy.filter(keysSection);
		}

		Set<java.util.Map.Entry<String, List<String>>> ts0 = keysSection.entrySet();
		Map<String, String> resultMap = new HashMap<String, String>(keys.length);

		List<Future<ResultSectionTransfer>> futureList = new ArrayList<Future<ResultSectionTransfer>>();
		for (java.util.Map.Entry<String, List<String>> e : ts0) {
			Future<ResultSectionTransfer> rst = work(e);
			futureList.add(rst);
		}

		RuntimeException re = null;
		try {
			for (Future<ResultSectionTransfer> f : futureList) {
				ResultSectionTransfer rst = f.get();
				List<String> _keys = rst.keys;
				int i = 0;
				for (String k : _keys) {
					resultMap.put(k, rst.values.get(i).toString());
					i++;
				}
			}

			return resultMap;
		} catch (InterruptedException ex) {
			re = new RuntimeException(ex);
		} catch (ExecutionException ex) {
			re = new RuntimeException(ex);
		}

		throw re;
	}

	private Future<ResultSectionTransfer> work(java.util.Map.Entry<String, List<String>> e) {
		final ResultSectionTransfer result = new ResultSectionTransfer();
		result.keys = e.getValue();

		Callable<ResultSectionTransfer> callable = new Callable<ResultSectionTransfer>() {
			public ResultSectionTransfer call() throws Exception {
				Jedis jedis = null;

				RuntimeException re = null;
				try {
					String nodeId = e.getKey().split("-")[0];
					JedisPool pool = sider.getRedisClusterTopology().getClusterMap().get(nodeId).getPool();
					jedis = pool.getResource();
					Pipeline pl = jedis.pipelined();
					List<String> subKeys = e.getValue();
					for (String k : subKeys) {
						pl.get(k);
					}

					result.values = pl.syncAndReturnAll();
					return result;
				} catch (Exception ex) {
					re = new RuntimeException(ex);
				} finally {
					if (null != jedis) {
						jedis.close();
					}
				}

				throw re;
			}
		};

		return ProcessWorkerThreadPool.getExec().submit(callable);
	}

	public class ResultSectionTransfer {
		private List<String> keys;
		private List<Object> values;
	}

	@Override
	public Map<String, String> mget(List<String> keys) {
		String _keys[] = (String[]) keys.toArray();
		return this.mget(_keys);
	}
}

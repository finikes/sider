package org.finikes.sider.async;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public interface AsyncRedisPipelindHandler {
	public Map<String, String> mget(String... keys);

	public Map<String, String> mget(List<String> keys);

	public void mset(String[] keys, String[] values);

	public void mset(List<String> keys, Collection<String> values);

	public void mset(Map<String, String> entrys);
}

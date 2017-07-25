package org.finikes.sider.async;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public abstract class AsyncRedisPipelindMSetHandler extends AsyncRedisPipelindHandlerAdapter {
	protected String unrealizedMsg = "Method unrealized.";

	@Override
	public final Map<String, String> mget(String... keys) {
		throw new RuntimeException(unrealizedMsg);
	}

	@Override
	public final Map<String, String> mget(List<String> keys) {
		throw new RuntimeException(unrealizedMsg);
	}

	public abstract void mset(String[] keys, String[] values);

	public abstract void mset(List<String> keys, Collection<String> values);

	public abstract void mset(Map<String, String> entrys);
}

package org.finikes.sider.async;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public abstract class AsyncRedisPipelindMGetHandler extends AsyncRedisPipelindHandlerAdapter {
	protected String unrealizedMsg = "Method unrealized.";

	public abstract Map<String, String> mget(String... keys);

	public abstract Map<String, String> mget(List<String> keys);

	@Override
	public final void mset(String[] keys, String[] values) {
		throw new RuntimeException(unrealizedMsg);
	}

	@Override
	public final void mset(List<String> keys, Collection<String> values) {
		throw new RuntimeException(unrealizedMsg);
	}

	@Override
	public final void mset(Map<String, String> entrys) {
		throw new RuntimeException(unrealizedMsg);
	}
}

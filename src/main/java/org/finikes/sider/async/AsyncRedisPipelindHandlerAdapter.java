package org.finikes.sider.async;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public abstract class AsyncRedisPipelindHandlerAdapter implements AsyncRedisPipelindHandler {
	protected String unrealizedMsg = "Method unrealized.";

	@Override
	public Map<String, String> mget(String... keys) {
		throw new RuntimeException(unrealizedMsg);
	}

	@Override
	public Map<String, String> mget(List<String> keys) {
		throw new RuntimeException(unrealizedMsg);
	}

	@Override
	public void mset(String[] keys, String[] values) {
		throw new RuntimeException(unrealizedMsg);
	}

	@Override
	public void mset(List<String> keys, Collection<String> values) {
		throw new RuntimeException(unrealizedMsg);
	}

	@Override
	public void mset(Map<String, String> entrys) {
		throw new RuntimeException(unrealizedMsg);
	}

}

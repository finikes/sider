package org.finikes.sider.strategy;

import java.util.List;
import java.util.Map;

public interface Strategy {
	public Map<String, List<String>> filter(Map<String, List<String>> keysSection);
}

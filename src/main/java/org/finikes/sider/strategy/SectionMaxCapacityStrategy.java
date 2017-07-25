package org.finikes.sider.strategy;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import org.finikes.sider.strategy.exception.StrategyException;

public class SectionMaxCapacityStrategy implements Strategy {
	private int sectionMaxCapacity;

	public int getSectionMaxCapacity() {
		return sectionMaxCapacity;
	}

	public void setSectionMaxCapacity(int sectionMaxCapacity) {
		this.sectionMaxCapacity = sectionMaxCapacity;
	}

	public SectionMaxCapacityStrategy(Integer sectionMaxCapacity) {
		this.sectionMaxCapacity = sectionMaxCapacity;
	}

	public SectionMaxCapacityStrategy() {
	}

	private Random ran = new Random();

	@Override
	public Map<String, List<String>> filter(Map<String, List<String>> keysSection) {
		Set<java.util.Map.Entry<String, List<String>>> set = keysSection.entrySet();
		Map<String, List<String>> _keysSection = new HashMap<String, List<String>>(
				keysSection.size() * sectionMaxCapacity);
		for (java.util.Map.Entry<String, List<String>> e : set) {
			String origNodeId = e.getKey();
			List<String> keys = e.getValue();
			int keysQuantity = keys.size();
			int sectionQuantity = keysQuantity / sectionMaxCapacity;
			int last = keysQuantity % sectionMaxCapacity;

			int j = 0;
			int i = 0;
			for (i = 0; i < sectionQuantity; i++) {
				j = i * sectionMaxCapacity;
				List<String> listx = keys.subList(j, j + sectionMaxCapacity);
				origNodeId = origNodeId + "-" + ran.nextInt(10000) * 10 + i;
				if (_keysSection.containsKey(origNodeId)) {
					throw new StrategyException("Don't have to deal with.");
				}
				_keysSection.put(origNodeId, listx);
			}

			if (0 != last) {
				List<String> listx = keys.subList(j + sectionMaxCapacity, j + sectionMaxCapacity + last);
				origNodeId = origNodeId + "-" + ran.nextInt(10000) * 10 + i;
				if (_keysSection.containsKey(origNodeId)) {
					throw new StrategyException("Don't have to deal with.");
				}
				_keysSection.put(origNodeId, listx);
			}
		}

		return _keysSection;
	}

}

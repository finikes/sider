package org.finikes.sider.strategy;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import org.finikes.sider.strategy.exception.StrategyException;

public class SectionQuantityStrategy implements Strategy {
	private int sectionQuantity;

	public int getSectionQuantity() {
		return sectionQuantity;
	}

	public void setSectionQuantity(int sectionQuantity) {
		this.sectionQuantity = sectionQuantity;
	}

	public SectionQuantityStrategy(Integer sectionQuantity) {
		this.sectionQuantity = sectionQuantity;
	}

	public SectionQuantityStrategy() {
	}

	private Random ran = new Random();

	@Override
	public Map<String, List<String>> filter(Map<String, List<String>> keysSection) {
		Set<java.util.Map.Entry<String, List<String>>> set = keysSection.entrySet();
		Map<String, List<String>> _keysSection = new HashMap<String, List<String>>(
				keysSection.size() * sectionQuantity);
		if (keysSection.size() <= sectionQuantity) {
			return keysSection;
		}

		for (java.util.Map.Entry<String, List<String>> e : set) {
			String origNodeId = e.getKey();
			List<String> keys = e.getValue();
			int keysNum = keys.size();
			int sectionCapacity = keysNum / sectionQuantity;
			int last = keysNum % sectionQuantity;

			int j = 0;
			int i = 0;
			for (i = 0; i < sectionCapacity; i++) {
				j = i * sectionQuantity;
				List<String> listx = keys.subList(j, j + sectionQuantity);
				origNodeId = origNodeId + "-" + ran.nextInt(10000) * 10 + i;
				if (_keysSection.containsKey(origNodeId)) {
					throw new StrategyException("Don't have to deal with.");
				}
				_keysSection.put(origNodeId, listx);
			}

			if (0 != last) {
				List<String> listx = keys.subList(j + sectionQuantity, j + sectionQuantity + last);
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

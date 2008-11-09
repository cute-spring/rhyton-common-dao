package com.rhyton.common.dao.iBatis;

import java.util.HashMap;
import java.util.Map;

public final class ParameterClassUtils {
	private ParameterClassUtils() {
	}

	public static <T> Map<String, T> put(String key, T value) {
		Map<String, T> map = new HashMap<String, T>();
		map.put(key, value);
		return map;
	}
}

package com.rhyton.common.dao.jdbc;

import java.util.HashMap;
import java.util.Map;

import org.springframework.jdbc.core.simple.ParameterizedRowMapper;
import org.springframework.util.Assert;
@SuppressWarnings("unchecked")
@Deprecated
public class ParameterizedRowMapperRegister {

	private final Map<String, ParameterizedRowMapper> parameterizedRowMapperMap = new HashMap<String, ParameterizedRowMapper>();

	public <T> void add(Class<T> requiredType, ParameterizedRowMapper<T> rm) {
		Assert.notNull(requiredType, "RequiredType must not be null");
		Assert.notNull(rm, "ParameterizedRowMapper must not be null");
		// System.out.println(rm.getClass());
		// System.out.println(requiredType);
		// System.out.println(requiredType.getName());
		parameterizedRowMapperMap.put(requiredType.getName(), rm);
	}

	@SuppressWarnings("unchecked")
	public <T> ParameterizedRowMapper<T> get(Class<T> requiredType) {
		Assert.notNull(requiredType, "RequiredType must not be null");
		return (ParameterizedRowMapper<T>) parameterizedRowMapperMap.get(requiredType.getName());
	}

	@SuppressWarnings("unchecked")
	public <T> ParameterizedRowMapper<T> get(String requiredTypeName) {
		Assert.notNull(requiredTypeName, "requiredTypeName must not be null");
		return (ParameterizedRowMapper<T>) parameterizedRowMapperMap.get(requiredTypeName);
	}

	// public void main(String[] args) {
	// addMapper(Role.class, roleMapper);
	// addMapper(null, null);
	// }

	// ParameterizedRowMapper<Role> roleMapper = new ParameterizedRowMapper() {
	// public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
	// return null;
	// }
	// };

}

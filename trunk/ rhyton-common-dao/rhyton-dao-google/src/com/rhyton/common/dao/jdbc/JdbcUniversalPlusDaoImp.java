package com.rhyton.common.dao.jdbc;

import org.springframework.jdbc.core.simple.SimpleJdbcOperations;
@Deprecated
public class JdbcUniversalPlusDaoImp implements JdbcUniversalPlusDao {
	
	private SimpleJdbcOperations simpleJdbcOperations;

	// -------------------------------------------------------------------------
	// Delegate methods
	// -------------------------------------------------------------------------

//	/**
//	 * @param sql
//	 * @param args
//	 * @return
//	 * @throws DataAccessException
//	 */
//	public List<T> query(String sql, Map args) throws DataAccessException {
//		return simpleJdbcOperations.query(sql, getPojoMapper(), args);
//	}
//
//	public List<T> query(String sql, Object... args) throws DataAccessException {
//		return simpleJdbcOperations.query(sql, getPojoMapper(), args);
//	}
//
//	public T queryForObject(String sql, Map args) throws DataAccessException {
//		return simpleJdbcOperations.queryForObject(sql, getPojoMapper(), args);
//	}
//
//	public T queryForObject(String sql, Object... args) throws DataAccessException {
//		return simpleJdbcOperations.queryForObject(sql, getPojoMapper(), args);
//	}

	public SimpleJdbcOperations getSimpleJdbcOperations() {
		return simpleJdbcOperations;
	}

	public void setSimpleJdbcOperations(SimpleJdbcOperations simpleJdbcOperations) {
		this.simpleJdbcOperations = simpleJdbcOperations;
	}
}

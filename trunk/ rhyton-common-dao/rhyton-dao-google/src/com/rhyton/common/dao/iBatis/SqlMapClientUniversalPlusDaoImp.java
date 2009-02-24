package com.rhyton.common.dao.iBatis;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.orm.ObjectRetrievalFailureException;
import org.springframework.orm.ibatis.SqlMapClientCallback;
import org.springframework.orm.ibatis.support.SqlMapClientDaoSupport;
import org.springframework.util.CollectionUtils;

import com.ibatis.sqlmap.client.SqlMapExecutor;
import com.rhyton.common.pagination.domain.PaginationHolder;
import com.rhyton.common.pagination.domain.PaginationInfo;
import com.rhyton.common.pagination.ibatis.IbatisPaginationFactory;

public class SqlMapClientUniversalPlusDaoImp extends SqlMapClientDaoSupport implements SqlMapClientUniversalPlusDao {
	/**
	 * Log variable for all child classes. Uses LogFactory.getLog(getClass()) from Commons Logging
	 */
	protected final Log log = LogFactory.getLog(getClass());

	/**
	 * {@inheritDoc}
	 */

	public <T> List<T> getAll(Class<T> clazz) {
		return _queryForList(iBatisDaoUtils.getSelectQuery(clazz.getName()), null);
	}

	public <T> T get(Class<T> clazz, Serializable primaryKey) {
		return _queryForObject(iBatisDaoUtils.getFindQuery(clazz.getName()), primaryKey);
	}

	public <T> T save(final T object) {
		String className = object.getClass().getName();
		Object primaryKey = iBatisDaoUtils.getPrimaryKeyValue(object);
		String keyId = (primaryKey != null) ? primaryKey.toString() : null;
		if (StringUtils.isBlank(keyId)) {
			primaryKey = _insert(iBatisDaoUtils.getInsertQuery(className), object);
		} else {
			_update(iBatisDaoUtils.getUpdateQuery(className), object);
		}
		// check for null id
		if (iBatisDaoUtils.getPrimaryKeyValue(object) == null) {
			throw new ObjectRetrievalFailureException(className, object);
		} else {
			return object;
		}
	}

	public <T> void remove(Class<T> clazz, Serializable id) {
		_update(iBatisDaoUtils.getDeleteByPrimaryKeyQuery(clazz.getName()), id);
	}

	public <T> T queryForObject(Class<T> clazz, String daoMethodName) throws DataAccessException {
		return queryForObject(clazz, daoMethodName, null);
	}

	public <T> T queryForObject(Class<T> clazz, String daoMethodName, Object parameterObject) throws DataAccessException {
		return _queryForObject(getStatementName(clazz, daoMethodName), parameterObject);
	}

	public <T> T queryForObject(T exampleEntity) {
		return _queryForObject(iBatisDaoUtils.getSelectQuery(exampleEntity.getClass().getName()), exampleEntity);
	}

	public <T> List<T> queryByExample(T exampleEntity) throws DataAccessException {
		return _queryForList(iBatisDaoUtils.getSelectQuery(exampleEntity.getClass().getName()), exampleEntity);
	}

	public <T> List<T> queryForList(Class<T> clazz, String daoMethodName) throws DataAccessException {
		return queryForList(clazz, daoMethodName, null);
	}

	public <T> List<T> queryForList(Class<T> clazz, String daoMethodName, Object parameterObject) throws DataAccessException {
		return _queryForList(getStatementName(clazz, daoMethodName), parameterObject);
	}

	public <T> void update(Class<T> clazz, String daoMethodName, Object parameter) {
		_update(getStatementName(clazz, daoMethodName), parameter);
	}

	public <T> void update(Class<T> clazz, String daoMethodName) {
		update(clazz, daoMethodName, null);
	}

	public <T> PaginationHolder<T> getPaginationHolder(Class<T> clazz, PaginationInfo pageInfo, Object parameterObj) {
		return new IbatisPaginationFactory<T>(getSqlMapClientTemplate(), clazz.getName(), parameterObj).getPaginationHolder(pageInfo);
	}

	public <T> PaginationHolder<T> getPaginationHolder(Class<T> clazz, String daoMethodName, PaginationInfo pageInfo, Object parameterObj) {
		return new IbatisPaginationFactory<T>(getSqlMapClientTemplate(), clazz.getName(), parameterObj, daoMethodName).getPaginationHolder(pageInfo);
	}

	public <T> void batchInsert(final Collection<T> parameterList) throws DataAccessException {
		if (CollectionUtils.isEmpty(parameterList))return;
		final String insertMethod = iBatisDaoUtils.getInsertQuery(this.getClassName(parameterList));
		this.getSqlMapClientTemplate().execute(new SqlMapClientCallback() {
			public Object doInSqlMapClient(SqlMapExecutor executor) throws SQLException {
				executor.startBatch();
				for (Object parameter : parameterList) {
					executor.insert(insertMethod, parameter);
				}
				executor.executeBatch();
				return null;
			}
		});
	}

	public <T> void batchRemove(Class<T> clazz, Collection<? extends Serializable> PrimaryKeyList) throws DataAccessException {
		if (CollectionUtils.isEmpty(PrimaryKeyList))return;
		_batchExecute(iBatisDaoUtils.getDeleteByPrimaryKeyQuery(clazz.getName()), PrimaryKeyList);
	}

	public <T> void batchUpdate(Collection<T> parameterList) throws DataAccessException {
		if (CollectionUtils.isEmpty(parameterList))return;
		_batchExecute(iBatisDaoUtils.getUpdateQuery(this.getClassName(parameterList)), parameterList);
	}

	// =========================================
	protected final <T> String getStatementName(Class<T> clazz, String methodName) {
		return clazz.getName() + "." + methodName;
	}

	protected final <T> String getClassName(Collection<T> parameterList) {
		return parameterList.iterator().next().getClass().getName();
	}

	// =============================================
	private Object _insert(String statementName, Object parameter) throws DataAccessException {
		return this.getSqlMapClientTemplate().insert(statementName, parameter);
	}

	private int _update(String statementName, Object parameter) throws DataAccessException {
		return this.getSqlMapClientTemplate().update(statementName, parameter);
	}

	@SuppressWarnings("unchecked")
	private <T> T _queryForObject(String statementId, Object parameterObject) throws DataAccessException {
		return (T) getSqlMapClientTemplate().queryForObject(statementId, parameterObject);
	}

	@SuppressWarnings("unchecked")
	private <T> List<T> _queryForList(String statementName, Object parameterObject) throws DataAccessException {
		return getSqlMapClientTemplate().queryForList(statementName, parameterObject);
	}

	@SuppressWarnings("unchecked")
	private void _batchExecute(final String fullStatementId, final Collection parameterList) throws DataAccessException {
		this.getSqlMapClientTemplate().execute(new SqlMapClientCallback() {
			public Object doInSqlMapClient(SqlMapExecutor executor) throws SQLException {
				executor.startBatch();
				for (Object parameter : parameterList) {
					executor.update(fullStatementId, parameter);
				}
				executor.executeBatch();
				return null;
			}
		});
	}

}

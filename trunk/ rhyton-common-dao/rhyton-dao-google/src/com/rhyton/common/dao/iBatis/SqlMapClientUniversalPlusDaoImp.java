package com.rhyton.common.dao.iBatis;

import java.io.Serializable;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.orm.ObjectRetrievalFailureException;
import org.springframework.orm.ibatis.support.SqlMapClientDaoSupport;
import org.springframework.util.ClassUtils;

import com.rhyton.common.pagination.domain.PaginationHolder;
import com.rhyton.common.pagination.domain.PaginationInfo;
import com.rhyton.common.pagination.ibatis.IbatisPaginationFactory;
@Deprecated
@SuppressWarnings("unchecked")
public class SqlMapClientUniversalPlusDaoImp extends SqlMapClientDaoSupport implements SqlMapClientUniversalPlusDao {
	/**
	 * Log variable for all child classes. Uses LogFactory.getLog(getClass()) from Commons Logging
	 */
	protected final Log log = LogFactory.getLog(getClass());

	/**
	 * {@inheritDoc}
	 */

	public <T> List<T> getAll(Class<T> clazz) {
		return getSqlMapClientTemplate().queryForList(iBatisDaoUtils.getSelectQuery(ClassUtils.getShortName(clazz)), null);
	}

	/**
	 * {@inheritDoc}
	 */

	public <T> T get(Class<T> clazz, Serializable primaryKey) {
		Object object = getSqlMapClientTemplate().queryForObject(iBatisDaoUtils.getFindQuery(ClassUtils.getShortName(clazz)),
				primaryKey);
		if (object == null) {
			throw new ObjectRetrievalFailureException(ClassUtils.getShortName(clazz), primaryKey);
		}
		return (T) object;
	}

	/**
	 * {@inheritDoc}
	 */
	public <T> T save(final T object) {

		String className = object.getClass().getName();
		Object primaryKey = iBatisDaoUtils.getPrimaryKeyValue(object);
		// Class primaryKeyClass = iBatisDaoUtils.getPrimaryKeyField(object).getType();
		String keyId = null;

		// check for null id
		if (primaryKey != null) {
			keyId = primaryKey.toString();
		}

		// check for new record
		if (org.apache.commons.lang.StringUtils.isBlank(keyId)) {
			primaryKey = getSqlMapClientTemplate().insert(iBatisDaoUtils.getInsertQuery(className), object);
		} else {
			getSqlMapClientTemplate().update(iBatisDaoUtils.getUpdateQuery(className), object);
		}

		// check for null id
		if (iBatisDaoUtils.getPrimaryKeyValue(object) == null) {
			throw new ObjectRetrievalFailureException(className, object);
		} else {
			return object;
		}
	}

	public <T> PaginationHolder<T> getPaginationHolder(PaginationInfo pageInfo, T exampleEntity) {
		return new IbatisPaginationFactory<T>(getSqlMapClientTemplate(), exampleEntity).getPaginationHolder(pageInfo);
	}

	public <T> void remove(Class<T> clazz, Serializable id) {
		getSqlMapClientTemplate().update(iBatisDaoUtils.getDeleteByPrimaryKeyQuery(clazz.getName()), id);
	}

	public <T> T queryForObject(Class<T> clazz, String daoMethodName) throws DataAccessException {
		return (T) getSqlMapClientTemplate().queryForObject(getStatementName(clazz, daoMethodName));
	}

	public <T> T queryForObject(Class<T> clazz, String daoMethodName, Object parameterObject) throws DataAccessException {
		return (T) getSqlMapClientTemplate().queryForObject(getStatementName(clazz, daoMethodName), parameterObject);
	}

	private final String getStatementName(Class clazz, String methodName) {
		return clazz.getName() + "." + methodName;
	}
}

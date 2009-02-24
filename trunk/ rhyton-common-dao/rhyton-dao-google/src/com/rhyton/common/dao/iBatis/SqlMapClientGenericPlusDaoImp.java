package com.rhyton.common.dao.iBatis;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.orm.ObjectRetrievalFailureException;
import org.springframework.orm.ibatis.SqlMapClientCallback;
import org.springframework.orm.ibatis.support.SqlMapClientDaoSupport;

import com.ibatis.sqlmap.client.SqlMapExecutor;
import com.rhyton.common.dao.GenericsUtils;
import com.rhyton.common.pagination.domain.PaginationHolder;
import com.rhyton.common.pagination.domain.PaginationInfo;
import com.rhyton.common.pagination.ibatis.IbatisPaginationFactory;

/**
 * This class serves as the Base class for all other DAOs - namely to hold common CRUD methods that they might all use. You should only need to extend
 * this class when your require custom CRUD logic.
 * <p>
 * To register this class in your Spring context file, use the following XML.
 * 
 * <pre>
 *      &lt;bean id=&quot;fooDao&quot; class=&quot;org.appfuse.dao.ibatis.GenericDaoiBatis&quot;&gt;
 *          &lt;constructor-arg value=&quot;org.appfuse.model.Foo&quot;/&gt;
 *          &lt;property name=&quot;sessionFactory&quot; ref=&quot;sessionFactory&quot;/&gt;
 *      &lt;/bean&gt;
 * </pre>
 * 
 * @author Bobby Diaz, Bryan Noll
 * @param <T> a type variable
 * @param <PK> the primary key for that type
 */
@SuppressWarnings("unchecked")
public class SqlMapClientGenericPlusDaoImp<T, PK extends Serializable> extends SqlMapClientDaoSupport implements SqlMapClientGenericPlusDao<T, PK> {
	/**
	 * Log variable for all child classes. Uses LogFactory.getLog(getClass()) from Commons Logging
	 */
	protected final Log log = LogFactory.getLog(getClass());

	private Class<T> persistentClass;

	public SqlMapClientGenericPlusDaoImp() {
		this.persistentClass = GenericsUtils.getSuperClassGenricType(getClass());
	}

	/**
	 * Constructor that takes in a class to see which type of entity to persist
	 * 
	 * @param persistentClass the class type you'd like to persist
	 */
	public SqlMapClientGenericPlusDaoImp(final Class<T> persistentClass) {
		this.persistentClass = persistentClass;
	}

	// -------------------------------------------------------------------------
	// Implementation CRUD methods
	// -------------------------------------------------------------------------
	/**
	 * {@inheritDoc}
	 */

	public T get(PK id) {
		T object = (T) getSqlMapClientTemplate().queryForObject(iBatisDaoUtils.getFindQuery(this.getClassName()), id);
		// if (object == null) {
		// log.warn("Uh oh, '" + this.persistentClass + "' object with id '" + id + "' not found...");
		// throw new ObjectRetrievalFailureException(this.getClassName(), id);
		// }
		return object;
	}

	/**
	 * {@inheritDoc}
	 */

	public boolean exists(PK id) {
		// T object = (T) getSqlMapClientTemplate().queryForObject(iBatisDaoUtils.getFindQuery(this.getClassName()), id);
		return get(id) != null;
	}

	/**
	 * {@inheritDoc}
	 */

	public T save(final T object) {
		if (object == null || !this.persistentClass.isAssignableFrom(object.getClass())) {
			// throw new InvalidTargetObjectTypeException("");
		}
		String className = this.getClassName();
		Object primaryKey = iBatisDaoUtils.getPrimaryKeyValue(object);
		String keyId = (primaryKey != null) ? primaryKey.toString() : null;

		// check for new record
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

	/**
	 * {@inheritDoc}
	 */
	public void remove(PK id) {
		// getSqlMapClientTemplate().update(iBatisDaoUtils.getDeleteByPrimaryKeyQuery(this.getClassName()), id);
		_update(iBatisDaoUtils.getDeleteByPrimaryKeyQuery(this.getClassName()), id);
	}

	public List<T> getAll() {
		// return getSqlMapClientTemplate().queryForList(iBatisDaoUtils.getSelectQuery(this.getClassName()), null);
		return _queryForList(iBatisDaoUtils.getSelectQuery(this.getClassName()), null);
	}

	// -------------------------------------------------------------------------
	// pagination methods
	// -------------------------------------------------------------------------

	public PaginationHolder<T> getPaginationHolder(PaginationInfo pageInfo, Object parameterObj) {
		// parameterObj = parameterObj == null ? this.getDefaultBeanInstance():parameterObj;
		return new IbatisPaginationFactory<T>(getSqlMapClientTemplate(), getClassName(), parameterObj).getPaginationHolder(pageInfo);
	}

	public PaginationHolder<T> getPaginationHolder(String daoMethodName, PaginationInfo pageInfo, Object parameterObj) {
		// parameterObj = parameterObj == null ? this.getDefaultBeanInstance():parameterObj;
		return new IbatisPaginationFactory<T>(getSqlMapClientTemplate(), getClassName(), parameterObj, daoMethodName).getPaginationHolder(pageInfo);
	}

	// -------------------------------------------------------------------------
	// query methods
	// -------------------------------------------------------------------------

	public T queryForObject(T exampleEntity) {
		// return (T) getSqlMapClientTemplate().queryForObject(iBatisDaoUtils.getSelectQuery(this.getClassName()), exampleEntity);
		return _queryForObject(iBatisDaoUtils.getSelectQuery(this.getClassName()), exampleEntity);
	}

	public T queryForObject(String daoMethodName) throws DataAccessException {
		// return (T) getSqlMapClientTemplate().queryForObject(getStatementName(daoMethodName));
		return queryForObject(daoMethodName, null);
	}

	public T queryForObject(String daoMethodName, Object parameterObject) throws DataAccessException {
		// return (T) getSqlMapClientTemplate().queryForObject(getStatementName(daoMethodName), parameterObject);
		return _queryForObject(getStatementName(daoMethodName), parameterObject);
	}

	public List<T> queryByExample(T exampleEntity) {
		// return getSqlMapClientTemplate().queryForList(iBatisDaoUtils.getSelectQuery(this.getClassName()), exampleEntity);
		return _queryForList(iBatisDaoUtils.getSelectQuery(this.getClassName()), exampleEntity);
	}

	public List<T> queryForList(String daoMethodName) throws DataAccessException {
		// return getSqlMapClientTemplate().queryForList(getStatementName(daoMethodName));
		return queryForList(daoMethodName, null);
	}

	public List<T> queryForList(String daoMethodName, Object parameterObject) throws DataAccessException {
		// return getSqlMapClientTemplate().queryForList(getStatementName(daoMethodName), parameterObject);
		return _queryForList(getStatementName(daoMethodName), parameterObject);
	}

	// -------------------------------------------------------------------------
	// update methods
	// -------------------------------------------------------------------------
	public void update(String daoMethodName, Object parameter) {
		this.getSqlMapClientTemplate().update(getStatementName(daoMethodName), parameter);
	}

	public void update(String daoMethodName) {
		// this.getSqlMapClientTemplate().update(getStatementName(daoMethodName));
		update(daoMethodName, null);
	}

	// ====================================batch execute====================================================//
	/**
	 * batch delete from db
	 * 
	 * @param PrimaryKeyList
	 * @throws DataAccessException
	 */
	public void batchRemove(final Collection<PK> PrimaryKeyList) throws DataAccessException {
		_batchExecute(iBatisDaoUtils.getDeleteByPrimaryKeyQuery(this.getClassName()), PrimaryKeyList);
	}

	/**
	 * batch update from db
	 * 
	 * @param parameterList
	 * @throws DataAccessException
	 */
	public void batchUpdate(final Collection<T> parameterList) throws DataAccessException {
		_batchExecute(iBatisDaoUtils.getUpdateQuery(this.getClassName()), parameterList);
	}

	/**
	 * batch insert from db
	 * 
	 * @param parameterList
	 * @throws DataAccessException
	 */
	public void batchInsert(final Collection<T> parameterList) throws DataAccessException {
		final String insertMethod = iBatisDaoUtils.getInsertQuery(this.getClassName());
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

	/**
	 * @param daoMethodName
	 * @param parameterList(javaBean\XML\Map)
	 * @throws DataAccessException
	 */
	protected void batchExecute(final String daoMethodName, final Collection parameterList) throws DataAccessException {
		_batchExecute(getStatementName(daoMethodName), parameterList);
	}

	protected Map<Object, T> queryForMap(String daoMethod, Object parameterObject, String keyProp) throws DataAccessException {
		return this.getSqlMapClientTemplate().queryForMap(getStatementName(daoMethod), parameterObject, keyProp);
	}

	protected Map queryForMap(String daoMethod, Object parameterObject, String keyProp, String valueProp) throws DataAccessException {
		return this.getSqlMapClientTemplate().queryForMap(getStatementName(daoMethod), parameterObject, keyProp, valueProp);
	}

	// -------------------------------------------------------------------------
	// Implementation hooks and helper methods
	// -------------------------------------------------------------------------
	private final String getClassName() {
		return this.persistentClass.getName();
	}

	protected final String getStatementName(String methodName) {
		return getClassName() + "." + methodName;
	}

	// private T getDefaultBeanInstance(){
	// try {
	// return persistentClass.newInstance();
	// } catch (InstantiationException e) {
	// e.printStackTrace();
	// } catch (IllegalAccessException e) {
	// e.printStackTrace();
	// }
	// return null;
	// }

	// ====================================================================================
	private Object _insert(String statementName, Object parameter) throws DataAccessException {
		return this.getSqlMapClientTemplate().insert(statementName, parameter);
	}

	private int _update(String statementName, Object parameter) throws DataAccessException {
		return this.getSqlMapClientTemplate().update(statementName, parameter);
	}

	private T _queryForObject(String statementId, Object parameterObject) throws DataAccessException {
		return (T) getSqlMapClientTemplate().queryForObject(statementId, parameterObject);
	}

	private List<T> _queryForList(String statementName, Object parameterObject) throws DataAccessException {
		return getSqlMapClientTemplate().queryForList(statementName, parameterObject);
	}

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

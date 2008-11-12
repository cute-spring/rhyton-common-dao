package com.rhyton.common.dao.iBatis;

import java.io.Serializable;
import java.sql.SQLException;
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
 * This class serves as the Base class for all other DAOs - namely to hold common CRUD methods that they might all use.
 * You should only need to extend this class when your require custom CRUD logic.
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
 * @param <T>
 *                a type variable
 * @param <PK>
 *                the primary key for that type
 */
@SuppressWarnings("unchecked")
public class SqlMapClientGenericPlusDaoImp<T, PK extends Serializable> extends SqlMapClientDaoSupport implements
	SqlMapClientGenericPlusDao<T, PK> {
    /**
     * Log variable for all child classes. Uses LogFactory.getLog(getClass()) from Commons Logging
     */
    protected final Log log = LogFactory.getLog(getClass());

    private Class<T> persistentClass;

    @SuppressWarnings("unchecked")
    public SqlMapClientGenericPlusDaoImp() {
	this.persistentClass = GenericsUtils.getSuperClassGenricType(getClass());
    }

    /**
     * Constructor that takes in a class to see which type of entity to persist
     * 
     * @param persistentClass
     *                the class type you'd like to persist
     */
    public SqlMapClientGenericPlusDaoImp(final Class<T> persistentClass) {
	this.persistentClass = persistentClass;
    }

    //-------------------------------------------------------------------------
    // Implementation CRUD methods
    //-------------------------------------------------------------------------
    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
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
    @SuppressWarnings("unchecked")
    public boolean exists(PK id) {
	T object = (T) getSqlMapClientTemplate().queryForObject(iBatisDaoUtils.getFindQuery(this.getClassName()), id);
	return object != null;
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    public T save(final T object) {
	if (object == null || !this.persistentClass.isAssignableFrom(object.getClass())) {
	    // throw new InvalidTargetObjectTypeException("");
	}
	String className = this.getClassName();
	Object primaryKey = iBatisDaoUtils.getPrimaryKeyValue(object);
	// Class primaryKeyClass = iBatisDaoUtils.getPrimaryKeyField(object).getType();
	String keyId = null;

	// check for null id
	if (primaryKey != null) {
	    keyId = primaryKey.toString();
	}

	// check for new record
	if (StringUtils.isBlank(keyId)) {
	    // iBatisDaoUtils.prepareObjectForSaveOrUpdate(object);
	    primaryKey = getSqlMapClientTemplate().insert(iBatisDaoUtils.getInsertQuery(className), object);
	    // iBatisDaoUtils.setPrimaryKey(object, primaryKeyClass, primaryKey);
	} else {
	    // iBatisDaoUtils.prepareObjectForSaveOrUpdate(object);
	    getSqlMapClientTemplate().update(iBatisDaoUtils.getUpdateQuery(className), object);
	}

	// check for null id
	if (iBatisDaoUtils.getPrimaryKeyValue(object) == null) {
	    throw new ObjectRetrievalFailureException(className, object);
	} else {
	    return object;
	}
    }

    public T insert(final T object) {
	if (object == null || !this.persistentClass.isAssignableFrom(object.getClass())) {
	    // throw new InvalidTargetObjectTypeException("");
	}
	String className = this.getClassName();

	// Object key = getSqlMapClientTemplate().insert(iBatisDaoUtils.getInsertQuery(className), object);
	getSqlMapClientTemplate().insert(iBatisDaoUtils.getInsertQuery(className), object);
	if (iBatisDaoUtils.getPrimaryKeyValue(object) == null) { //
	    throw new ObjectRetrievalFailureException(className, object);
	} else {
	    return object;
	}
    }

    public T update(final T object) {
	if (object == null || !this.persistentClass.isAssignableFrom(object.getClass())) {
	    // throw new InvalidTargetObjectTypeException("");
	}
	String className = this.getClassName();
	Object primaryKey = iBatisDaoUtils.getPrimaryKeyValue(object);

	String keyId = null;

	// check for null id
	if (primaryKey != null) {
	    keyId = primaryKey.toString();
	}

	// check for new record
	if (!StringUtils.isBlank(keyId)) {
	    getSqlMapClientTemplate().update(iBatisDaoUtils.getUpdateQuery(className), object);
	}

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
	getSqlMapClientTemplate().update(iBatisDaoUtils.getDeleteByPrimaryKeyQuery(this.getClassName()), id);
    }

    public List<T> getAll() {
	return getSqlMapClientTemplate().queryForList(iBatisDaoUtils.getSelectQuery(this.getClassName()), null);
    }

    //-------------------------------------------------------------------------
    // pagination methods
    //-------------------------------------------------------------------------
    public PaginationHolder<T> getPaginationHolder(PaginationInfo pageInfo, T exampleEntity) {
	return new IbatisPaginationFactory<T>(getSqlMapClientTemplate(), exampleEntity).getPaginationHolder(pageInfo);
    }

    public PaginationHolder<T> getPaginationHolder(final String daoMethodName, PaginationInfo pageInfo, T exampleEntity) {
	return new IbatisPaginationFactory<T>(getSqlMapClientTemplate(), exampleEntity, daoMethodName).getPaginationHolder(pageInfo);
    }

    //-------------------------------------------------------------------------
    // query methods
    //-------------------------------------------------------------------------
    @SuppressWarnings("unchecked")
    public List<T> queryByExample(T exampleEntity) {
	return getSqlMapClientTemplate().queryForList(iBatisDaoUtils.getSelectQuery(this.getClassName()), exampleEntity);
    }

    public List<T> queryForList(String daoMethodName) throws DataAccessException {
	return getSqlMapClientTemplate().queryForList(getStatementName(daoMethodName));
    }

    public List<T> queryForList(String daoMethodName, Object parameterObject) throws DataAccessException {
	return getSqlMapClientTemplate().queryForList(getStatementName(daoMethodName), parameterObject);
    }

    public T queryForObject(String daoMethodName) throws DataAccessException {
	return (T) getSqlMapClientTemplate().queryForObject(getStatementName(daoMethodName));
    }

    public T queryForObject(String daoMethodName, Object parameterObject) throws DataAccessException {
	return (T) getSqlMapClientTemplate().queryForObject(getStatementName(daoMethodName), parameterObject);
    }

    //-------------------------------------------------------------------------
    // update methods
    //-------------------------------------------------------------------------
    protected void update(String daoMethodName, Object parameter) {
	this.getSqlMapClientTemplate().update(getStatementName(daoMethodName), parameter);
    }

    protected void update(String daoMethodName) {
	this.getSqlMapClientTemplate().update(getStatementName(daoMethodName));
    }

    //====================================batch execute====================================================//
    /**
     * batch delete from db
     * 
     * @param PrimaryKeyList
     * @throws DataAccessException
     */
    public void batchRemove(final List<PK> PrimaryKeyList) throws DataAccessException {
	__batchExecute(iBatisDaoUtils.getDeleteByPrimaryKeyQuery(this.getClassName()), PrimaryKeyList);
    }

    /**
     * batch update from db
     * 
     * @param parameterList
     * @throws DataAccessException
     */
    public void batchUpdate(final List<T> parameterList) throws DataAccessException {
	__batchExecute(iBatisDaoUtils.getUpdateQuery(this.getClassName()), parameterList);
    }

    /**
     * @param daoMethodName
     * @param parameterList
     *                (javaBean\XML\Map)
     * @throws DataAccessException
     */
    protected void batchExecute(final String daoMethodName, final List parameterList) throws DataAccessException {
	__batchExecute(getStatementName(daoMethodName), parameterList);
    }

    private void __batchExecute(final String fullStatementId, final List parameterList) throws DataAccessException {
	SqlMapClientCallback callback = new SqlMapClientCallback() {
	    public Object doInSqlMapClient(SqlMapExecutor executor) throws SQLException {
		executor.startBatch();
		for (Object parameter : parameterList) {
		    executor.update(fullStatementId, parameter);
		}
		executor.executeBatch();
		return null;
	    }
	};
	this.getSqlMapClientTemplate().execute(callback);
    }
    
    
    
    /**
     * Executes a mapped SQL SELECT statement that returns data to populate
     * a number of result objects that will be keyed into a Map.
     * <p/>
     * The parameter object is generally used to supply the input
     * data for the WHERE clause parameter(s) of the SELECT statement.
     *
     * @param id              The name of the statement to execute.
     * @param parameterObject The parameter object (e.g. JavaBean, Map, XML etc.).
     * @param keyProp         The property to be used as the key in the Map.
     * @return A Map keyed by keyProp with values being the result object instance.
     * @throws java.sql.SQLException If an error occurs.
     */
    protected Map<Object,T> queryForMap(String daoMethod, Object parameterObject, String keyProp) throws DataAccessException {
	return this.getSqlMapClientTemplate().queryForMap(getStatementName(daoMethod), parameterObject, keyProp);
    }

    /**
     * Executes a mapped SQL SELECT statement that returns data to populate
     * a number of result objects from which one property will be keyed into a Map.
     * <p/>
     * The parameter object is generally used to supply the input
     * data for the WHERE clause parameter(s) of the SELECT statement.
     *
     * @param id              The name of the statement to execute.
     * @param parameterObject The parameter object (e.g. JavaBean, Map, XML etc.).
     * @param keyProp         The property to be used as the key in the Map.
     * @param valueProp       The property to be used as the value in the Map.
     * @return A Map keyed by keyProp with values of valueProp.
     * @throws java.sql.SQLException If an error occurs.
     */
    protected Map queryForMap(String daoMethod, Object parameterObject, String keyProp, String valueProp)  throws DataAccessException {
	return this.getSqlMapClientTemplate().queryForMap(getStatementName(daoMethod), parameterObject, keyProp,valueProp);
    }

    //-------------------------------------------------------------------------
    // Implementation hooks and helper methods
    //-------------------------------------------------------------------------
    private final String getClassName() {
	return this.persistentClass.getName();
    }

    private final String getStatementName(String methodName) {
	return getClassName() + "." + methodName;
    }

}

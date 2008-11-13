package com.rhyton.common.dao.hibernate;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.criterion.DetachedCriteria;
import org.springframework.dao.DataAccessException;
import org.springframework.orm.ObjectRetrievalFailureException;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import com.rhyton.common.pagination.domain.PaginationHolder;
import com.rhyton.common.pagination.domain.PaginationInfo;
import com.rhyton.common.pagination.hibernate.DetachedCriteriaPaginationFactory;
import com.rhyton.common.pagination.hibernate.HqlPaginationFactory;
import com.rhyton.common.pagination.hibernate.QueryByExamplePaginationFactory;

/**
 * This class serves as the Base class for all other DAOs - namely to hold common CRUD methods that they might all use.
 * You should only need to extend this class when your require custom CRUD logic.
 * <p>
 * To register this class in your Spring context file, use the following XML.
 * 
 * <pre>
 *      &lt;bean id=&quot;fooDao&quot; class=&quot;org.appfuse.dao.hibernate.GenericDaoHibernate&quot;&gt;
 *          &lt;constructor-arg value=&quot;org.appfuse.model.Foo&quot;/&gt;
 *          &lt;property name=&quot;sessionFactory&quot; ref=&quot;sessionFactory&quot;/&gt;
 *      &lt;/bean&gt;
 * </pre>
 * 
 * @author <a href="mailto:bwnoll@gmail.com">Bryan Noll</a>
 * @param <T>
 *                a type variable
 * @param <PK>
 *                the primary key for that type
 */
@SuppressWarnings("unchecked")
public class HibernateGenericPlusDaoImp<T, PK extends Serializable> extends HibernateDaoSupport implements HibernateGenericPlusDao<T, PK> {
    /**
     * Log variable for all child classes. Uses LogFactory.getLog(getClass()) from Commons Logging
     */
    protected final Log log = LogFactory.getLog(getClass());

    private Class<T> persistentClass;

    /**
     * Constructor that takes in a class to see which type of entity to persist
     * 
     * @param persistentClass
     *                the class type you'd like to persist
     */
    public HibernateGenericPlusDaoImp(final Class<T> persistentClass) {
	this.persistentClass = persistentClass;
    }

   
    public HibernateGenericPlusDaoImp() {
	ParameterizedType thisType = (ParameterizedType) getClass().getGenericSuperclass();
	System.out.println(thisType);
	persistentClass = (Class) thisType.getActualTypeArguments()[0];
	System.out.println(Arrays.toString(thisType.getActualTypeArguments()));
	System.out.println(persistentClass.getName());
    }

    /**
     * {@inheritDoc}
     */
   
    public List<T> getAll() throws DataAccessException {
	return super.getHibernateTemplate().loadAll(this.persistentClass);
    }

    /**
     * {@inheritDoc}
     */
   
    public T get(PK id) throws DataAccessException {
	T entity = (T) super.getHibernateTemplate().get(this.persistentClass, id);

	if (entity == null) {
	    log.warn("Uh oh, '" + this.persistentClass + "' object with id '" + id + "' not found...");
	    throw new ObjectRetrievalFailureException(this.persistentClass, id);
	}

	return entity;
    }

    /**
     * {@inheritDoc}
     */
   
    public boolean exists(PK id) throws DataAccessException {
	T entity = (T) super.getHibernateTemplate().get(this.persistentClass, id);
	return entity != null;
    }

    /**
     * {@inheritDoc}
     */
   
    public T save(T object) throws DataAccessException {
	// return (T) super.getHibernateTemplate().merge(object);
	super.getHibernateTemplate().persist(object);
	return object;
    }

    public void remove(PK id) throws DataAccessException {
	super.getHibernateTemplate().delete(this.get(id));
    }

    // -------------------------------------------------------------------------
    // Convenience finder methods for example
    // -------------------------------------------------------------------------

   
    public List<T> findByExample(T exampleEntity, int firstResult, int maxResults) throws DataAccessException {
	return super.getHibernateTemplate().findByExample(exampleEntity, firstResult, maxResults);
    }

   
    public List<T> findByExample(T exampleEntity) throws DataAccessException {
	return super.getHibernateTemplate().findByExample(exampleEntity);
    }

    // -------------------------------------------------------------------------
    // Convenience pagination finder methods
    // -------------------------------------------------------------------------

    public PaginationHolder<T> getPaginationHolder(PaginationInfo pageInfo, T exampleEntity) {
	return new QueryByExamplePaginationFactory<T>(getHibernateTemplate(), exampleEntity).getPaginationHolder(pageInfo);
    }

    public PaginationHolder getPaginationHolder(PaginationInfo pageInfo, DetachedCriteria criteria) {
	return new DetachedCriteriaPaginationFactory(getHibernateTemplate(), criteria).getPaginationHolder(pageInfo);
    }

    public PaginationHolder getPaginationHolder(PaginationInfo pageInfo, String hql, Object[] objects) {
	return new HqlPaginationFactory(getHibernateTemplate(), hql, objects).getPaginationHolder(pageInfo);
    }

}

package com.rhyton.common.dao.hibernate;

import java.io.Serializable;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.criterion.DetachedCriteria;
import org.springframework.orm.ObjectRetrievalFailureException;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import com.rhyton.common.pagination.domain.PaginationHolder;
import com.rhyton.common.pagination.domain.PaginationInfo;
import com.rhyton.common.pagination.hibernate.DetachedCriteriaPaginationFactory;
import com.rhyton.common.pagination.hibernate.HqlPaginationFactory;
import com.rhyton.common.pagination.hibernate.QueryByExamplePaginationFactory;

/**
 * This class serves as the a class that can CRUD any object witout any Spring configuration. The only downside is it
 * does require casting from Object to the object class. 这个类用于执行CRUD任何对象而不需任何spring配置.不利的方面是需求自己进行类型转化(但修改后应该不用了).
 * 
 * @author Bryan Noll
 */
@SuppressWarnings("unchecked")
public class HiberanteUniversalPlusDaoImp extends HibernateDaoSupport implements HiberanteUniversalPlusDao {

    /**
     * Log variable for all child classes. Uses LogFactory.getLog(getClass()) from Commons Logging
     */
    protected final Log log = LogFactory.getLog(getClass());

    // -------------------------------------------------------------------------
    // Convenience CRUD methods
    // -------------------------------------------------------------------------
    public <T> T get(Class<T> clazz, Serializable id) {
	Object obj = getHibernateTemplate().get(clazz, id);
	if (obj == null) {
	    throw new ObjectRetrievalFailureException(clazz, id);
	}
	return clazz.cast(obj);
    }

    public <T> List<T> getAll(Class<T> clazz) {
	return getHibernateTemplate().loadAll(clazz);
    }

    public <T> void remove(Class<T> clazz, Serializable id) {
	getHibernateTemplate().delete(get(clazz, id));
    }

    public <T> T save(T o) {
	return (T) getHibernateTemplate().merge(o);
    }

    // -------------------------------------------------------------------------
    // Convenience pagination finder methods
    // -------------------------------------------------------------------------

    public <T> PaginationHolder<T> getPaginationHolder(PaginationInfo pageInfo, T exampleEntity) {
	return new QueryByExamplePaginationFactory<T>(getHibernateTemplate(), exampleEntity).getPaginationHolder(pageInfo);
    }

    public PaginationHolder getPaginationHolder(PaginationInfo pageInfo, DetachedCriteria criteria) {
	return new DetachedCriteriaPaginationFactory(getHibernateTemplate(), criteria).getPaginationHolder(pageInfo);
    }

    public PaginationHolder getPaginationHolder(PaginationInfo pageInfo, String hql, Object[] objects) {
	return new HqlPaginationFactory(getHibernateTemplate(), hql, objects).getPaginationHolder(pageInfo);
    }

}

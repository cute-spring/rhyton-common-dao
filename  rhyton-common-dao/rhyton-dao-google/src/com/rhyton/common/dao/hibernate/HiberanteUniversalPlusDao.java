package com.rhyton.common.dao.hibernate;
 

import org.hibernate.criterion.DetachedCriteria;

import com.rhyton.common.dao.UniversalDao;
import com.rhyton.common.pagination.domain.PaginationHolder;
import com.rhyton.common.pagination.domain.PaginationInfo;
@SuppressWarnings("unchecked")
public interface HiberanteUniversalPlusDao extends UniversalDao {
	<T> PaginationHolder<T> getPaginationHolder(PaginationInfo pageInfo, T exampleEntity);

	PaginationHolder getPaginationHolder(PaginationInfo pageInfo, DetachedCriteria criteria);

	PaginationHolder getPaginationHolder(PaginationInfo pageInfo, String hql, Object[] objects);
}

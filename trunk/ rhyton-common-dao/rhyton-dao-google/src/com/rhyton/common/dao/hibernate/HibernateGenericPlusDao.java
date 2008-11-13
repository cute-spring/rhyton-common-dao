package com.rhyton.common.dao.hibernate;

import java.io.Serializable;
import java.util.List;

import org.springframework.dao.DataAccessException;

import com.rhyton.common.dao.GenericDao;
import com.rhyton.common.pagination.domain.PaginationHolder;
import com.rhyton.common.pagination.domain.PaginationInfo;

public interface HibernateGenericPlusDao<T, PK extends Serializable> extends GenericDao<T, PK> {

	List<T> findByExample(T exampleEntity) throws DataAccessException;

	PaginationHolder<T> getPaginationHolder(PaginationInfo pageInfo, T exampleEntity);
	
}

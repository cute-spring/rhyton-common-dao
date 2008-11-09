package com.rhyton.common.dao.jdbc;

import java.io.Serializable;

import com.rhyton.common.dao.GenericDao;

public interface JdbcGenericPlusDao<T, PK extends Serializable> extends GenericDao<T, PK> {

	// List<T> findByExample(T exampleEntity, int firstResult, int maxResults) throws DataAccessException;
	//
	// List<T> findByExample(T exampleEntity) throws DataAccessException;
	//
	
	
//	 PaginationHolder<T> getPaginationHolder(PaginationInfo pageInfo, T exampleEntity);
//	 
//	 PaginationHolder<T> getPaginationHolder(PaginationInfo pageInfo, T exampleEntity);

}

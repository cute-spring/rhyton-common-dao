package com.rhyton.common.dao.iBatis;

import java.io.Serializable;
import java.util.List;

import org.springframework.dao.DataAccessException;

import com.rhyton.common.dao.GenericDao;
import com.rhyton.common.pagination.domain.PaginationHolder;
import com.rhyton.common.pagination.domain.PaginationInfo;

public interface SqlMapClientGenericPlusDao<T, PK extends Serializable> extends GenericDao<T, PK> {
    PaginationHolder<T> getPaginationHolder(PaginationInfo pageInfo, T exampleEntity);

    PaginationHolder<T> getPaginationHolder(String id, PaginationInfo pageInfo, T exampleEntity);

    List<T> queryByExample(T exampleEntity) throws DataAccessException;

    T queryForObject(String id) throws DataAccessException;

    T queryForObject(String id, Object parameterObject) throws DataAccessException;

    List<T> queryForList(String id) throws DataAccessException;

    List<T> queryForList(String id, Object parameterObject) throws DataAccessException;

}

package com.rhyton.common.dao.iBatis;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

import org.springframework.dao.DataAccessException;

import com.rhyton.common.dao.GenericDao;
import com.rhyton.common.pagination.domain.PaginationHolder;
import com.rhyton.common.pagination.domain.PaginationInfo;

public interface SqlMapClientGenericPlusDao<T, PK extends Serializable> extends GenericDao<T, PK> {

    // ================================================================================================

    public PaginationHolder<T> getPaginationHolder(PaginationInfo pageInfo, Object parameterObj);

    public PaginationHolder<T> getPaginationHolder(String daoMethodName, PaginationInfo pageInfo, Object parameterObj);

    // ================================================================================================

    public T queryForObject(T exampleEntity);

    public T queryForObject(String daoMethodName) throws DataAccessException;

    public T queryForObject(String daoMethodName, Object parameterObject) throws DataAccessException;

    // ================================================================================================

    public List<T> queryByExample(T exampleEntity) throws DataAccessException;

    public List<T> queryForList(String daoMethodName) throws DataAccessException;

    public List<T> queryForList(String daoMethodName, Object parameterObject) throws DataAccessException;

    // ================================================================================================

    public void update(String daoMethodName, Object parameter);

    public void update(String daoMethodName);

    // =========================================批量操作=======================================================
    /**
     * 批量删除
     * 
     * @param PrimaryKeyList
     * @throws DataAccessException
     */
    public void batchRemove(Collection<PK> PrimaryKeyList) throws DataAccessException;

    /**
     * 批量更新
     * 
     * @param parameterList
     * @throws DataAccessException
     */
    public void batchUpdate(Collection<T> parameterList) throws DataAccessException;

    /**
     * 批量插入
     * 
     * @param parameterList
     * @throws DataAccessException
     */
    public void batchInsert(final Collection<T> parameterList) throws DataAccessException;

}

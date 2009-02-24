package com.rhyton.common.dao.iBatis;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

import org.springframework.dao.DataAccessException;

import com.rhyton.common.dao.UniversalDao;
import com.rhyton.common.pagination.domain.PaginationHolder;
import com.rhyton.common.pagination.domain.PaginationInfo;

 
public interface SqlMapClientUniversalPlusDao extends UniversalDao {
	// ================================================================================================

	public <T> PaginationHolder<T> getPaginationHolder(Class<T> clazz, PaginationInfo pageInfo, Object parameterObj);

	public <T> PaginationHolder<T> getPaginationHolder(Class<T> clazz, String daoMethodName, PaginationInfo pageInfo, Object parameterObj);

	// ================================================================================================

	public <T> T queryForObject(T exampleEntity);

	public <T> T queryForObject(Class<T> clazz, String daoMethodName) throws DataAccessException;

	public <T> T queryForObject(Class<T> clazz, String daoMethodName, Object parameterObject) throws DataAccessException;

	// ================================================================================================

	public <T> List<T> queryByExample(T exampleEntity) throws DataAccessException;

	public <T> List<T> queryForList(Class<T> clazz, String daoMethodName) throws DataAccessException;

	public <T> List<T> queryForList(Class<T> clazz, String daoMethodName, Object parameterObject) throws DataAccessException;

	// ================================================================================================

	public <T> void update(Class<T> clazz, String daoMethodName, Object parameter);

	public <T> void update(Class<T> clazz, String daoMethodName);

	// =========================================批量操作=======================================================
	/**
	 * 批量删除
	 * 
	 * @param PrimaryKeyList
	 * @throws DataAccessException
	 */
	public <T> void batchRemove(Class<T> clazz, Collection<? extends Serializable> PrimaryKeyList) throws DataAccessException;
	/**
	 * 批量更新
	 * 
	 * @param parameterList
	 * @throws DataAccessException
	 */
	public <T> void batchUpdate(Collection<T> parameterList) throws DataAccessException;

	/**
	 * 批量插入
	 * 
	 * @param parameterList
	 * @throws DataAccessException
	 */
	public <T> void batchInsert(final Collection<T> parameterList) throws DataAccessException;
}

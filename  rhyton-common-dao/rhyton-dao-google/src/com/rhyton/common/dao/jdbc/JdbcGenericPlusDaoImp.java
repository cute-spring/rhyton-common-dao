package com.rhyton.common.dao.jdbc;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.util.List;

import javax.sql.DataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcOperations;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.jdbc.support.incrementer.DataFieldMaxValueIncrementer;
import org.springframework.util.Assert;

import com.rhyton.common.domain.BaseEntity;
@Deprecated
public class JdbcGenericPlusDaoImp<T, PK extends Serializable> extends JdbcDaoSupport implements
		JdbcGenericPlusDao<T, PK>, InitializingBean {

	/**
	 * Log variable for all child classes. Uses LogFactory.getLog(getClass()) from Commons Logging
	 */
	protected final Log log = LogFactory.getLog(getClass());

	private DataFieldMaxValueIncrementer defaultIncrementer; // 框架默认的ID主键产生器

	private DataFieldMaxValueIncrementer incrementer;// 自定义主键产生器

	private ParameterizedRowMapperRegister parameterizedRowMapperRegister;

	private Class<T> persistentClass;

	private String persistentClassName;

	private ParameterizedRowMapper<T> pojoMapper = null;

	private SimpleJdbcOperations simpleJdbcOperations;

	private SqlRegister sqlRegister;

	@SuppressWarnings("unchecked")
	public JdbcGenericPlusDaoImp() {
		ParameterizedType thisType = (ParameterizedType) getClass().getGenericSuperclass();// System.out.println(thisType);
		this.persistentClass = (Class) thisType.getActualTypeArguments()[0];// System.out.println(Arrays.toString(thisType.getActualTypeArguments()));
		this.persistentClassName = persistentClass.getName();// System.out.println(this.persistentClassName);
	}

	// -------------------------------------------------------------------------
	// CRUD methods
	// -------------------------------------------------------------------------

	public boolean exists(PK id) throws DataAccessException {
		try {
			return get(id) == null;
		} catch (DataAccessException dae) {
			return false;
		}
	}

	public T get(PK id) throws DataAccessException {
		return getSimpleJdbcOperations().queryForObject(getSql("get"), getPojoMapper(), id);
	}

	public List<T> getAll() throws DataAccessException {
		return getSimpleJdbcOperations().query(getSql("getAll"), getPojoMapper());
	}

	public void remove(PK id) throws DataAccessException {
		getSimpleJdbcOperations().update(getSql("remove"), id);
	}

	public T save(T object) throws DataAccessException {
		Assert.notNull(object, "The object to be saved  must not be null");
		String sql = getSql("save");
		if (BaseEntity.class.isAssignableFrom(object.getClass())) {
			((BaseEntity) object).setId(this.getIncrementer().nextLongValue());
		} else {
			// 全局用一个默认的ID产生器,也可以自定义设置或实现
		}
		SqlParameterSource namedParameters = new BeanPropertySqlParameterSource(object);
		getSimpleJdbcOperations().update(sql, namedParameters); // (sql, namedParameters);
		return object;
	}


	// -------------------------------------------------------------------------
	// Setter methods for dependency injection
	// -------------------------------------------------------------------------

	public void setDefaultIncrementer(DataFieldMaxValueIncrementer defaultIncrementer) {
		this.defaultIncrementer = defaultIncrementer;
	}

	public void setIncrementer(DataFieldMaxValueIncrementer incrementer) {
		this.incrementer = incrementer;
	}

	public void setParameterizedRowMapperRegister(ParameterizedRowMapperRegister parameterizedRowMapperRegister) {
		this.parameterizedRowMapperRegister = parameterizedRowMapperRegister;
	}

	public void setSqlRegister(SqlRegister sqlRegister) {
		this.sqlRegister = sqlRegister;
	}

	protected SimpleJdbcTemplate createSimpleJdbcTemplate(DataSource dataSource) {
		return new SimpleJdbcTemplate(dataSource);
	}

	protected ParameterizedRowMapper<T> getPojoMapper() {
		return pojoMapper;
	}

	protected SimpleJdbcOperations getSimpleJdbcOperations() {
		return simpleJdbcOperations;
	}

	@Override
	protected void initDao() throws Exception {
		super.initDao();
		this.simpleJdbcOperations = createSimpleJdbcTemplate(this.getDataSource());
		this.pojoMapper = this.parameterizedRowMapperRegister.get(this.persistentClass);
		Assert.notNull(this.defaultIncrementer, "The default DataFieldMaxValueIncrementer must not be null");
		Assert.notNull(this.pojoMapper, "The parameterizedRowMapper of the POJO must not be null");
	}

	protected DataFieldMaxValueIncrementer getIncrementer() {
		return incrementer == null ? this.defaultIncrementer : incrementer;
	}

	private String getSql(String methodName) {
		return this.sqlRegister.getSql(this.persistentClassName, methodName);
	}

}

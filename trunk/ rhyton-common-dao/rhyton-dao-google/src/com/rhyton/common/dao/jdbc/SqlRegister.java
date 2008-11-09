package com.rhyton.common.dao.jdbc;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.InitializingBean;
@SuppressWarnings("unchecked")
@Deprecated
public class SqlRegister implements InitializingBean {
	// <distinctName,SQL> ,and default <className+method, SQL>
	private Map<String, String> sqlMap = new HashMap<String, String>();

	public void afterPropertiesSet() throws Exception {
		initSqlMap();
	}

	/**
	 * init the sql map with properties files.
	 */
	protected void initSqlMap() {

	}

	/**
	 * @param pojoClass the class type of the domain object
	 * @param methodName the postfix (the default name is method name)
	 * @return SQL
	 */
	public String getSql(Class pojoClass, String methodName) {
		return getSql(pojoClass.getName(), methodName);
	}

	/**
	 * @param pojoName the full name of the domain object
	 * @param methodName the postfix (the default name is method name)
	 * @return SQL
	 */
	public String getSql(String pojoName, String methodName) {
		return getSql(pojoName + "." + methodName);
	}

	/**
	 * @param distinctName
	 * @return SQL
	 */
	public String getSql(String distinctName) {
		return sqlMap.get(distinctName);
	}

}

/**
 * 
 */
package com.rhyton.common.dao.iBatis.type;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.ibatis.sqlmap.client.extensions.ParameterSetter;
import com.ibatis.sqlmap.client.extensions.ResultGetter;
import com.ibatis.sqlmap.engine.type.BaseTypeHandler;
import com.ibatis.sqlmap.engine.type.TypeHandler;
/**
 * An iBATIS type handler callback for java.lang.Booleans that are mapped to
 * either 'Y' or 'N' in the database. If a value is something other than 'Y' 
 * in the database, including <code>null</code>, the resulting Boolean will be false.
 * <p>
 * DB   -->  Java
 * ----------------
 * 'Y'       true
 * null      false
 * 'N'       false
 * 'blah'    false
 * 
 * Java  -->  DB
 * ----------------
 * null       'N'
 * false      'N'
 * true       'Y'
 * @version
 * @see
 * @since 2008-5-6 下午07:06:31
 * @updatehistory 时间:2008-5-6 下午07:06:31;
 */
public class YNBooleanTypeHandler extends BaseTypeHandler implements TypeHandler  {

	/** Indicates No or false. */
	static final String FALSE_STRING = "N";

	/** Indicates Yes or true. */
	static final String TRUE_STRING = "Y";

	/**
	 * Trims the String if not null.
	 */
	static String trim(String string) {
		return (string == null) ? null : string.trim();
	}

	/*
	 * (non-Javadoc)
	 * @see com.ibatis.sqlmap.engine.type.TypeHandler#getResult(java.sql.CallableStatement, int)
	 */
	public Object getResult(CallableStatement cs, int columnIndex) throws SQLException {
		final String dbValue = trim(cs.getString(columnIndex));
		if (cs.wasNull()) {
			return null;
		} else {
			return valueOf(dbValue);
		}
	}

	/**
	 * From DB to Java.
	 */
	public Object getResult(ResultGetter getter) throws SQLException {
		final String dbValue = trim(getter.getString());
		return valueOf(dbValue);
	}

	public Object getResult(ResultSet rs, int columnIndex) throws SQLException {
		final String dbValue = trim(rs.getString(columnIndex));
		return valueOf(dbValue);
	}

	public Object getResult(ResultSet rs, String columnName) throws SQLException {
		final String dbValue = trim(rs.getString(columnName));
		return valueOf(dbValue);
	}

	/**
	 * From Java to DB.
	 */
	public void setParameter(ParameterSetter setter, Object parameter) throws SQLException {
		if (parameter == null) {
			setter.setString(FALSE_STRING);
			return;
		}

		final Boolean bool = (Boolean) parameter;

		if (bool.booleanValue()) {
			setter.setString(TRUE_STRING);
		} else {
			setter.setString(FALSE_STRING);
		}
	}

	public void setParameter(PreparedStatement ps, int i, Object parameter, String jdbcType) throws SQLException {
		boolean b = ((Boolean) parameter).booleanValue();
		ps.setString(i, b ? TRUE_STRING : FALSE_STRING);
	}

	/**
	 * Converts DB value to the Java value.
	 */
	public Object valueOf(String s) {
		if (s == null) {
			return Boolean.FALSE;
		}

		final String value = trim(s);

		if (TRUE_STRING.equals(value)) {
			return Boolean.TRUE;
		}

		return Boolean.FALSE;
	}
}

package com.rhyton.common.dao.iBatis;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.text.MessageFormat;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.ClassUtils;

import com.rhyton.common.dao.annotation.PrimaryKey;

/**
 * General iBATIS Utilities class with rules for primary keys and query names.
 * 
 * @author Bobby Diaz, Bryan Noll
 */
public final class iBatisDaoUtils {

	private static final String PREFIX_INSERT = ".insert";

	private static final String PREFIX_UPDATE = ".update";

	private static final String PREFIX_DELETE = ".delete";

	private static final String PREFIX_SELECT = ".select";

	private static final String PREFIX_SELECT_PRIAMARYKEY = ".selectByPrimaryKey";

	private static final String PREFIX_DELETE_PRIAMARYKEY = ".deleteByPrimaryKey";

	// private static final String PREFIX_COUNT = ".count";
	//
	// private static final String PREFIX_SELECTMAP = ".selectByMap";
	//
	// private static final String PREFIX_SELECTSQL = ".selectBySql";

	/**
	 * Log variable for all child classes. Uses LogFactory.getLog(getClass()) from Commons Logging
	 */
	protected static final Log log = LogFactory.getLog(iBatisDaoUtils.class);

	/**
	 * Checkstyle rule: utility classes should not have protected constructor
	 */
	private iBatisDaoUtils() {
	}

	/**
	 * @return Returns the insert query name.
	 * @param className
	 *            the name of the class - returns "add" + className
	 */
	protected static String getInsertQuery(String className) {
		return className + PREFIX_INSERT;
	}

	/**
	 * @return Returns the update query name.
	 * @param className
	 *            the name of the class - returns "update" + className
	 */
	protected static String getUpdateQuery(String className) {
		return className + PREFIX_UPDATE;
	}

	protected static String getDeleteQuery(String className) {
		return className + PREFIX_DELETE;
	}

	/**
	 * @return Returns the delete query name.
	 * @param className
	 *            the name of the class - returns "delete" + className
	 */
	protected static String getDeleteByPrimaryKeyQuery(String className) {
		return className + PREFIX_DELETE_PRIAMARYKEY;
	}

	/**
	 * @return Returns the find query name.
	 * @param className
	 *            the name of the class - returns "get" + className
	 */
	protected static String getFindQuery(String className) {
		return className + PREFIX_SELECT_PRIAMARYKEY;
	}

	/**
	 * @return Returns the select query name.
	 * @param className
	 *            the name of the class - returns "get" + className + "s"
	 */
	protected static String getSelectQuery(String className) {
		return className + PREFIX_SELECT;
	}

	@SuppressWarnings("unchecked")
	protected static String getQuery(Class cls, String statementId) {
		return cls.getName() + "." + statementId;
	}

	protected static String getStatementIdQuery(Object obj, String statementId) {
		return obj.getClass().getName() + "." + statementId;
	}

	private static Method getPrimaryKeyGetter(Object obj) {
		Method[] methods = obj.getClass().getMethods();
		for (Method method : methods) {
			PrimaryKey findAnnotation = AnnotationUtils.findAnnotation(method, PrimaryKey.class);
			if (findAnnotation != null && method.getName().startsWith("get")) {
				return method;
			}
		}
		return null;
	}

	private static Method getPrimaryKeySetter(Object obj) {
		Method[] methods = obj.getClass().getMethods();
		for (Method method : methods) {
			PrimaryKey findAnnotation = AnnotationUtils.findAnnotation(method, PrimaryKey.class);
			if (findAnnotation != null && method.getName().startsWith("set")) {
				return method;
			}
		}
		return null;
	}

	/**
	 * Get the value of the primary key using reflection.
	 * 
	 * @param o
	 *            the object to examine
	 * @return the value as an Object
	 */
	protected static Object getPrimaryKeyValue(Object o) {
		// Use reflection to find the first property that has the name "id" or "Id"
		Method getterMethod = null;
		try {
			getterMethod = getPrimaryKeyGetter(o);
			return getterMethod.invoke(o);
		} catch (Exception e) {
			e.printStackTrace();
			log.error("Could not invoke method '" + getterMethod + "' on " + ClassUtils.getShortName(o.getClass()));
		}
		return null;
	}

	/**
	 * Prepare object for save or update by looking for a "version" field and incrementing it if it exists. This should probably
	 * be changed to look for the
	 * 
	 * @Version annotation instead.
	 * @param o
	 *            the object to examine
	 */
	protected static void prepareObjectForSaveOrUpdate(Object o) {
		try {
			Field[] fieldlist = o.getClass().getDeclaredFields();
			for (Field fld : fieldlist) {
				String fieldName = fld.getName();
				if (fieldName.equals("version")) {
					Method setMethod = o.getClass().getMethod("setVersion", Integer.class);
					Object value = o.getClass().getMethod("getVersion", (Class[]) null).invoke(o, (Object[]) null);
					if (value == null) {
						setMethod.invoke(o, 1);
					} else {
						setMethod.invoke(o, (Integer) value + 1);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.error("Could not prepare '" + ClassUtils.getShortName(o.getClass()) + "' for insert/update");
		}
	}

	/**
	 * Sets the primary key's value
	 * 
	 * @param o
	 *            the object to examine
	 * @param clazz
	 *            the class type of the primary key
	 * @param value
	 *            the value of the new primary key
	 */
	protected static void setPrimaryKey(Object o, Object value) {

		Method setMethod = null;
		try {
			setMethod = getPrimaryKeySetter(o);
			if (value != null) {
				setMethod.invoke(o, value);
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.error(MessageFormat.format("Could not set ''{0}.{1} with value {2}", ClassUtils.getShortName(o.getClass())));
		}
	}
}

package com.rhyton.common.test;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.SessionFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.test.AbstractTransactionalDataSourceSpringContextTests;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * Base class for running DAO tests.
 * 
 * 1) 将需要自动装配的属性变量声明为protected；
 * 2) 在测试类构造函数中调用setPopulateProtectedVariables(true)方法。
 */
public abstract class BaseTestCase extends AbstractTransactionalDataSourceSpringContextTests {
	/**
	 * Log variable for all child classes. Uses LogFactory.getLog(getClass()) from Commons Logging
	 */
	protected final Log log = LogFactory.getLog(getClass());
	/**
	 * ResourceBundle loaded from src/test/resources/${package.name}/ClassName.properties (if exists)
	 */
	protected ResourceBundle rb;

	/**
	 * Default constructor - populates "rb" variable if properties file exists for the class in src/test/resources.
	 */
	public BaseTestCase() {
		// Since a ResourceBundle is not required for each class, just
		// do a simple check to see if one exists
		String className = this.getClass().getName();
		/*
		 * 将属性声明为protected后并通过setPopulateProtectedVariables(true)
		 * 启用对属性变量直接注入的机制（启用反射机制注入），你就可以避免为属性变量编写对应的Setter方法了.
		 * 提示 属性如果声明为public，虽然你也调用了setPopulateProtectedVariables(true)方法,属性变量
		 * 依然不会被自动注入。所以这种机制仅限于protected的属性变量。 
		 */
		setPopulateProtectedVariables(true); // 启用直接对属性变量进行注释的机制

		 setAutowireMode(AUTOWIRE_BY_NAME);    
		 setDefaultRollback(true);  
		try {
			rb = ResourceBundle.getBundle(className);
		} catch (MissingResourceException mre) {
			// log.warn("No resource bundle found for: " + className);
		}
	}

	/**
	 * Sets AutowireMode to AUTOWIRE_BY_NAME and configures all context files needed to tests DAOs.
	 * 
	 * @return String array of Spring context files.
	 */
	protected String[] getConfigLocations() {
		setAutowireMode(AUTOWIRE_BY_NAME);
		return new String[] { "classpath:/spring-config/applicationContext-resources.xml",
				"classpath:/spring-config/applicationContext*.xml", };
	}

	/**
	 * Utility method to populate a javabean-style object with values from a Properties file
	 * 
	 * @param obj
	 *            the model object to populate
	 * @return Object populated object
	 * @throws Exception
	 *             if BeanUtils fails to copy properly
	 */
	protected Object populate(Object obj) throws Exception {
		// loop through all the beans methods and set its properties from its .properties file
		Map<String, String> map = new HashMap<String, String>();

		for (Enumeration<String> keys = rb.getKeys(); keys.hasMoreElements();) {
			String key = keys.nextElement();
			map.put(key, rb.getString(key));
		}

		BeanUtils.copyProperties(map, obj);

		return obj;
	}

	/**
	 * Create a HibernateTemplate from the SessionFactory and call flush() and clear() on it. Designed to be used after "save"
	 * methods in tests: http://issues.appfuse.org/browse/APF-178.
	 */
	protected void flush() {
		HibernateTemplate hibernateTemplate = new HibernateTemplate((SessionFactory) applicationContext.getBean("sessionFactory"));
		hibernateTemplate.flush();
		hibernateTemplate.clear();
	}
}

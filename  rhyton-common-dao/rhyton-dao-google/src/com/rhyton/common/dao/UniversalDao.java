package com.rhyton.common.dao;

import java.io.Serializable;
import java.util.List;

/**
 * Data Access Object (DAO) interface.
 * 
 * @author This thing used to be named simply 'GenericDao' in versions of appfuse prior to 2.0. It was renamed in an attempt to distinguish and
 *         describe it as something different than GenericDao. GenericDao is intended for subclassing, and was named Generic because 1) it has very
 *         general functionality and 2) is 'generic' in the Java 5 sense of the word... aka... it uses Generics. Implementations of this class are not
 *         intended for subclassing. You most likely would want to subclass GenericDao. The only real difference is that instances of java.lang.Class
 *         are passed into the methods in this class, and they are part of the constructor in the GenericDao, hence you'll have to do some casting if
 *         you use this one. 此类与'GenericDao'相似.与'GenericDao'的区别在于'GenericDao'是试图用于子类的继承,命名为'Generic'的原因 1)它有最基础能用的功能
 *         .2)'Generic'与在Java5中的单词意思一致(AKA是Also Known As的缩写当某人或某事有广为人知的别名时，可以用aka来介绍) 也是这样用的.本类的实现不是用试图用于继承.你大多情况可能更愿意继承于GenericDao.最大的区别在于本类中是把that
 *         instances of java.lang.Class传入到方法中,而GenericDao是把java.lang.Class这一类型信息作为了constructor的一部分,因此你将不得不作casting的动作在使用此类时(不过现在不用了).
 * @see com.rhyton.framework.core.dao.GenericDao
 */

public interface UniversalDao {

	/**
	 * Generic method used to get all objects of a particular type. This is the same as lookup up all rows in a table.
	 * 
	 * @param clazz the type of objects (a.k.a. while table) to get data from
	 * @return List of populated objects
	 */
	<T> List<T> getAll(Class<T> clazz);

	/**
	 * Generic method to get an object based on class and identifier. An ObjectRetrievalFailureException Runtime Exception is thrown if nothing is
	 * found.
	 * 
	 * @param clazz model class to lookup
	 * @param id the identifier (primary key) of the class
	 * @return a populated object
	 * @see org.springframework.orm.ObjectRetrievalFailureException
	 */
	<T> T get(Class<T> clazz, Serializable id);

	/**
	 * Generic method to save an object - handles both update and insert.
	 * 
	 * @param o the object to save
	 * @return a populated object
	 */
	<T> T save(T o);

	/**
	 * Generic method to delete an object based on class and id
	 * 
	 * @param clazz model class to lookup
	 * @param id the identifier (primary key) of the class
	 */
	<T> void remove(Class<T> clazz, Serializable id);

}

package com.rhyton.common.dao;

import java.io.Serializable;
import java.util.List;

import org.springframework.dao.DataAccessException;

/**
 * Generic DAO (Data Access Object) with common methods to CRUD POJOs.
 * <p>
 * Extend this interface if you want typesafe (no casting necessary) DAO's for your domain objects.
 * @author <a href="mailto:bwnoll@gmail.com">Bryan Noll</a>
 * @param <T> a type variable
 * @param <PK> the primary key for that type
 */
public interface GenericDao<T, PK extends Serializable> {

	/**
	 * Generic method used to get all objects of a particular type. This is the same as lookup up all rows in a table.
	 * @return List of populated objects
	 */
	List<T> getAll() throws DataAccessException;

	/**
	 * Generic method to get an object based on class and identifier. An ObjectRetrievalFailureException Runtime
	 * Exception is thrown if nothing is found.
	 * @param id the identifier (primary key) of the object to get
	 * @return a populated object
	 * @see org.springframework.orm.ObjectRetrievalFailureException
	 */
	T get(PK id) throws DataAccessException;

	/**
	 * Checks for existence of an object of type T using the id arg.
	 * @param id the id of the entity
	 * @return - true if it exists, false if it doesn't
	 */
	boolean exists(PK id) throws DataAccessException;

	/**
	 * Generic method to save an object - handles both update and insert.
	 * @param object the object to save
	 * @return the persisted object
	 */
	T save(T object) throws DataAccessException;

	/**
	 * Generic method to delete an object based on class and id
	 * @param id the identifier (primary key) of the object to remove
	 */
	void remove(PK id) throws DataAccessException;
}
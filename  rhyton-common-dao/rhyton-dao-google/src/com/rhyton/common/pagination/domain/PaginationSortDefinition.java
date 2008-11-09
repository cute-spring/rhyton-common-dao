package com.rhyton.common.pagination.domain;

/**
 * * Mutable implementation of the {@link PaginationSortDefinition} interface. Supports toggling the ascending value on
 * setting the same property again. Definition for sorting bean instances by a property.
 *  在我们的应用此类的用途只是做为记录排序信息的载体而已.
 * @author zhx
 * @version
 * @see org.springframework.beans.support.SortDefinition
 * @since 2008-1-11 上午10:49:25
 */
public class PaginationSortDefinition implements org.springframework.beans.support.SortDefinition {
	private boolean ascending = true;

	private boolean ignoreCase = true;

	private String property = "";

	/**
	 * Create an empty PaginationSortDefinition,
	 */
	public PaginationSortDefinition() {
	}

	/**
	 * Create a PaginationSortDefinition for the given settings.
	 * @param property the property to compare
	 * @param ascending whether to sort ascending (true) or descending (false)
	 */
	public PaginationSortDefinition(String property, boolean ascending) {
		this(property, true, ascending);
	}

	/**
	 * Create a PaginationSortDefinition for the given settings.
	 * @param property the property to compare
	 * @param ignoreCase whether upper and lower case in String values should be ignored
	 * @param ascending whether to sort ascending (true) or descending (false)
	 */
	public PaginationSortDefinition(String property, boolean ignoreCase, boolean ascending) {
		this.property = property;
		this.ignoreCase = ignoreCase;
		this.ascending = ascending;
	}

	public String getProperty() {
		return property;
	}

	public boolean isAscending() {
		return ascending;
	}

	/**
	 * Set whether upper and lower case in String values should be ignored.
	 */
	public boolean isIgnoreCase() {
		return ignoreCase;
	}

	/**
	 * Set whether to sort ascending (true) or descending (false).
	 */
	public void setAscending(boolean ascending) {
		this.ascending = ascending;
	}

	public void setIgnoreCase(boolean ignoreCase) {
		this.ignoreCase = ignoreCase;
	}

	public void setProperty(String property) {
		this.property = property;
	}

}

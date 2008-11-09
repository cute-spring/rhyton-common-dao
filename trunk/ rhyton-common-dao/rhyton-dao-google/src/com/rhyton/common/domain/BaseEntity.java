package com.rhyton.common.domain;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import com.rhyton.common.dao.annotation.PrimaryKey;

/**
 * Simple JavaBean domain object with an id property. Used as a base class for objects needing this property.
 * 
 * @author Ken Krebs
 * @author Juergen Hoeller
 */
public class BaseEntity {

	private Long id;

	public BaseEntity() {
	}

	public BaseEntity(Long id) {
		this.id = id;
	}

	@PrimaryKey
	public Long getId() {
		return id;
	}

	public boolean isNew() {
		return id == null;
	}

	@PrimaryKey
	public void setId(Long id) {
		this.id = id;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if ((obj == null) || (obj.getClass() != this.getClass()))
			return false;
		BaseEntity test = (BaseEntity) obj;
		return id == test.id;
	}

	@Override
	public int hashCode() {
		int hash = 7;
		hash = 31 * hash + id.intValue();
		return hash;
	}

	@Override
	public String toString() {
		ToStringBuilder tsb = new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE);
		tsb.append("id", id);
		return tsb.toString();
	}
}

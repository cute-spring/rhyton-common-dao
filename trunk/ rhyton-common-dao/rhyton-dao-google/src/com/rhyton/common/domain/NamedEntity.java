package com.rhyton.common.domain;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

/**
 * Simple JavaBean domain object adds a name property to <code>Entity</code>.
 * Used as a base class for objects needing these properties.
 *
 * @author Ken Krebs
 * @author Juergen Hoeller
 */
public class NamedEntity extends BaseEntity {

	private String name;

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return this.name;
	}

	public String toString() {
		ToStringBuilder tsb = new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE);
		tsb.append("id",this.getId());
		tsb.append("name",this.getName());
		return tsb.toString();
	}
}

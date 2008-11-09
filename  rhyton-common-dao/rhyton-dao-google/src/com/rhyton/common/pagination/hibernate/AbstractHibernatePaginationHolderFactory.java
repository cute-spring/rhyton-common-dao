package com.rhyton.common.pagination.hibernate;

import java.util.List;

import org.springframework.orm.hibernate3.HibernateTemplate;

import com.rhyton.common.pagination.PaginationHolderFactory;

public abstract class AbstractHibernatePaginationHolderFactory<T> extends PaginationHolderFactory<T> {
	protected HibernateTemplate hibernateTemplate;

	public List<T> getPageList(final int firstNum, final int lastNum) {
		log.info(this.getClass().getName() + "   firstNum = " + (firstNum) + " lastNum = " + lastNum);
		final int firstResult = firstNum - 1;
		final int maxResults = lastNum - firstResult;
		return getPageListByHibernate(firstResult, maxResults);
	}

	public abstract List<T> getPageListByHibernate(final int firstResult, final int maxResults);

	protected HibernateTemplate getHibernateTemplate() {
		return hibernateTemplate;
	}

	protected void setHibernateTemplate(HibernateTemplate hibernateTemplate) {
		this.hibernateTemplate = hibernateTemplate;
	}
}

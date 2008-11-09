package com.rhyton.common.pagination.hibernate;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.Example;
import org.hibernate.criterion.Projections;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.util.Assert;

public class QueryByExamplePaginationFactory<T> extends AbstractHibernatePaginationHolderFactory<T> {

	private T exampleEntity;

	public QueryByExamplePaginationFactory(HibernateTemplate ht, T exampleEntity) {
		super.setHibernateTemplate(ht);
		this.exampleEntity = exampleEntity;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<T> getPageListByHibernate(int firstResult, int maxResults) {
		return super.getHibernateTemplate().findByExample(exampleEntity, firstResult, maxResults);
	}

	@Override
	public int getTotalNumOfElements() {
		Assert.notNull(exampleEntity, "Example entity must not be null");
		super.getHibernateTemplate().execute(new HibernateCallback() {
			public Object doInHibernate(Session session) throws HibernateException {
				Criteria executableCriteria = session.createCriteria(exampleEntity.getClass());
				executableCriteria.add(Example.create(exampleEntity));
				return ((Integer) executableCriteria.setProjection(Projections.rowCount()).uniqueResult()).intValue();
			}
		}, true);
		return 0;
	}

}

package com.rhyton.common.pagination.hibernate;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Projection;
import org.hibernate.criterion.Projections;
import org.hibernate.impl.CriteriaImpl;
import org.hibernate.transform.ResultTransformer;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.HibernateTemplate;

/**
 * 此类实现了基于Hibernate的DetachedCriteria条件的分页查询.并已做了部分优化---在查询总启示录数时,去除掉了排序信息,认减少不必要的开销.
 * @author zhx
 * @version
 * @see
 * @since 2008-1-12 下午04:25:10
 * @updatehistory 时间:2008-1-12 下午04:25:10; 修改人:zhx; 说明:reason
 * @param <T>
 */
@SuppressWarnings("unchecked")
public class DetachedCriteriaPaginationFactory<T> extends AbstractHibernatePaginationHolderFactory<T> {

	private final DetachedCriteria detachedCriteria;

	private List emptyList = new ArrayList();

	private boolean emptyOrderEntries = false;

	private List orderEntries;

	private Projection projection;

	private Criteria projectionCriteria;

	private ResultTransformer resultTransformer;

	public DetachedCriteriaPaginationFactory(HibernateTemplate ht, DetachedCriteria criteria) {
		super.setHibernateTemplate(ht);
		// this.detachedCriteria = (DetachedCriteria)SerializationUtils.clone(criteria); //调用这个方法的确比较费时间
		this.detachedCriteria = criteria;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<T> getPageListByHibernate(final int firstResult, final int maxResults) {
		return (List<T>) super.getHibernateTemplate().execute(new HibernateCallback() {
			public Object doInHibernate(Session session) throws HibernateException {
				Criteria criteria = DetachedCriteriaPaginationFactory.this.detachedCriteria
						.getExecutableCriteria(session);
				return criteria.setFirstResult(firstResult).setMaxResults(maxResults).list();
			}
		}, true);
	}

	public int getTotalNumOfElements() {
		return (Integer) super.getHibernateTemplate().execute(new HibernateCallback() {
			public Object doInHibernate(Session session) throws HibernateException {

				CriteriaImpl criteria = (CriteriaImpl) DetachedCriteriaPaginationFactory.this.detachedCriteria
						.getExecutableCriteria(session);
				// 在调用setProjection方法前的预处理
				DetachedCriteriaPaginationFactory.this.prepareBeforeInvokeSetProjection(criteria);
				// 清除排序条件
				DetachedCriteriaPaginationFactory.this.eraseOrderInfoFromCriteriaImpl(criteria);

				// 通过查看源码可知此方法操作会对criteria对象也同时对detachedCriteria的部分状态产生影响.--->操作1
				criteria.setProjection(Projections.rowCount());
				int totalCount = (Integer) criteria.uniqueResult();// 通过查看源码可知,此方法的操作对criteria对象的内部状态不会产生任何影响.

				// 恢复排序条件
				DetachedCriteriaPaginationFactory.this.resumeOrderInfo4CriteriaImpl(criteria);
				// 恢复调用setProjection方法前的状态
				DetachedCriteriaPaginationFactory.this.afterInvokeSetProjection(criteria);

				return totalCount;
			}
		}, true);
	}

	/**
	 * 恢复CriteriaImpl对象状态到调用SetProjection方法之前
	 * @param criteria 要被处理的CriteriaImpl对象
	 */
	void afterInvokeSetProjection(CriteriaImpl criteria) {
		criteria.setProjection(projection);
		criteria.setResultTransformer(resultTransformer);
		setProjectionCriteria(criteria, projectionCriteria);
	}

	/**
	 * 清除掉criteria中的排序信息
	 * @param criteria
	 */
	void eraseOrderInfoFromCriteriaImpl(CriteriaImpl criteria) {
		try {
			Field field = CriteriaImpl.class.getDeclaredField("orderEntries");
			field.setAccessible(true);// 这是关键：）
			orderEntries = (List) field.get(criteria);
			if (orderEntries == null && orderEntries.size() == 0) {
				emptyOrderEntries = true;
			} else {
				field.set(criteria, emptyList);
			}
		} catch (Exception e) {
		}
	}

	/**
	 * 获取在调用CriteriaImpl对象的SetProjection方法时,将会受到影响的对象的引用.依据参见@org.hibernate.impl.CriteriaImpl源码setProjection方法.
	 * @param criteria 要被处理的CriteriaImpl对象
	 */
	void prepareBeforeInvokeSetProjection(CriteriaImpl criteria) {
		projection = criteria.getProjection();
		resultTransformer = criteria.getResultTransformer();
		projectionCriteria = criteria.getProjectionCriteria();
	}

	/**
	 * 恢复criteria被清除掉的排序信息
	 * @param criteria
	 */
	@SuppressWarnings("unchecked")
	void resumeOrderInfo4CriteriaImpl(CriteriaImpl criteria) {
		if (!emptyOrderEntries)
			emptyList.addAll(orderEntries);
	}

	/**
	 * 为 criteria对象设置projectionCriteria属性的值
	 * @param criteria
	 * @param projectionCriteria
	 */
	void setProjectionCriteria(CriteriaImpl criteria, Criteria projectionCriteria) {
		try {
			Field field = CriteriaImpl.class.getDeclaredField("projectionCriteria");
			field.setAccessible(true);// 这是关键：）
			field.set(criteria, projectionCriteria);
		} catch (Exception e) {
		}
	}
}
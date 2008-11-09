package com.rhyton.common.pagination.hibernate;

import java.sql.SQLException;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.util.Assert;

public class HqlPaginationFactory<T> extends AbstractHibernatePaginationHolderFactory<T> {
	private String hql;

	private Object[] args;// 命名参考JdbcTemplate

	private static final Object[] EMPTY_ARGUMENTS = {};

	public HqlPaginationFactory(HibernateTemplate ht, String hql, Object... args) {
		Assert.notNull(ht, "HibernateTemplate must not be null");
		Assert.notNull(hql, "HQL must not be null");
		super.setHibernateTemplate(ht);
		this.hql = hql;
		this.setArgs(args);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<T> getPageListByHibernate(final int firstResult, final int maxResults) {
		List list = (List) super.getHibernateTemplate().execute(new HibernateCallback() {
			public Object doInHibernate(Session session) throws HibernateException, SQLException {
				Query query = session.createQuery(hql);
				for (int i = 0; i < getArgs().length; i++) {
					query.setParameter(i, getArgs()[i]);
				}
				query.setFirstResult(firstResult).setMaxResults(maxResults);
				return query.list();
			}
		});
		return list;
	}

	public int getTotalNumOfElements() {
		return new Integer(super.getHibernateTemplate().find(dealCountHQL(hql), getArgs()).get(0).toString())
				.intValue();
	}

	/**
	 * String ss = "select distinct t.type_id ,distinct t.name, t.company_id from CL_FL_IC_REGISTER t"; String ss2 =
	 * "select distinct t.type_id from CL_FL_IC_REGISTER t"; String hs = "from RegRecord "; select count(distinct
	 * t.type_id) from CL_FL_IC_REGISTER t select count(distinct t.type_id) from CL_FL_IC_REGISTER t select count(*)
	 * from RegRecord
	 * @param rawHql
	 * @return
	 */
	private static String dealCountHQL(String rawHql) {
		String upHQL = rawHql.toUpperCase();
		StringBuffer sb = new StringBuffer(upHQL);
		int x = sb.indexOf("FROM");
		int i = sb.substring(0, x).indexOf("DISTINCT");
		StringBuffer sb2 = new StringBuffer();
		if (i != -1) {
			int j = sb.indexOf(",");
			j = j == -1 ? x : j;
			sb2.append(rawHql.substring(0, i)).append("count(").append(rawHql.substring(i, j - 1).trim()).append(") ")
					.append(rawHql.substring(x));
		} else {
			sb2.append("select count(*) ").append(rawHql.substring(x));
		}

		return sb2.toString();
	}

	public static void main(String[] args) {
		/*
		 * 处理HQL的方法测试
		 */
		String hql_0 = "select  distinct t.type_id ,distinct t.name, t.company_id  from CL_FL_IC_REGISTER t";
		String hql_1 = "select distinct t.type_id from CL_FL_IC_REGISTER t";
		String hql_2 = "from RegRecord ";

		System.out.println("原HQL：" + hql_0 + ";\r\t　 处理后: " + dealCountHQL(hql_0));
		System.out.println("原HQL：" + hql_1 + ";\r\t　 处理后: " + dealCountHQL(hql_1));
		System.out.println("原HQL：" + hql_2 + ",\r\t　 处理后: " + dealCountHQL(hql_2));
	}

	private Object[] getArgs() {
		return args;
	}

	private void setArgs(Object[] args) {
		this.args = (args == null ? EMPTY_ARGUMENTS : args);
	}

}

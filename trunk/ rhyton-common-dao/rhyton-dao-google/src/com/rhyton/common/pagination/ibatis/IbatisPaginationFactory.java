package com.rhyton.common.pagination.ibatis;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.orm.ibatis.SqlMapClientTemplate;

import com.rhyton.common.pagination.PaginationHolderFactory;

/**
 * @author zhangxin
 * 
 * @param <T>
 */
public class IbatisPaginationFactory<T> extends PaginationHolderFactory<T> {
	private Log log = LogFactory.getLog(IbatisPaginationFactory.class);

	private SqlMapClientTemplate smct;
	private T exampleEntity;

	private String className;

	private String methodName;

	private static final String POSTFIX_LISTPAGE_COUNT = ".listPageCount";

	private static final String POSTFIX_LISTPAGE = ".listPage";

	public IbatisPaginationFactory(SqlMapClientTemplate smct, T exampleEntity) {
		this.smct = smct;
		this.exampleEntity = exampleEntity;
		this.className = exampleEntity.getClass().getName();
	}

	public IbatisPaginationFactory(SqlMapClientTemplate smct, T exampleEntity, String methodName) {
		this.smct = smct;
		this.exampleEntity = exampleEntity;
		this.className = exampleEntity.getClass().getName();
		this.methodName = methodName;
	}

	@SuppressWarnings("unchecked")
	public List<T> getPageList(final int firstNum, final int lastNum) {
		if (log.isInfoEnabled()) {
			log.info("firstNum = " + firstNum + "; lastNum =  " + lastNum);
		}
		return (List<T>) this.smct.queryForList(getListPageStatementName(), this.getExampleEntity(), firstNum-1, lastNum);
	}

	public int getTotalNumOfElements() {
		if (log.isInfoEnabled()) {
			log.info("TotalNumOfElements SQL= " + this.getExampleEntity());
		}
		Integer tn = (Integer) this.smct.queryForObject(getListPageCountStatementName(), this.getExampleEntity());
		return tn.intValue();
	}

	private String getListPageCountStatementName() {
		return this.className + POSTFIX_LISTPAGE_COUNT + (this.methodName == null ? "" : ("_" + this.methodName));
	}

	private String getListPageStatementName() {
		return this.className + POSTFIX_LISTPAGE + (this.methodName == null ? "" : ("_" + this.methodName));
	}

	public T getExampleEntity() {
		return exampleEntity;
	}

	public void setExampleEntity(T exampleEntity) {
		this.exampleEntity = exampleEntity;
	}
}

package com.rhyton.common.pagination.ibatis;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.orm.ibatis.SqlMapClientTemplate;

import com.rhyton.common.pagination.PaginationHolderFactory;

/**
 * @author zhangxin
 * @param <T>
 */
public class IbatisPaginationFactory<T> extends PaginationHolderFactory<T> {
    private Log log = LogFactory.getLog(IbatisPaginationFactory.class);

    private SqlMapClientTemplate smct;

    private Object parameterObject;

    private String className;

    private String methodName;

    private static final String POSTFIX_LISTPAGE_COUNT = ".listPageCount";

    private static final String POSTFIX_LISTPAGE = ".listPage";

    public IbatisPaginationFactory(SqlMapClientTemplate smct, T exampleEntity) {
	this(smct, exampleEntity, null);
    }

    public IbatisPaginationFactory(SqlMapClientTemplate smct, String className, Object parameterObject) {
	this(smct, className, parameterObject, null);
    }

    public IbatisPaginationFactory(SqlMapClientTemplate smct, T exampleEntity, String methodName) {
	this(smct, exampleEntity.getClass().getName(), exampleEntity, methodName);
    }

    public IbatisPaginationFactory(SqlMapClientTemplate smct, String className, Object parameterObject, String methodName) {
	this.smct = smct;
	this.className = className;
	this.parameterObject = parameterObject;
	this.methodName = methodName;
    }

    @SuppressWarnings("unchecked")
    public List<T> getPageList(final int firstNum, final int lastNum) {
	if (log.isInfoEnabled()) {
	    log.info("firstNum = " + firstNum + "; lastNum =  " + lastNum);
	}
	return (List<T>) this.smct.queryForList(getListPageStatementName(), this.getParameterObject(), firstNum - 1, lastNum);
    }

    public int getTotalNumOfElements() {
	if (log.isInfoEnabled()) {
	    log.info("TotalNumOfElements SQL= " + this.getParameterObject());
	}
	Integer tn = (Integer) this.smct.queryForObject(getListPageCountStatementName(), this.getParameterObject());
	return tn.intValue();
    }

    private String getListPageCountStatementName() {
	return this.className + POSTFIX_LISTPAGE_COUNT + (this.methodName == null ? "" : ("_" + this.methodName));
    }

    private String getListPageStatementName() {
	return this.className + POSTFIX_LISTPAGE + (this.methodName == null ? "" : ("_" + this.methodName));
    }

    private Object getParameterObject() {
	return parameterObject;
    }

}

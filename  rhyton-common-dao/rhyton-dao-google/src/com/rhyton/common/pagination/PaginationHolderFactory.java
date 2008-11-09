package com.rhyton.common.pagination;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.rhyton.common.pagination.domain.PaginationHolder;
import com.rhyton.common.pagination.domain.PaginationInfo;

public abstract class PaginationHolderFactory<T> {

	protected final Log log = LogFactory.getLog(getClass());

	public PaginationHolderFactory() {
	}

	/**
	 * 返回本次查询的分页记录列表
	 * @param firstNum 记录的起始位置数
	 * @param lastNum 记录的结束位置数
	 * @return 分页记录列表
	 */
	protected abstract List<T> getPageList(final int firstNum, final int lastNum);

	/**
	 * 返回符合本次查询条件的总记录数
	 * @return 符合本次查询条件的总记录数
	 */
	protected abstract int getTotalNumOfElements();

	/**
	 * 返回分页结果(包括分页信息及分页记录列表)
	 * @param pi 分页查询的查询条件信息
	 * @return 分页结果的holder
	 */
	public PaginationHolder<T> getPaginationHolder(PaginationInfo pi) {
		pi = (pi == null) ? new PaginationInfo() : pi;
		PaginationHolder<T> holder = new PaginationHolder<T>(pi.getPageNo(), pi.getPageSize(), getTotalNumOfElements());
		List<T> pageList = getPageList(holder.getThisPageFirstElementNum(), holder.getThisPageLastElementNum());
		holder.setPageList(pageList);
		return holder;
	}

}

package com.rhyton.common.pagination.domain;

import java.io.Serializable;

/**
 * 进行分页查询时所需的基本分页条件
 * @author zhx
 * @version
 * @see
 * @since 2008-1-11 上午11:34:04
 */
public class PaginationInfo implements Serializable {

	private static final long serialVersionUID = -8717204048691826019L;

	public PaginationInfo() {
	}

	private int pageSize;// 页面显示的记录数

	private int pageNo;// 第几页

	private PaginationSortDefinition paginationSortDefinition; // 排序信息的描述.

	public int getPageNo() {
		return pageNo;
	}

	public int getPageSize() {
		return pageSize;
	}

	/**
	 * 设置当前的页数
	 * @param pageNumber
	 */
	public void setPageNo(int pageNo) {
		this.pageNo = pageNo;
	}

	/**
	 * 设置每页要显示的记录数
	 * @param pageSize
	 */
	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	@Override
	public String toString() {
		return this.getClass().getName() + "[pageNo:" + pageNo + " ;pageSize:" + pageSize + " ;]";
	}

	/**
	 * 尚未用到!!!!!!!!!!!!!!
	 * @return
	 */
	public PaginationSortDefinition getSortDefinition() {
		return paginationSortDefinition;
	}

	public void setSortDefinition(PaginationSortDefinition paginationSortDefinition) {
		this.paginationSortDefinition = paginationSortDefinition;
	}

}

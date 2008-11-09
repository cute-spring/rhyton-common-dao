package com.rhyton.common.pagination.domain;

import java.io.Serializable;
import java.util.List;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * 保存分页信息及分页列表对像
 * @author zhx
 * @param <T> a type variable (a POJO in the domain package)
 */
public class PaginationHolder<T> implements Serializable {

	private static final long serialVersionUID = 1L;

	private static Log log = LogFactory.getLog(PaginationHolder.class);

	private static final int DEFAULT_PAGE_SIZE = 15;

	private int endRowNum;// 本次查询所指定的截止行号

	private int lastPageNo;// 最后一页的页码数

	private int nextPageNo;// 下一页页码数

	/** ******************设置查询的结果************************* */
	private List<T> pageList;

	/** *******************初始化时不可缺少的属性************************* */

	private int pageNo;// 第几页

	private int pageSize;// 页面显示的记录数

	/** *********************计算得出的数据************************** */

	private int previousPageNo;// 前一页页码数

	private int startRowNum;// 本次查询所指定的起始行号

	private int totalNumOfElements; // 总记录数

	/**
	 * @param pageNo 要显示的页码号
	 * @param pageSize 要显示在页面中的记录条数
	 * @param totalNumOfElements 符合条件的总记录条数
	 */
	public PaginationHolder(int pageNo, int pageSize, int totalNumOfElements) {
		this.pageNo = pageNo;
		this.pageSize = pageSize;
		this.totalNumOfElements = totalNumOfElements;
		initPaginationParm();
	}

	/**
	 * 应该移到UI中去
	 * @param spePageNO
	 * @param speName
	 * @return
	 */
	public String getChangePageScript(int spePageNO, String speName) {
		if (spePageNO == this.getPageNo()) {
			return speName;
		} else {
			StringBuilder script = new StringBuilder("<a href=\"javascript:changePage('");
			script.append(spePageNO);
			script.append("');\" class=\"tableFooter\">").append(speName).append("</a>");
			return script.toString();
		}
	}

	public Log getLogger() {
		return log;
	}

	/**
	 * 最后一页的页码数
	 */
	public int getLastPageNo() {
		return lastPageNo;
	}

	/**
	 * 下一页页码数
	 */
	public int getNextPageNo() {
		return nextPageNo;
	}

	/**
	 * 当前是第几页
	 */
	public int getPageNo() {
		return pageNo;
	}

	/**
	 * 页面显示的记录数
	 */
	public int getPageSize() {
		return pageSize;
	}

	/**
	 * 前一页页码数
	 */
	public int getPreviousPageNo() {
		return previousPageNo;
	}

	/**
	 * 本次查询所指定的起始行号
	 */
	public int getThisPageFirstElementNum() {
		return startRowNum;
	}

	/**
	 * 本次查询所指定的截止行号
	 */
	public int getThisPageLastElementNum() {
		return endRowNum;
	}

	/**
	 * 符合条件的总记录数
	 */
	public int getTotalNumOfElements() {
		return totalNumOfElements;
	}

	/**
	 * 是否有前一页
	 */
	public boolean hasPreviousPage() {
		return getPageNo() > 0;
	}

	/**
	 * 是否有下一页
	 */
	public boolean hasNextPage() {
		return getLastPageNo() > getPageNo();
	}

	/**
	 * 是否是第一页
	 */
	public boolean isFirstPage() {
		return getPageNo() == 0;
	}

	/**
	 * 是否是最后一页
	 */
	public boolean isLastPage() {
		return getPageNo() >= getLastPageNo();
	}

	/**
	 * Return a list representing the current page.
	 * @return a list representing the current page.
	 */
	public List<T> getPageList() {
		return pageList;
	}

	/**
	 * set a list representing the current page.
	 * @param thisPageElements
	 */
	public void setPageList(List<T> pageList) {
		this.pageList = pageList;
	}

	@Override
	public String toString() {
		return ReflectionToStringBuilder.reflectionToString(this);
	}

	private void initPaginationParm() {
		// pageSize,totalNumOfElements,pageNo
		pageSize = pageSize <= 0 ? DEFAULT_PAGE_SIZE : pageSize;

		lastPageNo = pageSize > totalNumOfElements ? 1 : (totalNumOfElements + pageSize - 1) / pageSize;

		pageNo = pageNo < 1 ? 1 : pageNo;

		pageNo = pageNo > lastPageNo ? lastPageNo : pageNo;

		startRowNum = pageSize * (pageNo - 1) + 1;

		endRowNum = pageSize * pageNo;

		endRowNum = endRowNum > totalNumOfElements ? totalNumOfElements : endRowNum;

		previousPageNo = pageNo > 2 ? pageNo - 1 : 1;

		nextPageNo = pageNo < lastPageNo ? pageNo + 1 : lastPageNo;
	}

}

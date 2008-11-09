package com.rhyton.common.pagination.jdbc;

 

import java.util.Arrays;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;

import com.rhyton.common.pagination.PaginationHolderFactory;

/**
 * 建议当查询参数固定只是参数值变化时的SQL,用传入参数的方式进行查询(以PreparedStatement实现)
 * 建议当查询条件不固定时,SQL为动态组装的时,用直接传入SQL的形式进行查询,以加快速度. (以Statement实现,省去了SQL预处理的开销)
 * 参考见<<Java Programming with Oracle JDBC>>--->>Chapter 19
 * Performance--->>Statement Versus PreparedStatement http://www.oreilly.com/catalog/jorajdbc/chapter/ch19.html
 * @author zhx
 * @param <T>
 */
public class OracleSqlPaginationFactory<T> extends PaginationHolderFactory<T> {
	private Log log = LogFactory.getLog(OracleSqlPaginationFactory.class);

	private final SimpleJdbcTemplate simpleJdbcTemplate;

	private final String querySql;

	private ParameterizedRowMapper<T> rowMapper;

	private Object[] parmValues;

	public OracleSqlPaginationFactory(JdbcTemplate jt, String querySql, ParameterizedRowMapper<T> rm, Object... args) {
		this.simpleJdbcTemplate = new SimpleJdbcTemplate(jt);
		this.querySql = querySql;
		this.rowMapper = rm;
		this.parmValues = args;
	}

	public List<T> getPageList(final int firstNum, final int lastNum) {
		if (log.isInfoEnabled()) {
			log.info("firstNum = " + firstNum + "; lastNum =  " + lastNum);
			log.info("paginationSQL = " + getPaginationSql());
		}
		return getPageList(getPaginationSql(), firstNum, lastNum);
	}

	/* (non-Javadoc)
	 * @see org.appfuse.pagination.PaginationHolderFactory#getTotalNumOfElements()
	 */
	public int getTotalNumOfElements() {
		if (log.isInfoEnabled()) {
			log.info("TotalNumOfElements SQL= " + getCountSql(querySql));
		}
		return simpleJdbcTemplate.queryForInt(getCountSql(querySql), parmValues);
	}

	/**
	 * 待优化!!!!!!!!!!!!!!!!!!!!!
	 * @param originalSQl
	 * @return
	 */
	private String getCountSql(String originalSQl) {
		return " select count(*) from  (" + originalSQl + " )";
	}

	private List<T> getPageList(String paginationSql, int firstNum, final int lastNum) {
		int len = parmValues.length;
		Object[] allParmVal = new Object[len + 2];
		System.arraycopy(parmValues, 0, allParmVal, 0, len);
		allParmVal[len] = lastNum;
		allParmVal[len + 1] = firstNum;
		if (log.isInfoEnabled()) {
			log.info("paginationSQL SQL= " + paginationSql);
			log.info("parmValues = " + Arrays.toString(parmValues));
		}
		return simpleJdbcTemplate.query(paginationSql, rowMapper, allParmVal);
	}

	/**
	 * 待优化!!!!!!!!!!!!!!!!!!!!!
	 * @return
	 */
	private String getPaginationSql() {
		StringBuffer sql = new StringBuffer(" select * from (  select rownum rn , c.* from (  ").append(querySql)
				.append(" ) c  where rownum <= ? )  where rn >=  ?");
		return sql.toString();
	}
}

// Java Programming with Oracle JDBC: I would recommend you use PreparedStatements for all, except dynamic, SQL
// statements (See Tip #3).

// * Use PreparedStatements for all, except dynamic, SQL statements.
// * Use PreparedStatements for batching repetitive inserts or updates.
// * OraclePreparedStatement.setExecuteBatch() (proprietary method) is the fastest way to execute batch statements.

/*
 * 1.Databases analyze query statements to decide how to process them most optimally, then cache the resulting query
 * plan, keyed on the full statement. Reusing identical statements reuses the query plan; 2.Altering the statement
 * causes a new query plan to be generated for each new statement. However statements with parameters can have the query
 * plan reused, so use parameters rather than regenerating the statement with different values; 3.Using a new
 * connections requires a prepared statement to be recreated. Reusing connections allows a prepared statement to be
 * reused; 4.Connection pools should have associated PreparedStatement caches so that the PreparedStatements are
 * automatically reused.
 */

// Prepared SQL statements get compiled in the database only once, future invocations do not recompile them. The result
// of this is a decrease in the database load, and an increase in performance of up to 5x.
// With Statement, the same SQL statement with different parameters must be recompiled by the database each time. But
// PreparedStatements can be parametrized, and these do not need to be recompiled by the database for use with different
// parameters.
// PreparedStatement objects are compiled (prepared) by the JDBC driver or database for faster performance, and accept
// input parameters so they can be reused with different data.
// * It takes about 65 iterations of a prepared statement before its total time for execution catches up with a
// statement, because of prepared statement initialization overheads.
// * Use PreparedStatements to batch statements for optimal performance.
// Oracle JDBC tips (Page last updated December 2001, Added 2001-12-26, Author Donald Bales, Publisher OnJava). Tips:
// * From the client side, Statement is faster than PreparedStatement (except if you are batching statements) when using
// dynamic SQL.

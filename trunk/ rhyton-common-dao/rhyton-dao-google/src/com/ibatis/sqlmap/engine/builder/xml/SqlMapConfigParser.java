package com.ibatis.sqlmap.engine.builder.xml;

import java.io.InputStream;
import java.io.Reader;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.w3c.dom.Node;

import com.ibatis.common.resources.Resources;
import com.ibatis.common.xml.Nodelet;
import com.ibatis.common.xml.NodeletParser;
import com.ibatis.common.xml.NodeletUtils;
import com.ibatis.sqlmap.client.SqlMapClient;
import com.ibatis.sqlmap.client.SqlMapException;
import com.ibatis.sqlmap.engine.config.SqlMapConfiguration;
import com.ibatis.sqlmap.engine.datasource.DataSourceFactory;
import com.ibatis.sqlmap.engine.mapping.result.ResultObjectFactory;
import com.ibatis.sqlmap.engine.transaction.TransactionConfig;
import com.ibatis.sqlmap.engine.transaction.TransactionManager;

/**
 * for ibatis-2.3.4.726
 * @author alter by zhangxin
 *
 */
public class SqlMapConfigParser {

    protected final NodeletParser parser = new NodeletParser();
    private XmlParserState state = new XmlParserState();

    private boolean usingStreams = false;

    public SqlMapConfigParser() {
	parser.setValidation(true);
	parser.setEntityResolver(new SqlMapClasspathEntityResolver());

	addSqlMapConfigNodelets();
	addGlobalPropNodelets();
	addSettingsNodelets();
	addTypeAliasNodelets();
	addTypeHandlerNodelets();
	addTransactionManagerNodelets();
	addSqlMapNodelets();
	addResultObjectFactoryNodelets();

    }

    public SqlMapClient parse(Reader reader, Properties props) {
	if (props != null)
	    state.setGlobalProps(props);
	return parse(reader);
    }

    public SqlMapClient parse(Reader reader) {
	try {
	    usingStreams = false;

	    parser.parse(reader);
	    return state.getConfig().getClient();
	} catch (Exception e) {
	    throw new RuntimeException("Error occurred.  Cause: " + e, e);
	}
    }

    public SqlMapClient parse(InputStream inputStream, Properties props) {
	if (props != null)
	    state.setGlobalProps(props);
	return parse(inputStream);
    }

    public SqlMapClient parse(InputStream inputStream) {
	try {
	    usingStreams = true;

	    parser.parse(inputStream);
	    return state.getConfig().getClient();
	} catch (Exception e) {
	    throw new RuntimeException("Error occurred.  Cause: " + e, e);
	}
    }

    private void addSqlMapConfigNodelets() {
	parser.addNodelet("/sqlMapConfig/end()", new Nodelet() {
	    public void process(Node node) throws Exception {
		state.getConfig().finalizeSqlMapConfig();
	    }
	});
    }

    private void addGlobalPropNodelets() {
	parser.addNodelet("/sqlMapConfig/properties", new Nodelet() {
	    public void process(Node node) throws Exception {
		Properties attributes = NodeletUtils.parseAttributes(node, state.getGlobalProps());
		String resource = attributes.getProperty("resource");
		String url = attributes.getProperty("url");
		state.setGlobalProperties(resource, url);
	    }
	});
    }

    private void addSettingsNodelets() {
	parser.addNodelet("/sqlMapConfig/settings", new Nodelet() {
	    public void process(Node node) throws Exception {
		Properties attributes = NodeletUtils.parseAttributes(node, state.getGlobalProps());
		SqlMapConfiguration config = state.getConfig();

		String classInfoCacheEnabledAttr = attributes.getProperty("classInfoCacheEnabled");
		boolean classInfoCacheEnabled = (classInfoCacheEnabledAttr == null || "true".equals(classInfoCacheEnabledAttr));
		config.setClassInfoCacheEnabled(classInfoCacheEnabled);

		String lazyLoadingEnabledAttr = attributes.getProperty("lazyLoadingEnabled");
		boolean lazyLoadingEnabled = (lazyLoadingEnabledAttr == null || "true".equals(lazyLoadingEnabledAttr));
		config.setLazyLoadingEnabled(lazyLoadingEnabled);

		String statementCachingEnabledAttr = attributes.getProperty("statementCachingEnabled");
		boolean statementCachingEnabled = (statementCachingEnabledAttr == null || "true".equals(statementCachingEnabledAttr));
		config.setStatementCachingEnabled(statementCachingEnabled);

		String cacheModelsEnabledAttr = attributes.getProperty("cacheModelsEnabled");
		boolean cacheModelsEnabled = (cacheModelsEnabledAttr == null || "true".equals(cacheModelsEnabledAttr));
		config.setCacheModelsEnabled(cacheModelsEnabled);

		String enhancementEnabledAttr = attributes.getProperty("enhancementEnabled");
		boolean enhancementEnabled = (enhancementEnabledAttr == null || "true".equals(enhancementEnabledAttr));
		config.setEnhancementEnabled(enhancementEnabled);

		String useColumnLabelAttr = attributes.getProperty("useColumnLabel");
		boolean useColumnLabel = (useColumnLabelAttr == null || "true".equals(useColumnLabelAttr));
		config.setUseColumnLabel(useColumnLabel);

		String forceMultipleResultSetSupportAttr = attributes.getProperty("forceMultipleResultSetSupport");
		boolean forceMultipleResultSetSupport = "true".equals(forceMultipleResultSetSupportAttr);
		config.setForceMultipleResultSetSupport(forceMultipleResultSetSupport);

		String defaultTimeoutAttr = attributes.getProperty("defaultStatementTimeout");
		Integer defaultTimeout = defaultTimeoutAttr == null ? null : Integer.valueOf(defaultTimeoutAttr);
		config.setDefaultStatementTimeout(defaultTimeout);

		String useStatementNamespacesAttr = attributes.getProperty("useStatementNamespaces");
		boolean useStatementNamespaces = "true".equals(useStatementNamespacesAttr);
		state.setUseStatementNamespaces(useStatementNamespaces);
	    }
	});
    }

    private void addTypeAliasNodelets() {
	parser.addNodelet("/sqlMapConfig/typeAlias", new Nodelet() {
	    public void process(Node node) throws Exception {
		Properties prop = NodeletUtils.parseAttributes(node, state.getGlobalProps());
		String alias = prop.getProperty("alias");
		String type = prop.getProperty("type");
		state.getConfig().getTypeHandlerFactory().putTypeAlias(alias, type);
	    }
	});
    }

    private void addTypeHandlerNodelets() {
	parser.addNodelet("/sqlMapConfig/typeHandler", new Nodelet() {
	    public void process(Node node) throws Exception {
		Properties prop = NodeletUtils.parseAttributes(node, state.getGlobalProps());
		String jdbcType = prop.getProperty("jdbcType");
		String javaType = prop.getProperty("javaType");
		String callback = prop.getProperty("callback");

		javaType = state.getConfig().getTypeHandlerFactory().resolveAlias(javaType);
		callback = state.getConfig().getTypeHandlerFactory().resolveAlias(callback);

		state.getConfig().newTypeHandler(Resources.classForName(javaType), jdbcType, Resources.instantiate(callback));
	    }
	});
    }

    private void addTransactionManagerNodelets() {
	parser.addNodelet("/sqlMapConfig/transactionManager/property", new Nodelet() {
	    public void process(Node node) throws Exception {
		Properties attributes = NodeletUtils.parseAttributes(node, state.getGlobalProps());
		String name = attributes.getProperty("name");
		String value = NodeletUtils.parsePropertyTokens(attributes.getProperty("value"), state.getGlobalProps());
		state.getTxProps().setProperty(name, value);
	    }
	});
	parser.addNodelet("/sqlMapConfig/transactionManager/end()", new Nodelet() {
	    public void process(Node node) throws Exception {
		Properties attributes = NodeletUtils.parseAttributes(node, state.getGlobalProps());
		String type = attributes.getProperty("type");
		boolean commitRequired = "true".equals(attributes.getProperty("commitRequired"));

		state.getConfig().getErrorContext().setActivity("configuring the transaction manager");
		type = state.getConfig().getTypeHandlerFactory().resolveAlias(type);
		TransactionManager txManager;
		try {
		    state.getConfig().getErrorContext().setMoreInfo("Check the transaction manager type or class.");
		    TransactionConfig config = (TransactionConfig) Resources.instantiate(type);
		    config.setDataSource(state.getDataSource());
		    state.getConfig().getErrorContext().setMoreInfo("Check the transactio nmanager properties or configuration.");
		    config.setProperties(state.getTxProps());
		    config.setForceCommit(commitRequired);
		    config.setDataSource(state.getDataSource());
		    state.getConfig().getErrorContext().setMoreInfo(null);
		    txManager = new TransactionManager(config);
		} catch (Exception e) {
		    if (e instanceof SqlMapException) {
			throw (SqlMapException) e;
		    } else {
			throw new SqlMapException(
				"Error initializing TransactionManager.  Could not instantiate TransactionConfig.  Cause: " + e, e);
		    }
		}
		state.getConfig().setTransactionManager(txManager);
	    }
	});
	parser.addNodelet("/sqlMapConfig/transactionManager/dataSource/property", new Nodelet() {
	    public void process(Node node) throws Exception {
		Properties attributes = NodeletUtils.parseAttributes(node, state.getGlobalProps());
		String name = attributes.getProperty("name");
		String value = NodeletUtils.parsePropertyTokens(attributes.getProperty("value"), state.getGlobalProps());
		state.getDsProps().setProperty(name, value);
	    }
	});
	parser.addNodelet("/sqlMapConfig/transactionManager/dataSource/end()", new Nodelet() {
	    public void process(Node node) throws Exception {
		state.getConfig().getErrorContext().setActivity("configuring the data source");

		Properties attributes = NodeletUtils.parseAttributes(node, state.getGlobalProps());

		String type = attributes.getProperty("type");
		Properties props = state.getDsProps();

		type = state.getConfig().getTypeHandlerFactory().resolveAlias(type);
		try {
		    state.getConfig().getErrorContext().setMoreInfo("Check the data source type or class.");
		    DataSourceFactory dsFactory = (DataSourceFactory) Resources.instantiate(type);
		    state.getConfig().getErrorContext().setMoreInfo("Check the data source properties or configuration.");
		    dsFactory.initialize(props);
		    state.setDataSource(dsFactory.getDataSource());
		    state.getConfig().getErrorContext().setMoreInfo(null);
		} catch (Exception e) {
		    if (e instanceof SqlMapException) {
			throw (SqlMapException) e;
		    } else {
			throw new SqlMapException("Error initializing DataSource.  Could not instantiate DataSourceFactory.  Cause: " + e,
				e);
		    }
		}
	    }
	});
    }

    //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~开始~~~~~~~~~~~~修改iBatis对定义sqlMap的xml的解析方法使得支持*(wildcard)~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    private ResourcePatternResolver resourcePatternResolver = new PathMatchingResourcePatternResolver();

    private void dealSqlMapParserByInputStream(String resource) throws Exception {

	if (resource != null) {//resource
	    InputStream inputStream = null;
	    String _resource = null;
	    Resource[] resources = resourcePatternResolver.getResources(resource);
	    for (int i = 0; i < resources.length; i++) {
		_resource = StringUtils.substringAfterLast(resources[i].getFile().getAbsolutePath(), "classes");
		state.getConfig().getErrorContext().setResource(_resource);//resource
		inputStream = Resources.getResourceAsStream(_resource);//resource
		new SqlMapParser(state).parse(inputStream);
	    }
	} else {
	    throw new SqlMapException("The <sqlMap> element requires   a resource   attribute.");
	}

    }

    private void dealSqlMapParserByReader(String resource) throws Exception {
	if (resource != null) {//resource
	    Reader reader = null;
	    String _resource = null;
	    Resource[] resources = resourcePatternResolver.getResources(resource);
	    for (int i = 0; i < resources.length; i++) {
		_resource = StringUtils.substringAfterLast(resources[i].getFile().getAbsolutePath(), "classes");
		state.getConfig().getErrorContext().setResource(_resource);//resource
		reader = Resources.getResourceAsReader(_resource);//resource
		new SqlMapParser(state).parse(reader);
	    }
	} else {
	    throw new SqlMapException("The <sqlMap> element requires   a resource   attribute.");
	}
    }

    protected void addSqlMapNodelets() {
	parser.addNodelet("/sqlMapConfig/sqlMap", new Nodelet() {
	    public void process(Node node) throws Exception {
		state.getConfig().getErrorContext().setActivity("loading the SQL Map resource");

		Properties attributes = NodeletUtils.parseAttributes(node, state.getGlobalProps());

		String resource = attributes.getProperty("resource");

		if (usingStreams) {
		    dealSqlMapParserByInputStream(resource);
		} else {
		    dealSqlMapParserByReader(resource);
		}
	    }
	});
    }

    private void addResultObjectFactoryNodelets() {
	parser.addNodelet("/sqlMapConfig/resultObjectFactory", new Nodelet() {
	    public void process(Node node) throws Exception {
		Properties attributes = NodeletUtils.parseAttributes(node, state.getGlobalProps());
		String type = attributes.getProperty("type");

		state.getConfig().getErrorContext().setActivity("configuring the Result Object Factory");
		ResultObjectFactory rof;
		try {
		    rof = (ResultObjectFactory) Resources.instantiate(type);
		    state.getConfig().setResultObjectFactory(rof);
		} catch (Exception e) {
		    throw new SqlMapException("Error instantiating resultObjectFactory: " + type, e);
		}

	    }
	});
	parser.addNodelet("/sqlMapConfig/resultObjectFactory/property", new Nodelet() {
	    public void process(Node node) throws Exception {
		Properties attributes = NodeletUtils.parseAttributes(node, state.getGlobalProps());
		String name = attributes.getProperty("name");
		String value = NodeletUtils.parsePropertyTokens(attributes.getProperty("value"), state.getGlobalProps());
		state.getConfig().getDelegate().getResultObjectFactory().setProperty(name, value);
	    }
	});
    }

}

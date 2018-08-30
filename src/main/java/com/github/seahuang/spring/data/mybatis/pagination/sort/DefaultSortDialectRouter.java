package com.github.seahuang.spring.data.mybatis.pagination.sort;

import java.sql.Connection;
import java.util.Properties;

import javax.sql.DataSource;

import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.Invocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultSortDialectRouter implements SortDialectRouter {
	private static Logger logger = LoggerFactory.getLogger(DefaultSortDialectRouter.class);
	private ANSISortDialect standardDialect = new ANSISortDialect();
	private CaseSortDialect caseSortDialect = new CaseSortDialect();
	private Db2SortDialect db2SortDialect = new Db2SortDialect();
	
	@Override
	public SortDialect routeSortDialect(Properties properties, Invocation invocation) {
		String dialect = properties.getProperty("dialect");
		if(dialect != null){
			return routeByKeyWord(dialect);
		}
		Connection connection = null;
		try {
			MappedStatement mappedStatement = (MappedStatement)invocation.getArgs()[0];
			DataSource dataSource = mappedStatement.getConfiguration().getEnvironment().getDataSource();
			connection = dataSource.getConnection();
			String url = connection.getMetaData().getURL();
			return routeByKeyWord(url);
		} catch(Exception e) {
			logger.warn("Fail to route SortDialect, use ANSISortDialect", e);
			return standardDialect;
		} finally{
			if(connection != null){
				try{
					connection.close();
				}catch(Exception e){
					logger.warn("Fail to close connection", e);
				}
			}
		}
	}
	
	public SortDialect routeByKeyWord(String keyWord){
		keyWord = keyWord.trim().toLowerCase();
		if(keyWord.contains("db2")){
			return db2SortDialect;
		}
		if(keyWord.contains("mysql") || keyWord.contains("sqlserver")){
			return caseSortDialect;
		}
		return standardDialect;
	}

}

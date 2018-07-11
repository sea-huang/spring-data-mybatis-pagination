package com.github.seahuang.spring.data.mybatis.pagination.sort;

import java.util.Properties;

import org.apache.ibatis.plugin.Invocation;

/**
 * Route the SortDialect by properties or auto detected by Invocation information
 * @author 黄海
 * @since 1.0
 */
public interface SortDialectRouter {
	SortDialect routeSortDialect(Properties properties, Invocation invocation);
}

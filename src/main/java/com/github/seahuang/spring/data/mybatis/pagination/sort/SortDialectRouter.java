package com.github.seahuang.spring.data.mybatis.pagination.sort;

import java.util.Properties;

import org.apache.ibatis.plugin.Invocation;

public interface SortDialectRouter {
	SortDialect routeSortDialect(Properties properties, Invocation invocation);
}

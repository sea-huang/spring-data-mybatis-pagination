package com.github.seahuang.spring.data.mybatis.pagination.sort;

import org.springframework.data.domain.Sort;

/**
 * Generate order by sql by Sort object. adapters for databases
 * @author 黄海
 * @since 1.0
 */
public interface SortDialect {
	String renderSort(Sort sort);
}

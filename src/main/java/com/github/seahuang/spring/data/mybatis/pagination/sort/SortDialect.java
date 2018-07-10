package com.github.seahuang.spring.data.mybatis.pagination.sort;

import org.springframework.data.domain.Sort;

public interface SortDialect {
	String renderSort(Sort sort);
}

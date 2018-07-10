package com.github.seahuang.spring.data.mybatis.pagination.sort;

import java.util.Iterator;

import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;

public abstract class AbstractSortDialect implements SortDialect {
	@Override
	public String renderSort(Sort sort) {
		StringBuilder result = new StringBuilder();
		Iterator<Order> orders = sort.iterator();
		while(orders.hasNext()){
			result.append(", ").append(renderOrder(orders.next()));
		}
		return result.substring(2).toString();
	}

	public abstract String renderOrder(Order order);
}

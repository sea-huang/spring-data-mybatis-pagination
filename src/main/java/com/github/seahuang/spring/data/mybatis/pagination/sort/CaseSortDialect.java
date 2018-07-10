package com.github.seahuang.spring.data.mybatis.pagination.sort;

import org.springframework.data.domain.Sort.NullHandling;
import org.springframework.data.domain.Sort.Order;

/**
 * Workaround for Mysql/SqlServer
 * @author 黄海
 * @version V1.0
 */
public class CaseSortDialect extends AbstractSortDialect {
	private ANSISortDialect standardDialect = new ANSISortDialect();
	
	@Override
	public String renderOrder(Order order) {
		StringBuilder result = new StringBuilder();
		if (order.getNullHandling() == null
			|| order.getNullHandling() == NullHandling.NATIVE) {
			return standardDialect.renderOrder(order);
		}
		
		// Workaround for NULLS FIRST / LAST support.
		result.append("case when ").append(order.getProperty()).append(" is null then ");
		if (order.getNullHandling() == NullHandling.NULLS_FIRST) {
			result.append("0 else 1");
		} else {
			result.append("1 else 0");
		}
		result.append(" end, ");
		// Nulls precedence has already been handled so passing Native value.
		result.append(standardDialect.renderOrder(order.nullsNative()));
		return result.toString();
	}

}

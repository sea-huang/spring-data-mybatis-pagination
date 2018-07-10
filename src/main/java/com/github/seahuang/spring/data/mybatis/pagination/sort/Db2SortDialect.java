package com.github.seahuang.spring.data.mybatis.pagination.sort;

import org.springframework.data.domain.Sort.NullHandling;
import org.springframework.data.domain.Sort.Order;

public class Db2SortDialect extends AbstractSortDialect {
	private ANSISortDialect standardDialect = new ANSISortDialect();
	private CaseSortDialect caseSortDialect = new CaseSortDialect();
	
	@Override
	public String renderOrder(Order order) {
		StringBuilder result = new StringBuilder();
		if (order.getNullHandling() == null
			|| order.getNullHandling() == NullHandling.NATIVE) {
			return standardDialect.renderOrder(order);
		}
		
		// DB2 FTW!  A null precedence was explicitly requested, but DB2 "support" for null precedence
		// is a joke.  Basically it supports combos that align with what it does anyway.  Here is the
		// support matrix:
		//		* ASC + NULLS FIRST -> case statement
		//		* ASC + NULLS LAST -> just drop the NULLS LAST from sql fragment
		//		* DESC + NULLS FIRST -> just drop the NULLS FIRST from sql fragment
		//		* DESC + NULLS LAST -> case statement
		if ((order.getNullHandling() == NullHandling.NULLS_FIRST  && order.isDescending())
				|| (order.getNullHandling() == NullHandling.NULLS_LAST && order.isAscending())) {
			// we have one of:
			//		* ASC + NULLS LAST
			//		* DESC + NULLS FIRST
			// so just drop the null precedence.  *NOTE: we could pass along the null precedence here,
			// but only DB2 9.7 or greater understand it; dropping it is more portable across DB2 versions
			return standardDialect.renderOrder(order.nullsNative());
		}
		
		result.append(caseSortDialect.renderOrder(order));
		return result.toString();
	}
}

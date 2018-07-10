package com.github.seahuang.spring.data.mybatis.pagination.sort;

import org.springframework.data.domain.Sort.NullHandling;
import org.springframework.data.domain.Sort.Order;

/**
 * Standard Sort Dialect
 * @author 黄海
 * @version V1.0
 */
public class ANSISortDialect extends AbstractSortDialect {
	
	@Override
	public String renderOrder(Order order) {
		StringBuilder result = new StringBuilder();
		if(order.isIgnoreCase()){
			result.append("lower(").append(order.getProperty()).append(")");
		}else{
			result.append(order.getProperty());
		}
		result.append(" ").append(order.getDirection().name());
		
		if(order.getNullHandling() == null 
			|| order.getNullHandling() == NullHandling.NATIVE){
			return result.toString();
		}
		
		if (order.getNullHandling() == NullHandling.NULLS_FIRST) {
			result.append(" nulls first");
		}else if (order.getNullHandling() == NullHandling.NULLS_LAST) {
			result.append(" nulls last");
		}
		return result.toString();
	}
}

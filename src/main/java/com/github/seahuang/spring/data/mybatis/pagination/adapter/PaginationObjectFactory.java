package com.github.seahuang.spring.data.mybatis.pagination.adapter;

import java.util.ArrayList;

import org.apache.ibatis.reflection.factory.DefaultObjectFactory;
import org.springframework.data.domain.Page;

public class PaginationObjectFactory extends DefaultObjectFactory {
	private static final long serialVersionUID = -9163879198754089750L;
	
	@Override
	protected Class<?> resolveInterface(Class<?> type){
		if(Page.class.isAssignableFrom(type)){
			return ArrayList.class;
		}
		return super.resolveInterface(type);
	}

	@Override
	public <T> boolean isCollection(Class<T> type) {
		if(Page.class.isAssignableFrom(type)){
			return true;
		}
		return super.isCollection(type);
	}
}

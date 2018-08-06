package com.github.seahuang.spring.data.mybatis.pagination.adapter;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Plugin;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInterceptor;
import com.github.seahuang.spring.data.mybatis.pagination.SpringDataPageHelper;
import com.github.seahuang.spring.data.mybatis.pagination.sort.DefaultSortDialectRouter;
import com.github.seahuang.spring.data.mybatis.pagination.sort.SortDialectRouter;
import com.github.seahuang.spring.data.mybatis.pagination.util.PropertyConventions;

@Intercepts(@Signature(type = Executor.class, method = "query", args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class}))
public class PaginationPlugin implements Interceptor {
	private Properties properties;
	private PageInterceptor pageInterceptor = new PageInterceptor();
	private SortDialectRouter sortDialectRouter = new DefaultSortDialectRouter();
	private Boolean sortColumnCamelcaseToUnderscore = false;
	private Boolean disablePageHelper = false;
	
	public PaginationPlugin(){
		this.setProperties(new Properties());
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public Object intercept(Invocation invocation) throws Throwable {
		Pageable pageable = findPageable(invocation);
		if (pageable == null) {
			if (disablePageHelper) {
				return invocation.proceed();
			}
			return pageInterceptor.intercept(invocation);
		}
			
		try{
			boolean count = isResultTypePage(invocation);
			//Pageable pageNumber start from 0, but pageHelper start from 1
			PageHelper.startPage(pageable.getPageNumber() + 1, pageable.getPageSize(), count);
			if(pageable.getSort() != null){
				String orderBy = sortDialectRouter
					.routeSortDialect(properties, invocation)
					.renderSort(camelcaseToUnderscore(pageable.getSort()));
				PageHelper.orderBy(orderBy);
			}
			
			com.github.pagehelper.Page pageHelperPage = (com.github.pagehelper.Page)pageInterceptor.intercept(invocation);
			
			if(count){
				return new ListPageImpl(pageHelperPage.getResult(), pageable, pageHelperPage.getTotal());
			}else{
				return pageHelperPage.getResult();
			}
		}finally{
			SpringDataPageHelper.clear();
			PageHelper.clearPage();
		}
	}
	
	protected Pageable findPageable(Invocation invocation){
		if(SpringDataPageHelper.getPageable() != null){
			return  SpringDataPageHelper.getPageable();
		}
		Object params = invocation.getArgs()[1];
		if(params == null){
			return null;
		}
		if(params instanceof Pageable){
			return (Pageable)params;
		}
		if(params instanceof Map){
			for(Object each : ((Map<Object, Object>)params).values()){
				if(each instanceof Pageable){
					return (Pageable)each;
				}
			}
		}
		return null;
	}
	
	protected boolean isResultTypePage(Invocation invocation){
		try{
			if(SpringDataPageHelper.getCount()){
				return true;
			}
			String id = ((MappedStatement)invocation.getArgs()[0]).getId();
			Class<?> mapper =  Class.forName(id.substring(0, id.lastIndexOf(".")));
			String methodName = id.substring(id.lastIndexOf(".") + 1);
			for(Method each : mapper.getDeclaredMethods()){
				if(each.getName().equals(methodName)){
					if(Page.class.isAssignableFrom(each.getReturnType())){
						return true;
					}else{
						return false;
					}
				}
			}
			throw new RuntimeException("no method found: " + id);
		}catch(Exception e){
			throw new RuntimeException(e);
		}
		
	}
	
	protected Sort camelcaseToUnderscore(Sort source){
		if(!sortColumnCamelcaseToUnderscore){
			return source;
		}
		List<Order> orders = new ArrayList<Order>();
		Iterator<Order> iterator = source.iterator();
		while(iterator.hasNext()){
			Order order = iterator.next();
			orders.add(order.withProperty(PropertyConventions.lowerCamelcaseToUpperUnderscore(order.getProperty())));
		}
		return new Sort(orders);
	}

	@Override
	public Object plugin(Object target) {
        if (target instanceof Executor) {
            return Plugin.wrap(target, this);
        } else {
            return target;
        }
	}

	@Override
	public void setProperties(Properties properties) {
		this.properties = properties;
		this.sortColumnCamelcaseToUnderscore = Boolean.valueOf(properties.getProperty(
			"sortColumnCamelcaseToUnderscore", sortColumnCamelcaseToUnderscore.toString()));
		this.disablePageHelper = Boolean.valueOf(properties.getProperty(
				"disablePageHelper", disablePageHelper.toString()));
		pageInterceptor.setProperties(properties);
	}

	public void setPageInterceptor(PageInterceptor pageInterceptor) {
		this.pageInterceptor = pageInterceptor;
	}

	public void setSortDialectRouter(SortDialectRouter sortDialectRouter) {
		this.sortDialectRouter = sortDialectRouter;
	}

	public void setSortColumnCamelcaseToUnderscore(boolean sortColumnCamelcaseToUnderscore) {
		this.sortColumnCamelcaseToUnderscore = sortColumnCamelcaseToUnderscore;
	}

	public boolean isDisablePageHelper() {
		return disablePageHelper;
	}

	public void setDisablePageHelper(boolean disablePageHelper) {
		this.disablePageHelper = disablePageHelper;
	}
}

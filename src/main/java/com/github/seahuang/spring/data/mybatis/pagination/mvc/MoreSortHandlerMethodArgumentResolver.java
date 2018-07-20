package com.github.seahuang.spring.data.mybatis.pagination.mvc;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.MethodParameter;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.domain.Sort.NullHandling;
import org.springframework.data.domain.Sort.Order;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.SortHandlerMethodArgumentResolver;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import com.github.seahuang.spring.data.mybatis.pagination.mvc.MoreSortDefault.MoreSortDefaults;

/**
 * Spring-data-web don't support full feature of Pageable Sort for "ignore case" and "null handling".
 * This Resolve offer more syntax to do that: 
 * For example: http://host:port/context/resource?sort=p1,p2,p3,DESC|IGNORECASE|NULLS_LAST
 * {@link HandlerMethodArgumentResolver} to support to create {@link Sort} instances from request parameters or
 * {@link MoreSortDefault} annotations.<br>
 * Doc: {@link <a href="https://github.com/sea-huang/spring-data-mybatis-pagination">https://github.com/sea-huang/spring-data-mybatis-pagination</a>}
 * @author 黄海
 * @since 1.0
 */
public class MoreSortHandlerMethodArgumentResolver extends SortHandlerMethodArgumentResolver {
	private static Logger logger = LoggerFactory.getLogger(MoreSortHandlerMethodArgumentResolver.class);
	protected String propertyDelimiter = ",";
	protected String enhancedDelimiter = "|";
	protected String ignoreCaseFlag = "IGNORECASE";
	
	@Override
	public Sort resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
			final NativeWebRequest webRequest, WebDataBinderFactory binderFactory) {
		Map<String, OrderFeature> orderFeatureByProperty = parseParamFeatures(parameter, webRequest);
		
		Sort result = super.resolveArgument(parameter, mavContainer
			, new EnhancedSortWebRequestDelegate(webRequest), binderFactory);
		
		Map<String, Order> orders = applyParamFeatures(orderFeatureByProperty, result);
		
		processDefault(parameter, orders);
		return new Sort(orders.values().toArray(new Order[orders.size()]));
	}

	protected Map<String, Order> applyParamFeatures(Map<String, OrderFeature> orderFeatureByProperty, Sort result) {
		Map<String, Order> orders = new HashMap<String, Order>();
		if(result != null){
			for(Order order : result){
				OrderFeature feature = orderFeatureByProperty.get(order.getProperty());
				if(feature != null){
					order = feature.apply(order);
				}
				orders.put(order.getProperty(), order);
			}
		}
		return orders;
	}

	protected Map<String, OrderFeature> parseParamFeatures(MethodParameter parameter, final NativeWebRequest webRequest) {
		String[] originalDirectionParameter = webRequest.getParameterValues(getSortParameter(parameter));
		Map<String, OrderFeature> orderFeatureByProperty = new HashMap<String, OrderFeature>();
		if(originalDirectionParameter != null){
			for(String eachSort : originalDirectionParameter){
				if(!eachSort.contains(enhancedDelimiter)){
					continue;
				}
				String[] segments = eachSort.split(propertyDelimiter);
				OrderFeature feature = OrderFeature.parse(segments[segments.length-1].split(Pattern.quote(enhancedDelimiter)), ignoreCaseFlag);
				for(int i = 0; i < segments.length - 1; i++){
					orderFeatureByProperty.put(segments[i], feature);
				}
			}
		}
		return orderFeatureByProperty;
	}

	protected void processDefault(MethodParameter parameter, Map<String, Order> orders) {
		Direction defaultDirectioin = Sort.DEFAULT_DIRECTION;
		PageableDefault pageableDefault = parameter.getParameterAnnotation(PageableDefault.class);
		if(pageableDefault != null){
			defaultDirectioin = pageableDefault.direction();
		}
		MoreSortDefault moreDefault = parameter.getParameterAnnotation(MoreSortDefault.class);
		if(moreDefault != null){
			processDefault(orders, moreDefault, defaultDirectioin);
		}
		MoreSortDefaults moreDefaults = parameter.getParameterAnnotation(MoreSortDefaults.class);
		if(moreDefaults != null){
			for(MoreSortDefault each : moreDefaults.value()){
				processDefault(orders, each, defaultDirectioin);
			}
		}
	}

	protected void processDefault(Map<String, Order> orders, MoreSortDefault moreDefault, Direction defaultDirectioin) {
		String[] properties = moreDefault.value().length > 0 ? moreDefault.value() : moreDefault.sort();
		for(String each : properties){
			Order existing = orders.get(each);
			if(existing == null){
				existing = new Order(defaultDirectioin, each);
			}
			if(moreDefault.ignoreCase()){
				existing = existing.ignoreCase();
			}
			existing = existing.with(moreDefault.nullHandling());
			orders.put(each, existing);
		}
	}

	/* 
	 * (non-Javadoc)
	 * @see org.springframework.data.web.SortHandlerMethodArgumentResolver#setPropertyDelimiter(java.lang.String)
	 */
	@Override
	public void setPropertyDelimiter(String propertyDelimiter) {
		this.propertyDelimiter = propertyDelimiter;
		super.setPropertyDelimiter(propertyDelimiter);
	}
	
	protected static class OrderFeature {
		private boolean ignoreCase = false;
		private NullHandling nullHandling;
		
		public Order apply(Order order){
			Order result = order;
			if(ignoreCase){
				result = result.ignoreCase();
			}
			result.with(nullHandling);
			return result;
		}
		
		public static OrderFeature parse(String[] settings, String ignoreCaseFlag){
			OrderFeature result = new OrderFeature();
			for(String each : settings){
				each = each.trim();
				if(Direction.ASC.name().equalsIgnoreCase(each) ||
					Direction.DESC.name().equalsIgnoreCase(each)){
					continue;
				}
				if(ignoreCaseFlag.equalsIgnoreCase(each)){
					result.ignoreCase = true;
					continue;
				}
				try{
					result.nullHandling = NullHandling.valueOf(each.toUpperCase());
				}catch(Exception e){
					logger.warn("Invalid NullHandling: ", each.toUpperCase());
				}	
			}
			return result;
		}
		
		public boolean getIgnoreCase() {
			return ignoreCase;
		}
		public NullHandling getNullHandling() {
			return nullHandling;
		}
	}
	
	public class EnhancedSortWebRequestDelegate extends NativeWebRequestDelegate {
		public EnhancedSortWebRequestDelegate(NativeWebRequest deletegate) {
			super(deletegate);
		}
		
		@Override 
		public String[] getParameterValues(String paramName){
			String[] source = super.getParameterValues(paramName);
			if(source == null){
				return null;
			}
			String[] target = new String[source.length];
			for(int i = 0; i < source.length; i++){
				target[i] = strip(source[i]);
			}
			return target;
		}
		
		protected String strip(String source){
			if(!source.contains(enhancedDelimiter)){
				return source;
			}
			String upperLastSegment = source.substring(source.lastIndexOf(",")).toUpperCase();
			StringBuilder result = new StringBuilder(source.substring(0, source.length() - upperLastSegment.length()));
			if(upperLastSegment.contains(Direction.ASC.name())){
				result.append(",").append(Direction.ASC.name());
			}else if(upperLastSegment.contains(Direction.DESC.name())){
				result.append(",").append(Direction.DESC.name());
			}
			return result.toString();
		}
	}
	
}

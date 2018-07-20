package com.github.seahuang.spring.data.mybatis.pagination.bean;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.domain.Sort.Order;
import org.springframework.util.StringUtils;

/**
 * Bean style Sort implementation which is more compatible for RPC serialization.
 * Doc: {@link <a href="https://github.com/sea-huang/spring-data-mybatis-pagination">https://github.com/sea-huang/spring-data-mybatis-pagination</a>}
 * @author 黄海
 * @since 1.0
 */
public class SortBean extends Sort {
	private static final long serialVersionUID = -2364342741487794901L;
	private List<OrderBean> orders;
	
	public static SortBean from(Sort source){
		List<Order> orders =  new ArrayList<Order>();
		for(Order each : source){
			orders.add(each);
		}
		return new SortBean(orders);
	}
	
	protected SortBean(){
		super(new Order[]{null});
	}

	/**
	 * Creates a new {@link SortBean} instance using the given {@link Order}s.
	 * 
	 * @param orders must not be {@literal null}.
	 */
	public SortBean(Order... orders) {
		this(Arrays.asList(orders));
	}

	/**
	 * Creates a new {@link SortBean} instance.
	 * 
	 * @param orders must not be {@literal null} or contain {@literal null}.
	 */
	public SortBean(List<Order> orders) {
		this();
		if (null == orders || orders.isEmpty()) {
			throw new IllegalArgumentException("You have to provide at least one sort property to sort by!");
		}
		List<OrderBean> orderBeans = new ArrayList<OrderBean>();
		for(Order each : orders){
			orderBeans.add(OrderBean.from(each));
		}
		this.orders = orderBeans;
	}

	/**
	 * Creates a new {@link Sort} instance. Order defaults to {@value Direction#ASC}.
	 * 
	 * @param properties must not be {@literal null} or contain {@literal null} or empty strings
	 */
	public SortBean(String... properties) {
		this(DEFAULT_DIRECTION, properties);
	}

	/**
	 * Creates a new {@link Sort} instance.
	 * 
	 * @param direction defaults to {@linke Sort#DEFAULT_DIRECTION} (for {@literal null} cases, too)
	 * @param properties must not be {@literal null}, empty or contain {@literal null} or empty strings.
	 */
	public SortBean(Direction direction, String... properties) {
		this(direction, properties == null ? new ArrayList<String>() : Arrays.asList(properties));
	}

	/**
	 * Creates a new {@link SortBean} instance.
	 * 
	 * @param direction defaults to {@link Sort#DEFAULT_DIRECTION} (for {@literal null} cases, too)
	 * @param properties must not be {@literal null} or contain {@literal null} or empty strings.
	 */
	public SortBean(Direction direction, List<String> properties) {
		this();
		if (properties == null || properties.isEmpty()) {
			throw new IllegalArgumentException("You have to provide at least one property to sort by!");
		}

		this.orders = new ArrayList<OrderBean>(properties.size());

		for (String property : properties) {
			this.orders.add(new OrderBean(direction, property));
		}
	}

	/**
	 * Returns a new {@link Sort} consisting of the {@link Order}s of the current {@link Sort} combined with the given
	 * ones.
	 * 
	 * @param sort can be {@literal null}.
	 * @return
	 */
	public SortBean and(Sort sort) {

		if (sort == null) {
			return this;
		}

		ArrayList<Order> these = new ArrayList<Order>(this.orders);

		for (Order order : sort) {
			these.add(order);
		}

		return new SortBean(these);
	}

	/**
	 * Returns the order registered for the given property.
	 * 
	 * @param property
	 * @return
	 */
	public Order getOrderFor(String property) {

		for (Order order : this) {
			if (order.getProperty().equals(property)) {
				return order;
			}
		}

		return null;
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Iterable#iterator()
	 */
	@SuppressWarnings("unchecked")
	public Iterator<Order> iterator() {
		return ((List<Order>)(List<?>)this.orders).iterator();
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return StringUtils.collectionToCommaDelimitedString(orders);
	}

	public List<OrderBean> getOrders() {
		return orders;
	}

	public void setOrders(List<OrderBean> orders) {
		this.orders = orders;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((orders == null) ? 0 : orders.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		SortBean other = (SortBean) obj;
		if (orders == null) {
			if (other.orders != null)
				return false;
		} else if (!orders.equals(other.orders))
			return false;
		return true;
	}
	
	
}

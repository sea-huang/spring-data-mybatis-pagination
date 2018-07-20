package com.github.seahuang.spring.data.mybatis.pagination.bean;

import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.domain.Sort.NullHandling;
import org.springframework.data.domain.Sort.Order;
import org.springframework.util.StringUtils;

/**
 * Bean style Order implementation which is more compatible for RPC serialization.
 * Doc: {@link <a href="https://github.com/sea-huang/spring-data-mybatis-pagination">https://github.com/sea-huang/spring-data-mybatis-pagination</a>}
 * @author 黄海
 * @since 1.0
 */
public class OrderBean extends Sort.Order {
	private static final long serialVersionUID = -313397537699999994L;

	protected static final boolean DEFAULT_IGNORE_CASE = false;

	private Direction direction;
	private String property;
	private boolean ignoreCase;
	private NullHandling nullHandling;
	
	public static OrderBean from(Order source){
		return new OrderBean(source.getDirection(), source.getProperty(), source.isIgnoreCase(), source.getNullHandling());
	}
	
	protected OrderBean(){
		super(null, "fake");
	}
	
	/**
	 * Creates a new {@link Order} instance. if order is {@literal null} then order defaults to
	 * {@link Sort#DEFAULT_DIRECTION}
	 * 
	 * @param direction can be {@literal null}, will default to {@link Sort#DEFAULT_DIRECTION}
	 * @param property must not be {@literal null} or empty.
	 */
	public OrderBean(Direction direction, String property) {
		this(direction, property, DEFAULT_IGNORE_CASE, null);
	}

	/**
	 * Creates a new {@link Order} instance. if order is {@literal null} then order defaults to
	 * {@link Sort#DEFAULT_DIRECTION}
	 * 
	 * @param direction can be {@literal null}, will default to {@link Sort#DEFAULT_DIRECTION}
	 * @param property must not be {@literal null} or empty.
	 * @param nullHandling can be {@literal null}, will default to {@link NullHandling#NATIVE}.
	 */
	public OrderBean(Direction direction, String property, NullHandling nullHandlingHint) {
		this(direction, property, DEFAULT_IGNORE_CASE, nullHandlingHint);
	}

	/**
	 * Creates a new {@link Order} instance. Takes a single property. Direction defaults to
	 * {@link Sort#DEFAULT_DIRECTION}.
	 * 
	 * @param property must not be {@literal null} or empty.
	 */
	public OrderBean(String property) {
		this(Sort.DEFAULT_DIRECTION, property);
	}

	/**
	 * Creates a new {@link Order} instance. if order is {@literal null} then order defaults to
	 * {@link Sort#DEFAULT_DIRECTION}
	 * 
	 * @param direction can be {@literal null}, will default to {@link Sort#DEFAULT_DIRECTION}
	 * @param property must not be {@literal null} or empty.
	 * @param ignoreCase true if sorting should be case insensitive. false if sorting should be case sensitive.
	 * @param nullHandling can be {@literal null}, will default to {@link NullHandling#NATIVE}.
	 * @since 1.7
	 */
	private OrderBean(Direction direction, String property, boolean ignoreCase, NullHandling nullHandling) {
		this();
		if (!StringUtils.hasText(property)) {
			throw new IllegalArgumentException("Property must not null or empty!");
		}

		this.direction = direction == null ? Sort.DEFAULT_DIRECTION : direction;
		this.property = property;
		this.ignoreCase = ignoreCase;
		this.nullHandling = nullHandling == null ? NullHandling.NATIVE : nullHandling;
	}

	/**
	 * Returns the order the property shall be sorted for.
	 * 
	 * @return
	 */
	public Direction getDirection() {
		return direction;
	}

	/**
	 * Returns the property to order for.
	 * 
	 * @return
	 */
	public String getProperty() {
		return property;
	}

	/**
	 * Returns whether sorting for this property shall be ascending.
	 * 
	 * @return
	 */
	public boolean isAscending() {
		return this.direction.isAscending();
	}

	/**
	 * Returns whether sorting for this property shall be descending.
	 * 
	 * @return
	 * @since 1.13
	 */
	public boolean isDescending() {
		return this.direction.isDescending();
	}

	/**
	 * Returns whether or not the sort will be case sensitive.
	 * 
	 * @return
	 */
	public boolean isIgnoreCase() {
		return ignoreCase;
	}

	/**
	 * Returns a new {@link Order} with the given {@link Direction}.
	 * 
	 * @param direction
	 * @return
	 */
	public OrderBean with(Direction direction) {
		return new OrderBean(direction, this.property, this.ignoreCase, this.nullHandling);
	}

	/**
	 * Returns a new {@link Order}
	 * 
	 * @param property must not be {@literal null} or empty.
	 * @return
	 * @since 1.13
	 */
	public OrderBean withProperty(String property) {
		return new OrderBean(this.direction, property, this.ignoreCase, this.nullHandling);
	}

	/**
	 * Returns a new {@link Sort} instance for the given properties.
	 * 
	 * @param properties
	 * @return
	 */
	public SortBean withProperties(String... properties) {
		return new SortBean(this.direction, properties);
	}

	/**
	 * Returns a new {@link Order} with case insensitive sorting enabled.
	 * 
	 * @return
	 */
	public OrderBean ignoreCase() {
		return new OrderBean(direction, property, true, nullHandling);
	}

	/**
	 * Returns a {@link Order} with the given {@link NullHandling}.
	 * 
	 * @param nullHandling can be {@literal null}.
	 * @return
	 * @since 1.8
	 */
	public OrderBean with(NullHandling nullHandling) {
		return new OrderBean(direction, this.property, ignoreCase, nullHandling);
	}

	/**
	 * Returns a {@link Order} with {@link NullHandling#NULLS_FIRST} as null handling hint.
	 * 
	 * @return
	 * @since 1.8
	 */
	public OrderBean nullsFirst() {
		return with(NullHandling.NULLS_FIRST);
	}

	/**
	 * Returns a {@link Order} with {@link NullHandling#NULLS_LAST} as null handling hint.
	 * 
	 * @return
	 * @since 1.7
	 */
	public OrderBean nullsLast() {
		return with(NullHandling.NULLS_LAST);
	}

	/**
	 * Returns a {@link Order} with {@link NullHandling#NATIVE} as null handling hint.
	 * 
	 * @return
	 * @since 1.7
	 */
	public OrderBean nullsNative() {
		return with(NullHandling.NATIVE);
	}

	/**
	 * Returns the used {@link NullHandling} hint, which can but may not be respected by the used datastore.
	 * 
	 * @return
	 * @since 1.7
	 */
	public NullHandling getNullHandling() {
		return nullHandling;
	}

	public void setDirection(Direction direction) {
		this.direction = direction;
	}

	public void setProperty(String property) {
		this.property = property;
	}

	public void setIgnoreCase(boolean ignoreCase) {
		this.ignoreCase = ignoreCase;
	}

	public void setNullHandling(NullHandling nullHandling) {
		this.nullHandling = nullHandling;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((direction == null) ? 0 : direction.hashCode());
		result = prime * result + (ignoreCase ? 1231 : 1237);
		result = prime * result + ((nullHandling == null) ? 0 : nullHandling.hashCode());
		result = prime * result + ((property == null) ? 0 : property.hashCode());
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
		OrderBean other = (OrderBean) obj;
		if (direction != other.direction)
			return false;
		if (ignoreCase != other.ignoreCase)
			return false;
		if (nullHandling != other.nullHandling)
			return false;
		if (property == null) {
			if (other.property != null)
				return false;
		} else if (!property.equals(other.property))
			return false;
		return true;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("OrderBean [direction=").append(direction).append(", property=").append(property)
				.append(", ignoreCase=").append(ignoreCase).append(", nullHandling=").append(nullHandling).append("]");
		return builder.toString();
	}
	
}

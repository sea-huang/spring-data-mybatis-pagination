package com.github.seahuang.spring.data.mybatis.pagination.mvc;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.NullHandling;
import org.springframework.data.web.SortDefault;

/**
 * Spring-data-web don't support full feature of Pageable Sort for "ignore case" and "null handling".
 * This annotation makeup that.
 * Annotation to define more default {@link Sort} options to be used when injecting a {@link Sort} instance into a
 * controller handler method.
 * Doc: {@link <a href="https://github.com/sea-huang/spring-data-mybatis-pagination">https://github.com/sea-huang/spring-data-mybatis-pagination</a>}
 * 
 * @since 1.0
 * @author 黄海
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
public @interface MoreSortDefault {

	/**
	 * Alias for {@link #sort()} to make a declaration configuring fields only more concise.
	 * 
	 * @return
	 */
	String[] value() default {};

	/**
	 * The properties to sort by by default. If unset, no sorting will be applied at all.
	 * 
	 * @return
	 */
	String[] sort() default {};

	/**
	 * Whether ignore case for sorting property. Defaults to false.
	 * 
	 * @return whether ignore case for sorting property
	 */
	boolean ignoreCase() default false;
	
	/**
	 * NullHandling strategy. Default: Lets the data store decide what to do with nulls.
	 * @return NullHandling strategy. 
	 */
	NullHandling nullHandling() default NullHandling.NATIVE;

	/**
	 * Wrapper annotation to allow declaring multiple {@link MoreSortDefault} annotations on a method parameter.
	 * 
	 * @since 1.0
	 * @author 黄海
	 */
	@Documented
	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.PARAMETER)
	public @interface MoreSortDefaults {

		/**
		 * The individual {@link SortDefault} declarations to be sorted by.
		 * 
		 * @return
		 */
		MoreSortDefault[] value();
	}
}

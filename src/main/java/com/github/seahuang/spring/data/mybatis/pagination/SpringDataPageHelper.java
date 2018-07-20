package com.github.seahuang.spring.data.mybatis.pagination;

import org.springframework.data.domain.Pageable;

/**
 * A non-invasive way to paginate next mybatis call.
 * It is scored in ThreadLocal, will be cleared after calling.
 * Be careful in case unexpected call to be impacted.
 * Doc: {@link <a href="https://github.com/sea-huang/spring-data-mybatis-pagination">https://github.com/sea-huang/spring-data-mybatis-pagination</a>}
 * @author 黄海
 * @since 1.0
 */
public class SpringDataPageHelper {
	private static final ThreadLocal<Pageable> LOCAL_PAGEABLE = new ThreadLocal<Pageable>();
	private static final ThreadLocal<Boolean> LOCAL_COUNT = new ThreadLocal<Boolean>();
	
	/**
	 * Paginate next mybatis call with given pageable info
	 * @param pageable used to paginate the mybatis call
	 */
	public static void paginateNextCall(Pageable pageable){
		paginateNextCall(pageable, true);
	}
	
	/**
	 * 
	 * @param pageable used to paginate the mybatis call
	 * @param count whether to count the total records. If false, a normal List will be returned
	 */
	public static void paginateNextCall(Pageable pageable, boolean count){
		LOCAL_PAGEABLE.set(pageable);
		LOCAL_COUNT.set(count);
	}
	
	public static Pageable getPageable(){
		return LOCAL_PAGEABLE.get();
	}
	
	public static boolean getCount(){
		return LOCAL_COUNT.get() == null ? false : LOCAL_COUNT.get();
	}
	
	/**
	 * clear the pagination settings
	 */
	public static void clear(){
		LOCAL_PAGEABLE.remove();
		LOCAL_COUNT.remove();
	}
}

package com.github.seahuang.spring.data.mybatis.pagination.bean;

import java.io.Serializable;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
/**
 * Bean style Pageable implementation which is more compatible for RPC serialization.
 * Doc: {@link <a href="https://github.com/sea-huang/spring-data-mybatis-pagination">https://github.com/sea-huang/spring-data-mybatis-pagination</a>}
 * @author 黄海
 * @since 1.0
 */
public class PageableBean implements Pageable, Serializable {
	private static final long serialVersionUID = 6604702276378939342L;
	private int page;
	private int size;
	private SortBean sort;
	
	public static PageableBean from(Pageable source){
		return new PageableBean(source.getPageNumber(), source.getPageSize(), source.getSort());
	}
	
	protected PageableBean(){}
	/**
	 * Creates a new {@link PageRequest}. Pages are zero indexed, thus providing 0 for {@code page} will return the first
	 * page.
	 * 
	 * @param page zero-based page index.
	 * @param size the size of the page to be returned.
	 */
	public PageableBean(int page, int size) {
		this(page, size, null);
	}

	/**
	 * Creates a new {@link PageRequest} with sort parameters applied.
	 * 
	 * @param page zero-based page index.
	 * @param size the size of the page to be returned.
	 * @param direction the direction of the {@link Sort} to be specified, can be {@literal null}.
	 * @param properties the properties to sort by, must not be {@literal null} or empty.
	 */
	public PageableBean(int page, int size, Direction direction, String... properties) {
		this(page, size, new SortBean(direction, properties));
	}
	
	/**
	 * Creates a new {@link PageRequest} with sort parameters applied.
	 * 
	 * @param page zero-based page index.
	 * @param size the size of the page to be returned.
	 * @param sort can be {@literal null}.
	 */
	public PageableBean(int page, int size, Sort sort) {
		this.page = page;
		this.size = size;
		this.sort = SortBean.from(sort);
	}

	/* 
	 * (non-Javadoc)
	 * @see org.springframework.data.domain.Pageable#getPageSize()
	 */
	public int getPageSize() {
		return size;
	}

	/* 
	 * (non-Javadoc)
	 * @see org.springframework.data.domain.Pageable#getPageNumber()
	 */
	public int getPageNumber() {
		return page;
	}

	/* 
	 * (non-Javadoc)
	 * @see org.springframework.data.domain.Pageable#getOffset()
	 */
	public int getOffset() {
		return page * size;
	}

	/* 
	 * (non-Javadoc)
	 * @see org.springframework.data.domain.Pageable#hasPrevious()
	 */
	public boolean hasPrevious() {
		return page > 0;
	}

	/* 
	 * (non-Javadoc)
	 * @see org.springframework.data.domain.Pageable#previousOrFirst()
	 */
	public PageableBean previousOrFirst() {
		return hasPrevious() ? previous() : first();
	}
	
	/* 
	 * (non-Javadoc)
	 * @see org.springframework.data.domain.Pageable#next()
	 */
	public PageableBean next() {
		return new PageableBean(getPageNumber() + 1, getPageSize(), getSort());
	}

	/* 
	 * (non-Javadoc)
	 * @see org.springframework.data.domain.AbstractPageRequest#previous()
	 */
	public PageableBean previous() {
		return getPageNumber() == 0 ? this : new PageableBean(getPageNumber() - 1, getPageSize(), getSort());
	}

	/* 
	 * (non-Javadoc)
	 * @see org.springframework.data.domain.Pageable#first()
	 */
	public PageableBean first() {
		return new PageableBean(0, getPageSize(), getSort());
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.springframework.data.domain.Pageable#getSort()
	 */
	public SortBean getSort() {
		return sort;
	}
	public int getPage() {
		return page;
	}
	public void setPage(int page) {
		this.page = page;
	}
	public int getSize() {
		return size;
	}
	public void setSize(int size) {
		this.size = size;
	}
	public void setSort(SortBean sort) {
		this.sort = sort;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + page;
		result = prime * result + size;
		result = prime * result + ((sort == null) ? 0 : sort.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		PageableBean other = (PageableBean) obj;
		if (page != other.page)
			return false;
		if (size != other.size)
			return false;
		if (sort == null) {
			if (other.sort != null)
				return false;
		} else if (!sort.equals(other.sort))
			return false;
		return true;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("PageableBean [page=").append(page).append(", size=").append(size).append(", sort=").append(sort)
				.append("]");
		return builder.toString();
	}
	
}

package com.github.seahuang.spring.data.mybatis.pagination.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.springframework.beans.BeanUtils;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.util.Assert;

/**
 * Bean style Page implementation which is more compatible for RPC serialization.
 * Doc: {@link <a href="https://github.com/sea-huang/spring-data-mybatis-pagination">https://github.com/sea-huang/spring-data-mybatis-pagination</a>}
 * @author 黄海
 * @since 1.0
 */
public class PageBean<T> implements Page<T>, Serializable {
	private static final long serialVersionUID = -4969861122766495395L;
	private List<T> content = new ArrayList<T>();
	private PageableBean pageable;
	private long total;
	
	protected PageBean(){}
	
	public static <T> PageBean<T> from(Page<T> source, Pageable pageable){
		return new PageBean<T>(source.getContent(), pageable, source.getTotalElements());
	}
	/**
	 * Constructor of {@code PageImpl}.
	 * 
	 * @param content the content of this page, must not be {@literal null}.
	 * @param pageable the paging information, can be {@literal null}.
	 * @param total the total amount of items available. The total might be adapted considering the length of the content
	 *          given, if it is going to be the content of the last page. This is in place to mitigate inconsistencies
	 */
	public PageBean(List<T> content, Pageable pageable, long total) {

		Assert.notNull(content, "Content must not be null!");
		this.content.addAll(content);
		this.pageable = PageableBean.from(pageable);
		this.total = !content.isEmpty() && pageable != null && pageable.getOffset() + pageable.getPageSize() > total
				? pageable.getOffset() + content.size() : total;
	}

	/**
	 * Creates a new {@link PageImpl} with the given content. This will result in the created {@link Page} being identical
	 * to the entire {@link List}.
	 * 
	 * @param content must not be {@literal null}.
	 */
	public PageBean(List<T> content) {
		this(content, null, null == content ? 0 : content.size());
	}

	/*
	 * (non-Javadoc)
	 * @see org.springframework.data.domain.Slice#getNumber()
	 */
	public int getNumber() {
		return pageable == null ? 0 : pageable.getPageNumber();
	}

	/*
	 * (non-Javadoc)
	 * @see org.springframework.data.domain.Slice#getSize()
	 */
	public int getSize() {
		return pageable == null ? 0 : pageable.getPageSize();
	}

	/*
	 * (non-Javadoc)
	 * @see org.springframework.data.domain.Slice#getNumberOfElements()
	 */
	public int getNumberOfElements() {
		return content.size();
	}

	/*
	 * (non-Javadoc)
	 * @see org.springframework.data.domain.Slice#hasPrevious()
	 */
	public boolean hasPrevious() {
		return getNumber() > 0;
	}

	/*
	 * (non-Javadoc)
	 * @see org.springframework.data.domain.Slice#isFirst()
	 */
	public boolean isFirst() {
		return !hasPrevious();
	}

	/* 
	 * (non-Javadoc)
	 * @see org.springframework.data.domain.Slice#nextPageable()
	 */
	public PageableBean nextPageable() {
		return hasNext() ? pageable.next() : null;
	}

	/*
	 * (non-Javadoc)
	 * @see org.springframework.data.domain.Slice#previousPageable()
	 */
	public PageableBean previousPageable() {

		if (hasPrevious()) {
			return pageable.previousOrFirst();
		}

		return null;
	}

	/*
	 * (non-Javadoc)
	 * @see org.springframework.data.domain.Slice#hasContent()
	 */
	public boolean hasContent() {
		return !content.isEmpty();
	}

	/*
	 * (non-Javadoc)
	 * @see org.springframework.data.domain.Slice#getContent()
	 */
	public List<T> getContent() {
		return Collections.unmodifiableList(content);
	}

	/*
	 * (non-Javadoc)
	 * @see org.springframework.data.domain.Slice#getSort()
	 */
	public SortBean getSort() {
		return pageable == null ? null : pageable.getSort();
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Iterable#iterator()
	 */
	public Iterator<T> iterator() {
		return content.iterator();
	}

	/**
	 * Applies the given {@link Converter} to the content of the {@link Chunk}.
	 * 
	 * @param converter must not be {@literal null}.
	 * @return
	 */
	protected <S> List<S> getConvertedContent(Converter<? super T, ? extends S> converter) {

		Assert.notNull(converter, "Converter must not be null!");

		List<S> result = new ArrayList<S>(content.size());

		for (T element : this) {
			result.add(converter.convert(element));
		}

		return result;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.springframework.data.domain.Page#getTotalPages()
	 */
	@Override
	public int getTotalPages() {
		return getSize() == 0 ? 1 : (int) Math.ceil((double) total / (double) getSize());
	}

	/*
	 * (non-Javadoc)
	 * @see org.springframework.data.domain.Page#getTotalElements()
	 */
	@Override
	public long getTotalElements() {
		return total;
	}

	/* 
	 * (non-Javadoc)
	 * @see org.springframework.data.domain.Slice#hasNext()
	 */
	@Override
	public boolean hasNext() {
		return getNumber() + 1 < getTotalPages();
	}

	/* 
	 * (non-Javadoc)
	 * @see org.springframework.data.domain.Slice#isLast()
	 */
	@Override
	public boolean isLast() {
		return !hasNext();
	}

	/* 
	 * (non-Javadoc)
	 * @see org.springframework.data.domain.Slice#transform(org.springframework.core.convert.converter.Converter)
	 */
	@Override
	public <S> PageBean<S> map(Converter<? super T, ? extends S> converter) {
		return new PageBean<S>(getConvertedContent(converter), pageable, total);
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {

		String contentType = "UNKNOWN";
		List<T> content = getContent();

		if (content.size() > 0) {
			contentType = content.get(0).getClass().getName();
		}

		return String.format("Page %s of %d containing %s instances", getNumber() + 1, getTotalPages(), contentType);
	}
	
	public PageableBean getPageable() {
		return pageable;
	}
	public void setPageable(PageableBean pageable) {
		this.pageable = pageable;
	}
	public long getTotal() {
		return total;
	}
	public void setTotal(long total) {
		this.total = total;
	}
	public void setContent(List<T> content) {
		this.content = content;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((content == null) ? 0 : content.hashCode());
		result = prime * result + ((pageable == null) ? 0 : pageable.hashCode());
		result = prime * result + (int) (total ^ (total >>> 32));
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
		PageBean other = (PageBean) obj;
		if (content == null) {
			if (other.content != null)
				return false;
		} else if (!content.equals(other.content))
			return false;
		if (pageable == null) {
			if (other.pageable != null)
				return false;
		} else if (!pageable.equals(other.pageable))
			return false;
		if (total != other.total)
			return false;
		return true;
	}
	
}

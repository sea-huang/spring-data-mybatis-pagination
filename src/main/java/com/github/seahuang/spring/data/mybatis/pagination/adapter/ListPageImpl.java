package com.github.seahuang.spring.data.mybatis.pagination.adapter;

import java.util.ArrayList;
import java.util.List;

import org.springframework.core.convert.converter.Converter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.util.Assert;

public class ListPageImpl<T> extends ArrayList<T> implements ListPage<T> {
	private static final long serialVersionUID = -2703378972488492225L;
	private long total;
	private Pageable pageable;
	
	protected ListPageImpl(){}
	/**
	 * Constructor of {@code DubboPageImpl}.
	 * 
	 * @param content the content of this page, must not be {@literal null}.
	 * @param pageable the paging information, can be {@literal null}.
	 * @param total the total amount of items available. The total might be adapted considering the length of the content
	 *          given, if it is going to be the content of the last page. This is in place to mitigate inconsistencies
	 */
	public ListPageImpl(List<T> content, Pageable pageable, long total) {
		this.addAll(content);
		this.pageable = pageable;
		this.total = !content.isEmpty() && pageable != null && pageable.getOffset() + pageable.getPageSize() > total
				? pageable.getOffset() + content.size() : total;
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
	 * @see org.springframework.data.domain.Slice#transform(org.springframework.core.convert.converter.Converter)
	 */
	@Override
	public <S> Page<S> map(Converter<? super T, ? extends S> converter) {
		return new ListPageImpl<S>(getConvertedContent(converter), pageable, total);
	}
	
	/**
	 * Applies the given {@link Converter} to the content of the {@link Chunk}.
	 * 
	 * @param converter must not be {@literal null}.
	 * @return
	 */
	protected <S> List<S> getConvertedContent(Converter<? super T, ? extends S> converter) {

		Assert.notNull(converter, "Converter must not be null!");

		List<S> result = new ArrayList<S>(size());

		for (T element : this) {
			result.add(converter.convert(element));
		}

		return result;
	}

	@Override
	public int getNumber() {
		return pageable == null ? 0 : pageable.getPageNumber();
	}

	/*
	 * (non-Javadoc)
	 * @see org.springframework.data.domain.Slice#getSize()
	 */
	@Override
	public int getSize() {
		return pageable == null ? 0 : pageable.getPageSize();
	}

	/*
	 * (non-Javadoc)
	 * @see org.springframework.data.domain.Slice#getNumberOfElements()
	 */
	@Override
	public int getNumberOfElements() {
		return size();
	}

	/*
	 * (non-Javadoc)
	 * @see org.springframework.data.domain.Slice#getContent()
	 */
	@Override
	public List<T> getContent() {
		return this;
	}

	/*
	 * (non-Javadoc)
	 * @see org.springframework.data.domain.Slice#hasContent()
	 */
	@Override
	public boolean hasContent() {
		return !isEmpty();
	}

	/*
	 * (non-Javadoc)
	 * @see org.springframework.data.domain.Slice#getSort()
	 */
	@Override
	public Sort getSort() {
		return pageable == null ? null : pageable.getSort();
	}

	/*
	 * (non-Javadoc)
	 * @see org.springframework.data.domain.Slice#isFirst()
	 */
	@Override
	public boolean isFirst() {
		return !hasPrevious();
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
	 * @see org.springframework.data.domain.Slice#hasNext()
	 */
	@Override
	public boolean hasNext() {
		return getNumber() + 1 < getTotalPages();
	}

	/*
	 * (non-Javadoc)
	 * @see org.springframework.data.domain.Slice#hasPrevious()
	 */
	@Override
	public boolean hasPrevious() {
		return getNumber() > 0;
	}

	/* 
	 * (non-Javadoc)
	 * @see org.springframework.data.domain.Slice#nextPageable()
	 */
	@Override
	public Pageable nextPageable() {
		return hasNext() ? pageable.next() : null;
	}

	/*
	 * (non-Javadoc)
	 * @see org.springframework.data.domain.Slice#previousPageable()
	 */
	@Override
	public Pageable previousPageable() {
		if (hasPrevious()) {
			return pageable.previousOrFirst();
		}
		return null;
	}

}

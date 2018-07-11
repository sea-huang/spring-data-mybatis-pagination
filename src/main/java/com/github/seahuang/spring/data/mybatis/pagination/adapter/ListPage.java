package com.github.seahuang.spring.data.mybatis.pagination.adapter;

import java.util.List;

import org.springframework.data.domain.Page;

/**
 * Adapt Page to List which is more compatible for Mybatis return mapping
 * @author 黄海
 * @since 1.0
 */
public interface ListPage<T> extends Page<T>, List<T> {

}

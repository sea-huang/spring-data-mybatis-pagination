package com.github.seahuang.spring.data.mybatis.pagination.adapter;

import java.util.List;

import org.springframework.data.domain.Page;

public interface ListPage<T> extends Page<T>, List<T> {

}

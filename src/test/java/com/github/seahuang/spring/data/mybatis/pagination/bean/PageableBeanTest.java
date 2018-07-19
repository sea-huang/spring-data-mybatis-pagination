package com.github.seahuang.spring.data.mybatis.pagination.bean;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.domain.Sort.NullHandling;
import org.springframework.data.domain.Sort.Order;

import com.alibaba.fastjson.JSON;

public class PageableBeanTest {
	@Test
	public void testSerializable(){
		Sort sort = new Sort(new Order(Direction.DESC, "name", NullHandling.NULLS_LAST).ignoreCase()
				, new Order(Direction.DESC, "mobile", NullHandling.NULLS_LAST).ignoreCase());
		PageableBean pageable = PageableBean.from(new PageRequest(0,4,sort));
		String json = JSON.toJSONString(pageable);
		PageableBean copy = JSON.parseObject(json, PageableBean.class);
		Assert.assertEquals(pageable, copy);
	}
}

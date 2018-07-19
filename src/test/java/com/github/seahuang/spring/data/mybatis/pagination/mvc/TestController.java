package com.github.seahuang.spring.data.mybatis.pagination.mvc;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.domain.Sort.NullHandling;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.SortDefault;
import org.springframework.data.web.SortDefault.SortDefaults;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.github.seahuang.spring.data.mybatis.pagination.bean.PageableBean;
import com.github.seahuang.spring.data.mybatis.pagination.mvc.MoreSortDefault.MoreSortDefaults;

@RestController
@RequestMapping("/test")  
public class TestController {
	
	@RequestMapping("")
	public PageableBean acceptPageable(Pageable pageable){
		return PageableBean.from(pageable);
	}
	
	@RequestMapping("/default")
	public PageableBean defaultPageable(@MoreSortDefault(value={"AA","BB"}, ignoreCase=true
		, nullHandling=NullHandling.NULLS_LAST) Pageable pageable){
		return PageableBean.from(pageable);
	}
	
	@RequestMapping("/default/multiple")
	public PageableBean multipleDefaultPageable( @MoreSortDefaults({
			@MoreSortDefault(value="AA", ignoreCase=true)
			,@MoreSortDefault(sort="BB", nullHandling=NullHandling.NULLS_LAST)
		}) @PageableDefault(sort={"AA","BB"}, direction=Direction.DESC) Pageable pageable){
		return PageableBean.from(pageable);
	}

}

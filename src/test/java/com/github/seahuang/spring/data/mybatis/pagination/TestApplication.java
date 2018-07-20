package com.github.seahuang.spring.data.mybatis.pagination;

import java.util.List;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import com.github.seahuang.spring.data.mybatis.pagination.mvc.MoreSortHandlerMethodArgumentResolver;

@SpringBootApplication
public class TestApplication extends WebMvcConfigurerAdapter {
	
    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
        super.addArgumentResolvers(argumentResolvers);
        MoreSortHandlerMethodArgumentResolver sortResolver = new MoreSortHandlerMethodArgumentResolver();
        argumentResolvers.add(sortResolver);
        argumentResolvers.add(new PageableHandlerMethodArgumentResolver(sortResolver));
    }
    
	public static void main(String[] args){
		SpringApplication.run(TestApplication.class, args);
	}
}

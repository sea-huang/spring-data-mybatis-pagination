package com.github.seahuang.spring.data.mybatis.pagination;

import java.util.List;
import java.util.Properties;

import org.mybatis.spring.boot.autoconfigure.ConfigurationCustomizer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import com.github.seahuang.spring.data.mybatis.pagination.adapter.PaginationObjectFactory;
import com.github.seahuang.spring.data.mybatis.pagination.adapter.PaginationPlugin;
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
    
    @Bean
	public ConfigurationCustomizer paginationCustomizer(){
		return new ConfigurationCustomizer(){
			@Override
			public void customize(org.apache.ibatis.session.Configuration configuration) {
				configuration.setObjectFactory(new PaginationObjectFactory());
				PaginationPlugin plugin = new PaginationPlugin();
				Properties properties = new Properties();
				properties.setProperty("sortColumnCamelcaseToUnderscore", "true");
				properties.setProperty("disablePageHelper", "false");
				plugin.setProperties(properties);
				configuration.addInterceptor(plugin);
			}
		};
	}
    
	public static void main(String[] args){
		SpringApplication.run(TestApplication.class, args);
	}
}

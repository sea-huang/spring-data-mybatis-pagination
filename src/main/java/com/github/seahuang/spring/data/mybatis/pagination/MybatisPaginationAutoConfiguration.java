package com.github.seahuang.spring.data.mybatis.pagination;

import org.mybatis.spring.boot.autoconfigure.ConfigurationCustomizer;
import org.mybatis.spring.boot.autoconfigure.MybatisAutoConfiguration;
import org.mybatis.spring.boot.autoconfigure.MybatisProperties;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.github.seahuang.spring.data.mybatis.pagination.adapter.PaginationObjectFactory;
import com.github.seahuang.spring.data.mybatis.pagination.adapter.PaginationPlugin;

@Configuration
@ConditionalOnClass(MybatisProperties.class)
@ConditionalOnProperty(prefix = "mybatis.spring.data.pagination", name = "enabled", havingValue = "true", matchIfMissing = true)
@AutoConfigureBefore(MybatisAutoConfiguration.class)
public class MybatisPaginationAutoConfiguration {
	
	@Bean
	@ConditionalOnClass(ConfigurationCustomizer.class)
	public ConfigurationCustomizer paginationCustomizer(){
		return new ConfigurationCustomizer(){
			@Override
			public void customize(org.apache.ibatis.session.Configuration configuration) {
				configuration.setObjectFactory(new PaginationObjectFactory());
				configuration.addInterceptor(new PaginationPlugin());
			}
		};
	}
}

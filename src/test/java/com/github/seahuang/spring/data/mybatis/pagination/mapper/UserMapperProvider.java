package com.github.seahuang.spring.data.mybatis.pagination.mapper;

public class UserMapperProvider {
	public String providerSql(){
		return "SELECT * FROM T_USER";
	}
}

package com.github.seahuang.spring.data.mybatis.pagination.entity;

public class User {
	private Long id;
	private String name;
	private String mobile;
	private String camelCase;
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getMobile() {
		return mobile;
	}
	public void setMobile(String mobile) {
		this.mobile = mobile;
	}
	public String getCamelCase() {
		return camelCase;
	}
	public void setCamelCase(String camelCase) {
		this.camelCase = camelCase;
	}
}

package com.github.seahuang.spring.data.mybatis.pagination.util;

public class PropertyConventions {
	/**
	 * lowerCamelcase -> LOWER_CAMELCASE
	 */
	public static String lowerCamelcaseToUpperUnderscore(String source){
		StringBuilder result = new StringBuilder();
		for(int i = 0; i < source.length(); i++){
			char each = source.charAt(i);
			if(Character.isUpperCase(each)){
				result.append("_").append(Character.toLowerCase(each));
			}else{
				result.append(each);
			}
		}
		return result.toString().toUpperCase();
	}
}

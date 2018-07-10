package com.github.seahuang.spring.data.mybatis.pagination.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.SelectProvider;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.github.seahuang.spring.data.mybatis.pagination.adapter.ListPage;
import com.github.seahuang.spring.data.mybatis.pagination.entity.User;

@Mapper
public interface UserMapper {
    @Select("SELECT * FROM T_USER WHERE NAME like '%'||#{name}||'%'")
    List<User> findUserByName(@Param("name") String name);
    @Select("SELECT * FROM T_USER")
    ListPage<User> findPage(Pageable pageable);
    
    Page<User> selectInXml(@Param("name") String name, Pageable pageable);
    
    @SelectProvider(type=UserMapperProvider.class, method="providerSql")
    ListPage<User> selectInProvider(Pageable pageable);
}

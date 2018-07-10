package com.github.seahuang.spring.data.mybatis.pagination;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mybatis.spring.boot.test.autoconfigure.MybatisTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.domain.Sort.NullHandling;
import org.springframework.data.domain.Sort.Order;
import org.springframework.test.context.junit4.SpringRunner;

import com.github.seahuang.spring.data.mybatis.pagination.entity.User;
import com.github.seahuang.spring.data.mybatis.pagination.mapper.UserMapper;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = TestApplication.class) 
@Import(MybatisPaginationAutoConfiguration.class)
@MybatisTest
public class PaginationTest {
	@Autowired
	private UserMapper userMapper;
	
	@Test
	public void testPagination(){
		Sort sort = new Sort(new Order(Direction.DESC, "name", NullHandling.NULLS_LAST).ignoreCase()
			, new Order(Direction.DESC, "mobile", NullHandling.NULLS_LAST).ignoreCase());
		Page<User> users = userMapper.findPage(new PageRequest(0, 4, sort));
		System.out.println(users);
	}
	
	@Test
	public void testFindUserByName(){
		List<User> users = userMapper.findUserByName("a");
		System.out.println(users);
	}
	
	@Test
	public void testSelectInXml(){
		Sort sort = new Sort(new Order(Direction.DESC, "name", NullHandling.NULLS_LAST).ignoreCase()
				, new Order(Direction.DESC, "mobile", NullHandling.NULLS_LAST).ignoreCase());
		Page<User> users = userMapper.selectInXml("a", new PageRequest(0, 4, sort));
		System.out.println(users);
	}
	
	
	@Test
	public void testSelectInProvider(){
		Sort sort = new Sort(new Order(Direction.DESC, "name", NullHandling.NULLS_LAST).ignoreCase()
				, new Order(Direction.DESC, "mobile", NullHandling.NULLS_LAST).ignoreCase());
		Page<User> users = userMapper.selectInProvider(new PageRequest(0, 4, sort));
		System.out.println(users);
	}

}

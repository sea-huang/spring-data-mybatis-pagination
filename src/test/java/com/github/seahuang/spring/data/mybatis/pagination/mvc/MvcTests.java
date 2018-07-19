package com.github.seahuang.spring.data.mybatis.pagination.mvc;

import java.net.URL;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.embedded.LocalServerPort;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.domain.Sort.Order;
import org.springframework.test.context.junit4.SpringRunner;

import com.github.seahuang.spring.data.mybatis.pagination.bean.PageableBean;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class MvcTests {
	@Autowired
	private TestRestTemplate testRestTemplate;
	@LocalServerPort
	private int port;
    @Test
    public void testSimple() throws Exception{
    		URL base = new URL("http://localhost:" + port
    				+ "/test?page=1&size=10&sort=AA,DESC|IGNORECASE|NULLS_LAST");
    		Pageable expected = new PageRequest(1,10, new Sort(
    			new Order(Direction.DESC, "AA").ignoreCase().nullsLast()));
    		PageableBean response = testRestTemplate.getForObject(base.toString(), PageableBean.class);
    		Assert.assertEquals(PageableBean.from(expected), response);
    }
    
    @Test
    public void testUnordered() throws Exception{
    		URL base = new URL("http://localhost:" + port
    				+ "/test?page=1&size=10&sort=AA,IGNORECASE|DESC|NULLS_LAST");
    		Pageable expected = new PageRequest(1,10, new Sort(
    			new Order(Direction.DESC, "AA").ignoreCase().nullsLast()));
    		PageableBean response = testRestTemplate.getForObject(base.toString(), PageableBean.class);
    		Assert.assertEquals(PageableBean.from(expected), response);
    }
    
    @Test
    public void testMultiple() throws Exception{
    		URL base = new URL("http://localhost:" + port
    				+ "/test?page=1&size=10&sort=AA,BB,CC,IGNORECASE|DESC|NULLS_LAST");
    		Pageable expected = new PageRequest(1,10, new Sort(
    			new Order(Direction.DESC, "AA").ignoreCase().nullsLast()
    		  , new Order(Direction.DESC, "BB").ignoreCase().nullsLast()
    		  , new Order(Direction.DESC, "CC").ignoreCase().nullsLast()));
    		PageableBean response = testRestTemplate.getForObject(base.toString(), PageableBean.class);
    		Assert.assertEquals(PageableBean.from(expected), response);
    }
    
    @Test
    public void testDefault() throws Exception{
    		URL base = new URL("http://localhost:" + port
    				+ "/test/default?page=1&size=10");
    		Pageable expected = new PageRequest(1,10, new Sort(
    			new Order("AA").ignoreCase().nullsLast()
    		  , new Order("BB").ignoreCase().nullsLast()));
    		PageableBean response = testRestTemplate.getForObject(base.toString(), PageableBean.class);
    		Assert.assertEquals(PageableBean.from(expected), response);
    }
    
    @Test
    public void testDefaultMultiple() throws Exception{
    		URL base = new URL("http://localhost:" + port
    				+ "/test/default/multiple");
    		Pageable expected = new PageRequest(0,10, new Sort(
    			new Order(Direction.DESC, "AA").ignoreCase().nullsNative()
    		  , new Order(Direction.DESC, "BB").nullsLast()));
    		PageableBean response = testRestTemplate.getForObject(base.toString(), PageableBean.class);
    		Assert.assertEquals(PageableBean.from(expected), response);
    }
}

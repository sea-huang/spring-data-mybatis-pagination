#### [中文](https://github.com/sea-huang/spring-data-mybatis-pagination/blob/master/README_CN.md)

# spring-data-mybatis-pagination
Support <b>Pageable/Page/Sort/Order</b> from <b>spring-data-commons</b> in Mybatis.

<b>Auto generate and concatenate pagination sql</b> for most database types, implemented by [PageHelper](https://github.com/pagehelper/Mybatis-PageHelper). 
  
Support <b>full features of ordering</b> including "direction", "ignore case" and "null handlings", refer to hibernate dialect(rewritten).  

Add <b>full features</b> parsing for spring-data <b>mvc-support</b>.  

Bean Adapters enhance which is more <b>compatible for PRC serialization</b>. 


### Usage Examples
- spring-data like standard pagination, the sql in xml don't need involve pagination part, which is auto concatenated underneath.

  <pre>
	<b>Page</b>&ltUser&gt select(@Param("name") String name, <b>Pageable</b> pageable);
  </pre>

- If define the result type as list. no counting sql will be executed, but only offset/limit do

  <pre>
  <b>List</b>&ltUser&gt select(@Param("name") String name, <b>Pageable</b> pageable);
  </pre>

- For people who prefer @Select or @SqlProvider annotatain which don't explicitly define a result mapping strategy, Mybatis may not compatible to Page but only List results. In this cases, use ListPage<?> as the result which is a Page adapter to List.

	<pre>
	@Select("SELECT * FROM T_USER")
    <b>ListPage</b>&ltUser&gt findPage(<b>Pageable</b> pageable);
	</pre>
	
- If you don't like the spring-data standard invasive style. We have a non invasive way like [PageHelper](https://github.com/pagehelper/Mybatis-PageHelper)， the result can be cast to ListPage/Page

  <pre>
  <b>SpringDataPageHelper.paginateNextCall(new PageRequest(0,4));</b>
  List&ltUser&gt users = userMapper.findUserByName("a");
Assert.assertEquals(com.github.seahuang.spring.data.mybatis.pagination.adapter.<b>ListPageImpl</b>.class, users.getClass());
  </pre>
  
- Also, no-counting could be set. And in this case, result can't be cast to spring Page(you can consider it as a normal List)

  <pre>
  <b>SpringDataPageHelper.paginateNextCall(new PageRequest(0,4), false);</b>
  users = userMapper.findUserByName("a");
  Assert.assertEquals(com.github.pagehelper.Page.class, users.getClass());
  </pre>
 
- Default Page/Pageable/Sort/Order implementation in spring-data don't support deserialization in RPC call very well. We offer a bean style version
  
  <pre>
  Pageable pageable = new PageRequest(0,4,sort);
  <b>PageableBean</b> pageable = <b>PageableBean</b>.from(pageable);
  <b>PageBean&ltT&gt</b> page = <b>PageBean&ltT&gt</b>.from(new PageImpl<T>(), pageable);
  </pre>
  
- Official mvc feature don't support "ignore case" or "null handling"
  we makeup that.
  
  <pre>
  /test?page=1&size=10&sort=AA,BB,CC,<b>IGNORECASE</b>|DESC|<b>NULLS_LAST</b>
  </pre>
  
- Setup default, it's a supplimentary to official annotations
  <pre>
  	<b>@MoreSortDefault</b>(value={"AA","BB"}, ignoreCase=true
		, nullHandling=NullHandling.NULLS_LAST) Pageable pageable
  </pre>
  
- A more complicated case:
  <pre>
  <b>@MoreSortDefaults</b>({
		<b>@MoreSortDefault</b>(value="AA", ignoreCase=true)
		,<b>@MoreSortDefault</b>(sort="BB", nullHandling=NullHandling.NULLS_LAST)
	}) <b>@PageableDefault</b>(sort={"AA","BB"}, direction=Direction.DESC) Pageable pageable
  </pre>
  
    <br>
### Set up
- Add maven dependencies: PageHelper is not very stable. for 4.2.x

  ```xml
  <dependency>
  		<groupId>com.github.sea-huang</groupId>
  		<artifactId>spring-data-mybatis-pagination</artifactId>
  		<version>1.1.1</version>
  </dependency>
  ```	
- For pagehelper 5.1.x

  ```xml
  <dependency>
  		<groupId>com.github.sea-huang</groupId>
  		<artifactId>spring-data-mybatis-pagination</artifactId>
  		<version>2.0.0</version>
  </dependency>
  ```	

- For spring boot app, if mybatis-spring-boot-starter(min-version 1.2.1) is on classpath. PaginationPlugin is autoconfigured
- For other cases, PaginationObjectFactory/PaginationPlugin should be manually configured

  ```
  <configuration>
  		<objectFactory type="com.github.seahuang.spring.data.mybatis.pagination.adapter.PaginationObjectFactory"/>
  		<plugins>
 			<plugin interceptor="com.github.seahuang.spring.data.mybatis.pagination.adapter.PaginationPlugin">
 			</plugin>
 		</plugins>
  </configuration>
  ```
  
- Or

  <pre>
    @Bean
    public SqlSessionFactoryBean sqlSessionFactoryBean(DataSource dataSource){
    	SqlSessionFactoryBean result = new SqlSessionFactoryBean();
    	result.setDataSource(dataSource);
    	result.setObjectFactory(new <b>PaginationObjectFactory</b>());
    	result.setPlugins(new Interceptor[]{new <b>PaginationPlugin</b>()});
    	return result;
    }
  </pre>
  
- To setup the enhanced mvc order parsing. we need to configure a MoreSortHandlerMethodArgumentResolver to take the place of SortHandlerMethodArgumentResolver. For boot:

  <pre>
  @SpringBootApplication
  public class TestApplication extends WebMvcConfigurerAdapter {
	
    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
        super.addArgumentResolvers(argumentResolvers);
        <b>MoreSortHandlerMethodArgumentResolver</b> sortResolver = new <b>MoreSortHandlerMethodArgumentResolver</b>();
        argumentResolvers.add(sortResolver);
        argumentResolvers.add(new PageableHandlerMethodArgumentResolver(sortResolver));
    }
   }
  </pre>
  
- xml

  ```xml
  	<mvc:annotation-driven>  
	    <mvc:argument-resolvers>
			<ref bean="sortResolver"/>
	        <ref bean="pageableResolver" />
    	</mvc:argument-resolvers>
    </mvc:annotation-driven>
    <bean id="sortResolver" class="com.github.seahuang.spring.data.mybatis.pagination.mvc.MoreSortHandlerMethodArgumentResolver" />
	<bean id="pageableResolver" class="org.springframework.data.web.PageableHandlerMethodArgumentResolver">
	    <constructor-arg ref="sortResolver" />
	</bean>
  
  ```
[English](https://github.com/sea-huang/spring-data-mybatis-pagination/blob/master/README.md)

# spring-data-mybatis-pagination
支持 <b>spring-data-commons</b> 的 <b>Pageable/Page/Sort/Order</b> 在  Mybatis 中使用.

<b>分页SQL自动生成</b>，适配绝大多数数据库，底层由PageHelper实现[PageHelper](https://github.com/pagehelper/Mybatis-PageHelper). 
  
支持 <b>所有排序特性</b> 包括 "正序逆序", "忽略大小写" and "指定对null排序", 参考hibernate dialect重写实现.  

增加 spring-data <b>mvc-support</b>的<b>全特性</b> 解析.  

分页各接口的 Bean实现，具有更好的<b>PRC序列化兼容性</b>. 


### 使用方式
- 同spring-data 的标准分页使用方式, xml中的SQL正常写，不用涉及分页, 分页SQL自动生成

  <pre>
	<b>Page</b>&ltUser&gt select(@Param("name") String name, <b>Pageable</b> pageable);
  </pre>

- 如果返回类型是list. 只执行分页，不计算总条数
  <pre>
  <b>List</b>&ltUser&gt select(@Param("name") String name, <b>Pageable</b> pageable);
  </pre>

- 对于使用 @Select 或 @SqlProvider 没有显示声明返回结果映射的方式, Mybatis 不支持 Page 只支持 List 类型. 在这种情况下，使用 ListPage<?> 作为返回结果。
	<pre>
	@Select("SELECT * FROM T_USER")
    <b>ListPage</b>&ltUser&gt findPage(<b>Pageable</b> pageable);
	</pre>
	
- 如果你不喜欢 spring-data 标准的侵入式的方式，我们也提供像 [PageHelper](https://github.com/pagehelper/Mybatis-PageHelper)一样的方式， 返回结果可以强转成ListPage/Page

  <pre>
  <b>SpringDataPageHelper.paginateNextCall(new PageRequest(0,4));</b>
  List&ltUser&gt users = userMapper.findUserByName("a");
Assert.assertEquals(com.github.seahuang.spring.data.mybatis.pagination.adapter.<b>ListPageImpl</b>.class, users.getClass());
  </pre>
  
- 同样，可以设置不计算总数。 在这种情况下, 返回结果不能强转成 spring Page，只能当作普通List使用
  <pre>
  <b>SpringDataPageHelper.paginateNextCall(new PageRequest(0,4), false);</b>
  users = userMapper.findUserByName("a");
  Assert.assertEquals(com.github.pagehelper.Page.class, users.getClass());
  </pre>
 
- spring-data默认的 Page/Pageable/Sort/Order 实现在RPC中反序列化支持不好，我们提供了一个bean的实现版本，来提供更好的序列化。
  
  <pre>
  <b>PageableBean</b> pageable = <b>PageableBean</b>.from(new PageRequest(0,4,sort));
  <b>PageBean&ltT&gt</b> page = <b>PageBean&ltT&gt</b>.from(new PageImpl<T>());
  </pre>
  
- Spring 官方 mvc 特性不支持 "ignore case" 或 "null handling"
  我们做了补充
  
  <pre>
  /test?page=1&size=10&sort=AA,BB,CC,<b>IGNORECASE</b>|DESC|<b>NULLS_LAST</b>
  </pre>
  
- 设置默认值, 这是对官方设置的补充
  <pre>
  	<b>@MoreSortDefault</b>(value={"AA","BB"}, ignoreCase=true
		, nullHandling=NullHandling.NULLS_LAST) Pageable pageable
  </pre>
  
- 一个更复杂的例子:
  <pre>
  <b>@MoreSortDefaults</b>({
		<b>@MoreSortDefault</b>(value="AA", ignoreCase=true)
		,<b>@MoreSortDefault</b>(sort="BB", nullHandling=NullHandling.NULLS_LAST)
	}) <b>@PageableDefault</b>(sort={"AA","BB"}, direction=Direction.DESC) Pageable pageable
  </pre>
  
    <br>
### 设置
- 添加Maven依赖: PageHelper 不是很稳定. 只支持 4.2.x
  ```xml
  <dependency>
  		<groupId>com.github.sea-huang</groupId>
  		<artifactId>spring-data-mybatis-pagination</artifactId>
  		<version>1.0.0</version>
  </dependency>
  ```	
- 对 spring boot 应用, 如果 mybatis-spring-boot-starter(最小支持版本 1.2.1) 在类路径上. PaginationPlugin 会自动装配
- 对其他情况, PaginationObjectFactory/PaginationPlugin 需要手动配置
  ```
  <configuration>
  		<objectFactory type="com.github.seahuang.spring.data.mybatis.pagination.adapter.PaginationObjectFactory"/>
  		<plugins>
 			<plugin interceptor="com.github.seahuang.spring.data.mybatis.pagination.adapter.PaginationPlugin">
 			</plugin>
 		</plugins>
  </configuration>
  ```
  
- 或

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
  
- 要支持增加的mvc排序解析. 我们需要配置 MoreSortHandlerMethodArgumentResolver 来替代 SortHandlerMethodArgumentResolver. 对 boot:

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
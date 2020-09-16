# JDBC 必知必会
***
## 简述
&ensp;&ensp;&ensp;&ensp;介绍了数据库配置连接、数据库连接池

### 直接配置所需的Bean

- 数据源相关
  - DataSource（根据选择的连接池实现决定）
- 事务相关（可选）
  - PlatformTransactionManager（DataSourceTransactionManager）
  - TransactionTemplate
- 操作相关（可选）
  - JdbcTemplate

### 单个数据源配置
&ensp;&ensp;&ensp;&ensp;配置文件中填写相关信息，程序中注入DataSource即可：

```java
# 配置文件
spring.datasource.url=jdbc:h2:mem:testdb
spring.datasource.username=sa
spring.datasource.password=

# Bean注入
@Autowired
private DataSource dataSource;
```

### 多个数据源配置
&ensp;&ensp;&ensp;&ensp;首先关闭：数据源自动配置、事务自动配置、jdbc自动配置；手动创建数据源属性、数据源、事务；

```java
# 配置文件
foo.datasource.url=jdbc:h2:mem:foo
foo.datasource.username=sa
foo.datasource.password=

bar.datasource.url=jdbc:h2:mem:bar
bar.datasource.username=sa
bar.datasource.password=

# 关闭自动配置
@SpringBootApplication(exclude = { DataSourceAutoConfiguration.class,
        DataSourceTransactionManagerAutoConfiguration.class,
        JdbcTemplateAutoConfiguration.class})

# 手动创建
@Bean
@ConfigurationProperties("foo.datasource")
public DataSourceProperties fooDataSourceProperties() {
    return new DataSourceProperties();
}

@Bean
public DataSource fooDataSource() {
    DataSourceProperties dataSourceProperties = fooDataSourceProperties();
    log.info("foo datasource: {}", dataSourceProperties.getUrl());
    return dataSourceProperties.initializeDataSourceBuilder().build();
}

@Bean
@Resource
public PlatformTransactionManager fooTxManager(DataSource fooDataSource) {
    return new DataSourceTransactionManager(fooDataSource);
}
```

#### 其他尝试

- 1.是否能配置多个不同数据库的数据源？答案是可以，在工程中添加了MySQL的配置，并成功进行了连接。
  - 但没有找到使用连接的方法，也就是说在多种数据库的情况下，数据源配置、事务配置和JDBC模板是失效的
- 2.能够配置不同数据源，也就是连接池没有要求是同种数据库，池中的数据库连接能区分不同的数据库，且不出错
  - 如何优雅配置配置使用了MongoDB，MySQL、redis？

### 连接池
&ensp;&ensp;&ensp;&ensp;连接池不能共存，只能选择其一，使用Druid时需要在配置中排除HikariCP

#### 连接池：HikariCP
- 字节码级别优化
- 大量小改进

#### 连接池：Alibaba Druid
- 监控
- SQL防注入
- 内置加密配置
- 扩展点多

```java
# 配置示例，会覆盖掉HikariCP（框架中自动判断）
spring.datasource.druid.initial-size=5
spring.datasource.druid.max-active=5
spring.datasource.druid.min-idle=5
spring.datasource.druid.filters=conn,config,stat,slf4j

spring.datasource.druid.connection-properties=config.decrypt=true;config.decrypt.key=${public-key}
spring.datasource.druid.filter.config.enabled=true

spring.datasource.druid.test-on-borrow=true
spring.datasource.druid.test-on-return=true
spring.datasource.druid.test-while-idle=true
```

### Spring JDBC
#### 相关模块
- core，JdbcTemplate等相关核心接口和类
- DataSource，数据源相关的辅助类
- Object，将基本的JDBC操作封装成类
- support，错误码等其他辅助工具

#### JdbcTemplate相关操作函数
- query
- queryForObject
- queryForList
- update
- execute

#### 扩展
- JDBC是如何区分不同连接的，单个好像没有问题？多个时如何使用？
- JdbcTemplate和Entity、Repository之间有什么关联和区别？

### Spring的事务抽象
#### 一致的事务模型
- JDBC/Hibernate/MyBatis
- DataSource/JTA

#### 事务抽象的核心接口
- PlatformTransactionManager
  - DataSourceTransactionManager
  - HibernateTransactionManager
  - JtaTransactionManager

- TransactionDefinition
  - Propagation
  - Isolation
  - Timeout
  - Read-only status

#### 事务的传播特性
1、PROPAGATION_REQUIRED: 如果存在一个事务，则支持当前事务。如果没有事务则开启
2、PROPAGATION_SUPPORTS: 如果存在一个事务，支持当前事务。如果没有事务，则非事务的执行
3、PROPAGATION_MANDATORY: 如果已经存在一个事务，支持当前事务。如果没有一个活动的事务，则抛出异常。
4、PROPAGATION_REQUIRES_NEW: 总是开启一个新的事务。如果一个事务已经存在，则将这个存在的事务挂起。
5、PROPAGATION_NOT_SUPPORTED: 总是非事务地执行，并挂起任何存在的事务。
6、PROPAGATION_NEVER: 总是非事务地执行，如果存在一个活动事务，则抛出异常
7、PROPAGATION_NESTED：如果一个活动的事务存在，则运行在一个嵌套的事务中. 如果没有活动事务, 则按TransactionDefinition.PROPAGATION_REQUIRED 属性执行

#### Spring事务的隔离级别：
1、ISOLATION_DEFAULT： 这是一个PlatfromTransactionManager默认的隔离级别，使用数据库默认的事务隔离级别.
另外四个与JDBC的隔离级别相对应
2、ISOLATION_READ_UNCOMMITTED： 这是事务最低的隔离级别，它充许令外一个事务可以看到这个事务未提交的数据。
这种隔离级别会产生脏读，不可重复读和幻像读。
3、ISOLATION_READ_COMMITTED： 保证一个事务修改的数据提交后才能被另外一个事务读取。另外一个事务不能读取该事务未提交的数据
4、ISOLATION_REPEATABLE_READ： 这种事务隔离级别可以防止脏读，不可重复读。但是可能出现幻像读。
它除了保证一个事务不能读取另一个事务未提交的数据外，还保证了避免下面的情况产生(不可重复读)。
5、ISOLATION_SERIALIZABLE 这是花费最高代价但是最可靠的事务隔离级别。事务被处理为顺序执行。除了防止脏读，不可重复读外，还避免了幻像读。

#### 编程式事务
- TransactionTemplate
  - TransactionCallback
  - TransactionCallbackWithoutResult
- PlatformTransactionManager
  - 可以传入TransactionDefinition进行定义

#### 声明式事务
- 开启事务注解的方式
  - @EnableTransactionManagement

- 一些配置
  - ProxyTargetClass
  - mode
  - order

- @Transactional
  - transactionManager
  - propagation
  - isolation
  - timeout
  - readOnly
  - 怎么判断回滚

#### 扩展
- 事务的定义与应用场景？
- Spring的事务支持是否基于数据库是否支持事务？

### Spring 异常抽象
- 定义各个数据库的错误
- 可自定义：extends 相应异常类即可



## Spring 状态和数据查看
&ensp;&ensp;&ensp;&ensp;线上不能开启重要的点，容易出问题

- http://localhost:8080/actuator/beans

```java
management.endpoints.web.exposure.include=*
management.endpoints.web.exposure.include=beans,info,health
```

## 参考链接

- [Spring boot配置Mysql数据库](https://blog.csdn.net/sinat_29774479/article/details/78686519)
- [Spring编程：Spring事务的传播特性和隔离级别](https://blog.csdn.net/claram/article/details/51646645)
- []()
- []()
- []()
- []()
- []()
- []()
- []()
- []()
- []()
- []()
- []()
- []()
- []()
- []()
- []()
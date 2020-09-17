# O/R Mapping实践
***
## 对象与关系数据模型
### 背景及相关知识
对象与关系的范式不匹配

 性质 | Object | RDBMS 
---------|----------|---------
 粒度 | 类 | 表
 继承 | 有 | 没有
 唯一性 | a==b | 主键
 关联 | 引用 | 外键
 数据访问 | 逐级访问 | SQL数量要少

 Hibernate

 对象关系映射框架
 解放95%的数据持久化工作
 屏蔽底层数据库的各种细节

 JPA为对象关系映射提供了一种基于POJO的持久化模型
 - 简化数据持久化代码的开发工作
 - 为Java社区屏蔽不同持久化API的差异

 Spring Data：在保留底层存储特性的同时，提供相对一致的、基于Spring的编程模型，主要模块有：
 - Spring Data Commons
 - Spring Data JDBC
 - Spring Data JPA
 - Spring Data Redis
 - ......

### 定义JPA的实体对象
#### 常用JPA注解
- 实体
  - Entity、MappedSuperclass
  - Table（name）
- 主键
  - Id
    - GeneratedValue（strategy，generator）
    - SequenceGenerator（name，sequenceName）
- 映射
  - Column（name，nullable，length，insertable，updatable）
  - JoinTable（name）
  - JoinColumn（name）
- 关系
  - OneToOne、OneToMany、ManyToOne、ManyToMany
  - OrderBy

### 通过Spring JPA操作数据库
#### 注解
- @EnableJpaRepositories

- Repository<T, ID>接口：
  - CrudRepository<T, ID>
  - PagingAndSortingReposirory<T, ID>
  - JpaRepository<T, ID>

#### 定义查询
- 根据方法定义查询
  - find...By... / read...By.../query...By.../get...By...
  - count...By...
  - OrderBy...[Asc/Desc]
  - And / Or / IngoreCase
  - Top / First / Distinct

#### 分页查询
- PagingAndSortingRepository<T, ID>
- Pageable / Sort
- Slice<T> / Page<T>

### JPA Repository To Bean
#### Repository Bean 是如何创建的
- JpaRepositoriesRegistrar
  - 激活了@EnableJpaRepositories
  - 返回了JpaRepositoryConfigExtension
- RepositoryBeanDefinitionRegistrarSupport.registrerBeanDefinitions
  - 注册了Repository Bean(类型是JpaRepositoryFactoryBean)
- RepositoryConfigurationExtensionSupport.getRepositoryConfigurations
  - 取得Repository配置
- JpaRepositoryFactory.getTargetRepository
  - 创建了Repository

#### 接口中的方法是如何被解释的
- RepositoryFactorySupport.getRepository添加了Advice
  - DefaultMethodInvokingMethodInterceptor
  - QueryExecutorMethodIntercepter
- AbstractJpaQuery.execute执行具体的查询
- 语法解析在Part中

### Mybatis的使用
*如果对SQL有比较高的把控的要求/比较复杂，就可以使用Mybatis，简单的可以使用hibernate*

#### [Mybatis](https://github.com/mybatis/mybatis-3) 介绍
- 一款优秀的持久层框架
- 支持定制化SQL、存储过程和高级映射

#### Spring 中使用 Mybatis
- [Mybatis Spring Adapter](https://github.com/mybatis/spring)
- [Mybatis Spring-Boot-Starter](https://github.com/mybatis/spring-boot/starter)

##### Mybatis的简单配置

```java
mybatis.mapper-locations=classpath*:/mapper/**/*.xml
mybatis.type-aliases-package=geektime.spring.data.mybatis.model
mybatis.type-handlers-package=geektime.spring.data.mybatis.handler
mybatis.configuration.map-underscore-to-camel-case=true
```
##### Mapper的定义和扫描
- @MapperScan配置扫描位置
- @Mapper定义接口
- 映射的定义--XML与注解

#### [Mybatis Generator](http://www.mybatis.org/generator)
- Mybatis 代码生成器
- 根据数据库表生成相关代码
  - POJO
  - Mapper接口
  - SQL Map XML

##### 运行Mybatis Generator
- 命令行：java -jar xxx.jar -configfile generatorConfig.xml
- Maven Plugin(mybatis-generator-maven-plugin)
  - mvn mybatis-generator:generate
  - ${basedir}/src/main/resources/generatorConfig.xml
- Eclipese Plugin
- Java 程序
- Ant Task

##### 配置Mybatis Generator
- generatorConfiguration
- context
  - jdbcConnection
  - javaMedelGenerator
  - sqlMapGenerator
  - JavaClientGenerator(ANNOTATEDMAPER/XMLMAPPER/MIXEDMAPPER)
  - table

##### 生成时可以使用的插件
*内置插件都在org.mybatis.generator.plugins包中
 - FluentBuilderMethodPlugin
 - ToStringPlugin
 - SeralizablePlugin
 - RowBoundsPlugin
 - ......

##### 使用生成的对象
- 简单操作，直接使用生成的XXXMapper的方法
- 复杂查询，生成生成的XXXExample对象

#### [Mybatis PageHelper](https://pagehelper.github.io)
- 支持多种数据库
- 支持多种分页方式
- SpringBoot:[pagehelper-spring-boot-starter](https://github.com/pagehelper/pagehelper-spring-boot)

## 疑问
- 1.Mybatis-demo中的MoneyTypeHandler是怎么生效的？这些看着好迷。。。。。。



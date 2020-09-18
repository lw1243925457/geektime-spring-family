# Spring NoSQL实践
***
## MongoDB
### Spring 对 [MongoDB](https://www.mongodb.com) 的支持
- Spring Data MongoDB
  - MongoTemplate
  - Repository 支持

### Spring Data MongoDB 的基本用法
- 注解：
  - @Document
  - @Id
- MongoTemplate
  - save / remove
  - Criteria / Query / Update

### 其他
- MongoDB中文档处理转换的关键点


## Redis
### Spring 对 [Redis](https://redis.io) 的支持
- Spring Data Redis
  - 支持客户端 Jedis / Lettuce
  - RedisTemplate
  - Repository 支持

### Jedis客户端的简单使用
- Jedis不是线程安全的
- 通过JedisPool获得Jedis实例
- 直接使用Jedis中的方法

### Redis的哨兵模式
- Redis Sentinel 是Redis的一种高可用方案：监控、通知、自动故障转移、服务发现
- JedisSentinePool

### Redis的集群模式
- Redis Cluster
  - 数据自动分片（分成16384个hash slot）
  - 在部分节点失效时有一定可用性
- JedisCluster：不支持批量操作、不支持读写分离（Lettuce支持）
  - Jedis只从Master读数据，如果想要自动读写分离，可以定制
- Lettuce：内置支持读写分离；只读主、只读从；优先读主、优先读从
  - LettuceClientConfiguration:直接读
  - LettucePoolingClientConfiguration：池
  - LettuceClientConfigurationBuilderCustomizer

### Spring 中Redis使用
- RedisTemplate<K, V>：一定要设置过期时间
  - opsForxxx
- StringRedisTemplate
- Redis Repository：下面是实体注解
  - @RedisHash
  - @Id
  - @Indexed

#### 疑问点
- 1.Jedis的配置使用具体是什么样的有点迷糊？相应代码如下：

```java
@Bean
@ConfigurationProperties("redis")
public JedisPoolConfig jedisPoolConfig() {
	return new JedisPoolConfig();
}
```

- 2.其Autowried的bean的什么周期是怎么样的？如1中的JedisPoolConfig
- 3.CoffeeOrderRepository之类的实现细节是怎样的？如何确定其数据库类型和数据？而且其也没有加注解，代码大致如下：

```java
public interface CoffeeRepository extends JpaRepository<Coffee, Long> {
}
```

- 4.Money转换的具体细节？


### 其他
- JedisCluster源码：三层基础、发现节点函数

## Spring的缓存抽象
- 为Java方法增加缓存，缓存执行结果：加注解
- 支持ConcurrentMap、EhCache、Caffeine、JCache（JSR-107）
- 接口：
  - org.springframework.cache.Cache
  - org.springframework.cache.CacheManager

### 基于注解的缓存
- @EnableCaching
  - @Cacheable
  - @CacheEvict
  - @CachePut
  - @Caching
  - @CacheConfig
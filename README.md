## RedisManager工具使用说明

* Redis的配置方式

Redis的配置信息统一写在resources目录下的redis-config.properties配置文件中，代码中直接通过RedisManager即可获取Jedis客户端。
redis-config.properties支持的配置信息如下：

```
;------Jedis支持的配置如下-------
;连接池中的最大连接数,缺省100
max-total=100

;连接池中最大的空闲连接数,缺省10
max-idle=10

;获取Jedis实例时,最大的等待时间,单位毫秒,缺省3000
max-wait=3000

;redis命令执行的最长时间,单位毫秒,缺省3000.超时抛出SocketTimeoutException
timeout=3000

;Jedis实例配置实例
dev.client=127.0.0.1:6379


;------JedisCluster支持的配置如下------
;JedisCluster实例配置实例
dev.cluster=127.0.0.1:17001,127.0.0.1:17002,127.0.0.1:17003
```

* 支持Jedis(redis standalone), 使用示例请查看：src/test/java/JedisExample.java

* 支持JedisCluster(redis cluster), 使用示例请查看：src/test/java/JedisClusterExample.java

* 支持JedisCluster Pipeline(redis cluster), 使用示例请查看：src/test/java/JedisClusterPipelineExample.java


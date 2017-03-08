package org.junety.redis;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisCluster;

/**
 * Created by caijt on 2017/3/6.
 */
public class RedisManager {

    private RedisManager() {}

    public static Jedis getJedis(String name) {
        return JedisFactory.getInstance(name);
    }

    public static JedisCluster getJedisCluster(String name) {
        return JedisClusterFactory.getInstance(name);
    }

    public static JedisClusterPipeline getJedisClusterPipeline(JedisCluster jedisCluster) {
        // 一定要new, 不能缓存，防止多线程下命令执行被污染
        return new JedisClusterPipeline(jedisCluster);
    }
}

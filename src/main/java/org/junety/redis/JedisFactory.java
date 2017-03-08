package org.junety.redis;

import org.junety.redis.util.PropertiesLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Created by caijt on 2017/3/6.
 */
class JedisFactory {

    private static final Logger logger = LoggerFactory.getLogger(JedisFactory.class);

    // jedisPool最大连接数
    private static final int DEFAULT_MAX_TOTAL = 100;
    // jedis最大空闲连接数
    private static final int DEFAULT_MAX_IDLE = 10;
    // 获取实例时，最大的等待时间
    private static final int DEFAULT_MAX_WAIT = 3000;
    // 执行超时
    private static final int DEFAULT_TIME_OUT = 3000;

    private static final int MAX_TOTAL;
    private static final int MAX_IDLE;
    private static final int MAX_WAIT;
    private static final int TIMEOUT;

    private static Map<String, JedisPool> jedisPools = new HashMap<>();

    static {
        Properties prop = PropertiesLoader.load("redis-config.properties");
        MAX_TOTAL = prop.getProperty("max-total") == null ? DEFAULT_MAX_TOTAL : Integer.parseInt(prop.getProperty("max-total"));
        MAX_IDLE = prop.getProperty("max-idle") == null ? DEFAULT_MAX_IDLE : Integer.parseInt(prop.getProperty("max-idle"));
        MAX_WAIT = prop.getProperty("max-wait") == null ? DEFAULT_MAX_WAIT : Integer.parseInt(prop.getProperty("max-wait"));
        TIMEOUT = prop.getProperty("timeout") == null ? DEFAULT_TIME_OUT : Integer.parseInt(prop.getProperty("timeout"));
    }

    private JedisFactory() {}

    static Jedis getInstance(String name) {
        JedisPool jedisPool = jedisPools.get(name);
        if(jedisPool == null) {
            jedisPool = initJedisPool(name);
        }
        try {
            return jedisPool.getResource();
        } catch (Exception e) {
            logger.error("get resource from jedis pool error, caused by", e);
        }
        return null;
    }

    private static synchronized JedisPool initJedisPool(String name) {
        JedisPool jedisPool = jedisPools.get(name);
        if(jedisPool == null) {
            Properties prop = PropertiesLoader.load("redis-config.properties");
            String[] config = prop.getProperty(name).split(":");
            String host = config[0];
            int port = Integer.parseInt(config[1]);
            jedisPool = newJedisPool(host, port);
            jedisPools.put(name, jedisPool);
        }
        return jedisPool;
    }

    private static JedisPool newJedisPool(String host, int port) {
        JedisPoolConfig config = new JedisPoolConfig();
        //config.setTestOnReturn(true);
        //config.setTestOnBorrow(true);
        config.setMaxTotal(MAX_TOTAL);
        config.setMaxIdle(MAX_IDLE);
        config.setMaxWaitMillis(MAX_WAIT);
        return new JedisPool(config, host, port, TIMEOUT);
    }
}

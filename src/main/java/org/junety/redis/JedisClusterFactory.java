package org.junety.redis;

import org.junety.redis.util.PropertiesLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.JedisCluster;

import java.util.*;

/**
 * Created by caijt on 2017/3/6.
 */
class JedisClusterFactory {

    private static final Logger logger = LoggerFactory.getLogger(JedisClusterFactory.class);

    private static Map<String, JedisCluster> jedisClusterPool = new HashMap<>();

    static JedisCluster getInstance(String name) {
        JedisCluster jedisCluster = jedisClusterPool.get(name);
        if(jedisCluster == null) {
            jedisCluster = initJedisCluster(name);
        }
        try {
            return jedisCluster;
        } catch (Exception e) {
            logger.error("get resource from jedis pool error, caused by", e);
        }
        return null;
    }

    private static synchronized JedisCluster initJedisCluster(String name) {
        JedisCluster jedisCluster = jedisClusterPool.get(name);
        if(jedisCluster == null) {
            Properties prop = PropertiesLoader.load("redis-config.properties");
            String[] configArray = prop.getProperty(name).split(",");
            Set<HostAndPort> hostAndPorts = new HashSet<>();
            for(int i = 0; i < configArray.length; i++) {
                String config = configArray[i].trim();
                if(config.length() == 0) continue;
                String[] configPart = config.split(":");
                String host = configPart[0];
                int port = Integer.parseInt(configPart[1]);
                hostAndPorts.add(new HostAndPort(host, port));
            }
            jedisCluster = new JedisCluster(hostAndPorts);
            jedisClusterPool.put(name, jedisCluster);
        }
        return jedisCluster;
    }
}

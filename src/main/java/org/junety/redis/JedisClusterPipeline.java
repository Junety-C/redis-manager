package org.junety.redis;

import redis.clients.jedis.*;
import redis.clients.jedis.exceptions.JedisMovedDataException;
import redis.clients.jedis.exceptions.JedisRedirectionException;
import redis.clients.util.JedisClusterCRC16;
import redis.clients.util.SafeEncoder;

import java.io.Closeable;
import java.lang.reflect.Field;
import java.util.*;

/**
 * Created by caijt on 2017/3/7.
 */
public class JedisClusterPipeline extends PipelineBase implements Closeable {

    private final JedisSlotBasedConnectionHandler connectionHandler;
    private List<Client> clientList = new ArrayList<>();
    private Map<JedisPool, Jedis> jedisMap = new HashMap<>();

    JedisClusterPipeline(JedisCluster jedisCluster) {
        connectionHandler = getValueByReflection(BinaryJedisCluster.class, "connectionHandler", jedisCluster);
        if(connectionHandler == null) {
            throw new RuntimeException("cannot get connectionHandler field from JedisCluster");
        }
    }

    public void sync() {
        sync(null);
    }

    public List<Object> syncAndGetResponse() {
        List<Object> responseList = new ArrayList<>();
        sync(responseList);
        return responseList;
    }

    public void refreshCluster() {
        connectionHandler.renewSlotCache();
    }

    @Override
    public void close() {
        clean();
        for (Jedis jedis : jedisMap.values()) {
            jedis.close();
        }
        jedisMap.clear();
        clientList.clear();
    }

    private void sync(List<Object> responseList) {
        Set<Client> clientCollector = new HashSet<>();
        try {
            for (Client client : clientList) {
                Object data = generateResponse(client.getOne()).get();
                if (responseList != null) {
                    responseList.add(data);
                }
                // 收集pipeline执行时使用的client
                clientCollector.add(client);
            }
        } catch (JedisRedirectionException jre) {
            if (jre instanceof JedisMovedDataException) {
                refreshCluster();
            }
            throw jre;
        } finally {
            if (jedisMap.size() != clientCollector.size()) {
                // 所有还没有执行过的client要保证执行(flush)，防止放回连接池后后面的命令被污染
                for (Jedis jedis : jedisMap.values()) {
                    if (clientCollector.contains(jedis.getClient())) continue;
                    try {
                        jedis.getClient().getAll();
                    } catch (RuntimeException ignored) {
                    }
                }
            }
            this.close();
        }
    }

    @Override
    protected Client getClient(String key) {
        byte[] bKey = SafeEncoder.encode(key);
        return getClient(bKey);
    }

    @Override
    protected Client getClient(byte[] key) {
        Jedis jedis = getJedisBySlot(JedisClusterCRC16.getSlot(key));
        Client client = jedis.getClient();
        clientList.add(client);
        return client;
    }

    private Jedis getJedisBySlot(int slot) {
        JedisClusterInfoCache cache = getValueByReflection(JedisClusterConnectionHandler.class, "cache", connectionHandler);
        JedisPool pool = cache.getSlotPool(slot);
        Jedis jedis = jedisMap.get(pool);
        if (jedis == null) {
            jedis = pool.getResource();
            jedisMap.put(pool, jedis);
        }
        return jedis;
    }

    @SuppressWarnings({"unchecked"})
    private static <T> T getValueByReflection(Class<?> clazz, String fieldName, Object object) {
        try {
            Field field = clazz.getDeclaredField(fieldName);
            field.setAccessible(true);
            return (T) field.get(object);
        } catch (NoSuchFieldException | SecurityException e) {
            throw new RuntimeException(String.format("can not find or access field '%s' from %s", fieldName, clazz.getName()), e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(String.format("can not get value from %s", clazz.getName()), e);
        }
    }
}

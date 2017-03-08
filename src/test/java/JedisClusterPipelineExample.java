
import org.junety.redis.JedisClusterPipeline;
import org.junety.redis.RedisManager;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.Response;

import java.util.List;
import java.util.Set;

/**
 * Created by caijt on 2017/3/7.
 */
public class JedisClusterPipelineExample {

    public static void main(String[] args) {
        JedisCluster jedisCluster = RedisManager.getJedisCluster("dev.cluster");
        JedisClusterPipeline pipeline = RedisManager.getJedisClusterPipeline(jedisCluster);

        Response<String> respHello = pipeline.get("hello");
        Response<Set<String>> respSet = pipeline.smembers("set");
        Response<List<String>> respPerson = pipeline.hmget("person", "name", "age");
        pipeline.sync();

        System.out.println(respHello.get());
        System.out.println(respSet.get());
        System.out.println(respPerson.get());
    }
}


import org.junety.redis.RedisManager;
import redis.clients.jedis.JedisCluster;

/**
 * Created by caijt on 2017/3/7.
 */
public class JedisClusterExample {

    public static void main(String[] args) {
        JedisCluster jedisCluster = RedisManager.getJedisCluster("dev.cluster");
        String value = jedisCluster.get("hello");
        System.out.println(value);
    }
}

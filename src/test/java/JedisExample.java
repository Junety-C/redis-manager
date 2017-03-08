
import org.junety.redis.RedisManager;
import redis.clients.jedis.Jedis;

/**
 * Created by caijt on 2017/3/6.
 */
public class JedisExample {

    public static void main(String[] args) {
        // 方式一:
        try (Jedis jedis = RedisManager.getJedis("dev.client")) {
            System.out.println(jedis.get("hello"));
        } catch (Exception e) {
            e.printStackTrace();
        }

        // 方式二:
        Jedis jedis = RedisManager.getJedis("dev.client");
        try {
            System.out.println(jedis.get("hello"));
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if(jedis != null) {
                jedis.close();
            }
        }
    }
}
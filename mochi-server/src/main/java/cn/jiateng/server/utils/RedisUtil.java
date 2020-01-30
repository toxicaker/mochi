package cn.jiateng.server.utils;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

public class RedisUtil {

    private static JedisPool pool = new JedisPool(new JedisPoolConfig(), "localhost");

    public static Jedis jedis = pool.getResource();

    public static void close() {
        pool.close();
        jedis.close();
    }
}

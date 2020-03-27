package cn.toxicaker.common.util;

import redis.clients.jedis.Jedis;

public class RedisUtil {

    public static Jedis redisCli = new Jedis("localhost");
}

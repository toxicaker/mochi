package cn.jiateng.server.utils;
import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.JedisCluster;

import java.util.HashSet;
import java.util.Set;


public class RedisUtil {

    public static JedisCluster jedis;

    static {
        String address = PropReader.read("redis.cluster.nodes");
        String[] addresses = address.split(",");
        Set<HostAndPort> nodes = new HashSet<>();
        for (String addr : addresses) {
            String[] hostAndPort = addr.split(":");
            nodes.add(new HostAndPort(hostAndPort[0], Integer.parseInt(hostAndPort[1])));
        }
        jedis = new JedisCluster(nodes);
    }

    public static void close() {
        jedis.close();
    }
}

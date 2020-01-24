package cn.jiateng.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.*;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
public final class RedisUtil {

    private final RedisTemplate<String, Object> redisTemplate;

    @Autowired
    public RedisUtil(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public String stringGet(String key) {
        return (String) this.redisTemplate.opsForValue().get(key);
    }

    public void stringSet(String key, String val) {
        this.redisTemplate.opsForValue().set(key, val);
    }

    public void stringSet(String key, String val, long ttl) {
        this.redisTemplate.opsForValue().set(key, val, ttl);
    }

    public void zsetAdd(String key, String val, double score) {
        this.redisTemplate.opsForZSet().add(key, val, score);
    }

    public List<String> zsetGet(String key) {
        List<String> res = new ArrayList<>();
        long size = this.redisTemplate.opsForZSet().size(key);
        size = Math.min(1000, size);
        ScanOptions scanOptions = new ScanOptions.ScanOptionsBuilder().count(size).build();
        Cursor<ZSetOperations.TypedTuple<Object>> cursor = this.redisTemplate.opsForZSet().scan(key, scanOptions);
        while (cursor.hasNext()) {
            ZSetOperations.TypedTuple<Object> data = cursor.next();
            res.add((String) data.getValue());
        }
        return res;
    }

    public void setAdd(String key, String... val) {
        this.redisTemplate.opsForSet().add(key, val);
    }

    public Set<String> setGet(String key) {
        Set<String> res = new HashSet<>();
        long size = this.redisTemplate.opsForSet().size(key);
        size = Math.min(1000, size);
        ScanOptions scanOptions = new ScanOptions.ScanOptionsBuilder().count(size).build();
        Cursor<Object> cursor = this.redisTemplate.opsForSet().scan(key, scanOptions);
        while (cursor.hasNext()) {
            res.add((String) cursor.next());
        }
        return res;
    }

    public void listRAdd(String key, String... val) {
        this.redisTemplate.opsForList().rightPushAll(key, val);
    }

    public void listLAdd(String key, String... val) {
        this.redisTemplate.opsForList().leftPushAll(key, val);
    }

    public List<String> listGet(String key) {
        List<String> res = new ArrayList<>();
        long size = this.redisTemplate.opsForList().size(key);
        List<Object> r = this.redisTemplate.opsForList().range(key, 0, size);
        for (Object obj : r) {
            res.add((String) obj);
        }
        return res;
    }
}

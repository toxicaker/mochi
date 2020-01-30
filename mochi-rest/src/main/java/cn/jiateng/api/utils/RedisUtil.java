package cn.jiateng.api.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.*;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.TimeUnit;

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

    public void setRemove(String key, String... objs) {
        redisTemplate.opsForSet().remove(key, (Object) objs);
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
        if (r == null) {
            return res;
        }
        for (Object obj : r) {
            res.add((String) obj);
        }
        return res;
    }

    public boolean removeKey(String key) {
        return this.redisTemplate.delete(key);
    }

    public void mapAdd(String key, String mapKey, Object val) {
        this.redisTemplate.opsForHash().put(key, mapKey, val);
    }

    public void mapAdd(String key, Map<String, Object> data) {
        this.redisTemplate.opsForHash().putAll(key, data);
    }

    public void setExpire(String key, long time){
        this.redisTemplate.expire(key, time, TimeUnit.MILLISECONDS);
    }



    public Object mapGet(String key, String mapKey) {
        return this.redisTemplate.opsForHash().get(key, mapKey);
    }

    public boolean mapHasKey(String key, String mapKey) {
        return this.redisTemplate.opsForHash().hasKey(key, mapKey);
    }

    public boolean hasKey(String key){
        return this.redisTemplate.hasKey(key);
    }
}

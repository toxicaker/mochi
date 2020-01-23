package cn.jiateng.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
public final class RedisUtil {

    private final RedisTemplate<String, Object> redisTemplate;

    @Autowired
    public RedisUtil(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }


    public String getString(String key) {
        return (String) this.redisTemplate.opsForValue().get(key);
    }

    public void setString(String key, String val) {
        this.redisTemplate.opsForValue().set(key, val);
    }

    public void setString(String key, String val, long ttl) {
        this.redisTemplate.opsForValue().set(key, val, ttl);
    }
}

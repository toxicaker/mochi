package cn.jiateng.utils;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
class RedisUtilTest {

    @Autowired
    private RedisUtil redisUtil;

    @BeforeEach
    void setUp() {
        redisUtil.stringSet("test-str", "abc");
        redisUtil.setAdd("test-set", "abc", "def", "ggg");
        redisUtil.zsetAdd("test-zset", "abc", 2);
        redisUtil.zsetAdd("test-zset", "def", 3);
        redisUtil.zsetAdd("test-zset", "ggg", 1);
    }

    @Test
    void getString() {
        assertEquals("abc", redisUtil.stringGet("test-str"), "Test stringGet()");
    }

    @Test
    void setString() {
        redisUtil.stringSet("test-str1", "bcd");
        assertEquals("bcd", redisUtil.stringGet("test-str1"), "Test stringSet()");
    }

    @Test
    void zsetAdd() {
        redisUtil.zsetAdd("test-zset", "eee", 0);
        List<String> res = redisUtil.zsetGet("test-zset");
        assertEquals("eee", res.get(0), "Test zsetAdd()");
    }

    @Test
    void zsetGet() {
        List<String> res = redisUtil.zsetGet("test-zset");
        for(int i = 0 ; i < res.size(); i++){
            System.out.println(res.get(i));
        }
    }

    @Test
    void setAdd() {
        redisUtil.setAdd("test-set", "ccccccc");
        Set<String> res = redisUtil.setGet("test-set");
        assertTrue(res.contains("ccccccc"));
    }

    @Test
    void setGet() {
        Set<String> res = redisUtil.setGet("test-set");
        for (String re : res) {
            System.out.println(re);
        }
    }
}
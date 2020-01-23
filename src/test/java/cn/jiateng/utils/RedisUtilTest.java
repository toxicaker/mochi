package cn.jiateng.utils;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.jupiter.api.Assertions.*;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
class RedisUtilTest {

    @Autowired
    private RedisUtil redisUtil;

    @BeforeEach
    void setUp() {
        redisUtil.setString("test-str", "abc");
    }

    @Test
    void getString() {
        assertEquals("abc", redisUtil.getString("test-str"), "Test getString()");
    }

    @Test
    void setString() {
        redisUtil.setString("test-str1", "bcd");
        assertEquals("bcd", redisUtil.getString("test-str1"), "Test setString()");
    }
}
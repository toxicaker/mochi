package cn.jiateng.api.server.services.impl;

import cn.jiateng.api.Model.User;
import cn.jiateng.api.common.ServiceException;
import cn.jiateng.api.dao.UserDao;
import cn.jiateng.api.services.AuthService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;


@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
class AuthServiceImplTest {

    @Autowired
    private AuthService authService;
    @Autowired
    private UserDao userDao;

    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void signup() throws ServiceException {
        User user = new User();
        user.username = System.currentTimeMillis() + "";
        user.password = "123456";
        authService.signup(user, "123456");
        User users = userDao.findByUsername(user.username);
        assertEquals(user.username, users.username, "Test signup()");
        System.out.println(users);
    }

    @Test
    void signin() throws ServiceException {
        User user = new User();
        user.username = System.currentTimeMillis() + "";
        user.password = "123456";
        authService.signup(user, user.password);
        User user1 = authService.signin(user.username, "123456");
        assertEquals(user1.username, user.username, "Test signin()");
        User users = userDao.findByUsername(user.username);
        assertNotNull(users);
    }
}
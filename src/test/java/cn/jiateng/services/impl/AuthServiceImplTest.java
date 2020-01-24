package cn.jiateng.services.impl;

import cn.jiateng.Model.User;
import cn.jiateng.common.ServiceException;
import cn.jiateng.dao.UserDao;
import cn.jiateng.services.AuthService;
import cn.jiateng.services.UserService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;


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
        List<User> users = userDao.findByUsername(user.username);
        assertEquals(user.username, users.get(0).username, "Test signup()");
        System.out.println(users.get(0));
    }

    @Test
    void signin() throws ServiceException {
        User user = new User();
        user.username = System.currentTimeMillis() + "";
        user.password = "123456";
        authService.signup(user, user.password);
        System.out.println(user);
        User user1 = authService.signin(user.username, "123456");
        assertEquals(user1.username, user.username, "Test signin()");
        List<User> users = userDao.findByUsername(user.username);
        assertEquals(1, users.size(), "Test signin()");
    }
}
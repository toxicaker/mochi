package cn.jiateng.api.server.services.impl;

import cn.jiateng.api.Model.User;
import cn.jiateng.api.common.MyConst;
import cn.jiateng.api.common.ServiceException;
import cn.jiateng.api.dao.FriendDao;
import cn.jiateng.api.dao.FriendRequestDao;
import cn.jiateng.api.dao.UserDao;
import cn.jiateng.api.services.AuthService;
import cn.jiateng.api.services.UserService;
import cn.jiateng.api.utils.RedisUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.jupiter.api.Assertions.*;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
class UserServiceImplTest {

    @Autowired
    private AuthService authService;

    @Autowired
    private FriendDao friendDao;

    @Autowired
    private UserService userService;

    @Autowired
    private UserDao userDao;

    @Autowired
    private FriendRequestDao friendRequestDao;

    @Autowired
    private RedisUtil redisUtil;

    @BeforeEach
    void setUp() throws ServiceException {

    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void listFriends() {
        User user1 = userDao.findByUsername("username1");
        User user2 = userDao.findByUsername("username2");
        System.out.println(userService.listFriends(user1.id));
        System.out.println(userService.listFriends(user2.id));
        System.out.println(redisUtil.setGet(MyConst.redisKeyFriends(user1.id)));
    }

    @Test
    void removeFriend() {
        User user1 = userDao.findByUsername("username1");
        User user2 = userDao.findByUsername("username2");
        userService.removeFriend(user1.id, user2.id);
        System.out.println(userService.listFriends(user1.id));
        System.out.println(userService.listFriends(user2.id));
    }

    @Test
    void listFriendIds() {
        User user1 = userDao.findByUsername("username1");
        User user2 = userDao.findByUsername("username2");
        System.out.println(userService.listFriendIds(user1.id));
        System.out.println(userService.listFriendIds(user2.id));
    }

    @Test
    void requestFriend() throws ServiceException {
        User user1 = userDao.findByUsername("username1");
        User user2 = userDao.findByUsername("username2");
        userService.requestFriend(user1.id, user2.id, "e");
        System.out.println(friendRequestDao.findByRequesterIdAndRequesteeId(user1.id, user2.id));
    }

    @Test
    void declineFriendRequest() throws ServiceException {
        User user1 = userDao.findByUsername("username1");
        User user2 = userDao.findByUsername("username2");
        userService.declineFriendRequest(user1.id, user2.id);
    }

    @Test
    void acceptFriendRequest() throws ServiceException {
        User user1 = userDao.findByUsername("username1");
        User user2 = userDao.findByUsername("username2");
        userService.acceptFriendRequest(user1.id, user2.id);
    }

    @Test
    void listFriendRequests() {
        User user1 = userDao.findByUsername("username1");
        User user2 = userDao.findByUsername("username2");
        System.out.println(userService.listFriendRequests(user1.id, true));
        System.out.println(userService.listFriendRequests(user2.id, false));
    }
}
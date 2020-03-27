package cn.toxicaker.api.service.impl;

import cn.toxicaker.api.dao.UserDao;
import cn.toxicaker.api.model.User;
import cn.toxicaker.api.service.AuthService;
import cn.toxicaker.common.MyConst;
import cn.toxicaker.common.ServiceException;
import cn.toxicaker.common.util.RedisUtil;
import cn.toxicaker.common.util.TokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;


@Service
public class AuthServiceImpl implements AuthService {

    private final UserDao userDao;

    private static final int TIME = 8 * 60 * 1000; // 8 hours

    @Autowired
    public AuthServiceImpl(UserDao userDao) {
        this.userDao = userDao;
    }


    @Override
    public User signup(User user, String confirmPassword) throws ServiceException {
        User existUser = userDao.findByUsername(user.username);
        if (existUser != null) {
            throw new ServiceException("user " + user.username + " already exists");
        }
        if (!user.password.equals(confirmPassword)) {
            throw new ServiceException("please confirm the password");
        }
        user.password = TokenUtil.getMd5(user.password);
        if (user.nickname == null || "".equals(user.nickname)) {
            user.nickname = user.username;
        }
        user.createTime = System.currentTimeMillis();
        user.lastLoginTime = System.currentTimeMillis();
        user.token = TokenUtil.createToken(user.id);
        user = userDao.save(user);
        createSession(user.id, user.token);
        return user;
    }

    @Override
    public User signin(String username, String password) throws ServiceException {
        User existUser = userDao.findByUsernameAndPassword(username, TokenUtil.getMd5(password));
        if (existUser == null) {
            throw new ServiceException("username or password is incorrect");
        }
        existUser.lastLoginTime = System.currentTimeMillis();
        existUser.token = TokenUtil.createToken(existUser.id);
        existUser = userDao.save(existUser);
        createSession(existUser.id, existUser.token);
        return existUser;
    }

    @Override
    public void createSession(String userId, String token) {
        Map<String, String> session = new HashMap<>();
        session.put("userId", userId);
        session.put("token", token);
        RedisUtil.redisCli.hset(MyConst.redisKeySession(userId), session);
        RedisUtil.redisCli.expire(MyConst.redisKeySession(userId), TIME);
    }

    @Override
    public boolean checkAndUpdateSession(String token) {
        if (token == null || "".equals(token)) {
            return false;
        }
        String userId = TokenUtil.getUserIdByToken(token);
        if (userId == null || "".equals(userId)) {
            return false;
        }
        String existing = RedisUtil.redisCli.hget(MyConst.redisKeySession(userId), "token");
        if (existing.equals(token)) {
            RedisUtil.redisCli.expire(MyConst.redisKeySession(userId), TIME);
            return true;
        }
        return false;
    }
}

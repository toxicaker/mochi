package cn.jiateng.api.services.impl;

import cn.jiateng.api.Model.User;
import cn.jiateng.api.common.ServiceException;
import cn.jiateng.api.dao.UserDao;
import cn.jiateng.api.services.AuthService;
import cn.jiateng.api.utils.EncrypUtil;
import cn.jiateng.api.utils.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class AuthServiceImpl implements AuthService {

    private final UserDao userDao;

    private final RedisUtil redisUtil;

    @Autowired
    public AuthServiceImpl(UserDao userDao, RedisUtil redisUtil) {
        this.userDao = userDao;
        this.redisUtil = redisUtil;
    }


    @Override
    public User signup(User user, String confirmPassword) throws ServiceException {
        User existUser = userDao.findByUsername(user.username);
        if (existUser != null) {
            throw new ServiceException("user " + user.id + " already exists");
        }
        if (!user.password.equals(confirmPassword)) {
            throw new ServiceException("please confirm the password");
        }
        user.password = EncrypUtil.getMd5(user.password);
        if (user.nickname == null || "".equals(user.nickname)) {
            user.nickname = user.username;
        }
        user.createTime = System.currentTimeMillis();
        user.lastLoginTime = System.currentTimeMillis();
        userDao.save(user);
        return user;
    }

    @Override
    public User signin(String username, String password) throws ServiceException {
        User existUser = userDao.findByUsernameAndPassword(username, EncrypUtil.getMd5(password));
        if (existUser == null) {
            throw new ServiceException("username or password is incorrect");
        }
//        if(redisUtil.hasKey(MyConst.redisKeySession(existUser.id))){
//            // Todo: offline message
//        }
        existUser.lastLoginTime = System.currentTimeMillis();
        existUser = userDao.save(existUser);
        return existUser;
    }
}

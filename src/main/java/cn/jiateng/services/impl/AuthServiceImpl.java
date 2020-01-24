package cn.jiateng.services.impl;

import cn.jiateng.Model.User;
import cn.jiateng.common.ServiceException;
import cn.jiateng.dao.UserDao;
import cn.jiateng.services.AuthService;
import cn.jiateng.utils.EncrypUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.List;

@Service
public class AuthServiceImpl implements AuthService {

    private final UserDao userDao;

    @Autowired
    public AuthServiceImpl(UserDao userDao) {
        this.userDao = userDao;
    }

    @Override
    public boolean signup(User user, String confirmPassword) throws ServiceException {
        List<User> existUser = userDao.findByUsername(user.username);
        if (existUser.size() != 0) {
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
        return true;
    }

    @Override
    public User signin(String username, String password) throws ServiceException {
        List<User> existUser = userDao.findByUsernameAndPassword(username, EncrypUtil.getMd5(password));
        System.out.println("username = " + username + " password = " + EncrypUtil.getMd5(password));
        if (existUser.size() == 0) {
            throw new ServiceException("username or password is incorrect");
        }
        User user = existUser.get(0);
        user.lastLoginTime = System.currentTimeMillis();
        userDao.save(user);
        return user;
    }
}

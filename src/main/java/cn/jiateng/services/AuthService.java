package cn.jiateng.services;

import cn.jiateng.Model.User;
import cn.jiateng.common.ServiceException;

public interface AuthService {

    boolean signup(User user, String confirmPassword) throws ServiceException;

    User signin(String username, String password) throws ServiceException;

}

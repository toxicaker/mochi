package cn.jiateng.api.services;

import cn.jiateng.api.Model.User;
import cn.jiateng.api.common.ServiceException;

public interface AuthService {

    User signup(User user, String confirmPassword) throws ServiceException;

    User signin(String username, String password) throws ServiceException;

}

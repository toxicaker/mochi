package cn.toxicaker.api.service;

import cn.toxicaker.api.model.User;
import cn.toxicaker.common.ServiceException;

public interface AuthService {

    User signup(User user, String confirmPassword) throws ServiceException;

    User signin(String username, String password) throws ServiceException;

    void createSession(String userId, String token);

    boolean checkAndUpdateSession(String userId);

}

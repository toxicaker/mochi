package cn.jiateng.api.controllers;

import cn.jiateng.api.Model.User;
import cn.jiateng.api.common.JsonResp;
import cn.jiateng.api.common.ServiceException;
import cn.jiateng.api.services.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping(value = "/api/auth", produces = "application/json")
public class AuthController {

    private final AuthService authService;

    @Autowired
    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public JsonResp login(@RequestParam String username, @RequestParam String password) throws ServiceException {
        User user = authService.signin(username, password);
        Map<String, Object> data = Map.of("id", user.id,
                "username", user.username,
                "nickname", user.nickname,
                "lastLogin", user.lastLoginTime,
                "createTime", user.createTime);
        return new JsonResp(data);
    }

    @PostMapping("/signup")
    public JsonResp signup(@RequestParam String username, @RequestParam String password1, @RequestParam String password2) throws ServiceException {
        User user  = new User();
        user.username = username;
        user.password = password1;
        user = authService.signup(user, password2);
        Map<String, Object> data = Map.of("id", user.id,
                "username", user.username,
                "nickname", user.nickname,
                "lastLogin", user.lastLoginTime,
                "createTime", user.createTime);
        return new JsonResp(data);
    }

}

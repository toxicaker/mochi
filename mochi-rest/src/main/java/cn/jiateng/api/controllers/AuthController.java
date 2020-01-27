package cn.jiateng.api.controllers;

import cn.jiateng.api.Model.User;
import cn.jiateng.api.common.JsonResp;
import cn.jiateng.api.common.MyConfig;
import cn.jiateng.api.common.ServiceException;
import cn.jiateng.api.data.LoginForm;
import cn.jiateng.api.services.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
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
    public JsonResp login(@RequestBody LoginForm loginForm) throws ServiceException {
        User user = authService.signin(loginForm.username, loginForm.password);
        return new JsonResp(packUserData(user));
    }

    @PostMapping("/signup")
    public JsonResp signup(@RequestParam String username, @RequestParam String password1, @RequestParam String password2) throws ServiceException {
        User user = new User();
        user.username = username;
        user.password = password1;
        user = authService.signup(user, password2);
        return new JsonResp(packUserData(user));
    }

    private Map<String, Object> packUserData(User user) {
        Map<String, Object> data = new HashMap<>();
        data.put("id", user.id);
        data.put("username", user.username);
        data.put("nickname", user.nickname);
        data.put("lastLogin", user.lastLoginTime);
        data.put("createTime", user.createTime);
        return data;
    }

}

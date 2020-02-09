package cn.jiateng.api.controllers;

import cn.jiateng.api.Model.User;
import cn.jiateng.api.common.JsonResp;
import cn.jiateng.api.common.ServiceException;
import cn.jiateng.api.data.LoginForm;
import cn.jiateng.api.data.SignupForm;
import cn.jiateng.api.services.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/api/auth", produces = "application/json")
public class AuthController {

    private final AuthService authService;

    @Autowired
    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/sign-in")
    public JsonResp login(@RequestBody LoginForm loginForm) throws ServiceException {
        User user = authService.signin(loginForm.username, loginForm.password);
        return new JsonResp(user);
    }

    @PostMapping("/sign-up")
    public JsonResp signup(@RequestBody SignupForm signupForm) throws ServiceException {
        User user = new User();
        user.username = signupForm.username;
        user.password = signupForm.password1;
        user = authService.signup(user, signupForm.password2);
        return new JsonResp(user);
    }

}

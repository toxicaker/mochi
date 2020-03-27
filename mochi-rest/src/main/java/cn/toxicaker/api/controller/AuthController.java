package cn.toxicaker.api.controller;

import cn.toxicaker.api.forms.LoginForm;
import cn.toxicaker.api.forms.SignupForm;
import cn.toxicaker.api.model.User;
import cn.toxicaker.api.service.AuthService;
import cn.toxicaker.common.JsonResp;
import cn.toxicaker.common.ServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
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
        if (StringUtils.isEmpty(loginForm.username) || StringUtils.isEmpty(loginForm.password))
            throw new ServiceException("username or password cannot be empty");
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

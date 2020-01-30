package cn.jiateng.api.controllers;

import cn.jiateng.api.Model.User;
import cn.jiateng.api.common.JsonResp;
import cn.jiateng.api.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping(value = "/api/users", produces = "application/json")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/{userId}")
    public JsonResp getUser(@PathVariable String userId) {
        User user = userService.getUserById(userId);
        return new JsonResp(user);
    }

}

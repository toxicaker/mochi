package cn.jiateng.api.controllers;

import cn.jiateng.api.Model.User;
import cn.jiateng.api.common.JsonResp;
import cn.jiateng.api.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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

//    @PostMapping("/friends/add/{userId}")
//    public JsonResp sendFriendRequest(@PathVariable String userId, @RequestParam String friendId) {
//
//    }
//
//    @PostMapping("/friends/accept/{userId}/{requestId}")
//    public JsonResp acceptFriendRequest() {
//
//    }
//
//    @DeleteMapping("/friends/requests/{userId}/{requestId}")
//    public JsonResp declineFriendRequest() {
//
//    }
//
//    @DeleteMapping("/friends/{userId}")
//    public JsonResp deleteFriend(@PathVariable String userId, @RequestParam String friendId) {
//
//    }

}

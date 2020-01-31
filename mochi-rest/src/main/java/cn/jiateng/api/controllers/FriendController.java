package cn.jiateng.api.controllers;

import cn.jiateng.api.Model.FriendRequest;
import cn.jiateng.api.Model.User;
import cn.jiateng.api.common.JsonResp;
import cn.jiateng.api.common.ServiceException;
import cn.jiateng.api.data.FriendRequestForm;
import cn.jiateng.api.services.UserService;
import cn.jiateng.api.utils.AuthUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping(value = "/api/friends", produces = "application/json")
public class FriendController {

    private final UserService userService;

    private final AuthUtil authUtil;

    @Autowired
    public FriendController(UserService userService, AuthUtil authUtil) {
        this.userService = userService;
        this.authUtil = authUtil;
    }

    @GetMapping("")
    public JsonResp listFriends() {
        Set<User> friends = userService.listFriends(authUtil.getUserId());
        return new JsonResp(friends);
    }

    @DeleteMapping("/{friendId}")
    public JsonResp deleteFriend(@PathVariable String friendId) {
        userService.removeFriend(authUtil.getUserId(), friendId);
        return new JsonResp(null);
    }

    @PostMapping("/request/send")
    public JsonResp sendFriendRequest(@RequestBody FriendRequestForm friendRequestForm) throws ServiceException {
        FriendRequest request = userService.sendFriendRequest(authUtil.getUserId(), friendRequestForm.friendId, friendRequestForm.message);
        User user = userService.getUserById(request.requesteeId);
        Map<String, Object> data = new HashMap<>();
        data.put("request", request);
        data.put("userInfo", user);
        return new JsonResp(data);
    }

    @PostMapping("/request/accept/{requestId}")
    public JsonResp acceptFriendRequest(@PathVariable String requestId) throws ServiceException {
        FriendRequest request = userService.getFriendRequestById(requestId);
        userService.acceptFriendRequest(request.requesterId, request.requesteeId);
        return new JsonResp(null);
    }

    @DeleteMapping("/request/delete/{requestId}")
    public JsonResp deleteFriendRequest(@PathVariable String requestId) throws ServiceException {
        FriendRequest request = userService.getFriendRequestById(requestId);
        userService.declineFriendRequest(request.requesterId, request.requesteeId);
        return new JsonResp(null);
    }

    @GetMapping("/request")
    public JsonResp listFriendRequests(@RequestParam int status) {
        List<FriendRequest> friendRequests;
        if (status == 0) {
            friendRequests = userService.listFriendRequests(authUtil.getUserId(), true);
        } else {
            friendRequests = userService.listFriendRequests(authUtil.getUserId(), false);
        }
        List<Map<String, Object>> res = new ArrayList<>();
        for (FriendRequest request : friendRequests) {
            Map<String, Object> data = new HashMap<>();
            data.put("request", request);
            if (status == 0) data.put("userInfo", userService.getUserById(request.requesteeId));
            else data.put("userInfo", userService.getUserById(request.requesterId));
            res.add(data);
        }
        return new JsonResp(res);
    }
}

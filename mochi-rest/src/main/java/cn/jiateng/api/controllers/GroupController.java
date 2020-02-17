package cn.jiateng.api.controllers;

import cn.jiateng.api.Model.Group;
import cn.jiateng.api.Model.User;
import cn.jiateng.common.JsonResp;
import cn.jiateng.api.common.ServiceException;
import cn.jiateng.api.data.CreateGroupForm;
import cn.jiateng.api.security.SkipAuth;
import cn.jiateng.api.services.GroupService;
import cn.jiateng.api.services.UserService;
import cn.jiateng.api.utils.AuthUtil;
import cn.jiateng.api.utils.MapUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.rmi.ServerException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(value = "/api/groups", produces = "application/json")
public class GroupController {

    private final GroupService groupService;

    private final UserService userService;

    private final AuthUtil authUtil;

    @Autowired
    public GroupController(GroupService groupService, UserService userService, AuthUtil authUtil) {
        this.groupService = groupService;
        this.userService = userService;
        this.authUtil = authUtil;
    }

    @GetMapping("/{groupId}")
    public JsonResp getGroup(@PathVariable String groupId) {
        Group group = groupService.getGroupById(groupId);
        return new JsonResp(group);
    }

    @GetMapping("/list/{userId}")
    public JsonResp listGroups(@PathVariable String userId) throws ServiceException, IllegalAccessException {
        List<Group> groups = groupService.listGroups(userId);
        List<Map<String, Object>> res = new ArrayList<>();
        for (Group group : groups) {
            Map<String, Object> map = MapUtil.obj2Map(group);
            map.put("lastMessage", "");
            map.put("lastMessageTime", 0);
            res.add(map);
        }
        return new JsonResp(res);
    }

    @GetMapping("/members/{groupId}")
    @SkipAuth
    public JsonResp listGroupMembers(@PathVariable String groupId) throws ServiceException {
        List<User> members = groupService.listGroupMembers(groupId);
        return new JsonResp(members);
    }

    @PostMapping("/join/{groupId}")
    public JsonResp joinGroup(@PathVariable String groupId) throws ServerException {
        groupService.joinGroup(authUtil.getUserId(), groupId);
        return new JsonResp(null);
    }

    @PostMapping("/leave/{groupId}")
    public JsonResp leaveGroup(@PathVariable String groupId) throws ServerException {
        groupService.leaveGroup(authUtil.getUserId(), groupId);
        return new JsonResp(null);
    }

    @PostMapping("/create")
    public JsonResp createGroup(@RequestBody CreateGroupForm createGroupForm) {
        if (!createGroupForm.userIds.contains(authUtil.getUserId())) createGroupForm.userIds.add(authUtil.getUserId());
        Group group = groupService.createGroup(createGroupForm.name, createGroupForm.userIds);
        return new JsonResp(group);
    }

}

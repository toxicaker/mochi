package cn.jiateng.api.controllers;

import cn.jiateng.api.Model.Group;
import cn.jiateng.api.Model.User;
import cn.jiateng.api.Model.UserGroup;
import cn.jiateng.api.common.JsonResp;
import cn.jiateng.api.common.ServiceException;
import cn.jiateng.api.data.CreateGroupForm;
import cn.jiateng.api.services.GroupService;
import cn.jiateng.api.services.UserService;
import cn.jiateng.api.utils.AuthUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.rmi.ServerException;
import java.util.List;

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
    public JsonResp listGroups(@PathVariable String userId) throws ServiceException {
        List<Group> groups = groupService.listGroups(userId);
        return new JsonResp(groups);
    }

    @GetMapping("/members/{groupId}")
    public JsonResp listGroupMembers(@PathVariable String groupId) throws ServiceException {
        List<User> members = groupService.listGroupMembers(groupId);
        return new JsonResp(members);
    }

    @PostMapping("/join/{groupId}")
    public JsonResp joinGroup(@PathVariable String groupId) throws ServerException {
        UserGroup userGroup = groupService.joinGroup(authUtil.getUserId(), groupId);
        Group group = groupService.getGroupById(userGroup.groupId);
        return new JsonResp(group);
    }

    @PostMapping("/leave/{groupId}")
    public JsonResp leaveGroup(@PathVariable String groupId) throws ServerException {
        UserGroup userGroup = groupService.leaveGroup(authUtil.getUserId(), groupId);
        Group group = groupService.getGroupById(userGroup.groupId);
        return new JsonResp(group);
    }

    @PostMapping("/create")
    public JsonResp createGroup(@RequestBody CreateGroupForm createGroupForm) {
        if(!createGroupForm.userIds.contains(authUtil.getUserId())) createGroupForm.userIds.add(authUtil.getUserId());
        Group group = groupService.createGroup(createGroupForm.name, createGroupForm.userIds);
        return new JsonResp(group);
    }

}

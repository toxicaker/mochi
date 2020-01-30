package cn.jiateng.api.controllers;

import cn.jiateng.api.Model.Group;
import cn.jiateng.api.Model.User;
import cn.jiateng.api.common.JsonResp;
import cn.jiateng.api.services.GroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;
@RestController
@RequestMapping(value = "/api/groups", produces = "application/json")
public class GroupController {

    private final GroupService groupService;

    @Autowired
    public GroupController(GroupService groupService) {
        this.groupService = groupService;
    }

    @GetMapping("/{groupId}")
    public JsonResp getGroup(@PathVariable String groupId) {
        Group group = groupService.getGroupById(groupId);
        return new JsonResp(group);
    }

    @GetMapping("/members/{groupId}")
    public JsonResp listGroupMembers(@PathVariable String groupId){
        List<User> members = groupService.listGroupMembers(groupId);
        return new JsonResp(members);
    }
}

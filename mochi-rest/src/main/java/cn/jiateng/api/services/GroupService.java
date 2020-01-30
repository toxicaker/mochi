package cn.jiateng.api.services;

import cn.jiateng.api.Model.Group;
import cn.jiateng.api.Model.User;

import java.util.List;

public interface GroupService {

    Group getGroupById(String groupId);

    List<User> listGroupMembers(String groupId);
}

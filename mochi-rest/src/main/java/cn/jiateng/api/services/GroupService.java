package cn.jiateng.api.services;

import cn.jiateng.api.Model.Group;
import cn.jiateng.api.Model.User;
import cn.jiateng.api.Model.UserGroup;
import cn.jiateng.api.common.ServiceException;

import java.rmi.ServerException;
import java.util.List;

public interface GroupService {

    Group getGroupById(String groupId);

    List<User> listGroupMembers(String groupId) throws ServiceException;

    Group createGroup(String name, List<String> userIds);

    UserGroup joinGroup(String userId, String groupId) throws ServerException;

    UserGroup leaveGroup(String userId, String groupId) throws ServerException;

    List<Group> listGroups(String userId) throws ServiceException;
}

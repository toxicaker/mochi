package cn.jiateng.api.services.impl;

import cn.jiateng.api.Model.Group;
import cn.jiateng.api.Model.User;
import cn.jiateng.api.Model.UserGroup;
import cn.jiateng.api.dao.GroupDao;
import cn.jiateng.api.dao.UserDao;
import cn.jiateng.api.dao.UserGroupDao;
import cn.jiateng.api.services.GroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class GroupServiceImpl implements GroupService {


    private final GroupDao groupDao;

    private final UserGroupDao userGroupDao;

    private final UserDao userDao;

    @Autowired
    public GroupServiceImpl(GroupDao groupDao, UserGroupDao userGroupDao, UserDao userDao) {
        this.groupDao = groupDao;
        this.userGroupDao = userGroupDao;
        this.userDao = userDao;
    }

    @Override
    public Group getGroupById(String groupId) {
        return groupDao.findById(groupId).get();
    }

    @Override
    public List<User> listGroupMembers(String groupId) {
        List<UserGroup> userGroups = userGroupDao.findByGroupId(groupId);
        List<User> res = new ArrayList<>();
        for (UserGroup userGroup : userGroups) {
            String userId = userGroup.userId;
            Optional<User> user = userDao.findById(userId);
            user.ifPresent(res::add);
        }
        return res;
    }
}

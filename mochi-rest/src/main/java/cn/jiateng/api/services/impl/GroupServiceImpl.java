package cn.jiateng.api.services.impl;

import cn.jiateng.api.Model.Group;
import cn.jiateng.api.Model.User;
import cn.jiateng.api.Model.UserGroup;
import cn.jiateng.api.common.MyConst;
import cn.jiateng.api.common.ServiceException;
import cn.jiateng.api.dao.GroupDao;
import cn.jiateng.api.dao.UserDao;
import cn.jiateng.api.dao.UserGroupDao;
import cn.jiateng.api.services.GroupService;
import cn.jiateng.api.utils.RedisUtil;
import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.rmi.ServerException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class GroupServiceImpl implements GroupService {


    private final GroupDao groupDao;

    private final UserGroupDao userGroupDao;

    private final UserDao userDao;

    private final RedisUtil redisUtil;

    private Gson gson = new Gson();

    @Autowired
    public GroupServiceImpl(GroupDao groupDao, UserGroupDao userGroupDao, UserDao userDao, RedisUtil redisUtil) {
        this.groupDao = groupDao;
        this.userGroupDao = userGroupDao;
        this.userDao = userDao;
        this.redisUtil = redisUtil;
    }

    @Override
    public Group getGroupById(String groupId) {
        return groupDao.findById(groupId).get();
    }

    @Override
    public List<User> listGroupMembers(String groupId) throws ServiceException {
        if (!groupDao.findById(groupId).isPresent())
            throw new ServiceException("group " + groupId + " does not exist");
        // read from redis
        List<String> redisVal = redisUtil.listGet(MyConst.redisKeyGroupMembers(groupId));
        List<User> res = new ArrayList<>();
        if (redisVal.size() != 0) {
            for (String jsonStr : redisVal) {
                User user = gson.fromJson(jsonStr, User.class);
                res.add(user);
            }
            return res;
        }
        // read from mongodb
        List<UserGroup> userGroups = userGroupDao.findByGroupId(groupId);
        for (UserGroup userGroup : userGroups) {
            String userId = userGroup.userId;
            Optional<User> user = userDao.findById(userId);
            if (user.isPresent()) {
                res.add(user.get());
                // save to redis
                String jsonStr = gson.toJson(user.get());
                redisUtil.listLAdd(MyConst.redisKeyGroupMembers(groupId), jsonStr);
            }
        }
        return res;
    }

    @Override
    public Group createGroup(String name, List<String> userIds) {
        Group group = new Group();
        group.name = name;
        group.createTime = System.currentTimeMillis();
        group = groupDao.save(group);
        for (String uId : userIds) {
            UserGroup userGroup = new UserGroup();
            Optional<User> op = userDao.findById(uId);
            if (op.isPresent()) {
                userGroup.userId = uId;
                userGroup.groupId = group.id;
                userGroup.createTime = System.currentTimeMillis();
                userGroupDao.save(userGroup);
                // save to redis
                String jsonStr = gson.toJson(op.get());
                redisUtil.listLAdd(MyConst.redisKeyGroupMembers(group.id), jsonStr);
            }
            redisUtil.removeKey(MyConst.redisKeyGroups(uId));
        }
        return group;
    }

    @Override
    public UserGroup joinGroup(String userId, String groupId) throws ServerException {
        checkUserAndGroup(userId, groupId);
        UserGroup userGroup = userGroupDao.findByUserIdAndGroupId(userId, groupId);
        if (userGroup != null) throw new ServerException("user " + userId + " is already in the group " + groupId);
        userGroup = new UserGroup();
        userGroup.userId = userId;
        userGroup.groupId = groupId;
        userGroup.createTime = System.currentTimeMillis();
        userGroup = userGroupDao.save(userGroup);
        // remove redis key
        redisUtil.removeKey(MyConst.redisKeyGroupMembers(groupId));
        redisUtil.removeKey(MyConst.redisKeyGroups(userId));
        return userGroup;
    }

    @Override
    public UserGroup leaveGroup(String userId, String groupId) throws ServerException {
        checkUserAndGroup(userId, groupId);
        UserGroup userGroup = userGroupDao.findByUserIdAndGroupId(userId, groupId);
        if (userGroup == null) throw new ServerException("user " + userId + " not in the group " + groupId);
        userGroupDao.deleteByUserIdAndGroupId(userId, groupId);
        // remove redis key
        redisUtil.removeKey(MyConst.redisKeyGroupMembers(groupId));
        redisUtil.removeKey(MyConst.redisKeyGroups(userId));
        return userGroup;
    }

    @Override
    public List<Group> listGroups(String userId) throws ServiceException {
        Optional<User> user = userDao.findById(userId);
        if (!user.isPresent()) throw new ServiceException("user " + userId + " does not exist");
        List<String> redisVal = redisUtil.listGet(MyConst.redisKeyGroups(userId));
        List<Group> groups = new ArrayList<>();
        if (redisVal.size() != 0) {
            for (String jsonStr : redisVal) {
                Group group = gson.fromJson(jsonStr, Group.class);
                groups.add(group);
            }
            return groups;
        }
        List<UserGroup> userGroups = userGroupDao.findByUserId(userId);
        for (UserGroup userGroup : userGroups) {
            Optional<Group> group = groupDao.findById(userGroup.groupId);
            if (group.isPresent()) {
                groups.add(group.get());
                String jsonStr = gson.toJson(group.get());
                redisUtil.listLAdd(MyConst.redisKeyGroups(userId), jsonStr);
            }
        }
        return groups;
    }

    private void checkUserAndGroup(String userId, String groupId) throws ServerException {
        Optional<Group> group = groupDao.findById(groupId);
        if (!group.isPresent()) throw new ServerException("group " + groupId + " does not exist");
        Optional<User> user = userDao.findById(userId);
        if (!user.isPresent()) throw new ServerException("user " + userId + " does not exist");
    }
}

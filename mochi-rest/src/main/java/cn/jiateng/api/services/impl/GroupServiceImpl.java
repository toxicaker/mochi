package cn.jiateng.api.services.impl;

import cn.jiateng.api.Model.Group;
import cn.jiateng.api.Model.User;
import cn.jiateng.api.common.MyConst;
import cn.jiateng.api.common.ServiceException;
import cn.jiateng.api.dao.GroupDao;
import cn.jiateng.api.dao.UserDao;
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

    private final UserDao userDao;

    private final RedisUtil redisUtil;

    private Gson gson = new Gson();

    @Autowired
    public GroupServiceImpl(GroupDao groupDao, UserDao userDao, RedisUtil redisUtil) {
        this.groupDao = groupDao;
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
        List<String> userIds = groupDao.findById(groupId).get().userIds;
        for (String userId : userIds) {
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
        group.userIds = userIds;
        group = groupDao.save(group);
        for (String uId : userIds) {
            Optional<User> user = userDao.findById(uId);
            if(user.isPresent()){
                user.get().groupIds.add(group.id);
                redisUtil.removeKey(MyConst.redisKeyGroups(uId));
            }
        }
        return group;
    }

    @Override
    public void joinGroup(String userId, String groupId) throws ServerException {
        checkUserAndGroup(userId, groupId);
        Optional<User> user = userDao.findById(userId);
        if(user.get().groupIds.contains(groupId)) throw new ServerException("user " + userId + " is already in the group " + groupId);
        user.get().groupIds.add(groupId);
        // remove redis key
        redisUtil.removeKey(MyConst.redisKeyGroupMembers(groupId));
        redisUtil.removeKey(MyConst.redisKeyGroups(userId));
    }

    @Override
    public void leaveGroup(String userId, String groupId) throws ServerException {
        checkUserAndGroup(userId, groupId);
        Optional<User> user = userDao.findById(userId);
        user.get().groupIds.remove(groupId);
        // remove redis key
        redisUtil.removeKey(MyConst.redisKeyGroupMembers(groupId));
        redisUtil.removeKey(MyConst.redisKeyGroups(userId));
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
        for (String groupId: user.get().groupIds) {
            Optional<Group> group = groupDao.findById(groupId);
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

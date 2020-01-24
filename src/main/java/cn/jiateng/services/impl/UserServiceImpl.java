package cn.jiateng.services.impl;

import cn.jiateng.Model.Friend;
import cn.jiateng.Model.FriendRequest;
import cn.jiateng.Model.User;
import cn.jiateng.common.MyConst;
import cn.jiateng.common.ServiceException;
import cn.jiateng.dao.FriendDao;
import cn.jiateng.dao.FriendRequestDao;
import cn.jiateng.dao.UserDao;
import cn.jiateng.services.UserService;
import cn.jiateng.utils.RedisUtil;
import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class UserServiceImpl implements UserService {

    private final UserDao userDao;

    private final FriendDao friendDao;

    private final FriendRequestDao friendRequestDao;

    private final RedisUtil redisUtil;

    private final Gson gson;

    @Autowired
    public UserServiceImpl(UserDao userDao, RedisUtil redisUtil, Gson gson, FriendDao friendDao, FriendRequestDao friendRequestDao) {
        this.userDao = userDao;
        this.redisUtil = redisUtil;
        this.gson = gson;
        this.friendDao = friendDao;
        this.friendRequestDao = friendRequestDao;
    }

    @Override
    public Set<User> listFriends(String userId) {
        Set<User> res = new HashSet<>();
        Set<String> friends = redisUtil.setGet(MyConst.redisKeyFriends(userId));
        if (friends.size() == 0) {
            // read from mongodb
            List<Friend> relationships = friendDao.findAllByUserId1(userId);
            for (Friend friend : relationships) {
                Optional<User> user = userDao.findById(friend.userId2);
                user.ifPresent(res::add);
            }
            // set the result to redis
            for (User user : res) {
                redisUtil.setAdd(MyConst.redisKeyFriends(userId), gson.toJson(user));
            }
        } else {
            // read from redis
            for (String json : friends) {
                User user = gson.fromJson(json, User.class);
                res.add(user);
            }
        }
        return res;
    }

    @Override
    public Set<String> listFriendIds(String userId) {
        Set<String> res = redisUtil.setGet(MyConst.redisKeyFriends(userId));
        if (res.size() == 0) {
            // read from mongodb
            List<Friend> relationships = friendDao.findAllByUserId1(userId);
            for (Friend friend : relationships) {
                Optional<User> user = userDao.findById(friend.userId2);
                user.ifPresent(i -> res.add(i.id));
            }
            // set the result to redis
            for (String user : res) {
                redisUtil.setAdd(MyConst.redisKeyFriends(userId), user);
            }
        } else {
            Set<String> friendIds = new HashSet<>();
            // read from redis
            for (String json : res) {
                User user = gson.fromJson(json, User.class);
                friendIds.add(user.id);
            }
            return friendIds;
        }
        return res;
    }

    @Override
    public boolean requestFriend(String requesterId, String requesteeId, String message) throws ServiceException {
        if (checkUser(requesteeId) || checkUser(requesteeId)) {
            throw new ServiceException("requester or requestee doesn't exist");
        }
        FriendRequest friendRequest = friendRequestDao.findByRequesterIdAndRequesteeId(requesterId, requesteeId);
        if (friendRequest != null) {
            throw new ServiceException("friend request already sent");
        }
        Set<String> friends = this.listFriendIds(requesterId);
        if (friends.contains(requesteeId)) {
            throw new ServiceException("they are already friends");
        }
        FriendRequest request = new FriendRequest();
        request.requesterId = requesterId;
        request.requesteeId = requesteeId;
        request.message = message;
        request.createTime = System.currentTimeMillis();
        friendRequestDao.save(request);
        // Todo: nofity requestee
        return true;
    }

    @Override
    public boolean declineFriendRequest(String requesterId, String requesteeId) throws ServiceException {
        if (checkUser(requesteeId) || checkUser(requesteeId)) {
            throw new ServiceException("requester or requestee doesn't exist");
        }
        FriendRequest friendRequest = friendRequestDao.findByRequesterIdAndRequesteeId(requesterId, requesteeId);
        if (friendRequest == null) {
            throw new ServiceException("friend request does not exist");
        }
        friendRequest.status = -1;
        friendRequestDao.save(friendRequest);
        return true;
    }

    @Override
    public boolean acceptFriendRequest(String requesterId, String requesteeId) throws ServiceException {
        Optional<User> requester = userDao.findById(requesterId);
        Optional<User> requestee = userDao.findById(requesteeId);
        if (!requester.isPresent() || !requestee.isPresent()) {
            throw new ServiceException("requester or requestee doesn't exist");
        }
        FriendRequest friendRequest = friendRequestDao.findByRequesterIdAndRequesteeId(requesterId, requesteeId);
        if (friendRequest == null) {
            throw new ServiceException("friend request does not exist");
        }
        // remove redis data
        if (!redisUtil.removeKey(MyConst.redisKeyFriends(requesterId))) {
            throw new ServiceException("failed to accept friend request, reason: redis failure");
        }
        if (!redisUtil.removeKey(MyConst.redisKeyFriends(requesteeId))) {
            throw new ServiceException("failed to accept friend request, reason: redis failure");
        }

        // update friends
        if (!listFriendIds(requesterId).contains(requesteeId)) {
            Set<User> requesterFriends = listFriends(requesterId);
            requesterFriends.add(userDao.findById(requesteeId).get());
        }
        if (!listFriendIds(requesterId).contains(requesterId)) {
            Set<User> requesteeFriends = listFriends(requesteeId);
            requesteeFriends.add(userDao.findById(requesterId).get());
        }

        // update friend request
        friendRequest.status = 1;
        friendRequestDao.save(friendRequest);
        return true;
    }

    @Override
    public List<FriendRequest> listFriendRequests(String userId) {
        return friendRequestDao.findAllByRequesterId(userId);
    }


    private boolean checkUser(String userId) {
        return !userDao.findById(userId).isPresent();
    }
}

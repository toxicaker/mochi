package cn.jiateng.api.services.impl;

import cn.jiateng.api.Model.*;
import cn.jiateng.common.MyConst;
import cn.jiateng.api.common.ServiceException;
import cn.jiateng.api.dao.*;
import cn.jiateng.api.services.UserService;
import cn.jiateng.api.utils.RedisUtil;
import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class UserServiceImpl implements UserService {

    private final UserDao userDao;

    private final FriendRequestDao friendRequestDao;

    private final RedisUtil redisUtil;

    private final Gson gson;

    @Autowired
    public UserServiceImpl(UserDao userDao, RedisUtil redisUtil, Gson gson, FriendRequestDao friendRequestDao) {
        this.userDao = userDao;
        this.redisUtil = redisUtil;
        this.gson = gson;
        this.friendRequestDao = friendRequestDao;
    }

    @Override
    public User getUserById(String userId) {
        return userDao.findById(userId).get();
    }

    @Override
    public FriendRequest getFriendRequestById(String requestId) {
        return friendRequestDao.findById(requestId).get();
    }

    @Override
    public Set<User> listFriends(String userId) throws ServiceException {
        Set<User> res = new HashSet<>();
        Set<String> friends = redisUtil.setGet(MyConst.redisKeyFriends(userId));
        if (friends.size() == 0) {
            // read from mongodb
            Optional<User> user = userDao.findById(userId);
            if (!user.isPresent()) {
                throw new ServiceException("user " + userId + " does not exist");
            }
            List<String> friendIds = user.get().friendIds;
            for (String friendId : friendIds) {
                Optional<User> friend = userDao.findById(friendId);
                friend.ifPresent(res::add);
            }
            // set the result to redis
            for (User u : res) {
                redisUtil.setAdd(MyConst.redisKeyFriends(userId), gson.toJson(u));
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
        Optional<User> user = userDao.findById(userId);
        Set<String> res = new HashSet<>();
        if (user.isPresent()) {
            List<String> friendIds = user.get().friendIds;
            res.addAll(friendIds);
        }
        return res;
    }

    @Override
    public void removeFriend(String userId, String friendId) {
        // Todo: transaction?
        // remove redis key
        redisUtil.removeKey(MyConst.redisKeyFriends(userId));
        redisUtil.removeKey(MyConst.redisKeyFriends(friendId));
        Optional<User> user = userDao.findById(userId);
        Optional<User> friend = userDao.findById(friendId);
        user.ifPresent(value -> value.friendIds.remove(friendId));
        user.ifPresent(userDao::save);
        friend.ifPresent(value -> value.friendIds.remove(userId));
        friend.ifPresent(userDao::save);
    }

    @Override
    public FriendRequest sendFriendRequest(String requesterId, String requesteeId, String message) throws ServiceException {
        if (requesterId.equals(requesteeId)) {
            throw new ServiceException("you cannot send friend request to yourself");
        }
        if (checkUser(requesteeId) || checkUser(requesteeId)) {
            throw new ServiceException("requester or requestee doesn't exist");
        }
        FriendRequest friendRequest = friendRequestDao.findByRequesterIdAndRequesteeId(requesterId, requesteeId);
        if (friendRequest != null && friendRequest.status == 0) {
            throw new ServiceException("friend request already sent");
        }
        Set<String> friends = this.listFriendIds(requesterId);
        if (friends.contains(requesteeId)) {
            throw new ServiceException("they are already friends");
        }
        FriendRequest request = friendRequest;
        if (request == null) {
            request = new FriendRequest();
        }
        request.requesterId = requesterId;
        request.requesteeId = requesteeId;
        request.message = message;
        request.status = 0;
        request.createTime = System.currentTimeMillis();
        friendRequestDao.save(request);
        // Todo: nofity requestee
        return request;
    }

    @Override
    public void declineFriendRequest(String requesterId, String requesteeId) throws ServiceException {
        if (checkUser(requesteeId) || checkUser(requesteeId)) {
            throw new ServiceException("requester or requestee doesn't exist");
        }
        FriendRequest friendRequest = friendRequestDao.findByRequesterIdAndRequesteeId(requesterId, requesteeId);
        if (friendRequest == null) {
            throw new ServiceException("friend request does not exist");
        }
        if (friendRequest.status == 1) {
            throw new ServiceException("they are already friends");
        }
        friendRequest.status = -1;
        friendRequestDao.save(friendRequest);
    }

    @Override
    public void acceptFriendRequest(String requesterId, String requesteeId) throws ServiceException {
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
        redisUtil.removeKey(MyConst.redisKeyFriends(requesterId));
        redisUtil.removeKey(MyConst.redisKeyFriends(requesteeId));

        // update friends
        doAddFriend(requesterId, requesteeId);
        // update friend request
        friendRequest.status = 1;
        friendRequestDao.save(friendRequest);
        friendRequest = friendRequestDao.findByRequesterIdAndRequesteeId(requesteeId, requesterId);
        if (friendRequest != null) {
            friendRequest.status = 1;
            friendRequestDao.save(friendRequest);
        }
    }

    @Override
    public List<FriendRequest> listFriendRequests(String userId, boolean isRequester) {
        if (isRequester) return friendRequestDao.findAllByRequesterIdAndStatus(userId, 0);
        return friendRequestDao.findAllByRequesteeIdAndStatus(userId, 0);
    }


    @Override
    public void doAddFriend(String userId, String friendId) {
        Optional<User> user = userDao.findById(userId);
        Optional<User> friend = userDao.findById(friendId);
        if (!user.isPresent() || !friend.isPresent()) return;
        List<String> userFriends = user.get().friendIds;
        List<String> friendFriends = friend.get().friendIds;
        if (!userFriends.contains(friendId)) {
            userFriends.add(friendId);
        }
        if (!friendFriends.contains(userId)) {
            friendFriends.add(userId);
        }
        userDao.save(user.get());
        userDao.save(friend.get());
    }


    private boolean checkUser(String userId) {
        return !userDao.findById(userId).isPresent();
    }
}

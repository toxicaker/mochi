package cn.jiateng.api.services.impl;

import cn.jiateng.api.Model.Friend;
import cn.jiateng.api.Model.FriendRequest;
import cn.jiateng.api.Model.User;
import cn.jiateng.api.common.MyConst;
import cn.jiateng.api.common.ServiceException;
import cn.jiateng.api.dao.FriendDao;
import cn.jiateng.api.dao.FriendRequestDao;
import cn.jiateng.api.dao.UserDao;
import cn.jiateng.api.services.UserService;
import cn.jiateng.api.utils.RedisUtil;
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
    public User getUserById(String userId) {
        return userDao.findById(userId).get();
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
                System.out.println(gson.toJson(user));
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
        Set<User> friends = listFriends(userId);
        Set<String> res = new HashSet<>();
        for (User user : friends) {
            res.add(user.id);
        }
        return res;
    }

    @Override
    public void removeFriend(String userId, String friendId) {
        // Todo: transaction?
        // remove redis key
        redisUtil.removeKey(MyConst.redisKeyFriends(userId));
        redisUtil.removeKey(MyConst.redisKeyFriends(friendId));
        friendDao.deleteByUserId1AndUserId2(userId, friendId);
        friendDao.deleteByUserId1AndUserId2(friendId, userId);
    }

    @Override
    public FriendRequest requestFriend(String requesterId, String requesteeId, String message) throws ServiceException {
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
        redisUtil.removeKey(MyConst.redisKeyFriends(requesterId));
        redisUtil.removeKey(MyConst.redisKeyFriends(requesteeId));

        // update friends
        doAddFriend(requesterId, requesteeId);
        // update friend request
        friendRequest.status = 1;
        friendRequestDao.save(friendRequest);
        return true;
    }

    @Override
    public List<FriendRequest> listFriendRequests(String userId, boolean isRequester) {
        if (isRequester) return friendRequestDao.findAllByRequesteeIdAndStatus(userId, 0);
        return friendRequestDao.findAllByRequesteeIdAndStatus(userId, 0);
    }


    @Override
    public boolean doAddFriend(String userId, String friendId) throws ServiceException {
        Friend friend1 = friendDao.findByUserId1AndUserId2(userId, friendId);
        Friend friend2 = friendDao.findByUserId1AndUserId2(friendId, userId);
        if (friend1 != null || friend2 != null) {
            throw new ServiceException("they are already friend");
        }
        friend1 = new Friend();
        friend1.userId1 = userId;
        friend1.userId2 = friendId;

        friend2 = new Friend();
        friend2.userId1 = friendId;
        friend2.userId2 = userId;

        friendDao.save(friend1);
        friendDao.save(friend2);

        return true;
    }

    private boolean checkUser(String userId) {
        return !userDao.findById(userId).isPresent();
    }
}

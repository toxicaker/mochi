package cn.jiateng.services;

import cn.jiateng.Model.User;

import java.util.List;

public interface UserService {

    List<User> listFriends(String userId);

    boolean requestFriend(String requesterId, String requesteeId);

    boolean acceptFriendRequest(String userId, String requestId);

}

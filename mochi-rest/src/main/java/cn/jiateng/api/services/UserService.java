package cn.jiateng.api.services;

import cn.jiateng.api.Model.FriendRequest;
import cn.jiateng.api.Model.User;
import cn.jiateng.api.Model.UserGroup;
import cn.jiateng.api.common.ServiceException;

import java.rmi.ServerException;
import java.util.List;
import java.util.Set;

public interface UserService {

    User getUserById(String userId);

    FriendRequest getFriendRequestById(String requestId);

    Set<User> listFriends(String userId);

    Set<String> listFriendIds(String userId);

    void removeFriend(String userId, String friendId);

    FriendRequest sendFriendRequest(String requesterId, String requesteeId, String message) throws ServiceException;

    void acceptFriendRequest(String requesterId, String requesteeId) throws ServiceException;

    void declineFriendRequest(String requesterId, String reuqesteeId) throws ServiceException;

    List<FriendRequest> listFriendRequests(String userId, boolean isRequester);

    void doAddFriend(String userId, String friendId) throws ServiceException;

    void createGroup(String userId, String name, List<String> userIds);

    UserGroup joinGroup(String userId, String groupId) throws ServerException;

    UserGroup leaveGroup(String userId, String groupId) throws ServerException;
}

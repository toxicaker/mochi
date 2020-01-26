package cn.jiateng.api.services;

import cn.jiateng.api.Model.FriendRequest;
import cn.jiateng.api.Model.User;
import cn.jiateng.api.common.ServiceException;

import java.util.List;
import java.util.Set;

public interface UserService {

    User getUserById(String userId);

    Set<User> listFriends(String userId);

    Set<String> listFriendIds(String userId);

    void removeFriend(String userId, String friendId);

    FriendRequest requestFriend(String requesterId, String requesteeId, String message) throws ServiceException;

    boolean acceptFriendRequest(String requesterId, String requesteeId) throws ServiceException;

    boolean declineFriendRequest(String requesterId, String reuqesteeId) throws ServiceException;

    List<FriendRequest> listFriendRequests(String userId, boolean isRequester);

    boolean doAddFriend(String userId, String friendId) throws ServiceException;

}

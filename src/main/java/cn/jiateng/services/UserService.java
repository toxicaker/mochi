package cn.jiateng.services;

import cn.jiateng.Model.FriendRequest;
import cn.jiateng.Model.User;
import cn.jiateng.common.ServiceException;

import java.util.List;
import java.util.Set;

public interface UserService {

    Set<User> listFriends(String userId);

    Set<String> listFriendIds(String userId);

    boolean requestFriend(String requesterId, String requesteeId, String message) throws ServiceException;

    boolean acceptFriendRequest(String requesterId, String requesteeId) throws ServiceException;

    boolean declineFriendRequest(String requesterId, String reuqesteeId) throws ServiceException;

    List<FriendRequest> listFriendRequests(String userId);

}

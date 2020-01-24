package cn.jiateng.dao;

import cn.jiateng.Model.FriendRequest;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;


public interface FriendRequestDao extends MongoRepository<FriendRequest, String> {

    FriendRequest findByRequesterIdAndRequesteeId(String requesterId, String requesteeId);

    List<FriendRequest> findAllByRequesterId(String requesterId);

}

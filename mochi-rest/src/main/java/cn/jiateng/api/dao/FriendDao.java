package cn.jiateng.api.dao;

import cn.jiateng.api.Model.Friend;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface FriendDao extends MongoRepository<Friend, String> {

    List<Friend> findAllByUserId1(String userId1);

    Friend findByUserId1AndUserId2(String userId1, String userId2);

    void deleteByUserId1AndUserId2(String userId1, String userId2);

}

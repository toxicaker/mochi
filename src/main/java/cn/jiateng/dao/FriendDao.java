package cn.jiateng.dao;

import cn.jiateng.Model.Friend;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface FriendDao extends MongoRepository<Friend, String> {

    List<Friend> findAllByUserId1(String userId);

}

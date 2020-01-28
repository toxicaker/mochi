package cn.jiateng.api.dao;

import cn.jiateng.api.Model.UserGroup;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface UserGroupDao extends MongoRepository<UserGroup, String> {

    UserGroup findByUserIdAndGroupId(String userId, String groupId);

    List<UserGroup> findByGroupId(String groupId);

    List<UserGroup> findByUserId(String userId);
}

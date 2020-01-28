package cn.jiateng.api.dao;

import cn.jiateng.api.Model.Group;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface GroupDao extends MongoRepository<Group, String> {
}

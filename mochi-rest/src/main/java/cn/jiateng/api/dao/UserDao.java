package cn.jiateng.api.dao;

import cn.jiateng.api.Model.User;
import org.springframework.data.mongodb.repository.MongoRepository;


public interface UserDao extends MongoRepository<User, String> {

    User findByUsername(String username);

    User findByUsernameAndPassword(String username, String password);
}

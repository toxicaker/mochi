package cn.jiateng.dao;

import cn.jiateng.Model.User;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface UserDao extends MongoRepository<User, String> {

    List<User> findByUsername(String username);

    List<User> findByUsernameAndPassword(String username, String password);
}

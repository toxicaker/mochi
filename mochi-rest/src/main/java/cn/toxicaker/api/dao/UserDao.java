package cn.toxicaker.api.dao;


import cn.toxicaker.api.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface UserDao extends MongoRepository<User, String> {

    User findByUsername(String username);

    User findByUsernameAndPassword(String username, String password);
}

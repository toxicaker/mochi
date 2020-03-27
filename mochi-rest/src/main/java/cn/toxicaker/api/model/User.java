package cn.toxicaker.api.model;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "users")
public class User {
    @Id
    public String id;

    public String username;

    @JsonIgnore
    public String password;

    public String nickname;

    public Long lastLoginTime;

    public String token;

    public Long createTime;

}

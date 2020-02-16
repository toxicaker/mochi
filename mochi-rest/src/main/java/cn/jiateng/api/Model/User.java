package cn.jiateng.api.Model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.gson.annotations.Expose;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;

@Document(collection = "users")
public class User {
    @Id
    public String id;

    public String username;

    @JsonIgnore
    public String password;

    public String nickname;

    @Expose (serialize = false, deserialize = false)
    public List<String> friendIds = new ArrayList<>();

    @Expose (serialize = false, deserialize = false)
    public List<String> groupIds = new ArrayList<>();

    public Long lastLoginTime;

    public String token;

    public Long createTime;

    @Override
    public String toString() {
        return "User{" +
                "id='" + id + '\'' +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", nickname='" + nickname + '\'' +
                ", friendIds=" + friendIds +
                ", groupIds=" + groupIds +
                ", lastLoginTime=" + lastLoginTime +
                ", token='" + token + '\'' +
                ", createTime=" + createTime +
                '}';
    }
}

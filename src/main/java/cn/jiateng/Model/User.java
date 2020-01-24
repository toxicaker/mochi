package cn.jiateng.Model;

import org.springframework.data.annotation.Id;

public class User {
    @Id
    public String id;

    public String username;

    public String password;

    public String nickname;

    public Long lastLoginTime;

    public Long createTime;

    @Override
    public String toString() {
        return "User{" +
                "id='" + id + '\'' +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", nickname='" + nickname + '\'' +
                ", lastLoginTime=" + lastLoginTime +
                ", createTime=" + createTime +
                '}';
    }
}

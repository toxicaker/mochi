package cn.jiateng.api.Model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "friends")
public class Friend {
    @Id
    public String id;

    public String userId1;

    public String userId2;

    public Long createTime;

    @Override
    public String toString() {
        return "Friend{" +
                "id='" + id + '\'' +
                ", userId1='" + userId1 + '\'' +
                ", userId2='" + userId2 + '\'' +
                ", createTime=" + createTime +
                '}';
    }
}

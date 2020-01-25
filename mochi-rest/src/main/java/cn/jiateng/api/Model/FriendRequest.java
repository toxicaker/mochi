package cn.jiateng.api.Model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "friend_requests")
public class FriendRequest {

    @Id
    public String id;

    public String requesterId;

    public String requesteeId;

    public String message;

    public Integer status;  // 0 pending, 1 accepted, -1 declined

    public Long createTime;

    @Override
    public String toString() {
        return "FriendRequest{" +
                "id='" + id + '\'' +
                ", requesterId='" + requesterId + '\'' +
                ", requesteeId='" + requesteeId + '\'' +
                ", message='" + message + '\'' +
                ", status=" + status +
                ", createTime=" + createTime +
                '}';
    }
}

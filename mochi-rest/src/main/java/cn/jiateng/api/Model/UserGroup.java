package cn.jiateng.api.Model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "user_group")
public class UserGroup {
    @Id
    public String Id;

    public String userId;

    public String groupId;

    public Long createTime;
}

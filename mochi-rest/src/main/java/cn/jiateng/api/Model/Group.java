package cn.jiateng.api.Model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;


@Document(collection = "groups")
public class Group {
    @Id
    public String id;

    public String name;

    public Long createTime;
}

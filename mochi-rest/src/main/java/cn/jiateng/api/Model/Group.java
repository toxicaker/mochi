package cn.jiateng.api.Model;

import com.google.gson.annotations.Expose;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;


@Document(collection = "groups")
public class Group {
    @Id
    public String id;

    public String name;

    @Expose(serialize = false, deserialize = false)
    public List<String> userIds = new ArrayList<>();

    public Long createTime;

    @Override
    public String toString() {
        return "Group{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", userIds=" + userIds +
                ", createTime=" + createTime +
                '}';
    }
}

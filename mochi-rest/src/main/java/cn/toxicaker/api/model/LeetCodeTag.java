package cn.toxicaker.api.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document(collection = "leetcode_tags")
public class LeetCodeTag {

    @Id
    public String id;

    public String name;

    public String slug;

    @JsonIgnore
    public List<Integer> leetCodeIds;
}

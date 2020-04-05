package cn.toxicaker.api.dao;

import cn.toxicaker.api.model.LeetCodeTag;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface LeetCodeTagDao extends MongoRepository<LeetCodeTag, String> {

    LeetCodeTag findBySlug(String slug);
}

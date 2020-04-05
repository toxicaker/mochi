package cn.toxicaker.api.dao;

import cn.toxicaker.api.model.LeetCodeTag;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface LeetCodeTagDao extends MongoRepository<LeetCodeTag, String> {

    LeetCodeTag findBySlug(String slug);

    List<LeetCodeTag> findByLeetCodeIdsIn(int leetCodeId);
}

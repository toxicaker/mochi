package cn.toxicaker.api.dao;

import cn.toxicaker.api.model.LeetCodeProblem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.Optional;

public interface LeetCodeDao extends MongoRepository<LeetCodeProblem, String> {

    Page<LeetCodeProblem> findAll(Pageable pageable);

    Optional<LeetCodeProblem> findById(String id);
}

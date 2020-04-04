package cn.toxicaker.api.dao;

import cn.toxicaker.api.model.LeetCodeProblem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;


public interface LeetCodeDao extends MongoRepository<LeetCodeProblem, String> {

    Page<LeetCodeProblem> findAllByTypeAndDifficulty(boolean type, int difficulty, Pageable pageable);

    Page<LeetCodeProblem> findAllByType(boolean type, Pageable pageable);

    Page<LeetCodeProblem> findAllByDifficulty(int difficulty, Pageable pageable);

    LeetCodeProblem findByProblemNum(int number);

    @Query("{'$or' : [{'title' : {$regex : ?0}}, {'content' : {$regex : ?0}}]}")
    Page<LeetCodeProblem> findAllByTitleOrContentRegex(String keyword, Pageable pageable);

}

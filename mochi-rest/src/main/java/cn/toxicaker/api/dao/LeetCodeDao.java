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

    @Query("{$or : [{'title' : {$regex : ?0, $options : 'i'}}, {'content' : {$regex : ?0, $options : 'i'}}]}")
    Page<LeetCodeProblem> findAllByTitleOrContentRegex(String keyword, Pageable pageable);

    @Query("{$and: [{$or : [{title : {$regex : ?0, $options : 'i'}}, {content : {$regex : ?0, $options : 'i'}}]}, {type: ?1}]}")
    Page<LeetCodeProblem> findAllByTitleOrContentRegexAndType(String keyword, boolean type, Pageable pageable);

    @Query("{$and: [{$or : [{title : {$regex : ?0, $options : 'i'}}, {content : {$regex : ?0, $options : 'i'}}]}, {difficulty: ?1}]}")
    Page<LeetCodeProblem> findAllByTitleOrContentRegexAndDifficulty(String keyword, int difficulty, Pageable pageable);

    @Query("{$and: [{$or : [{title : {$regex : ?0, $options : 'i'}}, {content : {$regex : ?0, $options : 'i'}}]}, {type: ?1}, {difficulty: ?2}]}")
    Page<LeetCodeProblem> findAllByTitleOrContentRegexAndTypeAndDifficulty(String keyword, boolean type, int difficulty, Pageable pageable);

}

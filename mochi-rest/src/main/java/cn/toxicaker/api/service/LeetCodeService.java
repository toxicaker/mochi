package cn.toxicaker.api.service;

import cn.toxicaker.api.model.LeetCodeProblem;
import cn.toxicaker.api.model.LeetCodeTag;
import org.springframework.data.domain.Page;

import java.util.List;


public interface LeetCodeService {

    Page<LeetCodeProblem> listLeetCodeProblemsByPage(int startPage, int pageSize, String type, String difficulty);

    LeetCodeProblem getLeetCodeProblemById(String id);

    Page<LeetCodeProblem> searchLeetCodeProblemsByTitleAndContent(int startPage, int pageSize, String keyword, String type, String difficulty);

    LeetCodeProblem getLeetCodeProblemByNumber(int num);

    List<LeetCodeProblem> listLeetCodeProblemsByTagId(String tagId);

    List<LeetCodeTag> listTags();
}

package cn.toxicaker.api.service;

import cn.toxicaker.api.model.LeetCodeProblem;
import org.springframework.data.domain.Page;


public interface LeetCodeService {

    Page<LeetCodeProblem> listLeetCodeProblemsByPage(int startPage, int pageSize, String type, String difficulty);

    LeetCodeProblem getLeetCodeProblemById(String id);

    Page<LeetCodeProblem> searchLeetCodeProblemsByTitleAndContent(int startPage, int pageSize, String keyword, String type, String difficulty);

    LeetCodeProblem getLeetCodeProblemByNumber(int num);
}

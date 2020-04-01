package cn.toxicaker.api.service;

import cn.toxicaker.api.model.LeetCodeProblem;
import org.springframework.data.domain.Page;


public interface LeetCodeService {

    Page<LeetCodeProblem> listLeetCodeProblemsByPage(int startPage, int pageSize);

    LeetCodeProblem getLeetCodeProblemById(String id);
}

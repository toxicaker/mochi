package cn.toxicaker.api.service.impl;

import cn.toxicaker.api.dao.LeetCodeDao;
import cn.toxicaker.api.model.LeetCodeProblem;
import cn.toxicaker.api.service.LeetCodeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class LeetCodeServiceImpl implements LeetCodeService {

    @Autowired
    private LeetCodeDao leetCodeDao;

    @Override
    public Page<LeetCodeProblem> listLeetCodeProblemsByPage(int startPage, int pageSize) {
        Pageable pageable = PageRequest.of(startPage, pageSize, Sort.Direction.ASC, "problemNum");
        return leetCodeDao.findAll(pageable);
    }

    @Override
    public LeetCodeProblem getLeetCodeProblemById(String id) {
        Optional<LeetCodeProblem> res = leetCodeDao.findById(id);
        return res.orElse(null);
    }

    @Override
    public Page<LeetCodeProblem> searchLeetCodeProblemsByTitleAndContent(String keyword, int startPage, int pageSize) {
        Pageable pageable = PageRequest.of(startPage, pageSize, Sort.Direction.ASC, "problemNum");
        return leetCodeDao.findAllByTitleOrContentRegex(keyword, pageable);
    }

    @Override
    public LeetCodeProblem getLeetCodeProblemByNumber(int num) {
        return leetCodeDao.findByProblemNum(num);
    }
}

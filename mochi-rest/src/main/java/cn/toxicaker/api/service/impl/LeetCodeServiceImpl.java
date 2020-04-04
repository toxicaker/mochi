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
    public Page<LeetCodeProblem> listLeetCodeProblemsByPage(int startPage, int pageSize, String type, String difficulty) {
        Pageable pageable = PageRequest.of(startPage, pageSize, Sort.Direction.ASC, "problemNum");
        LeetCodeProblem.Difficulty diff = difficulty.equals("easy") ? LeetCodeProblem.Difficulty.EASY
                : difficulty.equals("medium") ? LeetCodeProblem.Difficulty.MEDIUM : LeetCodeProblem.Difficulty.HARD;
        LeetCodeProblem.Type t = type.equals("normal") ? LeetCodeProblem.Type.NORMAL : LeetCodeProblem.Type.PREMIUM;
        if ("".equals(type) && "".equals(difficulty)) {
            return leetCodeDao.findAll(pageable);
        } else if ("".equals(type)) {
            return leetCodeDao.findAllByDifficulty(diff.num, pageable);
        } else if ("".equals(difficulty)) {
            return leetCodeDao.findAllByType(t.type, pageable);
        } else {
            return leetCodeDao.findAllByTypeAndDifficulty(t.type, diff.num, pageable);
        }
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

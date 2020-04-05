package cn.toxicaker.api.service.impl;

import cn.toxicaker.api.dao.LeetCodeDao;
import cn.toxicaker.api.dao.LeetCodeTagDao;
import cn.toxicaker.api.model.LeetCodeProblem;
import cn.toxicaker.api.model.LeetCodeTag;
import cn.toxicaker.api.service.LeetCodeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class LeetCodeServiceImpl implements LeetCodeService {

    @Autowired
    private LeetCodeDao leetCodeDao;

    @Autowired
    private LeetCodeTagDao leetCodeTagDao;

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
    public Page<LeetCodeProblem> searchLeetCodeProblemsByTitleAndContent(int startPage, int pageSize, String keyword, String type, String difficulty) {
        Pageable pageable = PageRequest.of(startPage, pageSize, Sort.Direction.ASC, "problemNum");
        LeetCodeProblem.Difficulty diff = difficulty.equals("easy") ? LeetCodeProblem.Difficulty.EASY
                : difficulty.equals("medium") ? LeetCodeProblem.Difficulty.MEDIUM : LeetCodeProblem.Difficulty.HARD;
        LeetCodeProblem.Type t = type.equals("normal") ? LeetCodeProblem.Type.NORMAL : LeetCodeProblem.Type.PREMIUM;
        if ("".equals(type) && "".equals(difficulty)) {
            return leetCodeDao.findAllByTitleOrContentRegex(keyword, pageable);
        } else if ("".equals(type)) {
            return leetCodeDao.findAllByTitleOrContentRegexAndDifficulty(keyword, diff.num, pageable);
        } else if ("".equals(difficulty)) {
            return leetCodeDao.findAllByTitleOrContentRegexAndType(keyword, t.type, pageable);
        } else {
            return leetCodeDao.findAllByTitleOrContentRegexAndTypeAndDifficulty(keyword, t.type, diff.num, pageable);
        }
    }


    @Override
    public LeetCodeProblem getLeetCodeProblemByNumber(int num) {
        return leetCodeDao.findByProblemNum(num);
    }

    @Override
    public List<LeetCodeProblem> listLeetCodeProblemsByTagId(String tagId) {
        Optional<LeetCodeTag> leetCodeTag = leetCodeTagDao.findById(tagId);
        List<LeetCodeProblem> res = new ArrayList<>();
        if (!leetCodeTag.isPresent()) {
            return res;
        }
        for (int id : leetCodeTag.get().leetCodeIds) {
            LeetCodeProblem problem = leetCodeDao.findByLeetCodeId(id);
            if (problem != null) {
                res.add(problem);
            }
        }
        return res;
    }

    @Override
    public List<LeetCodeTag> listTags() {
        return leetCodeTagDao.findAll();
    }
}

package cn.toxicaker.api.controller;

import cn.toxicaker.api.model.LeetCodeProblem;
import cn.toxicaker.api.security.SkipAuth;
import cn.toxicaker.api.service.LeetCodeService;
import cn.toxicaker.common.JsonResp;
import cn.toxicaker.common.ServiceException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.text.DecimalFormat;
import java.util.*;
import java.util.stream.Stream;

@RestController
@RequestMapping(value = "/api/leetcode", produces = "application/json")
public class LeetCodeController {

    @Autowired
    private LeetCodeService leetCodeService;

    @GetMapping("/problems")
    @SkipAuth
    public JsonResp listProblems(@RequestParam(defaultValue = "0") int page,
                                 @RequestParam(defaultValue = "") String type,
                                 @RequestParam(defaultValue = "") String difficulty) throws ServiceException {
        if (!validateDifficulty(difficulty) || !validateType(type)) {
            throw new ServiceException("parameter type or difficulty is invalid");
        }
        Page<LeetCodeProblem> leetCodeProblems = leetCodeService.listLeetCodeProblemsByPage(page, 20, type, difficulty);
        Map<String, Object> res = new HashMap<>();
        res.put("page", page);
        res.put("totalPage", leetCodeProblems.getTotalPages());
        res.put("totalNum", leetCodeProblems.getTotalElements());
        res.put("data", packListProblemsData(leetCodeProblems.stream()));
        return new JsonResp(res);
    }


    @GetMapping("/problems/{id}")
    @SkipAuth
    public JsonResp getProblemById(@PathVariable String id) {
        LeetCodeProblem leetCodeProblem = leetCodeService.getLeetCodeProblemById(id);
        if (leetCodeProblem == null) {
            return new JsonResp(null);
        }
        return new JsonResp(packGetProblemByIdData(leetCodeProblem));
    }

    @GetMapping("/problems/search/{keyword}")
    @SkipAuth
    public JsonResp searchProblems(@PathVariable String keyword,
                                   @RequestParam(defaultValue = "0") int page,
                                   @RequestParam(defaultValue = "") String type,
                                   @RequestParam(defaultValue = "") String difficulty) throws ServiceException {
        Map<String, Object> obj = new HashMap<>();
        if (StringUtils.isNumeric(keyword)) {
            int num = Integer.parseInt(keyword);
            LeetCodeProblem leetCodeProblem = leetCodeService.getLeetCodeProblemByNumber(num);
            obj.put("page", 0);
            obj.put("totalPage", 1);
            if (leetCodeProblem == null) {
                obj.put("totalNum", 0);
                obj.put("data", new ArrayList<>());
                return new JsonResp(obj);
            } else {
                obj.put("totalNum", 1);
                obj.put("data", packListProblemsData(Stream.of(leetCodeProblem)));
                return new JsonResp(obj);
            }
        }
        if (!validateDifficulty(difficulty) || !validateType(type)) {
            throw new ServiceException("parameter type or difficulty is invalid");
        }
        Page<LeetCodeProblem> leetCodeProblems = leetCodeService.searchLeetCodeProblemsByTitleAndContent(page, 20, keyword, type, difficulty);
        obj.put("page", page);
        obj.put("totalPage", leetCodeProblems.getTotalPages());
        obj.put("totalNum", leetCodeProblems.getTotalElements());
        obj.put("data", packListProblemsData(leetCodeProblems.stream()));
        return new JsonResp(obj);
    }

    private Map<String, Object> packGetProblemByIdData(LeetCodeProblem problem) {
        Map<String, Object> obj = new HashMap<>();
        obj.put("id", problem.id);
        obj.put("number", problem.problemNum);
        obj.put("title", problem.title);
        obj.put("content", problem.content);
        obj.put("type", LeetCodeProblem.Type.getType(problem.type));
        obj.put("acceptance", problem.acceptance);
        obj.put("difficulty", LeetCodeProblem.Difficulty.getDifficulty(problem.difficulty));
        obj.put("frequency", problem.frequency);
        return obj;
    }

    private List<Map<String, Object>> packListProblemsData(Stream<LeetCodeProblem> stream) {
        List<Map<String, Object>> res = new ArrayList<>();
        stream.forEach(problem -> {
            Map<String, Object> obj = new HashMap<>();
            obj.put("id", problem.id);
            obj.put("number", problem.problemNum);
            obj.put("title", problem.title);
            obj.put("type", LeetCodeProblem.Type.getType(problem.type));
            DecimalFormat f = new DecimalFormat("##.00");
            obj.put("acceptance", f.format(problem.acceptance * 100) + "%");
            obj.put("difficulty", LeetCodeProblem.Difficulty.getDifficulty(problem.difficulty));
            obj.put("frequency", problem.frequency);
            res.add(obj);
        });
        return res;
    }

    private boolean validateType(String type) {
        if (type == null) {
            return false;
        }
        return type.equals("normal") || type.equals("premium") || type.equals("");
    }

    private boolean validateDifficulty(String difficulty) {
        if (difficulty == null) {
            return false;
        }
        return difficulty.equals("easy") || difficulty.equals("medium") || difficulty.equals("hard") || difficulty.equals("");
    }
}

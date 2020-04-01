package cn.toxicaker.api.controller;

import cn.toxicaker.api.model.LeetCodeProblem;
import cn.toxicaker.api.security.SkipAuth;
import cn.toxicaker.api.service.LeetCodeService;
import cn.toxicaker.common.JsonResp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

@RestController
@RequestMapping(value = "/api/leetcode", produces = "application/json")
public class LeetCodeController {

    @Autowired
    private LeetCodeService leetCodeService;

    @GetMapping("/problems")
    @SkipAuth
    public JsonResp listProblems(@RequestParam int page) {
        Page<LeetCodeProblem> leetCodeProblems = leetCodeService.listLeetCodeProblemsByPage(page, 20);
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
        return new JsonResp(packGetProblemByIdData(leetCodeProblem));
    }

    private Map<String, Object> packGetProblemByIdData(LeetCodeProblem problem) {
        Map<String, Object> obj = new HashMap<>();
        obj.put("id", problem.id);
        obj.put("number", problem.number);
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
            obj.put("number", problem.number);
            obj.put("title", problem.title);
            obj.put("type", LeetCodeProblem.Type.getType(problem.type));
            obj.put("acceptance", problem.acceptance);
            obj.put("difficulty", LeetCodeProblem.Difficulty.getDifficulty(problem.difficulty));
            obj.put("frequency", problem.frequency);
            res.add(obj);
        });
        return res;
    }
}

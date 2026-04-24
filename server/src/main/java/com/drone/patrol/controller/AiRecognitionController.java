package com.drone.patrol.controller;

import com.drone.patrol.common.Result;
import com.drone.patrol.entity.PatrolResult;
import com.drone.patrol.service.AiRecognitionService;
import com.drone.patrol.service.PatrolResultService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/ai")
public class AiRecognitionController {

    @Autowired
    private AiRecognitionService aiRecognitionService;

    @Autowired
    private PatrolResultService patrolResultService;

    @PostMapping("/recognize/{resultId}")
    public Result<Map<String, Object>> recognizePatrolResult(@PathVariable Long resultId) {
        PatrolResult result = patrolResultService.getById(resultId);
        if (result == null) {
            return Result.error("巡防结果不存在");
        }

        Map<String, Object> analysis = aiRecognitionService.analyzePatrolResult(result);

        result.setAiResult((String) analysis.get("aiResult"));
        patrolResultService.updateById(result);

        return Result.success(analysis);
    }

    @GetMapping("/analyze/{resultId}")
    public Result<Map<String, Object>> analyzePatrolResult(@PathVariable Long resultId) {
        PatrolResult result = patrolResultService.getById(resultId);
        if (result == null) {
            return Result.error("巡防结果不存在");
        }

        Map<String, Object> analysis = aiRecognitionService.analyzePatrolResult(result);
        return Result.success(analysis);
    }
}
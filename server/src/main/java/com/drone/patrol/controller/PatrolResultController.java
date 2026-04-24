package com.drone.patrol.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.drone.patrol.common.Result;
import com.drone.patrol.entity.PatrolResult;
import com.drone.patrol.service.PatrolResultService;
import com.drone.patrol.service.SysOperationLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/result")
public class PatrolResultController {

    @Autowired
    private PatrolResultService patrolResultService;

    @Autowired
    private SysOperationLogService sysOperationLogService;

    @GetMapping("/page")
    public Result<Page<PatrolResult>> getResultPage(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String resultCode,
            @RequestParam(required = false) String taskName,
            @RequestParam(required = false) String executorName,
            @RequestParam(required = false) String startTime,
            @RequestParam(required = false) String endTime) {
        return Result.success(patrolResultService.getResultPage(pageNum, pageSize, resultCode, taskName, executorName, startTime, endTime));
    }

    @GetMapping("/{id}")
    public Result<PatrolResult> getResultById(@PathVariable Long id) {
        return Result.success(patrolResultService.getById(id));
    }

    @GetMapping("/statistics")
    public Result<Object> getStatistics() {
        long totalCount = patrolResultService.count();
        
        java.util.Map<String, Object> stats = new java.util.HashMap<>();
        stats.put("totalCount", totalCount);
        
        return Result.success(stats);
    }

    @PutMapping
    public Result<Void> updateResult(@RequestBody PatrolResult result, HttpServletRequest request) {
        String role = (String) request.getAttribute("role");
        if (!"ADMIN".equals(role)) {
            return Result.error("无权限操作");
        }
        
        boolean success = patrolResultService.updateById(result);
        if (success) {
            String username = (String) request.getAttribute("username");
            Long userId = (Long) request.getAttribute("userId");
            String ip = request.getRemoteAddr();
            sysOperationLogService.recordLog(userId, username, "修改结果：" + result.getResultCode(), ip);
        }
        return success ? Result.success() : Result.error();
    }

    @DeleteMapping("/{id}")
    public Result<Void> deleteResult(@PathVariable Long id, HttpServletRequest request) {
        String role = (String) request.getAttribute("role");
        if (!"ADMIN".equals(role)) {
            return Result.error("无权限操作");
        }
        
        PatrolResult result = patrolResultService.getById(id);
        boolean success = patrolResultService.removeById(id);
        if (success && result != null) {
            String username = (String) request.getAttribute("username");
            Long userId = (Long) request.getAttribute("userId");
            String ip = request.getRemoteAddr();
            sysOperationLogService.recordLog(userId, username, "删除结果：" + result.getResultCode(), ip);
        }
        return success ? Result.success() : Result.error();
    }

    @GetMapping("/export")
    public void exportToExcel(
            @RequestParam(required = false) String resultCode,
            @RequestParam(required = false) String taskName,
            @RequestParam(required = false) String executorName,
            @RequestParam(required = false) String startTime,
            @RequestParam(required = false) String endTime,
            HttpServletResponse response) throws IOException {
        List<PatrolResult> resultList = patrolResultService.getResultPage(1, 10000, resultCode, taskName, executorName, startTime, endTime).getRecords();
        patrolResultService.exportToExcel(response, resultList);
    }
}

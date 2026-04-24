package com.drone.patrol.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.drone.patrol.common.Result;
import com.drone.patrol.entity.PatrolTask;
import com.drone.patrol.service.PatrolTaskService;
import com.drone.patrol.service.SysOperationLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/task")
public class PatrolTaskController {

    @Autowired
    private PatrolTaskService patrolTaskService;

    @Autowired
    private SysOperationLogService sysOperationLogService;

    @GetMapping("/page")
    public Result<Page<PatrolTask>> getTaskPage(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String taskName,
            @RequestParam(required = false) String executorName,
            @RequestParam(required = false) String deviceName,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String startTime,
            @RequestParam(required = false) String endTime) {
        return Result.success(patrolTaskService.getTaskPage(pageNum, pageSize, taskName, executorName, deviceName, status, startTime, endTime));
    }

    @GetMapping("/{id}")
    public Result<PatrolTask> getTaskById(@PathVariable Long id) {
        return Result.success(patrolTaskService.getById(id));
    }

    @GetMapping("/statistics")
    public Result<Object> getStatistics() {
        long pendingCount = patrolTaskService.count(new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<PatrolTask>().eq(PatrolTask::getStatus, "PENDING"));
        long executingCount = patrolTaskService.count(new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<PatrolTask>().eq(PatrolTask::getStatus, "EXECUTING"));
        long completedCount = patrolTaskService.count(new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<PatrolTask>().eq(PatrolTask::getStatus, "COMPLETED"));
        long cancelledCount = patrolTaskService.count(new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<PatrolTask>().eq(PatrolTask::getStatus, "CANCELLED"));
        
        java.util.Map<String, Object> stats = new java.util.HashMap<>();
        stats.put("pendingCount", pendingCount);
        stats.put("executingCount", executingCount);
        stats.put("completedCount", completedCount);
        stats.put("cancelledCount", cancelledCount);
        stats.put("totalCount", pendingCount + executingCount + completedCount + cancelledCount);
        
        return Result.success(stats);
    }

    @PostMapping
    public Result<Void> addTask(@RequestBody PatrolTask task, HttpServletRequest request) {
        String role = (String) request.getAttribute("role");
        if (!"ADMIN".equals(role)) {
            return Result.error("无权限操作");
        }
        
        task.setStatus("PENDING");
        task.setProgress(0);
        boolean success = patrolTaskService.save(task);
        if (success) {
            String username = (String) request.getAttribute("username");
            Long userId = (Long) request.getAttribute("userId");
            String ip = request.getRemoteAddr();
            sysOperationLogService.recordLog(userId, username, "新增任务：" + task.getTaskName(), ip);
        }
        return success ? Result.success() : Result.error();
    }

    @PutMapping
    public Result<Void> updateTask(@RequestBody PatrolTask task, HttpServletRequest request) {
        String role = (String) request.getAttribute("role");
        if (!"ADMIN".equals(role)) {
            return Result.error("无权限操作");
        }
        
        boolean success = patrolTaskService.updateById(task);
        if (success) {
            String username = (String) request.getAttribute("username");
            Long userId = (Long) request.getAttribute("userId");
            String ip = request.getRemoteAddr();
            sysOperationLogService.recordLog(userId, username, "修改任务：" + task.getTaskName(), ip);
        }
        return success ? Result.success() : Result.error();
    }

    @PutMapping("/start/{id}")
    public Result<Void> startTask(@PathVariable Long id, HttpServletRequest request) {
        String role = (String) request.getAttribute("role");
        if (!"ADMIN".equals(role)) {
            return Result.error("无权限操作");
        }
        
        boolean success = patrolTaskService.startTask(id);
        if (success) {
            String username = (String) request.getAttribute("username");
            Long userId = (Long) request.getAttribute("userId");
            String ip = request.getRemoteAddr();
            sysOperationLogService.recordLog(userId, username, "开始任务：" + id, ip);
        }
        return success ? Result.success() : Result.error();
    }

    @PutMapping("/complete/{id}")
    public Result<Void> completeTask(@PathVariable Long id, HttpServletRequest request) {
        String role = (String) request.getAttribute("role");
        if (!"ADMIN".equals(role)) {
            return Result.error("无权限操作");
        }
        
        boolean success = patrolTaskService.completeTask(id);
        if (success) {
            String username = (String) request.getAttribute("username");
            Long userId = (Long) request.getAttribute("userId");
            String ip = request.getRemoteAddr();
            sysOperationLogService.recordLog(userId, username, "完成任务：" + id, ip);
        }
        return success ? Result.success() : Result.error();
    }

    @PutMapping("/cancel/{id}")
    public Result<Void> cancelTask(@PathVariable Long id, HttpServletRequest request) {
        String role = (String) request.getAttribute("role");
        if (!"ADMIN".equals(role)) {
            return Result.error("无权限操作");
        }
        
        boolean success = patrolTaskService.cancelTask(id);
        if (success) {
            String username = (String) request.getAttribute("username");
            Long userId = (Long) request.getAttribute("userId");
            String ip = request.getRemoteAddr();
            sysOperationLogService.recordLog(userId, username, "取消任务：" + id, ip);
        }
        return success ? Result.success() : Result.error();
    }

    @PutMapping("/progress")
    public Result<Void> updateProgress(@RequestParam Long id, @RequestParam Integer progress, HttpServletRequest request) {
        boolean success = patrolTaskService.updateProgress(id, progress);
        return success ? Result.success() : Result.error();
    }

    @DeleteMapping("/{id}")
    public Result<Void> deleteTask(@PathVariable Long id, HttpServletRequest request) {
        String role = (String) request.getAttribute("role");
        if (!"ADMIN".equals(role)) {
            return Result.error("无权限操作");
        }
        
        PatrolTask task = patrolTaskService.getById(id);
        boolean success = patrolTaskService.removeById(id);
        if (success && task != null) {
            String username = (String) request.getAttribute("username");
            Long userId = (Long) request.getAttribute("userId");
            String ip = request.getRemoteAddr();
            sysOperationLogService.recordLog(userId, username, "删除任务：" + task.getTaskName(), ip);
        }
        return success ? Result.success() : Result.error();
    }
}

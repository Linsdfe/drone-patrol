package com.drone.patrol.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.drone.patrol.common.Result;
import com.drone.patrol.entity.PatrolRoute;
import com.drone.patrol.service.PatrolRouteService;
import com.drone.patrol.service.SysOperationLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/route")
public class PatrolRouteController {

    @Autowired
    private PatrolRouteService patrolRouteService;

    @Autowired
    private SysOperationLogService sysOperationLogService;

    @GetMapping("/page")
    public Result<Page<PatrolRoute>> getRoutePage(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String routeName) {
        return Result.success(patrolRouteService.getRoutePage(pageNum, pageSize, routeName));
    }

    @GetMapping("/{id}")
    public Result<PatrolRoute> getRouteById(@PathVariable Long id) {
        return Result.success(patrolRouteService.getById(id));
    }

    @GetMapping("/list")
    public Result<Page<PatrolRoute>> getAllRoutes() {
        return Result.success(patrolRouteService.getRoutePage(1, 100, null));
    }

    @PostMapping
    public Result<Void> addRoute(@RequestBody PatrolRoute route, HttpServletRequest request) {
        String role = (String) request.getAttribute("role");
        if (!"ADMIN".equals(role)) {
            return Result.error("无权限操作");
        }
        
        boolean success = patrolRouteService.save(route);
        if (success) {
            String username = (String) request.getAttribute("username");
            Long userId = (Long) request.getAttribute("userId");
            String ip = request.getRemoteAddr();
            sysOperationLogService.recordLog(userId, username, "新增航线：" + route.getRouteName(), ip);
        }
        return success ? Result.success() : Result.error();
    }

    @PutMapping
    public Result<Void> updateRoute(@RequestBody PatrolRoute route, HttpServletRequest request) {
        String role = (String) request.getAttribute("role");
        if (!"ADMIN".equals(role)) {
            return Result.error("无权限操作");
        }
        
        boolean success = patrolRouteService.updateById(route);
        if (success) {
            String username = (String) request.getAttribute("username");
            Long userId = (Long) request.getAttribute("userId");
            String ip = request.getRemoteAddr();
            sysOperationLogService.recordLog(userId, username, "修改航线：" + route.getRouteName(), ip);
        }
        return success ? Result.success() : Result.error();
    }

    @DeleteMapping("/{id}")
    public Result<Void> deleteRoute(@PathVariable Long id, HttpServletRequest request) {
        String role = (String) request.getAttribute("role");
        if (!"ADMIN".equals(role)) {
            return Result.error("无权限操作");
        }
        
        PatrolRoute route = patrolRouteService.getById(id);
        boolean success = patrolRouteService.deleteRoute(id);
        if (success && route != null) {
            String username = (String) request.getAttribute("username");
            Long userId = (Long) request.getAttribute("userId");
            String ip = request.getRemoteAddr();
            sysOperationLogService.recordLog(userId, username, "删除航线：" + route.getRouteName(), ip);
        }
        return success ? Result.success() : Result.error("航线已绑定任务，无法删除");
    }
}

package com.drone.patrol.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.drone.patrol.common.Result;
import com.drone.patrol.entity.SysUser;
import com.drone.patrol.service.SysOperationLogService;
import com.drone.patrol.service.SysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@RestController
@RequestMapping("/api/user")
public class SysUserController {

    @Autowired
    private SysUserService sysUserService;

    @Autowired
    private SysOperationLogService sysOperationLogService;

    @GetMapping("/page")
    public Result<Page<SysUser>> getUserPage(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String role) {
        return Result.success(sysUserService.getUserPage(pageNum, pageSize, username, role));
    }

    @GetMapping("/{id}")
    public Result<SysUser> getUserById(@PathVariable Long id) {
        return Result.success(sysUserService.getById(id));
    }

    @GetMapping("/current")
    public Result<SysUser> getCurrentUser(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        return Result.success(sysUserService.getById(userId));
    }

    @PostMapping
    public Result<Void> addUser(@RequestBody SysUser user, HttpServletRequest request) {
        String role = (String) request.getAttribute("role");
        if (!"ADMIN".equals(role)) {
            return Result.error("无权限操作");
        }
        
        if (!sysUserService.checkUsernameUnique(user.getUsername(), null)) {
            return Result.error("账号已存在");
        }
        
        boolean success = sysUserService.addUser(user);
        if (success) {
            String username = (String) request.getAttribute("username");
            Long userId = (Long) request.getAttribute("userId");
            String ip = request.getRemoteAddr();
            sysOperationLogService.recordLog(userId, username, "新增用户：" + user.getUsername(), ip);
        }
        return success ? Result.success() : Result.error();
    }

    @PutMapping
    public Result<Void> updateUser(@RequestBody SysUser user, HttpServletRequest request) {
        String role = (String) request.getAttribute("role");
        if (!"ADMIN".equals(role)) {
            return Result.error("无权限操作");
        }
        
        if (!sysUserService.checkUsernameUnique(user.getUsername(), user.getId())) {
            return Result.error("账号已存在");
        }
        
        boolean success = sysUserService.updateUser(user);
        if (success) {
            String username = (String) request.getAttribute("username");
            Long userId = (Long) request.getAttribute("userId");
            String ip = request.getRemoteAddr();
            sysOperationLogService.recordLog(userId, username, "修改用户：" + user.getUsername(), ip);
        }
        return success ? Result.success() : Result.error();
    }

    @DeleteMapping("/{id}")
    public Result<Void> deleteUser(@PathVariable Long id, HttpServletRequest request) {
        String role = (String) request.getAttribute("role");
        if (!"ADMIN".equals(role)) {
            return Result.error("无权限操作");
        }
        
        SysUser user = sysUserService.getById(id);
        boolean success = sysUserService.removeById(id);
        if (success && user != null) {
            String username = (String) request.getAttribute("username");
            Long userId = (Long) request.getAttribute("userId");
            String ip = request.getRemoteAddr();
            sysOperationLogService.recordLog(userId, username, "删除用户：" + user.getUsername(), ip);
        }
        return success ? Result.success() : Result.error();
    }

    @PutMapping("/password")
    public Result<Void> updatePassword(@RequestBody Map<String, String> params, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        String oldPassword = params.get("oldPassword");
        String newPassword = params.get("newPassword");
        
        boolean success = sysUserService.updatePassword(userId, oldPassword, newPassword);
        if (success) {
            String username = (String) request.getAttribute("username");
            String ip = request.getRemoteAddr();
            sysOperationLogService.recordLog(userId, username, "修改密码", ip);
        }
        return success ? Result.success() : Result.error("原密码错误");
    }
}

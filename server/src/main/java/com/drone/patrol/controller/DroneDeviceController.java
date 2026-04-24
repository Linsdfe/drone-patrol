package com.drone.patrol.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.drone.patrol.common.Result;
import com.drone.patrol.entity.DroneDevice;
import com.drone.patrol.service.DroneDeviceService;
import com.drone.patrol.service.SysOperationLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/device")
public class DroneDeviceController {

    @Autowired
    private DroneDeviceService droneDeviceService;

    @Autowired
    private SysOperationLogService sysOperationLogService;

    @GetMapping("/page")
    public Result<Page<DroneDevice>> getDevicePage(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String deviceCode,
            @RequestParam(required = false) String deviceName,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String owner) {
        return Result.success(droneDeviceService.getDevicePage(pageNum, pageSize, deviceCode, deviceName, status, owner));
    }

    @GetMapping("/{id}")
    public Result<DroneDevice> getDeviceById(@PathVariable Long id) {
        return Result.success(droneDeviceService.getById(id));
    }

    @GetMapping("/list/normal")
    public Result<Page<DroneDevice>> getNormalDevices() {
        return Result.success(droneDeviceService.getDevicePage(1, 100, null, null, "NORMAL", null));
    }

    @PostMapping
    public Result<Void> addDevice(@RequestBody DroneDevice device, HttpServletRequest request) {
        String role = (String) request.getAttribute("role");
        if (!"ADMIN".equals(role)) {
            return Result.error("无权限操作");
        }
        
        if (!droneDeviceService.checkDeviceCodeUnique(device.getDeviceCode(), null)) {
            return Result.error("设备编号已存在");
        }
        
        boolean success = droneDeviceService.save(device);
        if (success) {
            String username = (String) request.getAttribute("username");
            Long userId = (Long) request.getAttribute("userId");
            String ip = request.getRemoteAddr();
            sysOperationLogService.recordLog(userId, username, "新增设备：" + device.getDeviceCode(), ip);
        }
        return success ? Result.success() : Result.error();
    }

    @PutMapping
    public Result<Void> updateDevice(@RequestBody DroneDevice device, HttpServletRequest request) {
        String role = (String) request.getAttribute("role");
        if (!"ADMIN".equals(role)) {
            return Result.error("无权限操作");
        }
        
        if (!droneDeviceService.checkDeviceCodeUnique(device.getDeviceCode(), device.getId())) {
            return Result.error("设备编号已存在");
        }
        
        boolean success = droneDeviceService.updateById(device);
        if (success) {
            String username = (String) request.getAttribute("username");
            Long userId = (Long) request.getAttribute("userId");
            String ip = request.getRemoteAddr();
            sysOperationLogService.recordLog(userId, username, "修改设备：" + device.getDeviceCode(), ip);
        }
        return success ? Result.success() : Result.error();
    }

    @PutMapping("/status")
    public Result<Void> updateDeviceStatus(@RequestParam Long id, @RequestParam String status, HttpServletRequest request) {
        String role = (String) request.getAttribute("role");
        if (!"ADMIN".equals(role)) {
            return Result.error("无权限操作");
        }
        
        boolean success = droneDeviceService.updateDeviceStatus(id, status);
        if (success) {
            String username = (String) request.getAttribute("username");
            Long userId = (Long) request.getAttribute("userId");
            String ip = request.getRemoteAddr();
            sysOperationLogService.recordLog(userId, username, "修改设备状态：" + id + " -> " + status, ip);
        }
        return success ? Result.success() : Result.error();
    }

    @DeleteMapping("/{id}")
    public Result<Void> deleteDevice(@PathVariable Long id, HttpServletRequest request) {
        String role = (String) request.getAttribute("role");
        if (!"ADMIN".equals(role)) {
            return Result.error("无权限操作");
        }
        
        DroneDevice device = droneDeviceService.getById(id);
        boolean success = droneDeviceService.deleteDevice(id);
        if (success && device != null) {
            String username = (String) request.getAttribute("username");
            Long userId = (Long) request.getAttribute("userId");
            String ip = request.getRemoteAddr();
            sysOperationLogService.recordLog(userId, username, "删除设备：" + device.getDeviceCode(), ip);
        }
        return success ? Result.success() : Result.error("设备已绑定任务，无法删除");
    }
}

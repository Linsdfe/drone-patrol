package com.drone.patrol.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.drone.patrol.common.Result;
import com.drone.patrol.entity.SysOperationLog;
import com.drone.patrol.service.SysOperationLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/log")
public class SysOperationLogController {

    @Autowired
    private SysOperationLogService sysOperationLogService;

    @GetMapping("/page")
    public Result<Page<SysOperationLog>> getLogPage(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String startTime,
            @RequestParam(required = false) String endTime) {
        return Result.success(sysOperationLogService.getLogPage(pageNum, pageSize, username, startTime, endTime));
    }
}

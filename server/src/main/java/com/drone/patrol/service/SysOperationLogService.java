package com.drone.patrol.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.drone.patrol.entity.SysOperationLog;
import com.drone.patrol.mapper.SysOperationLogMapper;
import org.springframework.stereotype.Service;

@Service
public class SysOperationLogService extends ServiceImpl<SysOperationLogMapper, SysOperationLog> {

    public void recordLog(Long userId, String username, String operation, String ip) {
        SysOperationLog log = new SysOperationLog();
        log.setUserId(userId);
        log.setUsername(username);
        log.setOperation(operation);
        log.setIp(ip);
        save(log);
    }

    public Page<SysOperationLog> getLogPage(Integer pageNum, Integer pageSize, String username, String startTime, String endTime) {
        Page<SysOperationLog> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<SysOperationLog> wrapper = new LambdaQueryWrapper<>();
        
        if (username != null && !username.isEmpty()) {
            wrapper.like(SysOperationLog::getUsername, username);
        }
        if (startTime != null && !startTime.isEmpty()) {
            wrapper.ge(SysOperationLog::getCreateTime, startTime);
        }
        if (endTime != null && !endTime.isEmpty()) {
            wrapper.le(SysOperationLog::getCreateTime, endTime);
        }
        
        wrapper.orderByDesc(SysOperationLog::getCreateTime);
        return page(page, wrapper);
    }
}

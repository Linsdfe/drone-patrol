package com.drone.patrol.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.drone.patrol.entity.PatrolResult;
import com.drone.patrol.entity.PatrolTask;
import com.drone.patrol.mapper.PatrolTaskMapper;
import com.drone.patrol.mapper.PatrolResultMapper;
import com.drone.patrol.mapper.DroneDeviceMapper;
import com.drone.patrol.entity.DroneDevice;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class PatrolTaskService extends ServiceImpl<PatrolTaskMapper, PatrolTask> {

    @Autowired
    private PatrolResultMapper patrolResultMapper;

    @Autowired
    private DroneDeviceMapper droneDeviceMapper;

    public Page<PatrolTask> getTaskPage(Integer pageNum, Integer pageSize, String taskName, String executorName, String deviceName, String status, String startTime, String endTime) {
        Page<PatrolTask> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<PatrolTask> wrapper = new LambdaQueryWrapper<>();
        
        if (taskName != null && !taskName.isEmpty()) {
            wrapper.like(PatrolTask::getTaskName, taskName);
        }
        if (executorName != null && !executorName.isEmpty()) {
            wrapper.like(PatrolTask::getExecutorName, executorName);
        }
        if (status != null && !status.isEmpty()) {
            wrapper.eq(PatrolTask::getStatus, status);
        }
        if (startTime != null && !startTime.isEmpty()) {
            wrapper.ge(PatrolTask::getCreateTime, startTime);
        }
        if (endTime != null && !endTime.isEmpty()) {
            wrapper.le(PatrolTask::getCreateTime, endTime);
        }
        
        wrapper.orderByDesc(PatrolTask::getCreateTime);
        return page(page, wrapper);
    }

    @Transactional
    public boolean startTask(Long taskId) {
        PatrolTask task = getById(taskId);
        if (task == null || !"PENDING".equals(task.getStatus())) {
            return false;
        }
        
        task.setStatus("EXECUTING");
        task.setStartTime(LocalDateTime.now());
        task.setProgress(0);
        updateById(task);
        
        DroneDevice device = new DroneDevice();
        device.setId(task.getDeviceId());
        device.setStatus("IN_TASK");
        droneDeviceMapper.updateById(device);
        
        return true;
    }

    @Transactional
    public boolean completeTask(Long taskId) {
        PatrolTask task = getById(taskId);
        if (task == null || !"EXECUTING".equals(task.getStatus())) {
            return false;
        }
        
        task.setStatus("COMPLETED");
        task.setEndTime(LocalDateTime.now());
        task.setProgress(100);
        updateById(task);
        
        DroneDevice device = new DroneDevice();
        device.setId(task.getDeviceId());
        device.setStatus("NORMAL");
        droneDeviceMapper.updateById(device);
        
        PatrolResult result = new PatrolResult();
        result.setResultCode("RES" + System.currentTimeMillis());
        result.setTaskId(taskId);
        result.setDeviceId(task.getDeviceId());
        result.setRouteId(task.getRouteId());
        result.setExecutorId(task.getExecutorId());
        result.setExecutorName(task.getExecutorName());
        result.setCompleteTime(LocalDateTime.now());
        result.setSummary("巡防任务已完成");
        patrolResultMapper.insert(result);
        
        return true;
    }

    @Transactional
    public boolean cancelTask(Long taskId) {
        PatrolTask task = getById(taskId);
        if (task == null || "COMPLETED".equals(task.getStatus()) || "CANCELLED".equals(task.getStatus())) {
            return false;
        }
        
        task.setStatus("CANCELLED");
        task.setEndTime(LocalDateTime.now());
        updateById(task);
        
        if ("EXECUTING".equals(task.getStatus())) {
            DroneDevice device = new DroneDevice();
            device.setId(task.getDeviceId());
            device.setStatus("NORMAL");
            droneDeviceMapper.updateById(device);
        }
        
        return true;
    }

    public boolean updateProgress(Long taskId, Integer progress) {
        PatrolTask task = getById(taskId);
        if (task == null || !"EXECUTING".equals(task.getStatus())) {
            return false;
        }
        
        task.setProgress(progress);
        return updateById(task);
    }
}

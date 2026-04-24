package com.drone.patrol.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.drone.patrol.entity.DroneDevice;
import com.drone.patrol.entity.PatrolTask;
import com.drone.patrol.mapper.DroneDeviceMapper;
import com.drone.patrol.mapper.PatrolTaskMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DroneDeviceService extends ServiceImpl<DroneDeviceMapper, DroneDevice> {

    @Autowired
    private PatrolTaskMapper patrolTaskMapper;

    public Page<DroneDevice> getDevicePage(Integer pageNum, Integer pageSize, String deviceCode, String deviceName, String status, String owner) {
        Page<DroneDevice> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<DroneDevice> wrapper = new LambdaQueryWrapper<>();
        
        if (deviceCode != null && !deviceCode.isEmpty()) {
            wrapper.like(DroneDevice::getDeviceCode, deviceCode);
        }
        if (deviceName != null && !deviceName.isEmpty()) {
            wrapper.like(DroneDevice::getDeviceName, deviceName);
        }
        if (status != null && !status.isEmpty()) {
            wrapper.eq(DroneDevice::getStatus, status);
        }
        if (owner != null && !owner.isEmpty()) {
            wrapper.like(DroneDevice::getOwner, owner);
        }
        
        wrapper.orderByDesc(DroneDevice::getCreateTime);
        return page(page, wrapper);
    }

    public boolean checkDeviceCodeUnique(String deviceCode, Long excludeId) {
        LambdaQueryWrapper<DroneDevice> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(DroneDevice::getDeviceCode, deviceCode);
        if (excludeId != null) {
            wrapper.ne(DroneDevice::getId, excludeId);
        }
        return count(wrapper) == 0;
    }

    public boolean deleteDevice(Long id) {
        LambdaQueryWrapper<PatrolTask> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PatrolTask::getDeviceId, id);
        if (patrolTaskMapper.selectCount(wrapper) > 0) {
            return false;
        }
        return removeById(id);
    }

    public boolean updateDeviceStatus(Long id, String status) {
        DroneDevice device = new DroneDevice();
        device.setId(id);
        device.setStatus(status);
        return updateById(device);
    }
}

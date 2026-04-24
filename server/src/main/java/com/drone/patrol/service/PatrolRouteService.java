package com.drone.patrol.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.drone.patrol.entity.PatrolRoute;
import com.drone.patrol.entity.PatrolTask;
import com.drone.patrol.mapper.PatrolRouteMapper;
import com.drone.patrol.mapper.PatrolTaskMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PatrolRouteService extends ServiceImpl<PatrolRouteMapper, PatrolRoute> {

    @Autowired
    private PatrolTaskMapper patrolTaskMapper;

    public Page<PatrolRoute> getRoutePage(Integer pageNum, Integer pageSize, String routeName) {
        Page<PatrolRoute> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<PatrolRoute> wrapper = new LambdaQueryWrapper<>();
        
        if (routeName != null && !routeName.isEmpty()) {
            wrapper.like(PatrolRoute::getRouteName, routeName);
        }
        
        wrapper.orderByDesc(PatrolRoute::getCreateTime);
        return page(page, wrapper);
    }

    public boolean deleteRoute(Long id) {
        LambdaQueryWrapper<PatrolTask> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PatrolTask::getRouteId, id);
        if (patrolTaskMapper.selectCount(wrapper) > 0) {
            return false;
        }
        return removeById(id);
    }
}

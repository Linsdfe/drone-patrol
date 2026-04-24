package com.drone.patrol.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.drone.patrol.entity.PatrolRoute;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface PatrolRouteMapper extends BaseMapper<PatrolRoute> {
}

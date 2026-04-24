package com.drone.patrol.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.drone.patrol.entity.PatrolTask;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface PatrolTaskMapper extends BaseMapper<PatrolTask> {
}

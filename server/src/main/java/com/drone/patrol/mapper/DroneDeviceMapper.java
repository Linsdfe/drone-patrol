package com.drone.patrol.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.drone.patrol.entity.DroneDevice;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface DroneDeviceMapper extends BaseMapper<DroneDevice> {
}

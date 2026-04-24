package com.drone.patrol.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("patrol_route")
public class PatrolRoute {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String routeName;

    private String points;

    private Integer expectedDuration;

    private Integer flightHeight;

    private String remark;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}

package com.drone.patrol.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("patrol_task")
public class PatrolTask {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String taskName;

    private Long deviceId;

    private Long routeId;

    private Long executorId;

    private String executorName;

    private String status;

    private LocalDateTime startTime;

    private LocalDateTime endTime;

    private Integer progress;

    private String remark;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}

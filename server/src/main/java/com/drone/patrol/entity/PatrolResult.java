package com.drone.patrol.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("patrol_result")
public class PatrolResult {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String resultCode;

    private Long taskId;

    private Long deviceId;

    private Long routeId;

    private Long executorId;

    private String executorName;

    private LocalDateTime completeTime;

    private String summary;

    private String discovery;

    private String handling;

    private String aiResult;

    private String remark;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}

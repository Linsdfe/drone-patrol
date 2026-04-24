package com.drone.patrol;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 无人机巡防管控系统 - 主启动类
 *
 * 功能说明：
 * - 启动Spring Boot应用程序
 * - 自动扫描并注册Mapper接口
 * - 配置Web请求处理、数据安全、数据持久化等组件
 *
 * 使用方法：
 * - 直接运行main方法启动应用
 * - 访问 http://localhost:8080 查看API文档（如配置了Swagger）
 * - 默认端口：8080
 *
 * @author Drone Patrol Team
 * @version 1.0.0
 */
@SpringBootApplication
@MapperScan("com.drone.patrol.mapper")
public class DronePatrolApplication {

    /**
     * 应用主入口
     *
     * @param args 命令行参数
     */
    public static void main(String[] args) {
        SpringApplication.run(DronePatrolApplication.class, args);
    }
}

# 无人机巡防管控系统

<div align="center">

![Version](https://img.shields.io/badge/version-1.0.0-blue)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-2.7.18-green)
![Vue](https://img.shields.io/badge/Vue-3-green)
![Element Plus](https://img.shields.io/badge/Element%20Plus-最新-blue)

</div>

## 项目简介

这是一个面向高校课程设计和毕业设计的无人机巡防管控系统，采用前后端分离（Frontend-Backend Separation）架构，实现了完整的设备管理（Device Management）、航线规划（Route Planning）、任务执行（Task Execution）和结果管理（Result Management）业务闭环。

### 核心特性

- 🎯 **前后端分离**：Spring Boot + Vue 3 现代化架构
- 🔐 **安全认证**：JWT（JSON Web Token）无状态（Stateless）认证
- 🤖 **AI智能分析**：集成智谱AI（ZhipuAI），智能评估巡防风险
- 📊 **数据导出**：支持Excel报表导出
- 🗺️ **地图集成**：高德地图（Amap）航线可视化
- 📱 **响应式设计**：适配多种屏幕尺寸

---

## 系统架构图（System Architecture）

```mermaid
graph TB
    subgraph 前端["前端 Frontend - Vue 3"]
        UI["Element Plus<br/>UI组件库"]
        Router["Vue Router<br/>路由管理"]
        Store["Pinia Store<br/>状态管理"]
        Amap["高德地图 Amap<br/>航线可视化"]
        Axios["Axios<br/>HTTP客户端"]
    end

    subgraph 后端["后端 Backend - Spring Boot"]
        Controller["Controller 控制器层<br/>接收请求"]
        Service["Service 服务层<br/>业务逻辑"]
        Mapper["Mapper 数据访问层<br/>MyBatis-Plus"]
        Interceptor["JwtInterceptor<br/>JWT拦截器"]
        Config["Config 配置类<br/>安全/CORS/初始化"]
    end

    subgraph 中间件["中间件 Middleware"]
        Redis["Redis<br/>缓存"]
    end

    subgraph 数据库["数据库 Database"]
        MySQL["MySQL 8.0<br/>关系数据库"]
    end

    subgraph 外部服务["外部服务 External Service"]
        ZhipuAI["智谱AI ZhipuAI<br/>GLM-4 大模型"]
    end

    UI --> Router
    Router --> Store
    Store --> Axios
    Axios -->|"HTTP + JWT Token"| Controller
    Amap -.->|"地图API"| UI

    Controller --> Interceptor
    Interceptor -->|"验证Token"| Controller
    Controller --> Service
    Service --> Mapper
    Mapper --> MySQL
    Service --> Redis
    Service -->|"API调用"| ZhipuAI
```

---

## 实体类图（Entity Class Diagram）

系统共包含6个核心实体（Entity），它们之间的关系如下：

```mermaid
classDiagram
    class SysUser {
        +Long id
        +String username
        +String password
        +String nickname
        +String role
        +LocalDateTime createTime
        +LocalDateTime updateTime
    }

    class DroneDevice {
        +Long id
        +String deviceCode
        +String deviceName
        +String deviceModel
        +Integer batteryLife
        +String cameraParam
        +String owner
        +String status
        +String remark
        +LocalDateTime createTime
        +LocalDateTime updateTime
    }

    class PatrolRoute {
        +Long id
        +String routeName
        +String points
        +Integer expectedDuration
        +Integer flightHeight
        +String remark
        +LocalDateTime createTime
        +LocalDateTime updateTime
    }

    class PatrolTask {
        +Long id
        +String taskName
        +Long deviceId
        +Long routeId
        +Long executorId
        +String executorName
        +String status
        +LocalDateTime startTime
        +LocalDateTime endTime
        +Integer progress
        +String remark
        +LocalDateTime createTime
        +LocalDateTime updateTime
    }

    class PatrolResult {
        +Long id
        +String resultCode
        +Long taskId
        +Long deviceId
        +Long routeId
        +Long executorId
        +String executorName
        +LocalDateTime completeTime
        +String summary
        +String discovery
        +String handling
        +String aiResult
        +String remark
        +LocalDateTime createTime
        +LocalDateTime updateTime
    }

    class SysOperationLog {
        +Long id
        +Long userId
        +String username
        +String operation
        +String ip
        +LocalDateTime createTime
    }

    SysUser "1" --> "*" PatrolTask : executorId 执行人
    SysUser "1" --> "*" SysOperationLog : userId 操作人
    DroneDevice "1" --> "*" PatrolTask : deviceId 关联设备
    PatrolRoute "1" --> "*" PatrolTask : routeId 关联航线
    PatrolTask "1" --> "1" PatrolResult : taskId 关联任务
    DroneDevice "1" --> "*" PatrolResult : deviceId
    PatrolRoute "1" --> "*" PatrolResult : routeId
```

---

## 数据库ER图（Entity-Relationship Diagram）

```mermaid
erDiagram
    SYS_USER {
        bigint id PK "主键ID"
        varchar username UK "账号"
        varchar password "密码"
        varchar nickname "昵称"
        varchar role "角色 ADMIN/USER"
        datetime create_time "创建时间"
        datetime update_time "更新时间"
    }

    DRONE_DEVICE {
        bigint id PK "主键ID"
        varchar device_code UK "设备编号"
        varchar device_name "设备名称"
        varchar device_model "设备型号"
        int battery_life "续航时长(分钟)"
        varchar camera_param "摄像头参数"
        varchar owner "归属人"
        varchar status "状态 NORMAL/MAINTENANCE/SCRAPPED/IN_TASK"
        varchar remark "备注"
        datetime create_time "创建时间"
        datetime update_time "更新时间"
    }

    PATROL_ROUTE {
        bigint id PK "主键ID"
        varchar route_name "航线名称"
        text points "航线点位JSON"
        int expected_duration "预计巡防时长(分钟)"
        int flight_height "飞行高度(米)"
        varchar remark "备注"
        datetime create_time "创建时间"
        datetime update_time "更新时间"
    }

    PATROL_TASK {
        bigint id PK "主键ID"
        varchar task_name "任务名称"
        bigint device_id FK "关联设备ID"
        bigint route_id FK "关联航线ID"
        bigint executor_id FK "执行人ID"
        varchar executor_name "执行人姓名"
        varchar status "状态 PENDING/EXECUTING/COMPLETED/CANCELLED"
        datetime start_time "开始时间"
        datetime end_time "结束时间"
        int progress "执行进度(0-100)"
        varchar remark "备注"
        datetime create_time "创建时间"
        datetime update_time "更新时间"
    }

    PATROL_RESULT {
        bigint id PK "主键ID"
        varchar result_code UK "结果编号"
        bigint task_id FK "关联任务ID"
        bigint device_id FK "关联设备ID"
        bigint route_id FK "关联航线ID"
        bigint executor_id FK "执行人ID"
        varchar executor_name "执行人姓名"
        datetime complete_time "完成时间"
        varchar summary "巡防概述"
        text discovery "发现情况"
        text handling "处理情况"
        text ai_result "AI识别结果"
        varchar remark "备注"
        datetime create_time "创建时间"
        datetime update_time "更新时间"
    }

    SYS_OPERATION_LOG {
        bigint id PK "主键ID"
        bigint user_id FK "操作人ID"
        varchar username "操作人账号"
        varchar operation "操作内容"
        varchar ip "操作IP"
        datetime create_time "操作时间"
    }

    SYS_USER ||--o{ PATROL_TASK : "executor_id 执行"
    SYS_USER ||--o{ SYS_OPERATION_LOG : "user_id 操作"
    DRONE_DEVICE ||--o{ PATROL_TASK : "device_id 关联"
    PATROL_ROUTE ||--o{ PATROL_TASK : "route_id 关联"
    PATROL_TASK ||--|| PATROL_RESULT : "task_id 生成"
    DRONE_DEVICE ||--o{ PATROL_RESULT : "device_id"
    PATROL_ROUTE ||--o{ PATROL_RESULT : "route_id"
```

---

## 核心业务流程图（Business Flow）

```mermaid
flowchart TD
    A["管理员登录系统"] --> B["设备管理<br/>录入无人机设备"]
    B --> C["航线管理<br/>基于高德地图规划航线"]
    C --> D["任务管理<br/>创建巡防任务<br/>绑定设备+航线"]
    D --> E{"启动任务"}

    E -->|"开始执行"| F["任务状态: EXECUTING<br/>设备状态: IN_TASK"]
    F --> G["更新任务进度<br/>progress: 0→100"]
    G --> H{"任务完成?"}

    H -->|"是"| I["任务状态: COMPLETED<br/>设备状态: NORMAL"]
    H -->|"否"| G

    E -->|"取消任务"| J["任务状态: CANCELLED<br/>设备状态恢复: NORMAL"]

    I --> K["自动生成巡防结果<br/>PatrolResult"]
    K --> L["编辑巡防结果<br/>填写概述/发现/处理"]
    L --> M["AI智能分析<br/>调用智谱AI评估风险"]
    M --> N["查看分析报告<br/>风险评分+风险等级"]
    N --> O["导出Excel报表"]

    style A fill:#e1f5fe
    style B fill:#f3e5f5
    style C fill:#e8f5e9
    style D fill:#fff3e0
    style F fill:#fff9c4
    style I fill:#c8e6c9
    style K fill:#bbdefb
    style M fill:#f8bbd0
    style O fill:#d1c4e9
```

---

## 时序图（Sequence Diagram）

### 用户登录认证流程（Login Authentication）

```mermaid
sequenceDiagram
    actor User as 用户
    participant Vue as Vue前端
    participant Axios as Axios拦截器
    participant Auth as AuthController
    participant Svc as AuthService
    participant DB as SysUserMapper
    participant JWT as JwtUtil

    User->>Vue: 输入账号密码
    Vue->>Axios: POST /api/auth/login
    Axios->>Auth: login(LoginDTO)
    Auth->>Svc: login(loginDTO)
    Svc->>DB: selectOne(username)
    DB-->>Svc: SysUser对象

    alt 用户不存在
        Svc-->>Auth: Result.error("用户不存在")
        Auth-->>Vue: 错误响应
        Vue-->>User: 提示"用户不存在"
    else 用户存在
        Svc->>Svc: BCrypt.matches(password)
        alt 密码错误
            Svc-->>Auth: Result.error("密码错误")
            Auth-->>Vue: 错误响应
            Vue-->>User: 提示"密码错误"
        else 密码正确
            Svc->>JWT: generateToken(userId, username, role)
            JWT-->>Svc: JWT Token字符串
            Svc-->>Auth: Result.success(LoginVO)
            Auth-->>Axios: 响应数据
            Axios-->>Vue: {token, userId, username, nickname, role}
            Vue->>Vue: Pinia Store存储用户信息<br/>localStorage持久化
            Vue-->>User: 跳转到首页Dashboard
        end
    end
```

### 任务执行与完成流程（Task Execution & Completion）

```mermaid
sequenceDiagram
    actor Admin as 管理员
    participant TC as PatrolTaskController
    participant TS as PatrolTaskService
    participant TM as PatrolTaskMapper
    participant DM as DroneDeviceMapper
    participant RM as PatrolResultMapper

    Admin->>TC: PUT /api/task/start/{id}
    TC->>TS: startTask(taskId)
    TS->>TM: getById(taskId)
    TM-->>TS: PatrolTask对象

    alt 任务状态非PENDING
        TS-->>TC: return false
        TC-->>Admin: 操作失败
    else 任务状态为PENDING
        TS->>TS: 设置status=EXECUTING<br/>startTime=now<br/>progress=0
        TS->>TM: updateById(task)
        TS->>DM: updateById(device)<br/>设置status=IN_TASK
        DM-->>TS: 更新成功
        TS-->>TC: return true
        TC-->>Admin: 操作成功
    end

    Admin->>TC: PUT /api/task/complete/{id}
    TC->>TS: completeTask(taskId)
    TS->>TM: getById(taskId)
    TM-->>TS: PatrolTask对象

    alt 任务状态非EXECUTING
        TS-->>TC: return false
    else 任务状态为EXECUTING
        TS->>TS: 设置status=COMPLETED<br/>endTime=now<br/>progress=100
        TS->>TM: updateById(task)
        TS->>DM: updateById(device)<br/>设置status=NORMAL
        TS->>RM: insert(patrolResult)<br/>自动生成巡防结果
        RM-->>TS: 插入成功
        TS-->>TC: return true
        TC-->>Admin: 操作成功，结果已生成
    end
```

### AI智能分析流程（AI Analysis）

```mermaid
sequenceDiagram
    actor User as 用户
    participant AC as AiRecognitionController
    participant ARS as AiRecognitionService
    participant PRS as PatrolResultService
    participant Zhipu as 智谱AI API

    User->>AC: POST /api/ai/recognize/{resultId}
    AC->>PRS: getById(resultId)
    PRS-->>AC: PatrolResult对象

    alt 结果不存在
        AC-->>User: Result.error("巡防结果不存在")
    else 结果存在
        AC->>ARS: analyzePatrolResult(result)
        ARS->>ARS: buildPrompt(result)<br/>构建分析提示词

        loop 最多重试3次
            ARS->>Zhipu: HTTP POST /chat/completions<br/>Bearer {apiKey}
            Zhipu-->>ARS: AI响应JSON
        end

        alt AI调用成功
            ARS->>ARS: parseAiResponse()<br/>解析风险评分+风险等级
            ARS-->>AC: {aiResult, riskScore, riskLevel}
            AC->>PRS: updateById(result)<br/>保存AI分析结果
            AC-->>User: Result.success(analysis)
        else AI调用失败
            ARS->>ARS: fallbackAnalyze(result)<br/>本地备用分析
            ARS-->>AC: {aiResult, riskScore, riskLevel, fallback:true}
            AC-->>User: Result.success(analysis)
        end
    end
```

---

## 后端分层架构图（Backend Layered Architecture）

```mermaid
graph LR
    subgraph 表现层["表现层 Presentation Layer"]
        C1[AuthController<br/>认证]
        C2[DroneDeviceController<br/>设备管理]
        C3[PatrolRouteController<br/>航线管理]
        C4[PatrolTaskController<br/>任务管理]
        C5[PatrolResultController<br/>结果管理]
        C6[SysUserController<br/>用户管理]
        C7[SysOperationLogController<br/>操作日志]
        C8[AiRecognitionController<br/>AI识别]
    end

    subgraph 拦截层["拦截层 Interceptor Layer"]
        JWT[JwtInterceptor<br/>JWT令牌验证]
        CORS[WebConfig<br/>跨域CORS配置]
        EX[GlobalExceptionHandler<br/>全局异常处理]
    end

    subgraph 业务层["业务层 Service Layer"]
        S1[AuthService<br/>认证服务]
        S2[DroneDeviceService<br/>设备服务]
        S3[PatrolRouteService<br/>航线服务]
        S4[PatrolTaskService<br/>任务服务]
        S5[PatrolResultService<br/>结果服务+Excel导出]
        S6[SysUserService<br/>用户服务]
        S7[SysOperationLogService<br/>日志服务]
        S8[AiRecognitionService<br/>AI识别服务]
    end

    subgraph 数据层["数据层 Data Access Layer"]
        M1[SysUserMapper]
        M2[DroneDeviceMapper]
        M3[PatrolRouteMapper]
        M4[PatrolTaskMapper]
        M5[PatrolResultMapper]
        M6[SysOperationLogMapper]
    end

    C1 & C2 & C3 & C4 & C5 & C6 & C7 & C8 --> JWT
    JWT --> S1 & S2 & S3 & S4 & S5 & S6 & S7 & S8
    S1 & S6 --> M1
    S2 --> M2
    S3 --> M3
    S4 --> M4
    S5 --> M5
    S7 --> M6
```

---

## 技术栈

### 后端（Backend）

| 技术 | 版本 | 说明 |
|------|------|------|
| Spring Boot | 2.7.18 | 核心框架（Core Framework） |
| MySQL | 8.0 | 关系数据库（Relational Database） |
| Redis | - | 缓存中间件（Cache Middleware） |
| MyBatis-Plus | 3.5.5 | ORM框架（对象关系映射） |
| JWT (jjwt) | 0.11.5 | 身份认证（Authentication） |
| Apache POI | 5.2.5 | Excel操作（Spreadsheet Export） |
| Hutool | 5.8.26 | 工具类库（Utility Library） |
| Spring Security | - | 安全框架（Security Framework），禁用默认登录 |
| BCrypt | - | 密码加密（Password Encryption） |
| Lombok | - | 代码简化（Code Simplification） |

### 前端（Frontend）

| 技术 | 版本 | 说明 |
|------|------|------|
| Vue | 3 | 核心框架（Core Framework） |
| Vite | 5 | 构建工具（Build Tool） |
| Element Plus | 最新 | UI组件库（UI Component Library） |
| Vue Router | 4 | 路由管理（Router Management） |
| Pinia | - | 状态管理（State Management） |
| Axios | - | HTTP客户端（HTTP Client） |
| 高德地图 Amap | API | 地图服务（Map Service） |
| vue-i18n | 9.14 | 国际化（Internationalization） |

---

## 功能模块

### 功能模块总览图

```mermaid
graph TD
    subgraph 系统管理["系统管理 System Management"]
        S1["账号密码登录<br/>JWT认证"]
        S2["个人中心<br/>信息查看/密码修改"]
        S3["用户管理<br/>管理员专属 CRUD"]
        S4["操作日志<br/>操作记录查询"]
    end

    subgraph 设备管理["设备管理 Device Management"]
        D1["设备信息录入/编辑/删除"]
        D2["设备状态切换<br/>NORMAL/MAINTENANCE/SCRAPPED/IN_TASK"]
        D3["设备详情查看"]
    end

    subgraph 航线管理["航线管理 Route Management"]
        R1["高德地图可视化航线规划"]
        R2["航线信息管理 CRUD"]
        R3["航线地图回显"]
    end

    subgraph 任务管理["任务管理 Task Management"]
        T1["任务创建<br/>绑定设备+航线"]
        T2["任务状态管理<br/>PENDING→EXECUTING→COMPLETED/CANCELLED"]
        T3["任务执行进度展示<br/>0-100%"]
    end

    subgraph 结果管理["结果管理 Result Management"]
        RE1["任务完成自动生成结果"]
        RE2["结果编辑<br/>概述/发现/处理"]
        RE3["AI智能分析<br/>风险评分+风险等级"]
        RE4["Excel导出"]
    end

    系统管理 --> 设备管理
    设备管理 --> 航线管理
    航线管理 --> 任务管理
    任务管理 --> 结果管理
```

1. **系统管理（System Management）**
   - 账号密码登录（JWT认证 Authentication）
   - 个人中心（信息查看、密码修改）
   - 用户管理（管理员专属，CRUD增删改查）
   - 操作日志记录（Operation Log）

2. **设备管理（Device Management）**
   - 设备信息录入、编辑、删除
   - 设备状态切换（NORMAL正常 / MAINTENANCE维修中 / SCRAPPED已报废 / IN_TASK任务中）
   - 设备详情查看

3. **航线管理（Route Management）**
   - 基于高德地图的可视化航线规划
   - 航线信息管理（CRUD增删改查）
   - 航线地图回显

4. **任务管理（Task Management）**
   - 任务创建（绑定设备和航线）
   - 任务状态管理（PENDING待执行 → EXECUTING执行中 → COMPLETED已完成 / CANCELLED已取消）
   - 任务执行进度展示（Progress 0-100%）

5. **结果管理（Result Management）**
   - 任务完成自动生成结果
   - 结果编辑（概述Summary / 发现Discovery / 处理Handling）
   - AI智能分析（风险评分Risk Score + 风险等级Risk Level）
   - Excel导出

---

## 快速开始

### 环境要求

- JDK 8+
- Node.js 16+
- MySQL 8.0
- Redis

### 注意事项

⚠️ **重要：所有命令行操作必须在 Windows CMD 中执行，禁止使用 PowerShell！**

### 1. 数据库初始化（Database Initialization）

在 CMD 中执行以下操作：

```cmd
# 登录 MySQL（请替换为您的账号密码）
mysql -u root -p

# 执行以下 SQL 创建数据库
CREATE DATABASE drone_patrol DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

# 退出 MySQL
exit;

# 导入初始化脚本（请替换为实际路径）
mysql -u root -p drone_patrol < D:\_class\drone-patrol\sql\init.sql
```

### 2. Redis 启动

```cmd
# 方式1：如果已安装为系统服务
sc start Redis

# 方式2：解压版，请替换为您的Redis目录
D:
cd D:\Redis-x64-6.2.14
redis-server.exe redis.windows.conf
```

### 3. 后端启动（Backend Startup）

```cmd
# 进入后端目录
D:
cd D:\_class\drone-patrol\server

# 修改 application.yml 中的数据库和Redis配置
# 编辑 src\main\resources\application.yml

# 使用 Maven 启动
mvn spring-boot:run
```

后端默认端口：8080

### 4. 前端启动（Frontend Startup）

打开新的 CMD 窗口：

```cmd
# 进入前端目录
D:
cd D:\_class\drone-patrol\web

# 安装依赖
npm install

# 配置高德地图 Key（可选，需要时修改 index.html）
# 编辑 index.html，替换 YOUR_AMAP_KEY

# 启动开发服务
npm run dev
```

前端默认访问地址：http://localhost:3000

### 5. 系统登录（System Login）

默认账号：
- 管理员（Administrator）：admin / admin123
- 普通用户（User）：user / admin123

---

## 项目结构

```
drone-patrol/
├── server/                 # 后端项目 Backend
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/drone/patrol/
│   │   │   │   ├── common/         # 通用类 Common
│   │   │   │   │   └── Result.java           # 统一响应封装（Unified Response）
│   │   │   │   ├── config/          # 配置类 Configuration
│   │   │   │   │   ├── GlobalExceptionHandler.java  # 全局异常处理
│   │   │   │   │   ├── InitDataConfig.java          # 初始数据配置
│   │   │   │   │   ├── MybatisPlusConfig.java       # MyBatis-Plus分页配置
│   │   │   │   │   ├── PasswordEncoderConfig.java   # 密码编码器配置
│   │   │   │   │   ├── SecurityConfig.java          # Spring Security配置
│   │   │   │   │   ├── WebConfig.java               # CORS跨域+JWT拦截器配置
│   │   │   │   │   └── ZhipuConfig.java             # 智谱AI配置
│   │   │   │   ├── controller/       # 控制器层 Controller
│   │   │   │   │   ├── AuthController.java          # 认证控制器
│   │   │   │   │   ├── DroneDeviceController.java   # 设备管理控制器
│   │   │   │   │   ├── PatrolRouteController.java   # 航线管理控制器
│   │   │   │   │   ├── PatrolTaskController.java    # 任务管理控制器
│   │   │   │   │   ├── PatrolResultController.java  # 结果管理控制器
│   │   │   │   │   ├── SysUserController.java       # 用户管理控制器
│   │   │   │   │   ├── SysOperationLogController.java # 操作日志控制器
│   │   │   │   │   └── AiRecognitionController.java   # AI识别控制器
│   │   │   │   ├── dto/              # 数据传输对象 DTO（Data Transfer Object）
│   │   │   │   │   ├── LoginDTO.java               # 登录请求参数
│   │   │   │   │   └── RegisterDTO.java            # 注册请求参数
│   │   │   │   ├── entity/            # 实体类 Entity
│   │   │   │   │   ├── SysUser.java                # 系统用户
│   │   │   │   │   ├── DroneDevice.java            # 无人机设备
│   │   │   │   │   ├── PatrolRoute.java            # 巡防航线
│   │   │   │   │   ├── PatrolTask.java             # 巡防任务
│   │   │   │   │   ├── PatrolResult.java           # 巡防结果
│   │   │   │   │   └── SysOperationLog.java        # 操作日志
│   │   │   │   ├── interceptor/       # 拦截器 Interceptor
│   │   │   │   │   └── JwtInterceptor.java         # JWT令牌验证拦截器
│   │   │   │   ├── mapper/            # 数据访问层 Mapper（DAO）
│   │   │   │   ├── service/           # 业务逻辑层 Service
│   │   │   │   │   ├── AuthService.java            # 认证服务
│   │   │   │   │   ├── DroneDeviceService.java     # 设备服务
│   │   │   │   │   ├── PatrolRouteService.java     # 航线服务
│   │   │   │   │   ├── PatrolTaskService.java      # 任务服务
│   │   │   │   │   ├── PatrolResultService.java    # 结果服务（含Excel导出）
│   │   │   │   │   ├── SysUserService.java         # 用户服务
│   │   │   │   │   ├── SysOperationLogService.java # 日志服务
│   │   │   │   │   └── AiRecognitionService.java   # AI识别服务
│   │   │   │   ├── util/             # 工具类 Utility
│   │   │   │   │   └── JwtUtil.java               # JWT令牌工具类
│   │   │   │   ├── vo/               # 视图对象 VO（View Object）
│   │   │   │   │   └── LoginVO.java               # 登录响应数据
│   │   │   │   └── DronePatrolApplication.java     # 启动入口
│   │   │   └── resources/
│   │   │       └── application.yml                 # 应用配置文件
│   │   └── pom.xml                                 # Maven依赖配置
├── web/                   # 前端项目 Frontend
│   ├── src/
│   │   ├── components/              # 公共组件 Component
│   │   │   └── ParticleBackground.vue  # 粒子背景效果
│   │   ├── router/                  # 路由配置 Router
│   │   │   └── index.js             # 路由定义+导航守卫
│   │   ├── store/                   # 状态管理 Store（Pinia）
│   │   │   └── user.js              # 用户状态+localStorage持久化
│   │   ├── utils/                   # 工具函数 Utility
│   │   │   └── request.js           # Axios封装+请求/响应拦截器
│   │   ├── views/                   # 页面组件 View
│   │   │   ├── Login.vue            # 登录页
│   │   │   ├── Layout.vue           # 布局组件（侧边栏+顶栏）
│   │   │   ├── Dashboard.vue        # 首页仪表盘
│   │   │   ├── Device.vue           # 设备管理
│   │   │   ├── Route.vue            # 航线管理
│   │   │   ├── Task.vue             # 任务管理
│   │   │   ├── Result.vue           # 结果管理
│   │   │   ├── User.vue             # 用户管理
│   │   │   ├── Log.vue              # 操作日志
│   │   │   └── Profile.vue          # 个人中心
│   │   ├── App.vue                  # 根组件
│   │   └── main.js                  # 入口文件
│   ├── index.html                   # HTML模板（含高德地图Key）
│   ├── vite.config.js               # Vite配置（代理+别名）
│   └── package.json                 # 依赖配置
├── sql/                   # 数据库脚本 SQL
│   └── init.sql                     # 初始化脚本（建表）
└── README.md
```

---

## 前端路由与页面映射（Router & View Mapping）

```mermaid
graph LR
    subgraph 公共页面["公共页面 Public"]
        Login["/login<br/>Login.vue<br/>登录页"]
    end

    subgraph Layout布局["Layout布局 / "]
        Dashboard["/dashboard<br/>Dashboard.vue<br/>首页仪表盘"]
        Device["/device<br/>Device.vue<br/>设备管理"]
        Route["/route<br/>Route.vue<br/>航线管理"]
        Task["/task<br/>Task.vue<br/>任务管理"]
        Result["/result<br/>Result.vue<br/>结果管理"]
        User["/user<br/>User.vue<br/>用户管理<br/>🔒仅管理员"]
        Log["/log<br/>Log.vue<br/>操作日志"]
        Profile["/profile<br/>Profile.vue<br/>个人中心"]
    end

    Login -->|"登录成功"| Dashboard
    Dashboard --- Device --- Route --- Task --- Result
    Result --- User --- Log --- Profile
```

---

## 请求认证流程（Request Authentication Flow）

```mermaid
flowchart LR
    A["前端发起请求"] --> B{"请求URL是否为<br/>/api/auth/login<br/>或 /api/auth/register?"}
    B -->|"是"| C["放行，不拦截"]
    B -->|"否"| D["JwtInterceptor拦截"]
    D --> E{"请求头是否包含<br/>Authorization: Bearer Token?"}
    E -->|"无Token"| F["返回401<br/>未登录"]
    E -->|"有Token"| G{"JwtUtil.validateToken<br/>验证Token有效性"}
    G -->|"Token过期/无效"| H["返回401<br/>Token已过期"]
    G -->|"Token有效"| I["解析Token<br/>提取userId/username/role<br/>写入request属性"]
    I --> J["请求到达Controller"]
    J --> K{"检查role<br/>是否为ADMIN?"}
    K -->|"非ADMIN且需管理员权限"| L["返回无权限"]
    K -->|"权限足够"| M["执行业务逻辑"]
```

---

## CMD 专属操作脚本

为方便操作，项目提供以下 CMD 批处理脚本（需自行创建）：

### start-all.bat - 一键启动

```cmd
@echo off
echo 正在启动无人机巡防管控系统...

echo.
echo [1/4] 检查 MySQL 服务...
sc query MySQL80

echo.
echo [2/4] 启动 Redis...
start "Redis" cmd /k "cd /d D:\Redis-x64-6.2.14 && redis-server.exe redis.windows.conf"

echo.
echo [3/4] 等待 Redis 启动...
timeout /t 3 /nobreak

echo.
echo [4/4] 启动后端服务...
cd /d D:\_class\drone-patrol\server
start "后端" cmd /k "mvn spring-boot:run"

echo.
echo 后端服务启动中，等待 10 秒后启动前端...
timeout /t 10 /nobreak

echo.
echo 启动前端服务...
cd /d D:\_class\drone-patrol\web
start "前端" cmd /k "npm run dev"

echo.
echo ========================================
echo 系统正在启动...
echo 前端地址: http://localhost:3000
echo 后端地址: http://localhost:8080
echo 默认账号: admin / admin123
echo ========================================
pause
```

### 配置说明

- 数据库配置：server/src/main/resources/application.yml
- 高德地图 Key：web/index.html

---

## API 文档

### 认证接口（Authentication API）

#### 登录（Login）

```
POST /api/auth/login
Content-Type: application/json

Request:
{
  "username": "admin",
  "password": "admin123"
}

Response:
{
  "code": 200,
  "message": "登录成功",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiIs...",
    "userId": 1,
    "username": "admin",
    "nickname": "管理员",
    "role": "ADMIN"
  }
}
```

#### 注册（Register）

```
POST /api/auth/register
Content-Type: application/json

Request:
{
  "username": "newuser",
  "password": "password123",
  "nickname": "新用户"
}

Response:
{
  "code": 200,
  "message": "操作成功",
  "data": null
}
```

### 设备管理接口（Device API）

#### 获取设备列表（分页）

```
GET /api/device/page
Authorization: Bearer {token}

Query Parameters:
- pageNum: 页码（默认1）
- pageSize: 每页数量（默认10）
- deviceCode: 设备编号（可选，模糊查询）
- deviceName: 设备名称（可选，模糊查询）
- status: 设备状态（可选）
- owner: 归属人（可选，模糊查询）
```

#### 新增设备

```
POST /api/device
Authorization: Bearer {token}
Content-Type: application/json

Request:
{
  "deviceCode": "DRONE001",
  "deviceName": "巡检无人机1号",
  "deviceModel": "DJI Mavic 3",
  "batteryLife": 45,
  "cameraParam": "4K高清",
  "owner": "张三",
  "status": "NORMAL"
}
```

### 航线管理接口（Route API）

```
GET /api/route/page
Authorization: Bearer {token}

Query Parameters:
- pageNum: 页码
- pageSize: 每页数量
- routeName: 航线名称（可选，模糊查询）
```

### 任务管理接口（Task API）

```
GET /api/task/page
Authorization: Bearer {token}

Query Parameters:
- pageNum / pageSize
- taskName: 任务名称（可选）
- executorName: 执行人（可选）
- status: 任务状态（可选 PENDING/EXECUTING/COMPLETED/CANCELLED）
- startTime / endTime: 时间范围（可选）

PUT /api/task/start/{id}     - 开始执行任务
PUT /api/task/complete/{id}  - 完成任务
PUT /api/task/cancel/{id}    - 取消任务
PUT /api/task/progress?id=&progress=  - 更新进度
```

### 结果管理接口（Result API）

```
GET /api/result/page
Authorization: Bearer {token}

Query Parameters:
- pageNum / pageSize
- resultCode: 结果编号（可选）
- executorName: 执行人（可选）
- startTime / endTime: 时间范围（可选）

GET /api/result/export       - 导出Excel
PUT /api/result              - 编辑结果
```

### AI识别接口（AI Recognition API）

```
POST /api/ai/recognize/{resultId}   - AI分析并保存结果
GET /api/ai/analyze/{resultId}      - AI分析（不保存）
```

### 响应码说明（Response Code）

| 响应码 | 说明 |
|--------|------|
| 200 | 请求成功（Success） |
| 400 | 请求参数错误（Bad Request） |
| 401 | 未授权（Unauthorized，Token无效或过期） |
| 403 | 权限不足（Forbidden） |
| 500 | 服务器内部错误（Internal Server Error） |

---

## 配置说明

### 数据库配置（Database Configuration）

文件：`server/src/main/resources/application.yml`

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/drone_patrol?useUnicode=true&characterEncoding=utf-8&useSSL=false&serverTimezone=Asia/Shanghai
    username: root
    password: your_password
```

### Redis配置

```yaml
spring:
  redis:
    host: localhost
    port: 6379
    database: 0
    timeout: 5000ms
```

### JWT配置

```yaml
jwt:
  secret: your-32-character-secret-key-here
  expiration: 86400000  # 24小时（毫秒 milliseconds）
```

### 高德地图配置（Amap Configuration）

文件：`web/index.html`

```html
<script type="text/javascript">
  window._AMAP_API_KEY = 'YOUR_AMAP_KEY';
</script>
```

### 智谱AI配置（ZhipuAI Configuration）

```yaml
zhipu:
  api-key: your-zhipu-api-key
  base-url: https://open.bigmodel.cn/api/paas/v4/chat/completions
  model: glm-4-flash
```

---

## 常见问题

### 1. Maven 依赖下载慢

<details>
<summary>点击展开解决方案</summary>

配置 Maven 使用国内镜像源加速下载。在 Maven 的 `settings.xml` 文件中添加：

```xml
<mirrors>
  <mirror>
    <id>aliyun</id>
    <name>Aliyun Maven</name>
    <url>https://maven.aliyun.com/repository/public</url>
    <mirrorOf>central</mirrorOf>
  </mirror>
</mirrors>
```

</details>

### 2. npm 依赖下载慢

<details>
<summary>点击展开解决方案</summary>

配置使用淘宝镜像：

```cmd
npm config set registry https://registry.npmmirror.com
```

</details>

### 3. 高德地图不显示

<details>
<summary>点击展开解决方案</summary>

确保已在 `web/index.html` 中配置有效的高德地图 Key：

1. 注册高德开放平台账号
2. 创建应用获取 Key
3. 在 `index.html` 中替换 `YOUR_AMAP_KEY`

</details>

### 4. 数据库连接失败

<details>
<summary>点击展开解决方案</summary>

1. 检查 MySQL 服务是否启动
2. 验证 `application.yml` 中的数据库配置
3. 确认用户名密码正确
4. 检查数据库是否已创建

</details>

### 5. Redis 连接失败

<details>
<summary>点击展开解决方案</summary>

1. 检查 Redis 服务是否启动
2. 验证 `application.yml` 中的 Redis 配置
3. 确认端口号正确（默认6379）

</details>

### 6. 端口被占用

<details>
<summary>点击展开解决方案</summary>

如果 8080 或 3000 端口被占用，可以修改配置：

**后端端口**：修改 `server/src/main/resources/application.yml`

```yaml
server:
  port: 8081
```

**前端端口**：修改 `web/vite.config.js`

```javascript
server: {
  port: 3001
}
```

</details>

---

## 贡献指南

### 如何贡献

1. **Fork 本仓库**
2. **创建特性分支（Feature Branch）**：`git checkout -b feature/AmazingFeature`
3. **提交更改（Commit）**：`git commit -m 'Add some AmazingFeature'`
4. **推送到分支（Push）**：`git push origin feature/AmazingFeature`
5. **创建 Pull Request（合并请求）**

### 开发规范

#### 后端规范（Backend Convention）

- 控制器方法添加 Javadoc 注释
- 服务类添加类注释和方法注释
- 使用 LambdaQueryWrapper 进行查询构造
- 统一使用 Result 类返回响应（Unified Response）
- 写操作需记录操作日志（Operation Log）

#### 前端规范（Frontend Convention）

- 组件使用 `<script setup>` 语法
- 样式使用 Scoped CSS
- 统一使用 ElMessage 提示信息
- 页面文件放在 views 目录

### Commit 规范

```
feat: 新功能（Feature）
fix: 修复bug（Bug Fix）
docs: 文档更新（Documentation）
style: 代码格式（不影响功能）
refactor: 重构（Refactor）
test: 测试相关（Test）
chore: 构建/工具相关（Chore）
```

---

## 更新日志

### v1.0.0 (2024-01-01)

- ✨ 完成基础功能开发
- 🔐 实现JWT认证（Authentication）
- 🤖 集成AI智能分析（AI Analysis）
- 📊 Excel导出功能（Export）
- 🎨 现代化UI设计

---



## 技术支持

- Spring Boot 文档：https://spring.io/projects/spring-boot
- Element Plus 文档：https://element-plus.org/
- 高德地图 API 文档：https://lbs.amap.com/
- MyBatis-Plus 文档：https://baomidou.com/
- 智谱AI 文档：https://open.bigmodel.cn/

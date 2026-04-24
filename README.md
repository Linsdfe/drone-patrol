# 无人机巡防管控系统

<div align="center">

![Version](https://img.shields.io/badge/version-1.0.0-blue)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-2.7.18-green)
![Vue](https://img.shields.io/badge/Vue-3-green)
![Element Plus](https://img.shields.io/badge/Element%20Plus-最新-blue)

</div>

## 项目简介

这是一个面向高校课程设计和毕业设计的无人机巡防管控系统，采用前后端分离架构，实现了完整的设备管理、航线规划、任务执行和结果管理业务闭环。

### 核心特性

- 🎯 **前后端分离**：Spring Boot + Vue 3 现代化架构
- 🔐 **安全认证**：JWT Token 无状态认证
- 🤖 **AI智能分析**：集成智谱AI，智能评估巡防风险
- 📊 **数据导出**：支持Excel报表导出
- 🗺️ **地图集成**：高德地图航线可视化
- 📱 **响应式设计**：适配多种屏幕尺寸

## 技术栈

### 后端

| 技术 | 版本 | 说明 |
|------|------|------|
| Spring Boot | 2.7.18 | 核心框架 |
| MySQL | 8.0 | 关系数据库 |
| Redis | - | 缓存中间件 |
| MyBatis-Plus | 3.5.5 | ORM框架 |
| JWT | 0.11.5 | 身份认证 |
| Apache POI | 5.2.5 | Excel操作 |
| Hutool | 5.8.26 | 工具类库 |

### 前端

| 技术 | 版本 | 说明 |
|------|------|------|
| Vue | 3 | 核心框架 |
| Vite | 5 | 构建工具 |
| Element Plus | 最新 | UI组件库 |
| Vue Router | 4 | 路由管理 |
| Pinia | - | 状态管理 |
| Axios | - | HTTP客户端 |
| 高德地图 | API | 地图服务 |

## 功能模块

1. **系统管理**
   - 账号密码登录（JWT认证）
   - 个人中心（信息查看、密码修改）
   - 用户管理（管理员专属）
   - 操作日志记录

2. **设备管理**
   - 设备信息录入、编辑、删除
   - 设备状态切换
   - 设备详情查看

3. **航线管理**
   - 基于高德地图的可视化航线规划
   - 航线信息管理
   - 航线地图回显

4. **任务管理**
   - 任务创建（绑定设备和航线）
   - 任务状态管理
   - 任务执行进度展示

5. **结果管理**
   - 任务完成自动生成结果
   - 结果编辑
   - Excel导出

## 快速开始

### 环境要求

- JDK 8+
- Node.js 16+
- MySQL 8.0
- Redis

### 注意事项

⚠️ **重要：所有命令行操作必须在 Windows CMD 中执行，禁止使用 PowerShell！**

### 1. 数据库初始化

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

### 3. 后端启动

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

### 4. 前端启动

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

### 5. 系统登录

默认账号：
- 管理员：admin / admin123
- 普通用户：user / admin123

## 项目结构

```
drone-patrol/
├── server/                 # 后端项目
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/drone/patrol/
│   │   │   │   ├── common/         # 通用类
│   │   │   │   ├── controller/     # 控制器
│   │   │   │   ├── dto/            # 数据传输对象
│   │   │   │   ├── entity/         # 实体类
│   │   │   │   ├── mapper/         # 数据访问层
│   │   │   │   ├── service/        # 业务逻辑层
│   │   │   │   ├── util/           # 工具类
│   │   │   │   └── DronePatrolApplication.java
│   │   │   └── resources/
│   │   │       └── application.yml
│   │   └── pom.xml
├── web/                   # 前端项目
│   ├── src/
│   │   ├── router/        # 路由配置
│   │   ├── store/         # 状态管理
│   │   ├── utils/         # 工具函数
│   │   ├── views/         # 页面组件
│   │   ├── App.vue
│   │   └── main.js
│   ├── index.html
│   ├── package.json
│   └── vite.config.js
├── sql/                   # 数据库脚本
│   └── init.sql
└── README.md
```

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

## API 文档

### 认证接口

#### 登录

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

#### 注册

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

### 设备管理接口

#### 获取设备列表

```
GET /api/device/list
Authorization: Bearer {token}

Query Parameters:
- pageNum: 页码（默认1）
- pageSize: 每页数量（默认10）
- deviceCode: 设备编号（可选）

Response:
{
  "code": 200,
  "data": {
    "records": [...],
    "total": 100,
    "size": 10,
    "current": 1
  }
}
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
  "deviceType": "巡检型",
  "status": "在线"
}
```

### 结果管理接口

#### 获取结果列表

```
GET /api/result/list
Authorization: Bearer {token}

Query Parameters:
- pageNum: 页码
- pageSize: 每页数量
- resultCode: 结果编号（可选）
- executorName: 执行人（可选）
- startTime: 开始时间（可选）
- endTime: 结束时间（可选）
```

#### 导出Excel

```
POST /api/result/export
Authorization: Bearer {token}
Content-Type: application/json

Request:
{
  "resultCode": "",
  "executorName": "",
  "startTime": "",
  "endTime": ""
}

Response: 文件下载（application/vnd.openxmlformats-officedocument.spreadsheetml.sheet）
```

### 响应码说明

| 响应码 | 说明 |
|--------|------|
| 200 | 请求成功 |
| 400 | 请求参数错误 |
| 401 | 未授权（Token无效或过期） |
| 403 | 权限不足 |
| 500 | 服务器内部错误 |

---

## 配置说明

### 数据库配置

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
    password: # 如果有密码的话
```

### JWT配置

```yaml
jwt:
  secret: your-32-character-secret-key-here
  expiration: 86400000  # 24小时（毫秒）
```

### 高德地图配置

文件：`web/index.html`

```html
<script type="text/javascript">
  window._AMAP_API_KEY = 'YOUR_AMAP_KEY';
</script>
```

### 智谱AI配置（AI功能）

```yaml
zhipu:
  api-key: your-zhipu-api-key
  base-url: https://open.bigmodel.cn/api/paas/v4/chat/completions
  model: glm-4
```

---

## 项目结构详解

```
drone-patrol/
├── server/                          # Spring Boot 后端项目
│   ├── src/main/java/com/drone/patrol/
│   │   ├── common/                  # 通用类
│   │   │   └── Result.java          # 统一响应封装
│   │   ├── config/                   # 配置类
│   │   │   ├── GlobalExceptionHandler.java  # 全局异常处理
│   │   │   ├── MybatisPlusConfig.java       # MyBatis-Plus配置
│   │   │   ├── SecurityConfig.java          # 安全配置
│   │   │   ├── WebConfig.java               # Web配置
│   │   │   └── ZhipuConfig.java             # 智谱AI配置
│   │   ├── controller/               # 控制器层
│   │   │   ├── AuthController.java   # 认证控制器
│   │   │   ├── DroneDeviceController.java  # 设备管理
│   │   │   ├── PatrolResultController.java # 结果管理
│   │   │   ├── PatrolRouteController.java  # 航线管理
│   │   │   ├── PatrolTaskController.java    # 任务管理
│   │   │   ├── SysUserController.java      # 用户管理
│   │   │   └── SysOperationLogController.java # 日志
│   │   ├── dto/                      # 数据传输对象
│   │   ├── entity/                    # 实体类
│   │   ├── mapper/                   # 数据访问层
│   │   ├── service/                  # 业务逻辑层
│   │   │   ├── AiRecognitionService.java  # AI识别服务
│   │   │   └── PatrolResultService.java   # 结果服务（含Excel导出）
│   │   ├── interceptor/              # 拦截器
│   │   ├── util/                    # 工具类
│   │   │   └── JwtUtil.java         # JWT工具
│   │   └── vo/                      # 视图对象
│   └── src/main/resources/
│       └── application.yml           # 应用配置文件
│
├── web/                            # Vue 3 前端项目
│   ├── src/
│   │   ├── components/              # 公共组件
│   │   │   └── ParticleBackground.vue  # 粒子背景效果
│   │   ├── router/                  # 路由配置
│   │   ├── store/                   # 状态管理
│   │   │   └── user.js              # 用户状态
│   │   ├── utils/                   # 工具函数
│   │   │   └── request.js           # Axios封装
│   │   ├── views/                   # 页面组件
│   │   │   ├── Login.vue            # 登录页
│   │   │   ├── Layout.vue           # 布局组件
│   │   │   ├── Dashboard.vue        # 首页仪表盘
│   │   │   ├── Device.vue           # 设备管理
│   │   │   ├── Route.vue           # 航线管理
│   │   │   ├── Task.vue           # 任务管理
│   │   │   ├── Result.vue         # 结果管理
│   │   │   ├── User.vue           # 用户管理
│   │   │   ├── Log.vue            # 操作日志
│   │   │   └── Profile.vue        # 个人中心
│   │   ├── App.vue                 # 根组件
│   │   └── main.js                 # 入口文件
│   ├── index.html                   # HTML模板
│   ├── vite.config.js              # Vite配置
│   └── package.json                 # 依赖配置
│
├── sql/                            # 数据库脚本
│   └── init.sql                    # 初始化脚本
│
├── start-backend.bat                # 后端启动脚本
├── start-frontend.bat              # 前端启动脚本
└── README.md                        # 项目文档
```

---

## 贡献指南

### 如何贡献

1. **Fork 本仓库**
2. **创建特性分支**：`git checkout -b feature/AmazingFeature`
3. **提交更改**：`git commit -m 'Add some AmazingFeature'`
4. **推送到分支**：`git push origin feature/AmazingFeature`
5. **创建 Pull Request**

### 开发规范

#### 后端规范

- 控制器方法添加 Javadoc 注释
- 服务类添加类注释和方法注释
- 使用 LambdaQueryWrapper 进行查询构造
- 统一使用 Result 类返回响应

#### 前端规范

- 组件使用 `<script setup>` 语法
- 样式使用 Scoped CSS
- 统一使用 ElMessage 提示信息
- 页面文件放在 views 目录

### Commit 规范

```
feat: 新功能
fix: 修复bug
docs: 文档更新
style: 代码格式（不影响功能）
refactor: 重构
test: 测试相关
chore: 构建/工具相关
```

---

## 更新日志

### v1.0.0 (2024-01-01)

- ✨ 完成基础功能开发
- 🔐 实现JWT认证
- 🤖 集成AI智能分析
- 📊 Excel导出功能
- 🎨 现代化UI设计

---

## 许可证

本项目仅供学习交流使用，未经授权不得用于商业目的。

---

## 联系方式

- **项目作者**：Drone Patrol Team
- **版本**：v1.0.0
- **最后更新**：2024年

---

<div align="center">

**如果这个项目对您有帮助，请给我一个 Star ⭐**

</div>

## 技术支持

- Spring Boot 文档：https://spring.io/projects/spring-boot
- Element Plus 文档：https://element-plus.org/
- 高德地图 API 文档：https://lbs.amap.com/

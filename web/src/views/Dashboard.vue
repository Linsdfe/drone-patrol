<template>
  <div class="dashboard">
    <el-row :gutter="20">
      <el-col :span="6">
        <el-card class="stat-card" shadow="hover">
          <div class="stat-content">
            <div class="stat-icon" style="background: var(--color-primary);">
              <el-icon><Monitor /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ deviceCount }}</div>
              <div class="stat-label">设备总数</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card" shadow="hover">
          <div class="stat-content">
            <div class="stat-icon" style="background: var(--color-success);">
              <el-icon><Guide /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ routeCount }}</div>
              <div class="stat-label">航线总数</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card" shadow="hover">
          <div class="stat-content">
            <div class="stat-icon" style="background: var(--color-warning);">
              <el-icon><List /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ taskCount }}</div>
              <div class="stat-label">任务总数</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card" shadow="hover">
          <div class="stat-content">
            <div class="stat-icon" style="background: var(--color-danger);">
              <el-icon><Document /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ resultCount }}</div>
              <div class="stat-label">结果总数</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" style="margin-top: 20px;">
      <el-col :span="12">
        <el-card shadow="hover">
          <template #header>
            <span>任务状态统计</span>
          </template>
          <el-empty v-if="!taskStats" description="暂无数据" />
          <div v-else class="task-stats">
            <div class="stat-item">
              <span class="stat-dot" style="background: var(--color-primary);"></span>
              <span>待执行：{{ taskStats.pendingCount }}</span>
            </div>
            <div class="stat-item">
              <span class="stat-dot" style="background: var(--color-warning);"></span>
              <span>执行中：{{ taskStats.executingCount }}</span>
            </div>
            <div class="stat-item">
              <span class="stat-dot" style="background: var(--color-success);"></span>
              <span>已完成：{{ taskStats.completedCount }}</span>
            </div>
            <div class="stat-item">
              <span class="stat-dot" style="background: var(--color-info);"></span>
              <span>已取消：{{ taskStats.cancelledCount }}</span>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card shadow="hover">
          <template #header>
            <span>快速导航</span>
          </template>
          <div class="quick-nav">
            <el-button type="primary" @click="$router.push('/device')">设备管理</el-button>
            <el-button type="success" @click="$router.push('/route')">航线管理</el-button>
            <el-button type="warning" @click="$router.push('/task')">任务管理</el-button>
            <el-button type="danger" @click="$router.push('/result')">结果管理</el-button>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-row style="margin-top: 20px;">
      <el-col :span="24">
        <el-card shadow="hover">
          <template #header>
            <span>系统说明</span>
          </template>
          <div class="system-info">
            <p>欢迎使用无人机巡防管控系统！</p>
            <p>本系统实现了无人机设备管理、航线规划、任务执行、结果管理等完整业务流程。</p>
            <p>操作说明：</p>
            <ul>
              <li>1. 首先在设备管理中添加可用设备</li>
              <li>2. 然后在航线管理中规划巡防航线</li>
              <li>3. 接着在任务管理中创建并执行巡防任务</li>
              <li>4. 任务完成后可在结果管理中查看和导出结果</li>
            </ul>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import request from '@/utils/request'

const deviceCount = ref(0)
const routeCount = ref(0)
const taskCount = ref(0)
const resultCount = ref(0)
const taskStats = ref(null)

const loadData = async () => {
  try {
    const [deviceRes, routeRes, taskRes, taskStatsRes] = await Promise.all([
      request.get('/device/page', { params: { pageNum: 1, pageSize: 1 } }),
      request.get('/route/page', { params: { pageNum: 1, pageSize: 1 } }),
      request.get('/task/page', { params: { pageNum: 1, pageSize: 1 } }),
      request.get('/task/statistics')
    ])
    deviceCount.value = deviceRes.data.total
    routeCount.value = routeRes.data.total
    taskCount.value = taskRes.data.total
    taskStats.value = taskStatsRes.data
    resultCount.value = taskStatsRes.data.completedCount || 0
  } catch (error) {
  }
}

onMounted(() => {
  loadData()
})
</script>

<style scoped>
.dashboard {
  padding: 0;
}

.el-row {
  margin-bottom: 20px;
}

.stat-card {
  margin-bottom: 0;
  transition: all 0.3s ease;
}

.stat-card:hover {
  transform: translateY(-2px);
}

.stat-content {
  display: flex;
  align-items: center;
  gap: 24px;
  padding: 8px 0;
}

.stat-icon {
  width: 72px;
  height: 72px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-size: 32px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
}

.stat-info {
  flex: 1;
}

.stat-value {
  font-size: 32px;
  font-weight: 600;
  color: var(--color-text-primary);
  line-height: 1.2;
}

.stat-label {
  font-size: 14px;
  color: var(--color-text-secondary);
  margin-top: 8px;
  font-weight: 400;
}

.task-stats {
  padding: 16px 0;
}

.stat-item {
  padding: 14px 0;
  display: flex;
  align-items: center;
  gap: 12px;
  font-size: 15px;
  color: var(--color-text-regular);
  border-bottom: 1px solid var(--color-border-lighter);
}

.stat-item:last-child {
  border-bottom: none;
}

.stat-dot {
  width: 10px;
  height: 10px;
  border-radius: 50%;
  flex-shrink: 0;
}

.quick-nav {
  padding: 20px 0;
  display: flex;
  gap: 12px;
  flex-wrap: wrap;
}

.quick-nav .el-button {
  min-width: 120px;
  height: 40px;
  font-size: 14px;
}

.system-info {
  padding: 12px 0;
  line-height: 1.8;
}

.system-info p {
  margin: 12px 0;
  color: var(--color-text-regular);
  font-size: 14px;
}

.system-info ul {
  margin: 12px 20px;
  color: var(--color-text-regular);
}

.system-info li {
  margin: 8px 0;
  font-size: 14px;
}

:deep(.el-card__header) {
  padding: 16px 20px;
  font-size: 16px;
  font-weight: 600;
  color: var(--color-text-primary);
  border-bottom: 1px solid var(--color-border-lighter);
}

:deep(.el-card__body) {
  padding: 20px;
}

:deep(.el-empty__description) {
  color: var(--color-text-secondary);
}

</style>

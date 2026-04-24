<template>
  <!-- 布局容器 -->
  <el-container class="layout-container">
    <!-- 侧边栏 -->
    <el-aside width="200px">
      <div class="logo">无人机巡防</div>
      <!-- 导航菜单 -->
      <el-menu
        :default-active="activeMenu"
        router
        background-color="transparent"
        text-color="#4e5969"
        active-text-color="#1677ff"
      >
        <el-menu-item index="/dashboard">
          <el-icon><Odometer /></el-icon>
          <span>首页</span>
        </el-menu-item>
        <el-menu-item index="/device">
          <el-icon><Monitor /></el-icon>
          <span>设备管理</span>
        </el-menu-item>
        <el-menu-item index="/route">
          <el-icon><Guide /></el-icon>
          <span>航线管理</span>
        </el-menu-item>
        <el-menu-item index="/task">
          <el-icon><List /></el-icon>
          <span>任务管理</span>
        </el-menu-item>
        <el-menu-item index="/result">
          <el-icon><Document /></el-icon>
          <span>结果管理</span>
        </el-menu-item>
        <!-- 管理员专属菜单 -->
        <el-menu-item v-if="userStore.role === 'ADMIN'" index="/user">
          <el-icon><User /></el-icon>
          <span>用户管理</span>
        </el-menu-item>
        <el-menu-item index="/log">
          <el-icon><DocumentCopy /></el-icon>
          <span>操作日志</span>
        </el-menu-item>
      </el-menu>
    </el-aside>
    <!-- 主内容区 -->
    <el-container>
      <!-- 顶部导航栏 -->
      <el-header>
        <div class="header-left">
          <span>{{ currentTitle }}</span>
        </div>
        <div class="header-right">

          <!-- 用户下拉菜单 -->
          <el-dropdown @command="handleCommand" class="user-dropdown">
            <span class="user-info">
              <el-icon><User /></el-icon>
              {{ userStore.nickname || userStore.username }}
              <el-icon class="el-icon--right"><ArrowDown /></el-icon>
            </span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="profile">个人中心</el-dropdown-item>
                <el-dropdown-item command="logout">退出登录</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </el-header>
      <!-- 内容区域 -->
      <el-main>
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup>
import { ref, computed, onMounted, watch } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessageBox, ElMessage } from 'element-plus'
import { useUserStore } from '@/store/user'

// 路由实例
const router = useRouter()
// 当前路由信息
const route = useRoute()
// 用户状态管理
const userStore = useUserStore()

// 当前激活的菜单
const activeMenu = computed(() => route.path)
// 当前页面标题
const currentTitle = computed(() => route.meta.title || '首页')



/**
 * 处理下拉菜单命令
 * @param {string} command 命令类型
 */
const handleCommand = async (command) => {
  if (command === 'profile') {
    // 跳转到个人中心
    router.push('/profile')
  } else if (command === 'logout') {
    try {
      // 确认退出登录
      await ElMessageBox.confirm('确定要退出登录吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      })
      // 执行退出登录
      userStore.logout()
      ElMessage.success('退出成功')
      // 跳转到登录页
      router.push('/login')
    } catch (error) {
      // 取消退出
    }
  }
}
</script>

<style scoped>
.layout-container {
  width: 100%;
  height: 100%;
}

.el-aside {
  background-color: var(--color-bg-white);
  border-right: 1px solid var(--color-border-lighter);
  overflow-x: hidden;
  overflow-y: auto;
}

.logo {
  height: 60px;
  line-height: 60px;
  text-align: center;
  font-size: 18px;
  font-weight: 600;
  color: var(--color-text-primary);
  letter-spacing: 1px;
  background-color: var(--color-bg-white);
  border-bottom: 1px solid var(--color-border-lighter);
}

.el-menu {
  background-color: transparent !important;
}

.el-menu-item {
  height: 50px;
  line-height: 50px;
  margin: 4px 12px;
  padding-left: 20px !important;
  border-radius: 6px;
  color: var(--color-text-regular);
  font-size: 14px;
  transition: all 0.2s ease;
}

.el-menu-item:hover {
  background-color: var(--color-bg-dark) !important;
  color: var(--color-text-primary);
}

.el-menu-item.is-active {
  background-color: rgba(64, 158, 255, 0.08) !important;
  color: var(--color-text-primary);
  font-weight: 500;
}

.el-menu-item .el-icon {
  font-size: 18px;
  margin-right: 10px;
  color: var(--color-text-secondary);
}

.el-menu-item.is-active .el-icon {
  color: var(--color-primary);
}

.el-header {
  background-color: var(--color-bg-white);
  border-bottom: 1px solid var(--color-border-lighter);
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 24px;
  box-shadow: var(--shadow-light);
}

.header-left {
  font-size: 16px;
  font-weight: 500;
  color: var(--color-text-primary);
  letter-spacing: 0.5px;
}

.header-right {
  display: flex;
  align-items: center;
  gap: 12px;
}



.user-dropdown {
  margin-left: 0;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 8px;
  color: var(--color-text-regular);
  font-size: 14px;
  padding: 8px 12px;
  border-radius: 6px;
  cursor: pointer;
  transition: background-color 0.2s ease;
}

.user-info:hover {
  background-color: var(--color-bg-dark);
}

.el-main {
  background-color: var(--color-bg-base);
  padding: 20px;
  min-height: calc(100vh - 60px);
  transition: background-color 0.3s ease;
}

:deep(.el-card) {
  border: 1px solid var(--color-border-lighter);
}

:deep(.search-card) {
  margin-bottom: 16px;
}

:deep(.search-card .el-card__body) {
  padding: 16px 20px;
}

:deep(.el-table) {
  border: 1px solid var(--color-border-lighter);
  border-radius: 6px;
}

:deep(.el-table th) {
  background-color: var(--color-bg-light) !important;
  font-size: 13px;
}

:deep(.el-table td) {
  padding: 12px 0;
}

:deep(.el-button) {
  transition: all 0.2s ease;
}

:deep(.el-pagination) {
  margin-top: 16px;
  justify-content: flex-end;
}

:deep(.el-dropdown-menu__item) {
  padding: 10px 16px;
  font-size: 13px;
}

:deep(.el-dropdown-menu__item.is-active) {
  color: var(--color-primary);
  font-weight: 500;
}

:deep(.el-dropdown-menu__item:hover) {
  background-color: var(--color-primary-bg);
  color: var(--color-primary);
}
</style>

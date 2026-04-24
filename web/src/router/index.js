import { createRouter, createWebHistory } from 'vue-router'
import { useUserStore } from '@/store/user'

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/Login.vue')
  },
  {
    path: '/',
    component: () => import('@/views/Layout.vue'),
    redirect: '/dashboard',
    children: [
      {
        path: 'dashboard',
        name: 'Dashboard',
        component: () => import('@/views/Dashboard.vue'),
        meta: { title: '首页' }
      },
      {
        path: 'device',
        name: 'Device',
        component: () => import('@/views/Device.vue'),
        meta: { title: '设备管理' }
      },
      {
        path: 'route',
        name: 'Route',
        component: () => import('@/views/Route.vue'),
        meta: { title: '航线管理' }
      },
      {
        path: 'task',
        name: 'Task',
        component: () => import('@/views/Task.vue'),
        meta: { title: '任务管理' }
      },
      {
        path: 'result',
        name: 'Result',
        component: () => import('@/views/Result.vue'),
        meta: { title: '结果管理' }
      },
      {
        path: 'user',
        name: 'User',
        component: () => import('@/views/User.vue'),
        meta: { title: '用户管理', requireAdmin: true }
      },
      {
        path: 'log',
        name: 'Log',
        component: () => import('@/views/Log.vue'),
        meta: { title: '操作日志' }
      },
      {
        path: 'profile',
        name: 'Profile',
        component: () => import('@/views/Profile.vue'),
        meta: { title: '个人中心' }
      }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach((to, from, next) => {
  const userStore = useUserStore()
  const token = userStore.token

  if (to.path !== '/login' && !token) {
    next('/login')
  } else if (to.path === '/login' && token) {
    next('/')
  } else if (to.meta.requireAdmin && userStore.role !== 'ADMIN') {
    next('/')
  } else {
    next()
  }
})

export default router

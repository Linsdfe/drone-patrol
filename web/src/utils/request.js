import axios from 'axios'
import { ElMessage } from 'element-plus'
import { useUserStore } from '@/store/user'
import router from '@/router'

/**
 * Axios HTTP请求工具类
 *
 * 功能说明：
 * - 基于Axios封装的HTTP请求工具
 * - 自动添加JWT认证令牌
 * - 统一处理请求和响应拦截
 * - 集成Element Plus消息提示
 *
 * 配置说明：
 * - baseURL: API基础地址（/api）
 * - timeout: 请求超时时间（30秒，适应AI分析）
 *
 * 使用示例：
 * - GET请求：request.get('/user/info')
 * - POST请求：request.post('/user/login', data)
 * - 文件下载：request.post('/result/export', data, { responseType: 'blob' })
 *
 * @module request
 */
const request = axios.create({
  baseURL: '/api',
  timeout: 30000
})

/**
 * 请求拦截器
 *
 * 功能：
 * - 自动从用户Store获取JWT令牌
 * - 为请求头添加Authorization字段
 * - 格式：Bearer {token}
 *
 * @param {Object} config - Axios请求配置对象
 * @returns {Object} 处理后的配置对象
 */
request.interceptors.request.use(
  config => {
    const userStore = useUserStore()
    if (userStore.token) {
      config.headers.Authorization = 'Bearer ' + userStore.token
    }
    return config
  },
  error => {
    return Promise.reject(error)
  }
)

/**
 * 响应拦截器
 *
 * 功能：
 * - 统一处理响应结果
 * - code为200时返回数据，否则显示错误提示
 * - 401未授权时自动登出并跳转登录页
 * - 网络错误时显示错误消息
 *
 * @param {Object} response - Axios响应对象
 * @returns {Object|Promise} 成功返回数据，失败返回Promise错误
 */
request.interceptors.response.use(
  response => {
    const res = response.data
    if (res.code === 200) {
      return res
    } else {
      ElMessage.error(res.message || '请求失败')
      return Promise.reject(new Error(res.message || '请求失败'))
    }
  },
  error => {
    if (error.response && error.response.status === 401) {
      const userStore = useUserStore()
      userStore.logout()
      router.push('/login')
    }
    ElMessage.error(error.message || '网络错误')
    return Promise.reject(error)
  }
)

export default request

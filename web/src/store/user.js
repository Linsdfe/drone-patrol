import { defineStore } from 'pinia'

/**
 * 用户状态管理模块
 *
 * 功能说明：
 * - 管理用户登录状态（token、用户信息）
 * - 提供用户登录后数据存储
 * - 提供退出登录功能
 * - 数据持久化到localStorage
 *
 * 状态说明：
 * - token: JWT认证令牌
 * - userId: 用户ID
 * - username: 用户账号
 * - nickname: 用户昵称
 * - role: 用户角色（ADMIN管理员 / USER普通用户）
 *
 * @module useUserStore
 *
 * @example
 * // 在组件中使用
 * import { useUserStore } from '@/store/user'
 * const userStore = useUserStore()
 *
 * // 设置用户信息
 * userStore.setUser({ token: 'xxx', userId: 1, username: 'admin', nickname: '管理员', role: 'ADMIN' })
 *
 * // 退出登录
 * userStore.logout()
 */
export const useUserStore = defineStore('user', {
  /**
   * 状态定义
   *
   * 初始化时从localStorage读取数据，实现页面刷新后登录状态不丢失
   */
  state: () => ({
    /** JWT认证令牌 */
    token: localStorage.getItem('token') || '',
    /** 用户ID */
    userId: localStorage.getItem('userId') || '',
    /** 用户账号 */
    username: localStorage.getItem('username') || '',
    /** 用户昵称 */
    nickname: localStorage.getItem('nickname') || '',
    /** 用户角色（ADMIN管理员 / USER普通用户） */
    role: localStorage.getItem('role') || ''
  }),

  actions: {
    /**
     * 设置用户信息
     *
     * 功能：
     * - 更新Pinia状态
     * - 同步写入localStorage实现持久化
     *
     * @param {Object} data - 用户数据对象
     * @param {string} data.token - JWT令牌
     * @param {number|string} data.userId - 用户ID
     * @param {string} data.username - 用户账号
     * @param {string} data.nickname - 用户昵称
     * @param {string} data.role - 用户角色
     *
     * @example
     * setUser({
     *   token: 'eyJhbGciOiJIUzI1NiIs...',
     *   userId: 1,
     *   username: 'admin',
     *   nickname: '管理员',
     *   role: 'ADMIN'
     * })
     */
    setUser(data) {
      this.token = data.token
      this.userId = data.userId
      this.username = data.username
      this.nickname = data.nickname
      this.role = data.role
      localStorage.setItem('token', data.token)
      localStorage.setItem('userId', data.userId)
      localStorage.setItem('username', data.username)
      localStorage.setItem('nickname', data.nickname)
      localStorage.setItem('role', data.role)
    },

    /**
     * 退出登录
     *
     * 功能：
     * - 清空Pinia状态中的用户数据
     * - 清空localStorage中的用户数据
     *
     * @example
     * userStore.logout()
     * // 执行后跳转登录页
     */
    logout() {
      this.token = ''
      this.userId = ''
      this.username = ''
      this.nickname = ''
      this.role = ''
      localStorage.removeItem('token')
      localStorage.removeItem('userId')
      localStorage.removeItem('username')
      localStorage.removeItem('nickname')
      localStorage.removeItem('role')
    }
  }
})

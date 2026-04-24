<template>
  <div class="login-container">
    <ParticleBackground />
    <div class="login-box">
      <h2>无人机巡防管控系统</h2>
      <el-form :model="loginForm" :rules="rules" ref="loginFormRef" label-width="0">
        <el-form-item prop="username">
          <el-input v-model="loginForm.username" placeholder="请输入账号" prefix-icon="User" size="large" />
        </el-form-item>
        <el-form-item prop="password">
          <el-input v-model="loginForm.password" type="password" placeholder="请输入密码" prefix-icon="Lock" size="large" show-password @keyup.enter="handleLogin" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" size="large" style="width: 100%" :loading="loading" @click="handleLogin">登录</el-button>
        </el-form-item>
      </el-form>
      <div class="login-footer">
        <el-button type="text" @click="dialogVisible = true">注册</el-button>
      </div>
      <div class="tips">
        <p>默认管理员：admin / admin123</p>
        <p>普通用户：user / admin123</p>
      </div>
    </div>

    <el-dialog v-model="dialogVisible" title="用户注册" width="400px">
      <el-form :model="registerForm" :rules="registerRules" ref="registerFormRef" label-width="80px">
        <el-form-item label="账号" prop="username">
          <el-input v-model="registerForm.username" placeholder="请输入账号" />
        </el-form-item>
        <el-form-item label="密码" prop="password">
          <el-input v-model="registerForm.password" type="password" placeholder="请输入密码" show-password />
        </el-form-item>
        <el-form-item label="昵称" prop="nickname">
          <el-input v-model="registerForm.nickname" placeholder="请输入昵称" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleRegister" :loading="registerLoading">注册</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { useUserStore } from '@/store/user'
import request from '@/utils/request'
import ParticleBackground from '@/components/ParticleBackground.vue'

const router = useRouter()
const userStore = useUserStore()

const loginFormRef = ref(null)
const registerFormRef = ref(null)
const loading = ref(false)
const registerLoading = ref(false)
const dialogVisible = ref(false)

const loginForm = reactive({
  username: '',
  password: ''
})

const registerForm = reactive({
  username: '',
  password: '',
  nickname: ''
})

const rules = {
  username: [{ required: true, message: '请输入账号', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
}

const registerRules = {
  username: [{ required: true, message: '请输入账号', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }, { min: 6, message: '密码长度不能少于6位', trigger: 'blur' }],
  nickname: [{ required: true, message: '请输入昵称', trigger: 'blur' }]
}

const handleLogin = async () => {
  await loginFormRef.value.validate()
  loading.value = true
  try {
    const res = await request.post('/auth/login', loginForm)
    userStore.setUser(res.data)
    ElMessage.success('登录成功')
    router.push('/')
  } catch (error) {
  } finally {
    loading.value = false
  }
}

const handleRegister = async () => {
  await registerFormRef.value.validate()
  registerLoading.value = true
  try {
    await request.post('/auth/register', registerForm)
    ElMessage.success('注册成功')
    dialogVisible.value = false
    // 清空注册表单
    registerForm.username = ''
    registerForm.password = ''
    registerForm.nickname = ''
  } catch (error) {
  } finally {
    registerLoading.value = false
  }
}
</script>

<style scoped>
.login-container {
  width: 100%;
  height: 100%;
  background: linear-gradient(135deg, #1a1a2e 0%, #16213e 50%, #0f3460 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  position: relative;
  overflow: hidden;
}

.login-box {
  width: 420px;
  padding: 48px 40px;
  background: rgba(255, 255, 255, 0.95);
  border-radius: 16px;
  box-shadow: 0 25px 50px -12px rgba(0, 0, 0, 0.4);
  position: relative;
  z-index: 10;
  backdrop-filter: blur(10px);
  border: 1px solid rgba(255, 255, 255, 0.2);
}

.login-box h2 {
  text-align: center;
  margin-bottom: 40px;
  color: var(--color-text-primary);
  font-size: 26px;
  font-weight: 600;
  letter-spacing: 2px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.login-footer {
  margin-top: 20px;
  text-align: right;
}

.login-footer .el-button {
  color: var(--color-primary);
  font-size: 13px;
  padding: 4px 8px;
}

.login-footer .el-button:hover {
  color: #764ba2;
}

.tips {
  margin-top: 32px;
  padding-top: 20px;
  border-top: 1px solid var(--color-border-lighter);
  text-align: center;
  color: var(--color-text-secondary);
  font-size: 12px;
}

.tips p {
  margin: 6px 0;
  line-height: 1.6;
}

:deep(.el-input__wrapper) {
  padding: 12px 16px;
  border-radius: 8px;
}

:deep(.el-input__inner) {
  font-size: 14px;
}

:deep(.el-button--primary) {
  margin-top: 8px;
  height: 44px;
  font-size: 15px;
  font-weight: 500;
  letter-spacing: 2px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%) !important;
  border: none !important;
  box-shadow: 0 4px 15px rgba(102, 126, 234, 0.4) !important;
}

:deep(.el-button--primary:hover) {
  background: linear-gradient(135deg, #764ba2 0%, #667eea 100%) !important;
  box-shadow: 0 6px 20px rgba(102, 126, 234, 0.5) !important;
}

:deep(.el-dialog__header) {
  font-weight: 600;
  padding: 16px 20px;
  border-bottom: 1px solid var(--color-border-lighter);
}

:deep(.el-dialog__body .el-form-item) {
  margin-bottom: 20px;
}

:deep(.el-dialog__footer) {
  padding-top: 16px;
  border-top: 1px solid var(--color-border-lighter);
}

:deep(.el-dialog__footer .el-button--primary) {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%) !important;
  border: none !important;
}

:deep(.el-dialog__footer .el-button--primary:hover) {
  background: linear-gradient(135deg, #764ba2 0%, #667eea 100%) !important;
}

:deep(.el-dialog__footer .el-button:not(.el-button--primary)) {
  background: var(--color-bg-light);
  border-color: var(--color-border-base);
  color: var(--color-text-regular);
}

:deep(.el-dialog__footer .el-button:not(.el-button--primary):hover) {
  background: var(--color-bg-dark);
  border-color: var(--color-primary);
  color: var(--color-primary);
}
</style>

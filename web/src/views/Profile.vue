<template>
  <div class="profile-page">
    <el-card>
      <template #header>
        <span>个人中心</span>
      </template>

      <el-descriptions v-if="userInfo" :column="2" border>
        <el-descriptions-item label="账号">{{ userInfo.username }}</el-descriptions-item>
        <el-descriptions-item label="昵称">{{ userInfo.nickname }}</el-descriptions-item>
        <el-descriptions-item label="角色">
          <el-tag v-if="userInfo.role === 'ADMIN'" type="danger">管理员</el-tag>
          <el-tag v-else type="primary">普通用户</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="创建时间">{{ userInfo.createTime }}</el-descriptions-item>
      </el-descriptions>

      <el-divider />

      <h4>修改密码</h4>
      <el-form :model="passwordForm" :rules="passwordRules" ref="passwordFormRef" label-width="100px" style="max-width: 500px; margin-top: 20px;">
        <el-form-item label="原密码" prop="oldPassword">
          <el-input v-model="passwordForm.oldPassword" type="password" placeholder="请输入" show-password />
        </el-form-item>
        <el-form-item label="新密码" prop="newPassword">
          <el-input v-model="passwordForm.newPassword" type="password" placeholder="请输入" show-password />
        </el-form-item>
        <el-form-item label="确认密码" prop="confirmPassword">
          <el-input v-model="passwordForm.confirmPassword" type="password" placeholder="请输入" show-password />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleUpdatePassword" :loading="submitLoading">确认修改</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import request from '@/utils/request'

const submitLoading = ref(false)
const userInfo = ref(null)
const passwordFormRef = ref(null)

const passwordForm = reactive({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
})

const validateConfirmPassword = (rule, value, callback) => {
  if (value !== passwordForm.newPassword) {
    callback(new Error('两次输入密码不一致'))
  } else {
    callback()
  }
}

const passwordRules = {
  oldPassword: [{ required: true, message: '请输入原密码', trigger: 'blur' }],
  newPassword: [{ required: true, message: '请输入新密码', trigger: 'blur' }, { min: 6, message: '密码长度不能少于6位', trigger: 'blur' }],
  confirmPassword: [{ required: true, message: '请确认新密码', trigger: 'blur' }, { validator: validateConfirmPassword, trigger: 'blur' }]
}

const loadUserInfo = async () => {
  try {
    const res = await request.get('/user/current')
    userInfo.value = res.data
  } catch (error) {
  }
}

const handleUpdatePassword = async () => {
  await passwordFormRef.value.validate()
  submitLoading.value = true
  try {
    await request.put('/user/password', {
      oldPassword: passwordForm.oldPassword,
      newPassword: passwordForm.newPassword
    })
    ElMessage.success('密码修改成功')
    passwordForm.oldPassword = ''
    passwordForm.newPassword = ''
    passwordForm.confirmPassword = ''
  } catch (error) {
  } finally {
    submitLoading.value = false
  }
}

onMounted(() => {
  loadUserInfo()
})
</script>

<style scoped>
.profile-page {
  padding: 0;
}
</style>

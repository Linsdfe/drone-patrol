<template>
  <div class="device-page">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>设备管理</span>
          <el-button type="primary" v-if="isAdmin" @click="handleAdd">新增设备</el-button>
        </div>
      </template>

      <el-form :inline="true" :model="queryForm" class="search-form">
        <el-form-item label="设备编号">
          <el-input v-model="queryForm.deviceCode" placeholder="请输入" clearable />
        </el-form-item>
        <el-form-item label="设备名称">
          <el-input v-model="queryForm.deviceName" placeholder="请输入" clearable />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="queryForm.status" placeholder="请选择" clearable>
            <el-option label="正常" value="NORMAL" />
            <el-option label="维修中" value="MAINTENANCE" />
            <el-option label="已报废" value="SCRAPPED" />
            <el-option label="任务中" value="IN_TASK" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="loadData">查询</el-button>
          <el-button @click="resetQuery">重置</el-button>
        </el-form-item>
      </el-form>

      <el-table :data="tableData" border style="width: 100%" v-loading="loading">
        <el-table-column prop="deviceCode" label="设备编号" width="150" />
        <el-table-column prop="deviceName" label="设备名称" width="150" />
        <el-table-column prop="deviceModel" label="设备型号" width="150" />
        <el-table-column prop="batteryLife" label="续航时长(分钟)" width="150" />
        <el-table-column prop="cameraParam" label="摄像头参数" width="150" />
        <el-table-column prop="owner" label="归属人" width="120" />
        <el-table-column prop="status" label="状态" width="120">
          <template #default="{ row }">
            <el-tag v-if="row.status === 'NORMAL'" type="success">正常</el-tag>
            <el-tag v-else-if="row.status === 'MAINTENANCE'" type="warning">维修中</el-tag>
            <el-tag v-else-if="row.status === 'SCRAPPED'" type="info">已报废</el-tag>
            <el-tag v-else-if="row.status === 'IN_TASK'" type="primary">任务中</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="remark" label="备注" />
        <el-table-column label="操作" width="280" fixed="right">
          <template #default="{ row }">
            <div class="action-buttons">
              <el-button type="primary" size="small" link @click="handleDetail(row)">详情</el-button>
              <el-button type="primary" size="small" link v-if="isAdmin" @click="handleEdit(row)">编辑</el-button>
              <el-button type="primary" size="small" link v-if="isAdmin && row.status !== 'IN_TASK'" @click="handleChangeStatus(row)">切换状态</el-button>
              <el-button type="danger" size="small" link v-if="isAdmin" @click="handleDelete(row)">删除</el-button>
            </div>
          </template>
        </el-table-column>
      </el-table>

      <el-pagination
        v-model:current-page="queryForm.pageNum"
        v-model:page-size="queryForm.pageSize"
        :total="total"
        :page-sizes="[10, 20, 50, 100]"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="loadData"
        @current-change="loadData"
        style="margin-top: 20px; justify-content: flex-end"
      />
    </el-card>

    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="600px">
      <el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
        <el-form-item label="设备编号" prop="deviceCode">
          <el-input v-model="form.deviceCode" placeholder="请输入" :disabled="isEdit" />
        </el-form-item>
        <el-form-item label="设备名称" prop="deviceName">
          <el-input v-model="form.deviceName" placeholder="请输入" />
        </el-form-item>
        <el-form-item label="设备型号" prop="deviceModel">
          <el-input v-model="form.deviceModel" placeholder="请输入" />
        </el-form-item>
        <el-form-item label="续航时长" prop="batteryLife">
          <el-input-number v-model="form.batteryLife" :min="0" placeholder="请输入" />
        </el-form-item>
        <el-form-item label="摄像头参数" prop="cameraParam">
          <el-input v-model="form.cameraParam" placeholder="请输入" />
        </el-form-item>
        <el-form-item label="归属人" prop="owner">
          <el-input v-model="form.owner" placeholder="请输入" />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-select v-model="form.status" placeholder="请选择">
            <el-option label="正常" value="NORMAL" />
            <el-option label="维修中" value="MAINTENANCE" />
            <el-option label="已报废" value="SCRAPPED" />
          </el-select>
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input v-model="form.remark" type="textarea" placeholder="请输入" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit" :loading="submitLoading">确定</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="detailDialogVisible" title="设备详情" width="600px">
      <el-descriptions v-if="currentRow" :column="1" border>
        <el-descriptions-item label="设备编号">{{ currentRow.deviceCode }}</el-descriptions-item>
        <el-descriptions-item label="设备名称">{{ currentRow.deviceName }}</el-descriptions-item>
        <el-descriptions-item label="设备型号">{{ currentRow.deviceModel }}</el-descriptions-item>
        <el-descriptions-item label="续航时长">{{ currentRow.batteryLife }}分钟</el-descriptions-item>
        <el-descriptions-item label="摄像头参数">{{ currentRow.cameraParam }}</el-descriptions-item>
        <el-descriptions-item label="归属人">{{ currentRow.owner }}</el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag v-if="currentRow.status === 'NORMAL'" type="success">正常</el-tag>
          <el-tag v-else-if="currentRow.status === 'MAINTENANCE'" type="warning">维修中</el-tag>
          <el-tag v-else-if="currentRow.status === 'SCRAPPED'" type="info">已报废</el-tag>
          <el-tag v-else-if="currentRow.status === 'IN_TASK'" type="primary">任务中</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="备注">{{ currentRow.remark }}</el-descriptions-item>
        <el-descriptions-item label="创建时间">{{ currentRow.createTime }}</el-descriptions-item>
      </el-descriptions>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useUserStore } from '@/store/user'
import request from '@/utils/request'

const userStore = useUserStore()
const isAdmin = computed(() => userStore.role === 'ADMIN')

const loading = ref(false)
const submitLoading = ref(false)
const tableData = ref([])
const total = ref(0)
const dialogVisible = ref(false)
const detailDialogVisible = ref(false)
const isEdit = ref(false)
const dialogTitle = computed(() => isEdit.value ? '编辑设备' : '新增设备')
const formRef = ref(null)
const currentRow = ref(null)

const queryForm = reactive({
  pageNum: 1,
  pageSize: 10,
  deviceCode: '',
  deviceName: '',
  status: '',
  owner: ''
})

const form = reactive({
  id: null,
  deviceCode: '',
  deviceName: '',
  deviceModel: '',
  batteryLife: null,
  cameraParam: '',
  owner: '',
  status: 'NORMAL',
  remark: ''
})

const rules = {
  deviceCode: [{ required: true, message: '请输入设备编号', trigger: 'blur' }],
  deviceName: [{ required: true, message: '请输入设备名称', trigger: 'blur' }],
  status: [{ required: true, message: '请选择状态', trigger: 'change' }]
}

const loadData = async () => {
  loading.value = true
  try {
    const res = await request.get('/device/page', { params: queryForm })
    tableData.value = res.data.records
    total.value = res.data.total
  } catch (error) {
  } finally {
    loading.value = false
  }
}

const resetQuery = () => {
  queryForm.pageNum = 1
  queryForm.deviceCode = ''
  queryForm.deviceName = ''
  queryForm.status = ''
  queryForm.owner = ''
  loadData()
}

const handleAdd = () => {
  isEdit.value = false
  Object.assign(form, {
    id: null,
    deviceCode: '',
    deviceName: '',
    deviceModel: '',
    batteryLife: null,
    cameraParam: '',
    owner: '',
    status: 'NORMAL',
    remark: ''
  })
  dialogVisible.value = true
}

const handleEdit = (row) => {
  isEdit.value = true
  Object.assign(form, { ...row })
  dialogVisible.value = true
}

const handleDetail = (row) => {
  currentRow.value = row
  detailDialogVisible.value = true
}

const handleChangeStatus = async (row) => {
  try {
    await ElMessageBox.confirm('确定要切换设备状态吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    const newStatus = row.status === 'NORMAL' ? 'MAINTENANCE' : 'NORMAL'
    await request.put('/device/status', null, { params: { id: row.id, status: newStatus } })
    ElMessage.success('状态切换成功')
    loadData()
  } catch (error) {
  }
}

const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm('确定要删除该设备吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await request.delete(`/device/${row.id}`)
    ElMessage.success('删除成功')
    loadData()
  } catch (error) {
  }
}

const handleSubmit = async () => {
  await formRef.value.validate()
  submitLoading.value = true
  try {
    if (isEdit.value) {
      await request.put('/device', form)
      ElMessage.success('编辑成功')
    } else {
      await request.post('/device', form)
      ElMessage.success('新增成功')
    }
    dialogVisible.value = false
    loadData()
  } catch (error) {
  } finally {
    submitLoading.value = false
  }
}

onMounted(() => {
  loadData()
})
</script>

<style scoped>
.device-page {
  padding: 0;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 16px;
  font-weight: 600;
  color: var(--color-text-primary);
}

.search-form {
  margin-bottom: 20px;
}

.search-form :deep(.el-form-item) {
  margin-bottom: 16px;
}

.search-form :deep(.el-input),
.search-form :deep(.el-select) {
  width: 200px;
}

.action-buttons {
  display: flex;
  flex-wrap: nowrap;
  align-items: center;
  gap: 4px;
  white-space: nowrap;
}

.action-buttons .el-button {
  padding: 4px 8px;
  min-height: auto;
  height: auto;
}

:deep(.el-table) {
  font-size: 14px;
}

:deep(.el-table th) {
  background-color: var(--color-bg-light) !important;
  color: var(--color-text-primary);
  font-weight: 600;
}

:deep(.el-table__row) {
  transition: background-color 0.2s ease;
}

:deep(.el-descriptions__label) {
  background-color: var(--color-bg-light) !important;
  color: var(--color-text-regular);
  font-weight: 400;
}

:deep(.el-descriptions__content) {
  color: var(--color-text-primary);
}

:deep(.el-dialog__header) {
  font-weight: 600;
  font-size: 16px;
}

:deep(.el-dialog__body) {
  padding: 24px;
}

:deep(.el-form-item__label) {
  color: var(--color-text-regular);
  font-weight: 400;
}

:deep(.el-tag) {
  border-radius: 4px;
}

:deep(.el-pagination) {
  padding: 16px 0;
}
</style>

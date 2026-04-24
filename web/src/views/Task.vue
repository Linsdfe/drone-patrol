<template>
  <div class="task-page">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>任务管理</span>
          <el-button type="primary" v-if="isAdmin" @click="handleAdd">新增任务</el-button>
        </div>
      </template>

      <el-form :inline="true" :model="queryForm" class="search-form">
        <el-form-item label="任务名称">
          <el-input v-model="queryForm.taskName" placeholder="请输入" clearable />
        </el-form-item>
        <el-form-item label="执行人">
          <el-input v-model="queryForm.executorName" placeholder="请输入" clearable />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="queryForm.status" placeholder="请选择" clearable>
            <el-option label="待执行" value="PENDING" />
            <el-option label="执行中" value="EXECUTING" />
            <el-option label="已完成" value="COMPLETED" />
            <el-option label="已取消" value="CANCELLED" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="loadData">查询</el-button>
          <el-button @click="resetQuery">重置</el-button>
        </el-form-item>
      </el-form>

      <el-table :data="tableData" border style="width: 100%" v-loading="loading">
        <el-table-column prop="taskName" label="任务名称" width="180" />
        <el-table-column prop="executorName" label="执行人" width="120" />
        <el-table-column prop="status" label="状态" width="120">
          <template #default="{ row }">
            <el-tag v-if="row.status === 'PENDING'" type="primary">待执行</el-tag>
            <el-tag v-else-if="row.status === 'EXECUTING'" type="warning">执行中</el-tag>
            <el-tag v-else-if="row.status === 'COMPLETED'" type="success">已完成</el-tag>
            <el-tag v-else-if="row.status === 'CANCELLED'" type="info">已取消</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="progress" label="进度" width="150">
          <template #default="{ row }">
            <el-progress :percentage="row.progress || 0" :status="row.status === 'COMPLETED' ? 'success' : ''" />
          </template>
        </el-table-column>
        <el-table-column prop="startTime" label="开始时间" width="180" />
        <el-table-column prop="endTime" label="结束时间" width="180" />
        <el-table-column prop="remark" label="备注" />
        <el-table-column label="操作" width="320" fixed="right">
          <template #default="{ row }">
            <div class="action-buttons">
              <el-button type="primary" size="small" link @click="handleDetail(row)">详情</el-button>
              <el-button type="primary" size="small" link v-if="isAdmin && row.status === 'PENDING'" @click="handleStart(row)">开始</el-button>
              <el-button type="primary" size="small" link v-if="isAdmin && row.status === 'EXECUTING'" @click="handleComplete(row)">完成</el-button>
              <el-button type="warning" size="small" link v-if="isAdmin && (row.status === 'PENDING' || row.status === 'EXECUTING')" @click="handleCancel(row)">取消</el-button>
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
        <el-form-item label="任务名称" prop="taskName">
          <el-input v-model="form.taskName" placeholder="请输入" />
        </el-form-item>
        <el-form-item label="选择设备" prop="deviceId">
          <el-select v-model="form.deviceId" placeholder="请选择" style="width: 100%">
            <el-option v-for="item in deviceList" :key="item.id" :label="item.deviceName" :value="item.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="选择航线" prop="routeId">
          <el-select v-model="form.routeId" placeholder="请选择" style="width: 100%">
            <el-option v-for="item in routeList" :key="item.id" :label="item.routeName" :value="item.id" />
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

    <el-dialog v-model="detailDialogVisible" title="任务详情" width="800px">
      <el-descriptions v-if="currentRow" :column="2" border>
        <el-descriptions-item label="任务名称">{{ currentRow.taskName }}</el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag v-if="currentRow.status === 'PENDING'" type="primary">待执行</el-tag>
          <el-tag v-else-if="currentRow.status === 'EXECUTING'" type="warning">执行中</el-tag>
          <el-tag v-else-if="currentRow.status === 'COMPLETED'" type="success">已完成</el-tag>
          <el-tag v-else-if="currentRow.status === 'CANCELLED'" type="info">已取消</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="执行人">{{ currentRow.executorName }}</el-descriptions-item>
        <el-descriptions-item label="进度">{{ currentRow.progress || 0 }}%</el-descriptions-item>
        <el-descriptions-item label="开始时间">{{ currentRow.startTime }}</el-descriptions-item>
        <el-descriptions-item label="结束时间">{{ currentRow.endTime }}</el-descriptions-item>
        <el-descriptions-item label="备注" :span="2">{{ currentRow.remark }}</el-descriptions-item>
      </el-descriptions>
      <div v-if="currentRow && currentRow.routePoints" class="map-container" style="margin-top: 20px;">
        <div id="taskMap" style="width: 100%; height: 400px;"></div>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, nextTick, computed, onUnmounted } from 'vue'
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
const dialogTitle = computed(() => isEdit.value ? '编辑任务' : '新增任务')
const formRef = ref(null)
const currentRow = ref(null)
const deviceList = ref([])
const routeList = ref([])
let taskMap = null
let simulationInterval = null

const queryForm = reactive({
  pageNum: 1,
  pageSize: 10,
  taskName: '',
  executorName: '',
  deviceName: '',
  status: '',
  startTime: '',
  endTime: ''
})

const form = reactive({
  id: null,
  taskName: '',
  deviceId: null,
  routeId: null,
  executorId: userStore.userId,
  executorName: userStore.nickname || userStore.username,
  remark: ''
})

const rules = {
  taskName: [{ required: true, message: '请输入任务名称', trigger: 'blur' }],
  deviceId: [{ required: true, message: '请选择设备', trigger: 'change' }],
  routeId: [{ required: true, message: '请选择航线', trigger: 'change' }]
}

const loadData = async () => {
  loading.value = true
  try {
    const res = await request.get('/task/page', { params: queryForm })
    tableData.value = res.data.records
    total.value = res.data.total
  } catch (error) {
  } finally {
    loading.value = false
  }
}

const loadDeviceList = async () => {
  try {
    const res = await request.get('/device/list/normal')
    deviceList.value = res.data.records
  } catch (error) {
  }
}

const loadRouteList = async () => {
  try {
    const res = await request.get('/route/list')
    routeList.value = res.data.records
  } catch (error) {
  }
}

const resetQuery = () => {
  queryForm.pageNum = 1
  queryForm.taskName = ''
  queryForm.executorName = ''
  queryForm.deviceName = ''
  queryForm.status = ''
  queryForm.startTime = ''
  queryForm.endTime = ''
  loadData()
}

const handleAdd = () => {
  isEdit.value = false
  Object.assign(form, {
    id: null,
    taskName: '',
    deviceId: null,
    routeId: null,
    executorId: userStore.userId,
    executorName: userStore.nickname || userStore.username,
    remark: ''
  })
  loadDeviceList()
  loadRouteList()
  dialogVisible.value = true
}

const handleDetail = async (row) => {
  currentRow.value = { ...row }
  try {
    const routeRes = await request.get(`/route/${row.routeId}`)
    currentRow.value.routePoints = routeRes.data.points
  } catch (error) {
  }
  detailDialogVisible.value = true
  nextTick(() => {
    if (window.AMap && currentRow.value.routePoints) {
      taskMap = new AMap.Map('taskMap', {
        center: [116.397428, 39.90923],
        zoom: 10
      })
      const points = JSON.parse(currentRow.value.routePoints || '[]')
      if (points.length > 0) {
        const path = points.map(p => [p.lng, p.lat])
        const polyline = new AMap.Polyline({
          path: path,
          borderWeight: 2,
          strokeColor: '#3366FF',
          lineJoin: 'round'
        })
        taskMap.add(polyline)
        points.forEach(p => {
          const marker = new AMap.Marker({
            position: [p.lng, p.lat],
            map: taskMap
          })
        })
        taskMap.setFitView()
      }
    }
  })
}

const handleStart = async (row) => {
  try {
    await ElMessageBox.confirm('确定要开始执行该任务吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await request.put(`/task/start/${row.id}`)
    ElMessage.success('任务已开始')
    loadData()
  } catch (error) {
  }
}

const handleComplete = async (row) => {
  try {
    await ElMessageBox.confirm('确定要完成该任务吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await request.put(`/task/complete/${row.id}`)
    ElMessage.success('任务已完成')
    loadData()
  } catch (error) {
  }
}

const handleCancel = async (row) => {
  try {
    await ElMessageBox.confirm('确定要取消该任务吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await request.put(`/task/cancel/${row.id}`)
    ElMessage.success('任务已取消')
    loadData()
  } catch (error) {
  }
}

const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm('确定要删除该任务吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await request.delete(`/task/${row.id}`)
    ElMessage.success('删除成功')
    loadData()
  } catch (error) {
  }
}

const handleSubmit = async () => {
  await formRef.value.validate()
  submitLoading.value = true
  try {
    await request.post('/task', form)
    ElMessage.success('新增成功')
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

onUnmounted(() => {
  if (taskMap) {
    taskMap.destroy()
    taskMap = null
  }
  if (simulationInterval) {
    clearInterval(simulationInterval)
    simulationInterval = null
  }
})
</script>

<style scoped>
.task-page {
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

.map-container {
  margin-top: 20px;
  border: 1px solid var(--color-border-lighter);
  border-radius: 8px;
  overflow: hidden;
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

:deep(.el-descriptions__label) {
  background-color: var(--color-bg-light) !important;
  color: var(--color-text-regular);
  font-weight: 400;
}

:deep(.el-descriptions__content) {
  color: var(--color-text-primary);
}

:deep(.el-tag) {
  border-radius: 4px;
}

:deep(.el-pagination) {
  padding: 16px 0;
}
</style>

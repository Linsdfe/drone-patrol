<template>
  <div class="result-page">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>结果管理</span>
          <el-button type="primary" @click="handleExport">导出Excel</el-button>
        </div>
      </template>

      <el-form :inline="true" :model="queryForm" class="search-form">
        <el-form-item label="结果编号">
          <el-input v-model="queryForm.resultCode" placeholder="请输入" clearable />
        </el-form-item>
        <el-form-item label="执行人">
          <el-input v-model="queryForm.executorName" placeholder="请输入" clearable />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="loadData">查询</el-button>
          <el-button @click="resetQuery">重置</el-button>
        </el-form-item>
      </el-form>

      <el-table :data="tableData" border style="width: 100%" v-loading="loading">
        <el-table-column prop="resultCode" label="结果编号" width="200" />
        <el-table-column prop="executorName" label="执行人" width="120" />
        <el-table-column prop="completeTime" label="完成时间" width="180" />
        <el-table-column prop="summary" label="巡防概述" width="200" />
        <el-table-column prop="discovery" label="发现情况" width="200" />
        <el-table-column prop="handling" label="处理情况" width="200" />
        <el-table-column label="操作" width="320" fixed="right">
          <template #default="{ row }">
            <div class="action-buttons">
              <el-button type="primary" size="small" link @click="handleDetail(row)">详情</el-button>
              <el-button type="success" size="small" link @click="handleAiRecognize(row)" :loading="aiLoading && currentAiId === row.id">AI识别</el-button>
              <el-button type="primary" size="small" link v-if="isAdmin" @click="handleEdit(row)">编辑</el-button>
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

    <el-dialog v-model="dialogVisible" title="编辑结果" width="600px">
      <el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
        <el-form-item label="巡防概述" prop="summary">
          <el-input v-model="form.summary" type="textarea" placeholder="请输入" />
        </el-form-item>
        <el-form-item label="发现情况" prop="discovery">
          <el-input v-model="form.discovery" type="textarea" placeholder="请输入" />
        </el-form-item>
        <el-form-item label="处理情况" prop="handling">
          <el-input v-model="form.handling" type="textarea" placeholder="请输入" />
        </el-form-item>
        <el-form-item label="AI识别结果" prop="aiResult">
          <el-input v-model="form.aiResult" type="textarea" placeholder="预留功能" disabled />
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

    <el-dialog v-model="detailDialogVisible" title="结果详情" width="800px">
      <el-descriptions v-if="currentRow" :column="2" border>
        <el-descriptions-item label="结果编号">{{ currentRow.resultCode }}</el-descriptions-item>
        <el-descriptions-item label="任务ID">{{ currentRow.taskId }}</el-descriptions-item>
        <el-descriptions-item label="设备ID">{{ currentRow.deviceId }}</el-descriptions-item>
        <el-descriptions-item label="航线ID">{{ currentRow.routeId }}</el-descriptions-item>
        <el-descriptions-item label="执行人">{{ currentRow.executorName }}</el-descriptions-item>
        <el-descriptions-item label="完成时间">{{ currentRow.completeTime }}</el-descriptions-item>
        <el-descriptions-item label="巡防概述" :span="2">{{ currentRow.summary }}</el-descriptions-item>
        <el-descriptions-item label="发现情况" :span="2">{{ currentRow.discovery }}</el-descriptions-item>
        <el-descriptions-item label="处理情况" :span="2">{{ currentRow.handling }}</el-descriptions-item>
        <el-descriptions-item label="AI识别结果" :span="2">
          <el-tag v-if="currentRow.aiResult" type="success">已完成</el-tag>
          <el-tag v-else type="info">未识别</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="备注" :span="2">{{ currentRow.remark }}</el-descriptions-item>
      </el-descriptions>
      <div v-if="currentRow && currentRow.aiResult" class="ai-result-container">
        <div class="ai-result-title">AI识别报告</div>
        <pre class="ai-result-content">{{ currentRow.aiResult }}</pre>
      </div>
      <div v-if="currentRow && currentRow.routePoints" class="map-container" style="margin-top: 20px;">
        <div id="resultMap" style="width: 100%; height: 400px;"></div>
      </div>
    </el-dialog>

    <el-dialog v-model="aiDialogVisible" title="AI智能分析" width="700px">
      <div v-if="aiAnalysisResult" class="ai-analysis-container">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="风险等级">
            <el-tag :type="getRiskTagType(aiAnalysisResult.riskLevel)">{{ aiAnalysisResult.riskLevel }}</el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="风险评分">{{ aiAnalysisResult.riskScore }}/100</el-descriptions-item>
        </el-descriptions>
        <div class="ai-result-title" style="margin-top: 20px;">详细分析报告</div>
        <pre class="ai-result-content">{{ aiAnalysisResult.aiResult }}</pre>
      </div>
      <div v-else-if="aiLoading" class="ai-loading-container">
        <el-icon class="is-loading"><Loading /></el-icon>
        <span>AI正在分析中，请稍候...</span>
      </div>
      <template #footer>
        <el-button @click="aiDialogVisible = false">关闭</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, nextTick, computed, onUnmounted } from 'vue'
import { ElMessage, ElMessageBox, ElIcon } from 'element-plus'
import { Loading } from '@element-plus/icons-vue'
import axios from 'axios'
import { useUserStore } from '@/store/user'
import request from '@/utils/request'

const userStore = useUserStore()
const isAdmin = computed(() => userStore.role === 'ADMIN')

const loading = ref(false)
const submitLoading = ref(false)
const aiLoading = ref(false)
const currentAiId = ref(null)
const aiDialogVisible = ref(false)
const aiAnalysisResult = ref(null)
const tableData = ref([])
const total = ref(0)
const dialogVisible = ref(false)
const detailDialogVisible = ref(false)
const formRef = ref(null)
const currentRow = ref(null)
let resultMap = null

const queryForm = reactive({
  pageNum: 1,
  pageSize: 10,
  resultCode: '',
  taskName: '',
  executorName: '',
  startTime: '',
  endTime: ''
})

const form = reactive({
  id: null,
  summary: '',
  discovery: '',
  handling: '',
  aiResult: '',
  remark: ''
})

const rules = {}

const loadData = async () => {
  loading.value = true
  try {
    const res = await request.get('/result/page', { params: queryForm })
    tableData.value = res.data.records
    total.value = res.data.total
  } catch (error) {
  } finally {
    loading.value = false
  }
}

const resetQuery = () => {
  queryForm.pageNum = 1
  queryForm.resultCode = ''
  queryForm.taskName = ''
  queryForm.executorName = ''
  queryForm.startTime = ''
  queryForm.endTime = ''
  loadData()
}

const handleEdit = (row) => {
  Object.assign(form, { ...row })
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
      resultMap = new AMap.Map('resultMap', {
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
        resultMap.add(polyline)
        points.forEach(p => {
          const marker = new AMap.Marker({
            position: [p.lng, p.lat],
            map: resultMap
          })
        })
        resultMap.setFitView()
      }
    }
  })
}

const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm('确定要删除该结果吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await request.delete(`/result/${row.id}`)
    ElMessage.success('删除成功')
    loadData()
  } catch (error) {
  }
}

const handleAiRecognize = async (row) => {
  currentAiId.value = row.id
  aiDialogVisible.value = true
  aiAnalysisResult.value = null
  aiLoading.value = true
  try {
    const res = await request.post(`/ai/recognize/${row.id}`)
    aiAnalysisResult.value = res.data
    currentRow.value = { ...currentRow.value, aiResult: res.data.aiResult }
    ElMessage.success('AI识别完成')
  } catch (error) {
    ElMessage.error('AI识别失败')
  } finally {
    aiLoading.value = false
    currentAiId.value = null
  }
}

const getRiskTagType = (level) => {
  if (level === '无风险') return 'success'
  if (level === '低风险') return 'success'
  if (level === '中风险') return 'warning'
  if (level === '高风险') return 'danger'
  if (level === '极高风险') return 'danger'
  return 'info'
}

/**
 * 处理导出Excel
 * 导出当前筛选条件下的所有巡防结果数据
 */
const handleExport = async () => {
  try {
    ElMessage.info('正在导出数据，请稍候...')

    const params = new URLSearchParams()
    for (const key in queryForm) {
      if (key !== 'pageNum' && key !== 'pageSize' && queryForm[key]) {
        params.append(key, queryForm[key])
      }
    }

    const response = await axios({
      url: '/api/result/export',
      method: 'get',
      params: params,
      headers: {
        Authorization: 'Bearer ' + userStore.token
      },
      responseType: 'blob'
    })

    const blob = new Blob([response.data], {
      type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet'
    })
    const url = window.URL.createObjectURL(blob)
    const link = document.createElement('a')
    link.href = url

    const contentDisposition = response.headers['content-disposition']
    let fileName = '巡防结果导出.xlsx'
    if (contentDisposition) {
      const fileNameMatch = contentDisposition.match(/filename=(.+)/)
      if (fileNameMatch && fileNameMatch.length > 1) {
        fileName = decodeURIComponent(fileNameMatch[1])
      }
    }

    link.download = fileName
    document.body.appendChild(link)
    link.click()
    document.body.removeChild(link)
    window.URL.revokeObjectURL(url)

    ElMessage.success('导出成功')
  } catch (error) {
    ElMessage.error('导出失败，请重试')
  }
}

const handleSubmit = async () => {
  submitLoading.value = true
  try {
    await request.put('/result', form)
    ElMessage.success('编辑成功')
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
  if (resultMap) {
    resultMap.destroy()
    resultMap = null
  }
})
</script>

<style scoped>
.result-page {
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

.ai-result-container {
  margin-top: 20px;
  padding: 15px;
  background-color: var(--color-bg-light);
  border-radius: 8px;
}

.ai-result-title {
  font-size: 14px;
  font-weight: 600;
  color: var(--color-text-primary);
  margin-bottom: 10px;
}

.ai-result-content {
  white-space: pre-wrap;
  word-wrap: break-word;
  font-family: 'Helvetica Neue', Helvetica, 'PingFang SC', 'Hiragino Sans GB', 'Microsoft YaHei', Arial, sans-serif;
  font-size: 13px;
  line-height: 1.6;
  color: var(--color-text-regular);
  margin: 0;
  padding: 10px;
  background-color: var(--color-bg-white);
  border: 1px solid var(--color-border-lighter);
  border-radius: 6px;
  max-height: 400px;
  overflow-y: auto;
}

.ai-analysis-container {
  padding: 10px;
}

.ai-loading-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 40px 20px;
  color: var(--color-text-secondary);
}

.ai-loading-container .el-icon {
  font-size: 32px;
  margin-bottom: 10px;
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

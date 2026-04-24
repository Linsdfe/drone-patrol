<template>
  <div class="route-page">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>航线管理</span>
          <el-button type="primary" v-if="isAdmin" @click="handleAdd">新增航线</el-button>
        </div>
      </template>

      <el-form :inline="true" :model="queryForm" class="search-form">
        <el-form-item label="航线名称">
          <el-input v-model="queryForm.routeName" placeholder="请输入" clearable />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="loadData">查询</el-button>
          <el-button @click="resetQuery">重置</el-button>
        </el-form-item>
      </el-form>

      <el-table :data="tableData" border style="width: 100%" v-loading="loading">
        <el-table-column prop="routeName" label="航线名称" width="200" />
        <el-table-column prop="expectedDuration" label="预计时长(分钟)" width="150" />
        <el-table-column prop="flightHeight" label="飞行高度(米)" width="150" />
        <el-table-column prop="remark" label="备注" />
        <el-table-column prop="createTime" label="创建时间" width="180" />
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <div class="action-buttons">
              <el-button type="primary" size="small" link @click="handleDetail(row)">详情</el-button>
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

    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="800px">
      <el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
        <el-form-item label="航线名称" prop="routeName">
          <el-input v-model="form.routeName" placeholder="请输入" />
        </el-form-item>
        <el-form-item label="预计时长" prop="expectedDuration">
          <el-input-number v-model="form.expectedDuration" :min="0" placeholder="请输入" />
        </el-form-item>
        <el-form-item label="飞行高度" prop="flightHeight">
          <el-input-number v-model="form.flightHeight" :min="0" placeholder="请输入" />
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input v-model="form.remark" type="textarea" placeholder="请输入" />
        </el-form-item>
      </el-form>
      <div class="map-container">
        <div id="map" style="width: 100%; height: 400px;"></div>
      </div>
      <div style="margin-top: 10px;">
        <el-button type="primary" size="small" @click="clearRoute">清除航线</el-button>
        <span style="color: var(--color-text-secondary); margin-left: 10px;">提示：点击地图添加航点，右键删除航点</span>
      </div>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit" :loading="submitLoading">确定</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="detailDialogVisible" title="航线详情" width="800px">
      <el-descriptions v-if="currentRow" :column="2" border>
        <el-descriptions-item label="航线名称">{{ currentRow.routeName }}</el-descriptions-item>
        <el-descriptions-item label="预计时长">{{ currentRow.expectedDuration }}分钟</el-descriptions-item>
        <el-descriptions-item label="飞行高度">{{ currentRow.flightHeight }}米</el-descriptions-item>
        <el-descriptions-item label="创建时间">{{ currentRow.createTime }}</el-descriptions-item>
        <el-descriptions-item label="备注" :span="2">{{ currentRow.remark }}</el-descriptions-item>
      </el-descriptions>
      <div class="map-container" style="margin-top: 20px;">
        <div id="detailMap" style="width: 100%; height: 400px;"></div>
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
const dialogTitle = computed(() => isEdit.value ? '编辑航线' : '新增航线')
const formRef = ref(null)
const currentRow = ref(null)
let map = null
let detailMap = null
let markers = []
let polyline = null

const queryForm = reactive({
  pageNum: 1,
  pageSize: 10,
  routeName: ''
})

const form = reactive({
  id: null,
  routeName: '',
  points: '[]',
  expectedDuration: null,
  flightHeight: null,
  remark: ''
})

const rules = {
  routeName: [{ required: true, message: '请输入航线名称', trigger: 'blur' }]
}

const initMap = () => {
  if (!window.AMap) {
    ElMessage.warning('请先在index.html中配置高德地图key')
    return
  }
  map = new AMap.Map('map', {
    center: [116.397428, 39.90923],
    zoom: 10
  })
  map.on('click', (e) => {
    addMarker(e.lnglat)
  })
}

const initDetailMap = () => {
  if (!window.AMap) return
  detailMap = new AMap.Map('detailMap', {
    center: [116.397428, 39.90923],
    zoom: 10
  })
}

const addMarker = (lnglat) => {
  const marker = new AMap.Marker({
    position: lnglat,
    map: map
  })
  marker.on('rightclick', () => {
    const index = markers.indexOf(marker)
    if (index > -1) {
      markers.splice(index, 1)
      map.remove(marker)
      updatePolyline()
    }
  })
  markers.push(marker)
  updatePolyline()
}

const updatePolyline = () => {
  if (polyline) {
    map.remove(polyline)
  }
  if (markers.length > 1) {
    const path = markers.map(m => m.getPosition())
    polyline = new AMap.Polyline({
      path: path,
      borderWeight: 2,
      strokeColor: '#3366FF',
      lineJoin: 'round'
    })
    map.add(polyline)
  }
  const points = markers.map(m => ({ lng: m.getPosition().lng, lat: m.getPosition().lat }))
  form.points = JSON.stringify(points)
}

const clearRoute = () => {
  markers.forEach(m => map.remove(m))
  markers = []
  if (polyline) {
    map.remove(polyline)
    polyline = null
  }
  form.points = '[]'
}

const loadData = async () => {
  loading.value = true
  try {
    const res = await request.get('/route/page', { params: queryForm })
    tableData.value = res.data.records
    total.value = res.data.total
  } catch (error) {
  } finally {
    loading.value = false
  }
}

const resetQuery = () => {
  queryForm.pageNum = 1
  queryForm.routeName = ''
  loadData()
}

const handleAdd = () => {
  isEdit.value = false
  Object.assign(form, {
    id: null,
    routeName: '',
    points: '[]',
    expectedDuration: null,
    flightHeight: null,
    remark: ''
  })
  dialogVisible.value = true
  nextTick(() => {
    initMap()
    clearRoute()
  })
}

const handleEdit = (row) => {
  isEdit.value = true
  Object.assign(form, { ...row })
  dialogVisible.value = true
  nextTick(() => {
    initMap()
    clearRoute()
    const points = JSON.parse(row.points || '[]')
    points.forEach(p => {
      addMarker([p.lng, p.lat])
    })
  })
}

const handleDetail = (row) => {
  currentRow.value = row
  detailDialogVisible.value = true
  nextTick(() => {
    initDetailMap()
    const points = JSON.parse(row.points || '[]')
    if (points.length > 0) {
      const path = points.map(p => [p.lng, p.lat])
      const polyline = new AMap.Polyline({
        path: path,
        borderWeight: 2,
        strokeColor: '#3366FF',
        lineJoin: 'round'
      })
      detailMap.add(polyline)
      points.forEach(p => {
        const marker = new AMap.Marker({
          position: [p.lng, p.lat],
          map: detailMap
        })
      })
      detailMap.setFitView()
    }
  })
}

const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm('确定要删除该航线吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await request.delete(`/route/${row.id}`)
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
      await request.put('/route', form)
      ElMessage.success('编辑成功')
    } else {
      await request.post('/route', form)
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

onUnmounted(() => {
  if (map) {
    map.destroy()
    map = null
  }
  if (detailMap) {
    detailMap.destroy()
    detailMap = null
  }
})
</script>

<style scoped>
.route-page {
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

<template>
  <div class="log-page">
    <el-card>
      <template #header>
        <span>操作日志</span>
      </template>

      <el-form :inline="true" :model="queryForm" class="search-form">
        <el-form-item label="操作人">
          <el-input v-model="queryForm.username" placeholder="请输入" clearable />
        </el-form-item>
        <el-form-item label="开始时间">
          <el-date-picker v-model="queryForm.startTime" type="datetime" placeholder="选择日期" value-format="YYYY-MM-DD HH:mm:ss" />
        </el-form-item>
        <el-form-item label="结束时间">
          <el-date-picker v-model="queryForm.endTime" type="datetime" placeholder="选择日期" value-format="YYYY-MM-DD HH:mm:ss" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="loadData">查询</el-button>
          <el-button @click="resetQuery">重置</el-button>
        </el-form-item>
      </el-form>

      <el-table :data="tableData" border style="width: 100%" v-loading="loading">
        <el-table-column prop="username" label="操作人" width="150" />
        <el-table-column prop="operation" label="操作内容" />
        <el-table-column prop="ip" label="操作IP" width="150" />
        <el-table-column prop="createTime" label="操作时间" width="180" />
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
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import request from '@/utils/request'

const loading = ref(false)
const tableData = ref([])
const total = ref(0)

const queryForm = reactive({
  pageNum: 1,
  pageSize: 10,
  username: '',
  startTime: '',
  endTime: ''
})

const loadData = async () => {
  loading.value = true
  try {
    const res = await request.get('/log/page', { params: queryForm })
    tableData.value = res.data.records
    total.value = res.data.total
  } catch (error) {
  } finally {
    loading.value = false
  }
}

const resetQuery = () => {
  queryForm.pageNum = 1
  queryForm.username = ''
  queryForm.startTime = ''
  queryForm.endTime = ''
  loadData()
}

onMounted(() => {
  loadData()
})
</script>

<style scoped>
.log-page {
  padding: 0;
}

.search-form {
  margin-bottom: 20px;
}
</style>

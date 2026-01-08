<template>
  <div class="asset-container">
    <el-card shadow="never">
      <template #header>
        <div class="card-header">
          <span class="title">资产列表</span>
          <el-button type="primary" icon="Plus">新增资产</el-button>
        </div>
      </template>

      <el-table :data="tableData" v-loading="loading" stripe style="width: 100%">
        <el-table-column prop="assetName" label="资产名称" min-width="150" />
        <el-table-column prop="assetSn" label="序列号" width="150" />
        <el-table-column prop="category" label="分类" width="120" />
        <el-table-column label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 0 ? 'success' : 'warning'">
              {{ row.status === 0 ? '闲置' : '领用中' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="userNickname" label="当前领用人" width="120">
          <template #default="{ row }">
            <span>{{ row.userNickname || '-' }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="location" label="存放地点" />
        <el-table-column label="操作" width="150" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" v-if="row.status === 0">领用</el-button>
            <el-button link type="danger" v-else>退库</el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination-container">
        <el-pagination
            v-model:current-page="currentPage"
            v-model:page-size="pageSize"
            :total="total"
            :page-sizes="[10, 20, 50]"
            layout="total, sizes, prev, pager, next"
            @size-change="fetchData"
            @current-change="fetchData"
        />
      </div>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import axios from 'axios'
import { ElMessage } from 'element-plus'

// --- 类型定义 ---
interface AssetVO {
  id: number
  assetName: string
  assetSn: string
  category: string
  status: number
  location: string
  userNickname: string | null
  createTime: string
}

interface PageResult<T> {
  records: T[]
  total: number
  size: number
  current: number
}

interface Result<T> {
  code: number
  message: string
  data: T
}

// --- 状态变量 ---
const loading = ref(false)
const tableData = ref<AssetVO[]>([])
const total = ref(0)
const currentPage = ref(1)
const pageSize = ref(10)

// --- 获取数据 ---
const fetchData = async () => {
  loading.value = true
  try {
    const token = localStorage.getItem('token')
    const res = await axios.get<Result<PageResult<AssetVO>>>('/api/assets/list', {
      params: {
        current: currentPage.value,
        size: pageSize.value
      },
      headers: { Authorization: `Bearer ${token}` }
    })

    if (res.data.code === 200) {
      tableData.value = res.data.data.records
      total.value = res.data.data.total
    } else {
      ElMessage.error(res.data.message)
    }
  } catch (error) {
    ElMessage.error('获取资产列表失败，请检查登录状态')
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  fetchData()
})
</script>

<style scoped>
.asset-container {
  padding: 20px;
}
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.title {
  font-size: 18px;
  font-weight: bold;
}
.pagination-container {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}
</style>
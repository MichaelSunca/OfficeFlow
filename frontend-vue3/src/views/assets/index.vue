<template>
  <div class="asset-container">
    <el-card shadow="never">
      <template #header>
        <div class="card-header">
          <div class="left">
            <span class="title">资产台账</span>
            <el-input
                v-model="searchQuery.assetName"
                placeholder="搜索资产名称"
                style="width: 200px; margin-left: 20px"
                clearable
                @clear="fetchData"
                @keyup.enter="fetchData"
            />
            <el-select
                v-model="searchQuery.status"
                placeholder="状态"
                clearable
                style="width: 120px; margin-left: 10px"
                @change="fetchData"
            >
              <el-option label="闲置" :value="0" />
              <el-option label="领用中" :value="1" />
              <el-option label="维修" :value="2" />
            </el-select>
          </div>
          <el-button type="primary" :icon="Plus" @click="handleAdd">新增资产</el-button>
        </div>
      </template>

      <el-table :data="tableData" v-loading="loading" stripe style="width: 100%">
        <el-table-column prop="assetName" label="资产名称" min-width="150" show-overflow-tooltip />
        <el-table-column prop="assetSn" label="序列号" width="150" />
        <el-table-column prop="category" label="分类" width="120" />

        <el-table-column label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="statusMap[row.status]?.type">
              {{ statusMap[row.status]?.text }}
            </el-tag>
          </template>
        </el-table-column>

        <el-table-column prop="userNickname" label="当前领用人" width="120">
          <template #default="{ row }">
            <span :class="{ 'text-gray': !row.userNickname }">
              {{ row.userNickname || '暂无' }}
            </span>
          </template>
        </el-table-column>

        <el-table-column prop="location" label="存放地点" min-width="150" />

        <el-table-column label="操作" width="250" fixed="right">
          <template #default="{ row }">
            <el-button
                link
                type="primary"
                v-if="row.status === 0"
                @click="handleTransfer(row, 'CLAIM')"
            >
              领用
            </el-button>

            <el-button
                link
                type="warning"
                v-else-if="row.status === 1"
                @click="handleTransfer(row, 'RETURN')"
            >
              退库
            </el-button>

            <el-button link type="success" @click="timelineRef.open(row.id)">
              轨迹
            </el-button>

            <el-button link type="primary" @click="handleEdit(row)">编辑</el-button>

            <el-button link type="danger" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination-container">
        <el-pagination
            v-model:current-page="currentPage"
            v-model:page-size="pageSize"
            :total="total"
            :page-sizes="[10, 20, 50, 100]"
            layout="total, sizes, prev, pager, next, jumper"
            @size-change="handleSizeChange"
            @current-change="handleCurrentChange"
        />
      </div>
    </el-card>

    <AssetDialog ref="assetDialogRef" @refresh="fetchData" />
    <AssetTimeline ref="timelineRef" />
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { Plus } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from "element-plus"
import AssetDialog from './components/AssetDialog.vue'
import AssetTimeline from './components/AssetTimeline.vue'
import {
  getAssetListApi,
  claimAssetApi,
  returnAssetApi,
  deleteAssetApi,
  type AssetVO
} from "@/api/asset"

// --- 状态变量 ---
const loading = ref(false)
const tableData = ref<AssetVO[]>([])
const total = ref(0)
const currentPage = ref(1)
const pageSize = ref(10)
const assetDialogRef = ref()
const timelineRef = ref()

// 搜索查询参数
const searchQuery = reactive({
  assetName: '',
  status: null as number | null
})

const statusMap: Record<number, { text: string, type: 'success' | 'warning' | 'danger' | 'info' }> = {
  0: { text: '闲置', type: 'success' },
  1: { text: '领用中', type: 'warning' },
  2: { text: '维修', type: 'danger' },
  3: { text: '报废', type: 'info' }
}

// --- 核心业务逻辑 ---

const fetchData = async () => {
  loading.value = true
  try {
    const res = await getAssetListApi({
      current: currentPage.value,
      size: pageSize.value,
      assetName: searchQuery.assetName,
      status: searchQuery.status
    })
    tableData.value = res.records
    total.value = res.total
  } catch (error) {
    console.error('获取资产列表失败:', error)
  } finally {
    loading.value = false
  }
}

// 处理资产转移（领用/退库）
const handleTransfer = async (row: AssetVO, type: 'CLAIM' | 'RETURN') => {
  const isClaim = type === 'CLAIM'
  const actionText = isClaim ? '领用' : '退库'
  const apiFunc = isClaim ? claimAssetApi : returnAssetApi

  try {
    const { value: remark } = await ElMessageBox.prompt(
        `请输入${actionText}资产 [${row.assetName}] 的备注信息`,
        `${actionText}确认`,
        {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          inputPlaceholder: '备注信息（如：领用地点、用途等）',
        }
    )

    await apiFunc({
      assetId: row.id,
      remark: remark || ''
    })

    ElMessage.success(`${actionText}成功`)
    fetchData()
  } catch (error) {
    // 用户取消操作
  }
}

const handleDelete = (row: AssetVO) => {
  ElMessageBox.confirm(
      `确定要删除资产 [${row.assetName}] 吗？删除后可在轨迹中追溯。`,
      '安全警告',
      {
        confirmButtonText: '确定删除',
        cancelButtonText: '取消',
        type: 'warning',
      }
  ).then(async () => {
    await deleteAssetApi(row.id)
    ElMessage.success('资产已安全删除')
    fetchData()
  }).catch(() => {})
}

// --- 交互回调 ---

const handleSizeChange = (val: number) => {
  pageSize.value = val
  fetchData()
}

const handleCurrentChange = (val: number) => {
  currentPage.value = val
  fetchData()
}

const handleAdd = () => {
  assetDialogRef.value?.open()
}

const handleEdit = (row: AssetVO) => {
  assetDialogRef.value?.open(row)
}

onMounted(() => {
  fetchData()
})
</script>

<style scoped>
.asset-container {
  padding: 0;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.card-header .left {
  display: flex;
  align-items: center;
}

.title {
  font-size: 16px;
  font-weight: 600;
  color: #303133;
}

.pagination-container {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}

.text-gray {
  color: #909399;
  font-style: italic;
}

/* 深度选择器修改表头背景色 */
:deep(.el-table th) {
  background-color: #f5f7fa !important;
  color: #606266;
}
</style>
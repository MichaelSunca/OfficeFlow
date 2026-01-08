<template>
  <div class="asset-container">
    <el-card shadow="never">
      <template #header>
        <div class="card-header">
          <span class="title">资产台账</span>
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

        <el-table-column label="操作" width="180" fixed="right">
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
                v-else
                @click="handleTransfer(row, 'RETURN')"
            >
              退库
            </el-button>

            <el-button link type="primary" @click="handleEdit(row)">编辑</el-button>
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
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { Plus } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from "element-plus"
// 💡 导入封装好的 API 和类型
import {
  getAssetListApi,
  claimAssetApi,
  returnAssetApi,
  type AssetVO
} from "@/api/asset" // 省略 .ts 后缀通常更规范

// --- 1. 状态变量 ---
const loading = ref(false)
const tableData = ref<AssetVO[]>([])
const total = ref(0)
const currentPage = ref(1)
const pageSize = ref(10)

// 状态映射表：用于标签渲染 [0:闲置, 1:领用中, 2:维修]
const statusMap: Record<number, { text: string, type: 'success' | 'warning' | 'danger' | 'info' }> = {
  0: { text: '闲置', type: 'success' },
  1: { text: '领用中', type: 'warning' },
  2: { text: '维修', type: 'danger' }
}

// --- 2. 核心业务逻辑 ---

// 获取分页列表
const fetchData = async () => {
  loading.value = true
  try {
    const res = await getAssetListApi({
      current: currentPage.value,
      size: pageSize.value
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
    // 调出带输入框的弹窗，强制要求或提示输入备注
    const { value: remark } = await ElMessageBox.prompt(
        `请输入${actionText}资产 [${row.assetName}] 的备注信息`,
        `${actionText}确认`,
        {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          inputPlaceholder: '备注信息（如：领用地点、当前状态等）',
        }
    )

    // 发送请求
    await apiFunc({
      assetId: row.id,
      remark: remark || ''
    })

    ElMessage.success(`${actionText}成功`)
    fetchData() // 刷新列表数据
  } catch (error) {
    // Catch 块捕获的是用户点击“取消”或关闭弹窗，无需额外提示
  }
}

// --- 3. 交互与分页回调 ---

const handleSizeChange = (val: number) => {
  pageSize.value = val
  fetchData()
}

const handleCurrentChange = (val: number) => {
  currentPage.value = val
  fetchData()
}

// 新增资产
const handleAdd = () => {
  console.log('点击了新增资产')
  // 下一步：实现新增弹窗
}

// 编辑资产
const handleEdit = (row: AssetVO) => {
  console.log('正在编辑资产:', row.assetName)
  // 下一步：实现编辑回显逻辑
}

onMounted(() => {
  fetchData()
})
</script>

<style scoped>
.asset-container {
  /* 已经在 Layout 内部，此处主要控制内间距 */
  padding: 0;
}

.card-header {
  display: flex;
  justify-content: space-between;
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

/* 深度选择器，微调表格表头样式 */
:deep(.el-table th) {
  background-color: #f5f7fa !important;
  color: #606266;
}
</style>
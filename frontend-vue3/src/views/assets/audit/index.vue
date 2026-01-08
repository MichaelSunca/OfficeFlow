<template>
  <div class="audit-container">
    <el-card shadow="never">
      <template #header>
        <div class="card-header">
          <span class="title">领用审批管理</span>
          <div class="header-tips">
            <el-tag type="info" effect="plain">此处仅显示待处理的申请</el-tag>
          </div>
        </div>
      </template>

      <el-table :data="tableData" v-loading="loading" stripe style="width: 100%">
        <el-table-column prop="assetName" label="申请资产" min-width="150" show-overflow-tooltip />
        <el-table-column prop="assetSn" label="序列号" width="150" />

        <el-table-column prop="userNickname" label="申请人" width="120">
          <template #default="{ row }">
            <el-tag size="small" effect="light">{{ row.userNickname }}</el-tag>
          </template>
        </el-table-column>

        <el-table-column prop="createTime" label="申请时间" width="180" />

        <el-table-column prop="remark" label="申请备注" min-width="200" show-overflow-tooltip />

        <el-table-column label="操作" width="180" fixed="right">
          <template #default="{ row }">
            <el-button
                link
                type="success"
                @click="handleAudit(row, 1)"
            >
              通过
            </el-button>

            <el-button
                link
                type="danger"
                @click="handleAudit(row, 2)"
            >
              驳回
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination-container">
        <el-pagination
            v-model:current-page="currentPage"
            v-model:page-size="pageSize"
            :total="total"
            layout="total, prev, pager, next"
            @current-change="handleCurrentChange"
        />
      </div>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
// 💡 请确保你的 api/asset.ts 中定义了这两个方法
import { getPendingAuditListApi, auditClaimApi } from "@/api/asset.ts"

// --- 响应式变量 ---
const loading = ref(false)
const tableData = ref([])
const total = ref(0)
const currentPage = ref(1)
const pageSize = ref(10)

// --- 核心逻辑 ---

// 获取待审批列表
const fetchData = async () => {
  loading.value = true
  try {
    const res = await getPendingAuditListApi({
      current: currentPage.value,
      size: pageSize.value
    })
    tableData.value = res.records
    total.value = res.total
  } catch (error) {
    console.error('获取审批列表失败:', error)
  } finally {
    loading.value = false
  }
}

// 处理审批操作
const handleAudit = (row: any, result: number) => {
  const isPass = result === 1
  const actionText = isPass ? '通过' : '驳回'
  const confirmType = isPass ? 'success' : 'warning'

  ElMessageBox.prompt(
      `请输入对 [${row.assetName}] 的审批意见`,
      `${actionText}申请`,
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: confirmType,
        inputPlaceholder: isPass ? '可选，输入审批意见' : '必须输入驳回理由',
        inputValidator: (value) => {
          if (!isPass && !value) {
            return '驳回时必须填写原因'
          }
          return true
        }
      }
  ).then(async ({ value }) => {
    try {
      await auditClaimApi({
        recordId: row.id,
        auditResult: result,
        auditRemark: value || (isPass ? '同意领用' : '')
      })
      ElMessage.success(`已${actionText}`)
      fetchData() // 刷新列表
    } catch (error) {
      // 错误已由拦截器处理
    }
  }).catch(() => {
    // 用户取消操作
  })
}

const handleCurrentChange = (val: number) => {
  currentPage.value = val
  fetchData()
}

onMounted(() => {
  fetchData()
})
</script>

<style scoped>
.audit-container {
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

/* 深度选择器修改表头背景色，保持与台账一致 */
:deep(.el-table th) {
  background-color: #f5f7fa !important;
  color: #606266;
}
</style>
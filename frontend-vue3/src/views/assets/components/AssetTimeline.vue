<template>
  <el-dialog v-model="visible" title="资产流转历史轨迹" width="600px" destroy-on-close>
    <div v-loading="loading" class="timeline-wrapper">
      <el-empty v-if="records.length === 0" description="暂无流转记录" />

      <el-timeline v-else>
        <el-timeline-item
            v-for="(item, index) in records"
            :key="index"
            :type="getTimelineItemType(item.actionType)"
            :timestamp="formatDate(item.createTime)"
            placement="top"
        >
          <el-card shadow="never" class="record-card">
            <div class="card-header-row">
              <span class="action-name">{{ getActionName(item.actionType) }}</span>
              <el-tag size="small" effect="light" type="info">
                操作人：{{ item.userNickname || '系统管理员' }}
              </el-tag>
            </div>

            <div v-if="item.remark" class="remark-box">
              <el-icon><ChatDotRound /></el-icon>
              <span class="remark-text">{{ item.remark }}</span>
            </div>

            <div class="status-track">
              <div class="status-node">
                <span class="label">原状态</span>
                <el-tag size="small" type="info" border>{{ getStatusLabel(item.oldStatus) }}</el-tag>
              </div>

              <el-icon class="arrow-icon"><Right /></el-icon>

              <div class="status-node">
                <span class="label">新状态</span>
                <el-tag size="small" :type="getStatusTag(item.newStatus)" border>
                  {{ getStatusLabel(item.newStatus) }}
                </el-tag>
              </div>
            </div>
          </el-card>
        </el-timeline-item>
      </el-timeline>
    </div>
  </el-dialog>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { getAssetRecordsApi } from '@/api/asset'
import { Right, ChatDotRound } from '@element-plus/icons-vue'
import dayjs from 'dayjs' // 建议使用 dayjs 处理日期

const visible = ref(false)
const loading = ref(false)
const records = ref<any[]>([])

// 暴露给父组件的打开方法
const open = async (assetId: number) => {
  visible.value = true
  loading.value = true
  try {
    const res = await getAssetRecordsApi(assetId)
    records.value = res || []
  } catch (error) {
    console.error('获取履历失败', error)
  } finally {
    loading.value = false
  }
}

// 格式化日期
const formatDate = (date: string) => {
  return date ? dayjs(date).format('YYYY-MM-DD HH:mm:ss') : ''
}

// 动作映射
const getActionName = (type: string) => {
  const map: Record<string, string> = {
    CLAIM: '资产领用',
    RETURN: '资产退库',
    REPAIR: '报修登记',
    ADD: '入库登记',
    DELETE: '资产下架'
  }
  return map[type] || type
}

// 状态文本映射
const getStatusLabel = (status: any) => {
  if (status === null || status === undefined) return '无'
  const s = Number(status)
  const map: Record<number, string> = {
    0: '闲置',
    1: '领用中',
    2: '维修中',
    3: '已报废'
  }
  return map[s] || `未知(${s})`
}

// 状态样式映射
const getStatusTag = (status: any) => {
  const s = Number(status)
  const map: any = { 0: 'success', 1: 'warning', 2: 'danger', 3: 'info' }
  return map[s] || ''
}

// 时间线节点颜色
const getTimelineItemType = (type: string) => {
  if (type === 'CLAIM') return 'primary'
  if (type === 'RETURN') return 'success'
  if (type === 'DELETE') return 'danger'
  return 'info'
}

defineExpose({ open })
</script>

<style scoped>
.timeline-wrapper {
  max-height: 500px;
  overflow-y: auto;
  padding: 10px 15px;
}

.record-card {
  border-radius: 8px;
  background-color: #fcfcfc;
}

.card-header-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}

.action-name {
  font-weight: 600;
  font-size: 15px;
  color: #303133;
}

.remark-box {
  background: #f4f4f5;
  padding: 8px 12px;
  border-radius: 4px;
  margin-bottom: 12px;
  display: flex;
  align-items: flex-start;
  gap: 6px;
  color: #606266;
  font-size: 13px;
}

.status-track {
  display: flex;
  align-items: center;
  gap: 15px;
  padding-top: 8px;
  border-top: 1px dashed #ebeef5;
}

.status-node {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.status-node .label {
  font-size: 11px;
  color: #909399;
}

.arrow-icon {
  margin-top: 15px;
  color: #dcdfe6;
}
</style>
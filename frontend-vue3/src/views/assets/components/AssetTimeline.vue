<template>
  <el-dialog v-model="visible" title="资产流转时间轴" width="500px" destroy-on-close>
    <div v-loading="loading" style="padding: 10px 20px">
      <el-empty v-if="records.length === 0" description="暂无流转记录" />

      <el-timeline v-else>
        <el-timeline-item
            v-for="(item, index) in records"
            :key="index"
            :type="getTimelineItemType(item.actionType)"
            :hollow="true"
            :timestamp="item.createTime"
            placement="top"
        >
          <el-card shadow="never" class="record-card">
            <h4>{{ getActionName(item.actionType) }}</h4>
            <p v-if="item.remark" class="remark-text">
              <strong>备注：</strong>{{ item.remark }}
            </p>
            <div class="status-change">
              <el-tag size="small" type="info">{{ getStatusLabel(item.oldStatus) }}</el-tag>
              <el-icon class="arrow-icon"><Right /></el-icon>
              <el-tag size="small" :type="getStatusTag(item.newStatus)">
                {{ getStatusLabel(item.newStatus) }}
              </el-tag>
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
import { Right } from '@element-plus/icons-vue'

const visible = ref(false)
const loading = ref(false)
const records = ref<any[]>([])

// 打开弹窗并加载数据
const open = async (assetId: number) => {
  visible.value = true
  loading.value = true
  try {
    const res = await getAssetRecordsApi(assetId)
    records.value = res || []
  } finally {
    loading.value = false
  }
}

// 格式化动作名称
const getActionName = (type: string) => {
  const map: any = {
    CLAIM: '资产领用',
    RETURN: '资产退库',
    REPAIR: '开始维修',
    ADD: '初始入库',
    DELETE: '逻辑删除'
  }
  return map[type] || type
}

// 时间线节点颜色
const getTimelineItemType = (type: string) => {
  if (type === 'CLAIM') return 'primary'
  if (type === 'RETURN') return 'success'
  if (type === 'REPAIR') return 'warning'
  return 'info'
}

// 状态标签文字
const getStatusLabel = (status: number) => {
  if (status === 0) return '闲置'
  if (status === 1) return '领用中'
  if (status === 2) return '维修中'
  return '未知'
}

// 状态标签颜色
const getStatusTag = (status: number) => {
  const map: any = { 0: 'info', 1: 'success', 2: 'danger' }
  return map[status] || ''
}

defineExpose({ open })
</script>

<style scoped>
.record-card h4 {
  margin: 0 0 8px 0;
  font-size: 14px;
}
.remark-text {
  font-size: 13px;
  color: #666;
  margin-bottom: 8px;
}
.status-change {
  display: flex;
  align-items: center;
  gap: 8px;
}
.arrow-icon {
  font-size: 12px;
  color: #999;
}
</style>
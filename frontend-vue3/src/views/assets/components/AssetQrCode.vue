<template>
  <el-dialog v-model="visible" title="资产标签预览" width="450px" align-center>
    <div class="print-preview-box">
      <div id="qrCodeTag" class="asset-tag">
        <div class="tag-body">
          <div class="qr-code">
            <qrcode-vue :value="qrValue" :size="95" level="M" render-as="canvas" />
          </div>
          <div class="tag-details">
            <div class="company-name">OFFICE FLOW</div>
            <div class="detail-item title">{{ assetInfo.assetName }}</div>
            <div class="detail-item sn">SN: {{ assetInfo.assetSn }}</div>
            <div class="detail-item cat">分类: {{ assetInfo.category }}</div>
          </div>
        </div>
      </div>
    </div>

    <template #footer>
      <el-button @click="visible = false">取消</el-button>
      <el-button type="primary" @click="handlePrint">立即打印</el-button>
    </template>
  </el-dialog>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import QrcodeVue from 'qrcode.vue'

const visible = ref(false)
const assetInfo = ref<any>({})
const qrValue = ref('')

const open = (row: any) => {
  assetInfo.value = row
  qrValue.value = `ASSET:${row.assetSn}`
  visible.value = true
}

const handlePrint = () => {
  // 针对专业热敏打印机的调用
  window.print()
}

defineExpose({ open })
</script>

<style scoped>
/* 弹窗内的预览样式 */
.print-preview-box {
  display: flex;
  justify-content: center;
  background: #f0f2f5;
  padding: 20px;
}

/* 核心标签样式：50mm * 30mm */
.asset-tag {
  width: 50mm;
  height: 30mm;
  padding: 2mm;
  background: #fff;
  border: 1px dashed #dcdfe6; /* 预览时显示虚线，打印时隐藏 */
  box-sizing: border-box;
  display: flex;
  flex-direction: column;
}

.tag-body {
  display: flex;
  height: 100%;
  align-items: center;
}

.qr-code {
  flex-shrink: 0;
  margin-right: 2mm;
}

.tag-details {
  flex: 1;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  height: 100%;
  overflow: hidden;
}

.company-name {
  font-size: 8pt;
  font-weight: bold;
  border-bottom: 0.5pt solid #000;
  margin-bottom: 2pt;
  white-space: nowrap;
}

.detail-item {
  font-size: 7pt;
  line-height: 1.2;
  color: #000;
  word-break: break-all;
  display: -webkit-box;
  -webkit-line-clamp: 1;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.detail-item.title {
  font-weight: bold;
  font-size: 8pt;
  -webkit-line-clamp: 2; /* 资产名称允许两行 */
}

/* 打印专用 CSS 优化 */
@media print {
  /* 隐藏非打印内容 */
  body * {
    visibility: hidden;
  }

  /* 强制设定打印纸张大小 */
  @page {
    size: 50mm 30mm;
    margin: 0; /* 彻底移除页眉页脚 */
  }

  #qrCodeTag, #qrCodeTag * {
    visibility: visible;
  }

  #qrCodeTag {
    position: fixed;
    left: 0;
    top: 0;
    border: none; /* 打印时关闭边框 */
    width: 50mm;
    height: 30mm;
    margin: 0;
    padding: 2mm;
  }
}
</style>
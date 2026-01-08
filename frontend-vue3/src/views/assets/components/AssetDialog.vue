<template>
  <el-dialog
      v-model="visible"
      :title="form.id ? '编辑资产' : '新增资产'"
      width="550px"
      @closed="handleClosed"
      destroy-on-close
  >
    <el-form
        ref="formRef"
        :model="form"
        :rules="rules"
        label-width="100px"
        label-position="right"
        style="padding-right: 20px"
    >
      <el-form-item label="资产名称" prop="assetName">
        <el-input v-model="form.assetName" placeholder="例如：MacBook Pro 16寸" />
      </el-form-item>

      <el-form-item label="序列号/SN" prop="assetSn">
        <el-input v-model="form.assetSn" placeholder="机身唯一序列号" />
      </el-form-item>

      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="分类" prop="category">
            <el-select v-model="form.category" placeholder="请选择" style="width: 100%">
              <el-option label="IT设备" value="Electronics" />
              <el-option label="办公家具" value="Furniture" />
              <el-option label="行政用品" value="General" />
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="价格" prop="price">
            <el-input-number
                v-model="form.price"
                :precision="2"
                :step="100"
                :min="0"
                controls-position="right"
                style="width: 100%"
            />
          </el-form-item>
        </el-col>
      </el-row>

      <el-form-item label="购买日期" prop="purchaseDate">
        <el-date-picker
            v-model="form.purchaseDate"
            type="date"
            placeholder="选择日期"
            value-format="YYYY-MM-DD"
            style="width: 100%"
        />
      </el-form-item>

      <el-form-item label="存放地点" prop="location">
        <el-input v-model="form.location" placeholder="例如：A座302或行政前台" />
      </el-form-item>
    </el-form>

    <template #footer>
      <span class="dialog-footer">
        <el-button @click="visible = false">取消</el-button>
        <el-button type="primary" :loading="submitting" @click="handleSubmit">
          确认
        </el-button>
      </span>
    </template>
  </el-dialog>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import type { FormInstance, FormRules } from 'element-plus'
import { ElMessage } from 'element-plus'
import { addAssetApi, updateAssetApi, type AssetFormDTO, type AssetVO } from '@/api/asset'

// --- 定义事件与状态 ---
const emit = defineEmits(['refresh'])
const visible = ref(false)
const submitting = ref(false)
const formRef = ref<FormInstance>()

// 表单初始结构
const initialForm: AssetFormDTO = {
  id: undefined,
  assetName: '',
  assetSn: '',
  category: '',
  price: 0,
  location: '',
  purchaseDate: ''
}

const form = reactive<AssetFormDTO>({ ...initialForm })

// --- 校验规则 ---
const rules = reactive<FormRules>({
  assetName: [{ required: true, message: '请输入资产名称', trigger: 'blur' }],
  assetSn: [
    { required: true, message: '请输入唯一序列号', trigger: 'blur' },
    { pattern: /^[A-Za-z0-9-]+$/, message: '仅支持字母、数字和中划线', trigger: 'blur' }
  ],
  category: [{ required: true, message: '请选择分类', trigger: 'change' }],
  price: [{ required: true, message: '请输入价格', trigger: 'blur' }]
})

/**
 * 打开弹窗
 * @param row 如果传入 row 则为编辑模式，不传则为新增模式
 */
const open = (row?: AssetVO) => {
  visible.value = true
  if (row) {
    // 编辑：使用 Object.assign 进行浅拷贝，避免直接修改父组件引用的对象
    // 确保字段一一对应
    Object.assign(form, {
      id: row.id,
      assetName: row.assetName,
      assetSn: row.assetSn,
      category: row.category,
      price: row.price,
      location: row.location,
      purchaseDate: row.purchaseDate
    })
  } else {
    // 新增：重置为初始状态
    Object.assign(form, initialForm)
  }
}

/**
 * 提交表单
 */
const handleSubmit = async () => {
  if (!formRef.value) return

  try {
    // 前端预校验
    await formRef.value.validate()
    submitting.value = true

    // 💡 逻辑：判断 id 是否存在决定调用哪一个 POST 接口
    if (form.id) {
      await updateAssetApi(form)
      ElMessage.success('资产更新成功')
    } else {
      await addAssetApi(form)
      ElMessage.success('资产录入成功')
    }

    visible.value = false
    emit('refresh') // 触发父组件 fetchData 刷新列表
  } catch (error) {
    console.error('保存资产失败:', error)
  } finally {
    submitting.value = false
  }
}

/**
 * 弹窗关闭后的清理逻辑
 */
const handleClosed = () => {
  if (!formRef.value) return
  formRef.value.resetFields() // 清除校验残余红字
}

// 必须暴露 open 方法给父组件通过 ref 调用
defineExpose({ open })
</script>

<style scoped>
.dialog-footer {
  padding-top: 10px;
}
</style>
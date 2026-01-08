<template>
  <div class="search-container">
    <el-card>
      <div style="margin-bottom: 20px; display: flex; gap: 10px">
        <el-input v-model="searchId" placeholder="输入用户ID" style="width: 200px" />
        <el-button type="primary" @click="handleSearch" :loading="loading">搜索</el-button>
        <el-button @click="reset">重置</el-button>
      </div>

      <el-table :data="tableData" border style="width: 100%" v-loading="loading">
        <el-table-column prop="id" label="ID" width="100" />
        <el-table-column prop="username" label="用户名" />
        <el-table-column prop="nickname" label="昵称" />
        <el-table-column label="头像" width="100">
          <template #default="scope">
            <el-avatar :src="scope.row.avatar" />
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { getUserByIdApi, type UserVO } from '@/api/user'

const searchId = ref('')
const loading = ref(false)
const tableData = ref<UserVO[]>([]) // 表格数据数组

const handleSearch = async () => {
  if (!searchId.value) return
  loading.value = true
  try {
    const res = await getUserByIdApi(searchId.value)
    // 如果返回的是单个对象，转成数组给表格显示
    tableData.value = res ? [res] : []
  } finally {
    loading.value = false
  }
}

const reset = () => {
  searchId.value = ''
  tableData.value = []
}
</script>
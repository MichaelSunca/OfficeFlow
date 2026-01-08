<template>
  <div class="login-container">
    <el-card class="login-card">
      <template #header>
        <div class="login-header">
          <el-icon size="26" color="#409EFF"><Lock /></el-icon>
          <h2>OfficeFlow 登录</h2>
        </div>
      </template>

      <el-form :model="loginForm" :rules="rules" ref="loginRef" label-position="top">
        <el-form-item label="账号" prop="username">
          <el-input
              v-model="loginForm.username"
              placeholder="请输入管理员账号"
              :prefix-icon="User"
          />
        </el-form-item>

        <el-form-item label="密码" prop="password">
          <el-input
              v-model="loginForm.password"
              type="password"
              placeholder="请输入密码"
              :prefix-icon="Key"
              show-password
          />
        </el-form-item>

        <el-button
            type="primary"
            class="login-btn"
            @click="handleLogin"
            :loading="loading"
        >
          立即登录
        </el-button>
      </el-form>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { Lock, User, Key } from '@element-plus/icons-vue'
import { ref, reactive } from 'vue'
import { ElMessage, type FormInstance, type FormRules } from 'element-plus'
import { http } from '@/utils/request'
import { useRouter } from 'vue-router' // 引入路由跳转

const router = useRouter()

const loginRef = ref<FormInstance>() // 获得表单实例的 TS 类型
const loading = ref(false)

const loginForm = reactive({
  username: '',
  password: ''
})

// 定义校验规则的类型
const rules = reactive<FormRules>({
  username: [{ required: true, message: '请输入账号', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
})

// 登录逻辑
const handleLogin = async () => {
  if (!loginRef.value) return

  await loginRef.value.validate(async (valid) => {
    if (valid) {
      loading.value = true
      try {
        // 使用封装好的 http。注意：我们在 request.ts 拦截器里已经处理过 code === 200
        // 所以这里返回的 data 直接就是 token 字符串
        const res = await http.post<any>('/auth/login', loginForm)

        localStorage.setItem('token', res.token)
        ElMessage.success('登录成功！欢迎回来')

        // 关键：登录成功后跳转到搜索页
        router.push('/user-search')

      } catch (error) {
        // 拦截器已经报过 ElMessage.error 了，这里可以保持静默或处理 loading
      } finally {
        loading.value = false
      }
    }
  })
}
</script>

<style scoped>
.login-container {
  /* 确保占据整个视口高度和宽度 */
  height: 100vh;
  width: 100vw;
  display: flex;
  justify-content: center;
  align-items: center;
  /* 换一个明亮的背景色，确保没有被黑色覆盖 */
  background-color: #f0f2f5;
  /* 避免出现滚动条 */
  margin: 0;
  overflow: hidden;
}

.login-card {
  width: 450px; /* 稍微加宽一点更美观 */
  border-radius: 12px;
  background: #fff;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
}

.login-header {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 10px; /* 图标和文字的间距 */
}

.login-header h2 {
  margin: 0;
  font-size: 22px;
  color: #303133;
}

.login-btn {
  width: 100%;
  margin-top: 10px;
  padding: 12px 0; /* 让按钮厚实一点 */
}
</style>
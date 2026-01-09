<template>
  <el-container class="app-wrapper">
    <el-aside :width="isCollapse ? '64px' : '210px'" class="sidebar-container">
      <div class="logo-container">
        <el-icon size="24"><Platform /></el-icon>
        <span v-show="!isCollapse" class="logo-title">OfficeFlow EAM</span>
      </div>
      <el-scrollbar>
        <el-menu
            :default-active="$route.path"
            :collapse="isCollapse"
            background-color="#304156"
            text-color="#bfcbd9"
            active-text-color="#409EFF"
            unique-opened
            router
        >
          <el-menu-item index="/assets">
            <el-icon><Box /></el-icon>
            <span>资产台账</span>
          </el-menu-item>

          <el-menu-item v-if="userStore.userRole === 'ADMIN'" index="/audit">
            <el-icon><Checked /></el-icon>
            <span>领用审批</span>
          </el-menu-item>

          <el-menu-item v-if="userStore.userRole === 'ADMIN'" index="/user-search">
            <el-icon><Search /></el-icon>
            <span>职员查询</span>
          </el-menu-item>
        </el-menu>
      </el-scrollbar>
    </el-aside>

    <el-container class="main-container">
      <el-header class="fixed-header">
        <div class="header-left">
          <el-icon class="collapse-btn" @click="isCollapse = !isCollapse">
            <Expand v-if="isCollapse" />
            <Fold v-else />
          </el-icon>
        </div>
        <div class="header-right">
          <el-dropdown trigger="click">
            <div class="avatar-wrapper">
              <el-avatar :size="30" src="https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png" />
              <span class="user-name">{{ userStore.userRole === 'ADMIN' ? '管理员' : '普通用户' }}</span>
              <el-icon class="el-icon--right"><arrow-down /></el-icon>
            </div>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item>个人信息</el-dropdown-item>
                <el-dropdown-item divided @click="handleLogout">退出登录</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </el-header>

      <el-main class="app-main">
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/store/user' // 💡 引入 Pinia Store
import { ElMessageBox, ElMessage } from 'element-plus'
import {
  Platform, Box, Search, Expand, Fold, Checked, ArrowDown
} from '@element-plus/icons-vue'

const isCollapse = ref(false)
const router = useRouter()
const userStore = useUserStore() // 💡 初始化 Store

// 退出登录逻辑
const handleLogout = () => {
  ElMessageBox.confirm('确定要退出登录并返回登录页面吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    // 💡 核心操作：调用 Store 的 logout 清理 token 和 role
    userStore.logout()
    router.push('/login')
    ElMessage.success('已安全退出系统')
  }).catch(() => {
    // 点击取消则不做任何操作
  })
}
</script>

<style scoped>
.app-wrapper { height: 100vh; width: 100vw; display: flex; }
.sidebar-container { background-color: #304156; transition: width 0.3s; overflow: hidden; }
.logo-container {
  height: 50px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  background: #2b2f3a;
  overflow: hidden;
  white-space: nowrap;
}
.logo-title { margin-left: 10px; font-weight: bold; }
.fixed-header {
  height: 50px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  border-bottom: 1px solid #d8dce5;
  background: #fff;
  padding: 0 15px;
}
.collapse-btn { cursor: pointer; font-size: 20px; transition: color 0.3s; }
.collapse-btn:hover { color: #409EFF; }
.header-right { display: flex; align-items: center; }
.avatar-wrapper { display: flex; align-items: center; cursor: pointer; outline: none; }
.user-name { margin: 0 8px; font-size: 14px; color: #606266; }
.app-main { background-color: #f0f2f5; padding: 20px; flex: 1; overflow-y: auto; }

/* 菜单折叠动画修正 */
.el-menu--collapse span { visibility: hidden; }
</style>
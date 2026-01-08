<template>
  <el-container class="app-wrapper">
    <el-aside width="210px" class="sidebar-container">
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
          <el-menu-item index="/user-search">
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
              <span class="user-name">Admin</span>
            </div>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item @click="handleLogout">退出登录</el-dropdown-item>
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
import { Platform, Box, Search, Expand, Fold } from '@element-plus/icons-vue'

const isCollapse = ref(false)
const router = useRouter()

const handleLogout = () => {
  localStorage.removeItem('token')
  router.push('/login')
}
</script>

<style scoped>
.app-wrapper { height: 100vh; width: 100vw; }
.sidebar-container { background-color: #304156; transition: width 0.3s; }
.logo-container {
  height: 50px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  background: #2b2f3a;
}
.logo-title { margin-left: 10px; font-weight: bold; }
.fixed-header {
  height: 50px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  border-bottom: 1px solid #d8dce5;
}
.collapse-btn { cursor: pointer; font-size: 20px; }
.avatar-wrapper { display: flex; align-items: center; cursor: pointer; }
.user-name { margin-left: 8px; font-size: 14px; }
.app-main { background-color: #f0f2f5; padding: 20px; }
</style>
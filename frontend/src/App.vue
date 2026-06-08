<template>
  <el-container class="app-container">
    <el-aside width="220px" class="aside">
      <div class="logo">
        <el-icon size="32"><Document /></el-icon>
        <span class="title">学术分析系统</span>
      </div>
      <el-menu
        :default-active="activeMenu"
        router
        class="menu"
        background-color="#001529"
        text-color="#ffffffa6"
        active-text-color="#ffffff"
      >
        <el-menu-item index="/">
          <el-icon><DataAnalysis /></el-icon>
          <span>首页概览</span>
        </el-menu-item>
        <el-menu-item index="/papers">
          <el-icon><Collection /></el-icon>
          <span>论文列表</span>
        </el-menu-item>
        <el-menu-item index="/authors">
          <el-icon><User /></el-icon>
          <span>作者分析</span>
        </el-menu-item>
        <el-menu-item index="/institutions">
          <el-icon><OfficeBuilding /></el-icon>
          <span>机构分析</span>
        </el-menu-item>
        <el-menu-item index="/citations">
          <el-icon><Connection /></el-icon>
          <span>引文网络</span>
        </el-menu-item>
      </el-menu>
    </el-aside>
    <el-container>
      <el-header class="header">
        <span class="header-title">{{ pageTitle }}</span>
      </el-header>
      <el-main class="main">
        <router-view v-slot="{ Component }">
          <transition name="fade" mode="out-in">
            <component :is="Component" />
          </transition>
        </router-view>
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { useRoute } from 'vue-router'

const route = useRoute()

const activeMenu = computed(() => route.path)

const pageTitle = computed(() => {
  const titleMap: Record<string, string> = {
    '/': '首页概览',
    '/papers': '论文列表',
    '/paper': '论文详情',
    '/authors': '作者分析',
    '/institutions': '机构分析',
    '/citations': '引文网络'
  }
  for (const [path, title] of Object.entries(titleMap)) {
    if (route.path.startsWith(path)) {
      return title
    }
  }
  return '学术论文分析系统'
})
</script>

<style scoped>
.app-container {
  height: 100vh;
}

.aside {
  background-color: #001529;
  display: flex;
  flex-direction: column;
}

.logo {
  height: 64px;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 10px;
  color: #fff;
  background-color: #002140;
}

.logo .title {
  font-size: 18px;
  font-weight: bold;
}

.menu {
  flex: 1;
  border-right: none;
}

.header {
  background-color: #fff;
  border-bottom: 1px solid #e6e6e6;
  display: flex;
  align-items: center;
}

.header-title {
  font-size: 18px;
  font-weight: 600;
  color: #303133;
}

.main {
  background-color: #f5f7fa;
  padding: 20px;
}

.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.3s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}
</style>

<style>
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

html, body, #app {
  width: 100%;
  height: 100%;
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, 'Helvetica Neue', Arial, sans-serif;
}
</style>

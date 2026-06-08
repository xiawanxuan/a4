<template>
  <div class="paper-detail">
    <el-page-header @back="goBack" content="论文详情" class="page-header" />

    <el-card v-loading="paperStore.loading" class="detail-card" shadow="never">
      <template v-if="paperStore.currentPaper">
        <h2 class="paper-title">{{ paperStore.currentPaper.title }}</h2>

        <div class="paper-meta">
          <el-tag type="primary" effect="plain">
            {{ paperStore.currentPaper.publicationYear }}
          </el-tag>
          <span class="journal">{{ paperStore.currentPaper.journalName }}</span>
          <span class="citations">
            <el-icon><Star /></el-icon>
            被引 {{ paperStore.currentPaper.totalCitations }} 次
          </span>
        </div>

        <el-descriptions :column="2" border class="descriptions">
          <el-descriptions-item label="作者">
            <span v-for="(author, index) in paperStore.currentPaper.authors || []" :key="author.id">
              {{ author.name }}
              <span v-if="index < (paperStore.currentPaper.authors?.length || 0) - 1">, </span>
            </span>
          </el-descriptions-item>
          <el-descriptions-item label="机构">
            <span v-if="paperStore.currentPaper.institutions && paperStore.currentPaper.institutions.length > 0">
              {{ paperStore.currentPaper.institutions[0].name }}
            </span>
          </el-descriptions-item>
          <el-descriptions-item label="期刊">
            {{ paperStore.currentPaper.journalName || '-' }}
          </el-descriptions-item>
          <el-descriptions-item label="影响因子">
            {{ paperStore.currentPaper.impactFactor || '-' }}
          </el-descriptions-item>
          <el-descriptions-item label="卷/期">
            {{ paperStore.currentPaper.volume || '-' }} / {{ paperStore.currentPaper.issue || '-' }}
          </el-descriptions-item>
          <el-descriptions-item label="页码">
            {{ paperStore.currentPaper.pages || '-' }}
          </el-descriptions-item>
          <el-descriptions-item label="DOI" :span="2">
            {{ paperStore.currentPaper.doi || '-' }}
          </el-descriptions-item>
        </el-descriptions>

        <div class="section">
          <h3 class="section-title">摘要</h3>
          <p class="abstract">{{ paperStore.currentPaper.abstractText }}</p>
        </div>

        <div class="section">
          <h3 class="section-title">关键词</h3>
          <div class="keywords">
            <el-tag
              v-for="(keyword, index) in keywordsList"
              :key="index"
              type="info"
              effect="plain"
              class="keyword-tag"
            >
              {{ keyword }}
            </el-tag>
          </div>
        </div>

        <div class="section">
          <h3 class="section-title">统计信息</h3>
          <el-row :gutter="20">
            <el-col :span="8">
              <el-statistic title="被引次数" :value="paperStore.currentPaper.totalCitations || 0" />
            </el-col>
            <el-col :span="8">
              <el-statistic title="参考文献" :value="paperStore.currentPaper.totalReferences || 0" />
            </el-col>
            <el-col :span="8">
              <el-statistic title="作者数量" :value="paperStore.currentPaper.authors?.length || 0" />
            </el-col>
          </el-row>
        </div>
      </template>

      <template v-else>
        <el-empty description="暂无数据" />
      </template>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, onBeforeUnmount } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { usePaperStore } from '@/stores/paper'

const route = useRoute()
const router = useRouter()
const paperStore = usePaperStore()

const keywordsList = computed(() => {
  if (!paperStore.currentPaper?.keywords) return []
  return paperStore.currentPaper.keywords.split(',').map(k => k.trim()).filter(k => k)
})

function goBack() {
  router.back()
}

onMounted(() => {
  const id = route.params.id
  if (id) {
    paperStore.fetchPaperById(parseInt(id as string))
  }
})

onBeforeUnmount(() => {
  paperStore.clearCurrentPaper()
})
</script>

<style scoped>
.paper-detail {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.page-header {
  background: #fff;
  padding: 16px 20px;
  border-radius: 8px;
}

.detail-card {
  border-radius: 8px;
}

.paper-title {
  font-size: 24px;
  font-weight: 600;
  color: #303133;
  margin: 0 0 16px 0;
  line-height: 1.5;
}

.paper-meta {
  display: flex;
  align-items: center;
  gap: 16px;
  margin-bottom: 24px;
  flex-wrap: wrap;
}

.paper-meta .journal {
  color: #606266;
  font-size: 14px;
}

.paper-meta .citations {
  color: #e6a23c;
  font-size: 14px;
  display: flex;
  align-items: center;
  gap: 4px;
}

.descriptions {
  margin-top: 16px;
}

.section {
  margin-top: 24px;
}

.section-title {
  font-size: 16px;
  font-weight: 600;
  color: #303133;
  margin: 0 0 12px 0;
  padding-left: 8px;
  border-left: 3px solid #409eff;
}

.abstract {
  color: #606266;
  line-height: 1.8;
  text-align: justify;
  text-indent: 2em;
  margin: 0;
}

.keywords {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.keyword-tag {
  font-size: 13px;
}
</style>

<template>
  <div class="dashboard">
    <el-row :gutter="20">
      <el-col :span="6">
        <el-card shadow="never" class="stat-card paper-count">
          <div class="stat-content">
            <div class="stat-info">
              <div class="stat-number">{{ stats.paperCount }}</div>
              <div class="stat-label">论文总数</div>
            </div>
            <div class="stat-icon">
              <el-icon><Document /></el-icon>
            </div>
          </div>
          <div class="stat-footer">
            <span class="increase">+12%</span>
            <span class="compare">较上月</span>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="never" class="stat-card citation-count">
          <div class="stat-content">
            <div class="stat-info">
              <div class="stat-number">{{ stats.citationCount }}</div>
              <div class="stat-label">总被引次数</div>
            </div>
            <div class="stat-icon">
              <el-icon><Connection /></el-icon>
            </div>
          </div>
          <div class="stat-footer">
            <span class="increase">+8%</span>
            <span class="compare">较上月</span>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="never" class="stat-card author-count">
          <div class="stat-content">
            <div class="stat-info">
              <div class="stat-number">{{ stats.authorCount }}</div>
              <div class="stat-label">作者数量</div>
            </div>
            <div class="stat-icon">
              <el-icon><User /></el-icon>
            </div>
          </div>
          <div class="stat-footer">
            <span class="increase">+5%</span>
            <span class="compare">较上月</span>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="never" class="stat-card journal-count">
          <div class="stat-content">
            <div class="stat-info">
              <div class="stat-number">{{ stats.journalCount }}</div>
              <div class="stat-label">期刊数量</div>
            </div>
            <div class="stat-icon">
              <el-icon><Reading /></el-icon>
            </div>
          </div>
          <div class="stat-footer">
            <span class="increase">+3%</span>
            <span class="compare">较上月</span>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" style="margin-top: 20px">
      <el-col :span="12">
        <el-card shadow="never" class="chart-card">
          <template #header>
            <div class="card-header">
              <span>年度论文发表趋势</span>
              <el-radio-group v-model="trendType" size="small" @change="updateTrendChart">
                <el-radio-button value="paper">论文数</el-radio-button>
                <el-radio-button value="citation">被引量</el-radio-button>
              </el-radio-group>
            </div>
          </template>
          <div ref="trendChartRef" class="chart-container"></div>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card shadow="never" class="chart-card">
          <template #header>
            <span>研究领域分布</span>
          </template>
          <div ref="categoryChartRef" class="chart-container"></div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" style="margin-top: 20px">
      <el-col :span="8">
        <el-card shadow="never" class="rank-card">
          <template #header>
            <span>高被引论文 TOP10</span>
            <el-link type="primary" :underline="false" @click="goToCitations">查看更多</el-link>
          </template>
          <div class="rank-list">
            <div
              v-for="(paper, index) in topCitedPapers"
              :key="paper.id"
              class="rank-item"
            >
              <div class="rank-number" :class="'rank-' + (index + 1)">{{ index + 1 }}</div>
              <div class="rank-info">
                <div class="rank-title" :title="paper.title">{{ paper.title }}</div>
                <div class="rank-meta">
                  <span>{{ paper.publicationYear }}年</span>
                  <span class="citations">{{ paper.totalCitations }} 被引</span>
                </div>
              </div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card shadow="never" class="rank-card">
          <template #header>
            <span>高产作者 TOP10</span>
            <el-link type="primary" :underline="false" @click="goToAuthors">查看更多</el-link>
          </template>
          <div class="rank-list">
            <div
              v-for="(author, index) in topAuthors"
              :key="author.id"
              class="rank-item"
            >
              <div class="rank-number" :class="'rank-' + (index + 1)">{{ index + 1 }}</div>
              <div class="rank-info">
                <div class="rank-title">{{ author.name }}</div>
                <div class="rank-meta">
                  <span>{{ author.totalPublications }} 篇论文</span>
                  <span class="citations">{{ author.totalCitations }} 被引</span>
                </div>
              </div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card shadow="never" class="rank-card">
          <template #header>
            <span>热门期刊 TOP10</span>
            <el-link type="primary" :underline="false">查看更多</el-link>
          </template>
          <div class="rank-list">
            <div
              v-for="(journal, index) in topJournals"
              :key="journal.journalId"
              class="rank-item"
            >
              <div class="rank-number" :class="'rank-' + (index + 1)">{{ index + 1 }}</div>
              <div class="rank-info">
                <div class="rank-title">{{ journal.journalName }}</div>
                <div class="rank-meta">
                  <span class="citations">{{ journal.paperCount }} 篇</span>
                </div>
              </div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" style="margin-top: 20px">
      <el-col :span="24">
        <el-card shadow="never" class="chart-card">
          <template #header>
            <div class="card-header">
              <span>快速入口</span>
            </div>
          </template>
          <div class="quick-entry">
            <div class="entry-item" @click="goToPapers">
              <div class="entry-icon papers">
                <el-icon><Document /></el-icon>
              </div>
              <div class="entry-name">论文管理</div>
            </div>
            <div class="entry-item" @click="goToCitations">
              <div class="entry-icon citations">
                <el-icon><Connection /></el-icon>
              </div>
              <div class="entry-name">引文网络</div>
            </div>
            <div class="entry-item" @click="goToAuthors">
              <div class="entry-icon authors">
                <el-icon><User /></el-icon>
              </div>
              <div class="entry-name">作者分析</div>
            </div>
            <div class="entry-item" @click="goToInstitutions">
              <div class="entry-icon institutions">
                <el-icon><OfficeBuilding /></el-icon>
              </div>
              <div class="entry-name">机构分析</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, onBeforeUnmount } from 'vue'
import { useRouter } from 'vue-router'
import * as echarts from 'echarts'
import {
  Document,
  Connection,
  User,
  Reading,
  OfficeBuilding
} from '@element-plus/icons-vue'
import type { Paper, Author, DataOverview, PublicationTrend, ResearchArea, JournalDistribution } from '@/types'
import { getOverview, getTopCitedPapers, getCoreAuthors, getJournalDistribution, getPublicationTrend, getResearchAreas } from '@/api/analysis'

const router = useRouter()

const trendType = ref<'paper' | 'citation'>('paper')
const trendChartRef = ref<HTMLElement>()
const categoryChartRef = ref<HTMLElement>()

let trendChart: echarts.ECharts | null = null
let categoryChart: echarts.ECharts | null = null

const stats = reactive({
  paperCount: 0,
  citationCount: 0,
  authorCount: 0,
  journalCount: 0
})

const topCitedPapers = ref<Paper[]>([])
const topAuthors = ref<Author[]>([])
const topJournals = ref<JournalDistribution[]>([])
const publicationTrend = ref<PublicationTrend[]>([])
const researchAreas = ref<ResearchArea[]>([])

async function loadOverview() {
  try {
    const res = await getOverview()
    stats.paperCount = res.totalPapers
    stats.citationCount = res.totalCitations
    stats.authorCount = res.totalAuthors
    stats.journalCount = res.totalJournals
  } catch (error) {
    console.error('Failed to load overview:', error)
  }
}

async function loadTopCitedPapers() {
  try {
    const res = await getTopCitedPapers({ limit: 10 })
    topCitedPapers.value = res
  } catch (error) {
    console.error('Failed to load top cited papers:', error)
  }
}

async function loadCoreAuthors() {
  try {
    const res = await getCoreAuthors({ limit: 10 })
    topAuthors.value = res.coreAuthors
  } catch (error) {
    console.error('Failed to load core authors:', error)
  }
}

async function loadJournalDistribution() {
  try {
    const res = await getJournalDistribution({ limit: 10 })
    topJournals.value = res
  } catch (error) {
    console.error('Failed to load journal distribution:', error)
  }
}

async function loadPublicationTrend() {
  try {
    const res = await getPublicationTrend()
    publicationTrend.value = res
  } catch (error) {
    console.error('Failed to load publication trend:', error)
  }
}

async function loadResearchAreas() {
  try {
    const res = await getResearchAreas({ limit: 10 })
    researchAreas.value = res
  } catch (error) {
    console.error('Failed to load research areas:', error)
  }
}

function initTrendChart() {
  if (!trendChartRef.value) return
  trendChart = echarts.init(trendChartRef.value)
  updateTrendChart()
}

function updateTrendChart() {
  if (!trendChart) return

  const years = publicationTrend.value.map(item => String(item.year))
  const paperData = publicationTrend.value.map(item => item.count)
  const citationData = publicationTrend.value.map(item => item.count * 10)

  const option: echarts.EChartsOption = {
    tooltip: {
      trigger: 'axis',
      axisPointer: { type: 'cross' }
    },
    grid: {
      left: '3%',
      right: '4%',
      bottom: '3%',
      containLabel: true
    },
    xAxis: {
      type: 'category',
      boundaryGap: false,
      data: years,
      axisLabel: { fontSize: 12 }
    },
    yAxis: {
      type: 'value',
      axisLabel: { fontSize: 12 }
    },
    series: [{
      name: trendType.value === 'paper' ? '论文数' : '被引量',
      type: 'line',
      smooth: true,
      symbol: 'circle',
      symbolSize: 8,
      data: trendType.value === 'paper' ? paperData : citationData,
      lineStyle: {
        width: 3,
        color: trendType.value === 'paper' ? '#409eff' : '#67c23a'
      },
      itemStyle: {
        color: trendType.value === 'paper' ? '#409eff' : '#67c23a'
      },
      areaStyle: {
        color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
          { offset: 0, color: trendType.value === 'paper' ? 'rgba(64, 158, 255, 0.4)' : 'rgba(103, 194, 58, 0.4)' },
          { offset: 1, color: trendType.value === 'paper' ? 'rgba(64, 158, 255, 0.05)' : 'rgba(103, 194, 58, 0.05)' }
        ])
      }
    }]
  }

  trendChart.setOption(option)
}

function initCategoryChart() {
  if (!categoryChartRef.value) return
  categoryChart = echarts.init(categoryChartRef.value)

  const colors = ['#5470c6', '#91cc75', '#fac858', '#ee6666', '#73c0de', '#3ba272', '#fc8452', '#9a60b4']
  const data = researchAreas.value.map((item, index) => ({
    value: item.paperCount,
    name: item.keyword,
    itemStyle: { color: colors[index % colors.length] }
  }))

  const option: echarts.EChartsOption = {
    tooltip: {
      trigger: 'item',
      formatter: '{b}: {c} 篇 ({d}%)'
    },
    legend: {
      orient: 'vertical',
      right: 10,
      top: 'center',
      textStyle: { fontSize: 12 }
    },
    series: [{
      name: '研究领域',
      type: 'pie',
      radius: ['40%', '70%'],
      center: ['35%', '50%'],
      avoidLabelOverlap: false,
      itemStyle: {
        borderRadius: 8,
        borderColor: '#fff',
        borderWidth: 2
      },
      label: {
        show: false,
        position: 'center'
      },
      emphasis: {
        label: {
          show: true,
          fontSize: 16,
          fontWeight: 'bold'
        }
      },
      labelLine: {
        show: false
      },
      data
    }]
  }

  categoryChart.setOption(option)
}

function goToPapers() {
  router.push('/papers')
}

function goToCitations() {
  router.push('/citations')
}

function goToAuthors() {
  router.push('/authors')
}

function goToInstitutions() {
  router.push('/institutions')
}

function handleResize() {
  trendChart?.resize()
  categoryChart?.resize()
}

onMounted(async () => {
  await Promise.all([
    loadOverview(),
    loadTopCitedPapers(),
    loadCoreAuthors(),
    loadJournalDistribution(),
    loadPublicationTrend(),
    loadResearchAreas()
  ])
  initTrendChart()
  initCategoryChart()
  window.addEventListener('resize', handleResize)
})

onBeforeUnmount(() => {
  window.removeEventListener('resize', handleResize)
  trendChart?.dispose()
  categoryChart?.dispose()
})
</script>

<style scoped>
.dashboard {
  padding: 20px;
  background: #f5f7fa;
  min-height: 100%;
}

.stat-card {
  border-radius: 8px;
  overflow: hidden;
}

.stat-card :deep(.el-card__body) {
  padding: 20px;
}

.stat-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.stat-info {
  flex: 1;
}

.stat-number {
  font-size: 28px;
  font-weight: 700;
  color: #303133;
  line-height: 1.2;
}

.stat-label {
  font-size: 14px;
  color: #909399;
  margin-top: 8px;
}

.stat-icon {
  width: 56px;
  height: 56px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 28px;
  color: #fff;
}

.paper-count .stat-icon {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.citation-count .stat-icon {
  background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
}

.author-count .stat-icon {
  background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
}

.journal-count .stat-icon {
  background: linear-gradient(135deg, #43e97b 0%, #38f9d7 100%);
}

.stat-footer {
  margin-top: 16px;
  padding-top: 12px;
  border-top: 1px solid #f0f0f0;
  display: flex;
  align-items: center;
  gap: 8px;
}

.increase {
  font-size: 13px;
  color: #67c23a;
  font-weight: 500;
}

.compare {
  font-size: 12px;
  color: #c0c4cc;
}

.chart-card {
  border-radius: 8px;
}

.chart-card :deep(.el-card__header) {
  padding: 16px 20px;
}

.chart-card :deep(.el-card__body) {
  padding: 16px 20px 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.chart-container {
  width: 100%;
  height: 300px;
}

.rank-card {
  border-radius: 8px;
  height: 100%;
}

.rank-card :deep(.el-card__header) {
  padding: 16px 20px;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.rank-card :deep(.el-card__body) {
  padding: 0 20px 20px;
}

.rank-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.rank-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 10px;
  border-radius: 6px;
  transition: background 0.2s;
  cursor: pointer;
}

.rank-item:hover {
  background: #f5f7fa;
}

.rank-number {
  width: 24px;
  height: 24px;
  border-radius: 6px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 12px;
  font-weight: 600;
  color: #fff;
  background: #c0c4cc;
  flex-shrink: 0;
}

.rank-number.rank-1 {
  background: linear-gradient(135deg, #ff6b6b, #ee5a24);
}

.rank-number.rank-2 {
  background: linear-gradient(135deg, #ffa502, #ff7f50);
}

.rank-number.rank-3 {
  background: linear-gradient(135deg, #ffd32a, #ffb142);
}

.rank-info {
  flex: 1;
  min-width: 0;
}

.rank-title {
  font-size: 13px;
  color: #303133;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  line-height: 1.4;
}

.rank-meta {
  display: flex;
  justify-content: space-between;
  margin-top: 4px;
  font-size: 12px;
  color: #909399;
}

.rank-meta .citations {
  color: #409eff;
  font-weight: 500;
}

.quick-entry {
  display: flex;
  gap: 30px;
  padding: 10px 0;
}

.entry-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 12px;
  cursor: pointer;
  padding: 20px 30px;
  border-radius: 12px;
  transition: all 0.3s;
}

.entry-item:hover {
  background: #f5f7fa;
  transform: translateY(-2px);
}

.entry-icon {
  width: 64px;
  height: 64px;
  border-radius: 16px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 30px;
  color: #fff;
}

.entry-icon.papers {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.entry-icon.citations {
  background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
}

.entry-icon.authors {
  background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
}

.entry-icon.institutions {
  background: linear-gradient(135deg, #43e97b 0%, #38f9d7 100%);
}

.entry-name {
  font-size: 14px;
  color: #606266;
  font-weight: 500;
}
</style>

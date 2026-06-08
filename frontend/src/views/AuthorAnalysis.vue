<template>
  <div class="author-analysis">
    <el-row :gutter="20" class="overview-row">
      <el-col :span="6" v-for="(item, index) in overviewCards" :key="index">
        <el-card class="overview-card" shadow="hover">
          <div class="overview-content">
            <div class="overview-icon" :style="{ background: item.color }">
              <el-icon :size="28"><component :is="item.icon" /></el-icon>
            </div>
            <div class="overview-info">
              <div class="overview-value">
                <el-skeleton v-if="loading.overview" :rows="1" animated />
                <span v-else>{{ item.value }}</span>
              </div>
              <div class="overview-label">{{ item.label }}</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-card class="tab-card" shadow="never">
      <el-tabs v-model="activeTab" class="analysis-tabs">
        <el-tab-pane label="核心作者排行" name="ranking">
          <div v-loading="loading.ranking" class="tab-content">
            <div class="table-wrapper">
              <el-table :data="coreAuthors" stripe table-layout="fixed" style="width: 100%">
              <el-table-column type="index" label="排名" width="80" align="center">
                <template #default="{ $index }">
                  <span v-if="$index < 3" class="rank-badge" :class="'rank-' + ($index + 1)">{{ $index + 1 }}</span>
                  <span v-else>{{ $index + 1 }}</span>
                </template>
              </el-table-column>
              <el-table-column prop="name" label="作者姓名" min-width="150" />
              <el-table-column prop="affiliationName" label="机构" min-width="200" show-overflow-tooltip>
                <template #default="{ row }">
                  {{ row.affiliationName || '-' }}
                </template>
              </el-table-column>
              <el-table-column prop="totalPublications" label="发文数" width="120" sortable align="center" />
              <el-table-column prop="totalCitations" label="被引数" width="120" sortable align="center" />
              <el-table-column prop="hIndex" label="H-index" width="120" sortable align="center" />
              </el-table>
            </div>
          </div>
        </el-tab-pane>

        <el-tab-pane label="发文趋势" name="trend">
          <div v-loading="loading.trend" class="tab-content">
            <div ref="trendChartRef" class="chart-container"></div>
          </div>
        </el-tab-pane>

        <el-tab-pane label="作者合作网络" name="collaboration">
          <div v-loading="loading.collaboration" class="tab-content">
            <div ref="collaborationChartRef" class="chart-container"></div>
          </div>
        </el-tab-pane>

        <el-tab-pane label="研究领域分布" name="areas">
          <div v-loading="loading.areas" class="tab-content">
            <div ref="areasChartRef" class="chart-container"></div>
          </div>
        </el-tab-pane>
      </el-tabs>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted, onBeforeUnmount, watch, nextTick } from 'vue'
import * as echarts from 'echarts'
import {
  getOverview,
  getCoreAuthors,
  getPublicationTrend,
  getAuthorCollaboration,
  getResearchAreas
} from '@/api/analysis'
import type {
  DataOverview,
  Author,
  PublicationTrend,
  ResearchArea,
  AuthorAnalysisData,
  CollaborationEdge
} from '@/types'
import { User, Star, Document, Bell } from '@element-plus/icons-vue'

const activeTab = ref('ranking')
const trendChartRef = ref<HTMLElement>()
const collaborationChartRef = ref<HTMLElement>()
const areasChartRef = ref<HTMLElement>()

let trendChart: echarts.ECharts | null = null
let collaborationChart: echarts.ECharts | null = null
let areasChart: echarts.ECharts | null = null

const loading = reactive({
  overview: true,
  ranking: true,
  trend: true,
  collaboration: true,
  areas: true
})

const overviewData = ref<DataOverview>({
  totalPapers: 0,
  totalAuthors: 0,
  totalInstitutions: 0,
  totalCitations: 0,
  totalJournals: 0
})

const coreAuthors = ref<Author[]>([])
const publicationTrend = ref<PublicationTrend[]>([])
const collaborationNodes = ref<Author[]>([])
const collaborationEdges = ref<CollaborationEdge[]>([])
const researchAreas = ref<ResearchArea[]>([])

const overviewCards = computed(() => [
  { label: '作者总数', value: overviewData.value.totalAuthors.toLocaleString(), icon: User, color: 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)' },
  { label: '核心作者数', value: coreAuthors.value.length.toLocaleString(), icon: Star, color: 'linear-gradient(135deg, #f093fb 0%, #f5576c 100%)' },
  { label: '总发文量', value: overviewData.value.totalPapers.toLocaleString(), icon: Document, color: 'linear-gradient(135deg, #4facfe 0%, #00f2fe 100%)' },
  { label: '总被引次数', value: overviewData.value.totalCitations.toLocaleString(), icon: Bell, color: 'linear-gradient(135deg, #43e97b 0%, #38f9d7 100%)' }
])

async function loadOverview() {
  loading.overview = true
  try {
    const res = await getOverview()
    overviewData.value = res
  } catch (error) {
    console.error('Failed to load overview:', error)
    overviewData.value = {
      totalPapers: 0,
      totalAuthors: 0,
      totalInstitutions: 0,
      totalCitations: 0,
      totalJournals: 0
    }
  } finally {
    loading.overview = false
  }
}

async function loadCoreAuthors() {
  loading.ranking = true
  try {
    const res = await getCoreAuthors()
    coreAuthors.value = res.coreAuthors
  } catch (error) {
    console.error('Failed to load core authors:', error)
    coreAuthors.value = []
  } finally {
    loading.ranking = false
  }
}

async function loadPublicationTrend() {
  loading.trend = true
  try {
    const res = await getPublicationTrend()
    publicationTrend.value = res
  } catch (error) {
    console.error('Failed to load publication trend:', error)
    publicationTrend.value = []
  } finally {
    loading.trend = false
    await nextTick()
    initTrendChart()
  }
}

async function loadCollaboration() {
  loading.collaboration = true
  try {
    const res = await getAuthorCollaboration()
    collaborationNodes.value = res.collaborationNodes
    collaborationEdges.value = res.collaborationEdges
  } catch (error) {
    console.error('Failed to load author collaboration:', error)
    collaborationNodes.value = []
    collaborationEdges.value = []
  } finally {
    loading.collaboration = false
    await nextTick()
    initCollaborationChart()
  }
}

async function loadResearchAreas() {
  loading.areas = true
  try {
    const res = await getResearchAreas()
    researchAreas.value = res
  } catch (error) {
    console.error('Failed to load research areas:', error)
    researchAreas.value = []
  } finally {
    loading.areas = false
    await nextTick()
    initAreasChart()
  }
}

function initTrendChart() {
  if (!trendChartRef.value || publicationTrend.value.length === 0) return
  if (trendChart) {
    trendChart.dispose()
  }
  trendChart = echarts.init(trendChartRef.value)
  const option: echarts.EChartsOption = {
    tooltip: {
      trigger: 'axis',
      formatter: '{b}年<br/>发文量: {c} 篇'
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
      data: publicationTrend.value.map(item => item.year),
      axisLabel: {
        formatter: '{value}年'
      }
    },
    yAxis: {
      type: 'value',
      name: '发文数量',
      axisLabel: {
        formatter: '{value}'
      }
    },
    series: [
      {
        name: '发文量',
        type: 'line',
        smooth: true,
        symbol: 'circle',
        symbolSize: 8,
        lineStyle: {
          width: 3,
          color: new echarts.graphic.LinearGradient(0, 0, 1, 0, [
            { offset: 0, color: '#667eea' },
            { offset: 1, color: '#764ba2' }
          ])
        },
        areaStyle: {
          color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
            { offset: 0, color: 'rgba(102, 126, 234, 0.5)' },
            { offset: 1, color: 'rgba(118, 75, 162, 0.1)' }
          ])
        },
        itemStyle: {
          color: '#667eea',
          borderWidth: 2,
          borderColor: '#fff'
        },
        data: publicationTrend.value.map(item => item.count)
      }
    ]
  }
  trendChart.setOption(option)
}

function initCollaborationChart() {
  if (!collaborationChartRef.value || collaborationNodes.value.length === 0) return
  if (collaborationChart) {
    collaborationChart.dispose()
  }
  collaborationChart = echarts.init(collaborationChartRef.value)

  const nodes = collaborationNodes.value.map(node => ({
    id: String(node.id),
    name: node.name,
    value: node.totalPublications || 10,
    category: 0
  }))

  const links = collaborationEdges.value.map(edge => ({
    source: String(edge.source),
    target: String(edge.target),
    value: edge.weight
  }))

  const categories = [
    { name: '作者' }
  ]

  const option: echarts.EChartsOption = {
    tooltip: {
      formatter: function (params: any) {
        if (params.dataType === 'edge') {
          return `合作次数: ${params.data.value}`
        }
        return `${params.data.name}<br/>发文量: ${params.data.value}`
      }
    },
    legend: {
      data: categories.map(c => c.name),
      bottom: 10
    },
    series: [
      {
        type: 'graph',
        layout: 'force',
        animation: true,
        label: {
          show: true,
          position: 'right',
          formatter: '{b}',
          fontSize: 12
        },
        draggable: true,
        data: nodes,
        links: links,
        categories: categories,
        force: {
          repulsion: 400,
          edgeLength: [80, 200],
          gravity: 0.1
        },
        lineStyle: {
          color: '#999',
          curveness: 0.2,
          width: 2,
          opacity: 0.6
        },
        emphasis: {
          focus: 'adjacency',
          lineStyle: {
            width: 4
          }
        },
        roam: true,
        scaleLimit: {
          min: 0.5,
          max: 2
        },
        symbolSize: function (value: number) {
          return Math.max(20, Math.min(60, value / 2))
        },
        itemStyle: {
          color: '#667eea'
        }
      }
    ]
  }
  collaborationChart.setOption(option)
}

function initAreasChart() {
  if (!areasChartRef.value || researchAreas.value.length === 0) return
  if (areasChart) {
    areasChart.dispose()
  }
  areasChart = echarts.init(areasChartRef.value)
  const colors = ['#667eea', '#764ba2', '#f093fb', '#f5576c', '#4facfe', '#00f2fe', '#43e97b', '#38f9d7']
  const option: echarts.EChartsOption = {
    tooltip: {
      trigger: 'item',
      formatter: '{b}: {c} 篇 ({d}%)'
    },
    legend: {
      orient: 'vertical',
      right: '5%',
      top: 'center',
      itemGap: 12
    },
    series: [
      {
        name: '研究领域',
        type: 'pie',
        radius: ['45%', '70%'],
        center: ['35%', '50%'],
        avoidLabelOverlap: true,
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
            fontSize: 18,
            fontWeight: 'bold'
          },
          itemStyle: {
            shadowBlur: 10,
            shadowOffsetX: 0,
            shadowColor: 'rgba(0, 0, 0, 0.5)'
          }
        },
        labelLine: {
          show: false
        },
        data: researchAreas.value.map((item, index) => ({
          value: item.paperCount,
          name: item.keyword,
          itemStyle: { color: colors[index % colors.length] }
        }))
      }
    ]
  }
  areasChart.setOption(option)
}

function handleResize() {
  trendChart?.resize()
  collaborationChart?.resize()
  areasChart?.resize()
}

watch(activeTab, async (newTab) => {
  await nextTick()
  if (newTab === 'trend' && !loading.trend) {
    initTrendChart()
  } else if (newTab === 'collaboration' && !loading.collaboration) {
    initCollaborationChart()
  } else if (newTab === 'areas' && !loading.areas) {
    initAreasChart()
  }
})

onMounted(() => {
  loadOverview()
  loadCoreAuthors()
  loadPublicationTrend()
  loadCollaboration()
  loadResearchAreas()
  window.addEventListener('resize', handleResize)
})

onBeforeUnmount(() => {
  window.removeEventListener('resize', handleResize)
  trendChart?.dispose()
  collaborationChart?.dispose()
  areasChart?.dispose()
})
</script>

<style scoped>
.author-analysis {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.overview-row {
  margin-bottom: 0;
}

.overview-card {
  border-radius: 12px;
  border: none;
  overflow: hidden;
}

.overview-content {
  display: flex;
  align-items: center;
  gap: 16px;
}

.overview-icon {
  width: 60px;
  height: 60px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  flex-shrink: 0;
}

.overview-info {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.overview-value {
  font-size: 28px;
  font-weight: 700;
  color: #303133;
  line-height: 1.2;
}

.overview-label {
  font-size: 14px;
  color: #909399;
}

.tab-card {
  border-radius: 12px;
  border: none;
}

.analysis-tabs :deep(.el-tabs__header) {
  margin-bottom: 20px;
}

.analysis-tabs :deep(.el-tabs__nav-wrap::after) {
  display: none;
}

.analysis-tabs :deep(.el-tabs__item) {
  font-size: 15px;
  font-weight: 500;
  padding: 0 24px;
  height: 48px;
  line-height: 48px;
}

.tab-content {
  min-height: 500px;
}

.table-wrapper {
  width: 100%;
  overflow-x: auto;
  -webkit-overflow-scrolling: touch;
}

.table-wrapper :deep(.el-table) {
  table-layout: fixed;
  width: 100%;
}

.table-wrapper :deep(.el-table__body-wrapper) {
  overflow-x: auto;
  -webkit-overflow-scrolling: touch;
}

.table-wrapper :deep(.el-table__header-wrapper) {
  overflow-x: auto;
  -webkit-overflow-scrolling: touch;
}

.table-wrapper :deep(.el-table__body),
.table-wrapper :deep(.el-table__header) {
  table-layout: fixed;
  width: 100%;
}

.table-wrapper :deep(.el-table__row) {
  display: table-row;
}

.table-wrapper :deep(.el-table__cell) {
  box-sizing: border-box;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: normal;
  word-break: break-word;
}

@-moz-document url-prefix() {
  .table-wrapper :deep(.el-table) {
    width: 100%;
    table-layout: fixed;
  }

  .table-wrapper :deep(.el-table__body),
  .table-wrapper :deep(.el-table__header) {
    width: 100% !important;
    table-layout: fixed;
  }

  .table-wrapper :deep(.el-table__body-wrapper),
  .table-wrapper :deep(.el-table__header-wrapper) {
    width: 100%;
    overflow-x: auto;
  }

  .table-wrapper :deep(.el-table__row) {
    display: table-row;
  }

  .table-wrapper :deep(.el-table__cell) {
    display: table-cell;
    float: none;
  }
}

@media not all and (min-resolution:.001dpcm) {
  @supports (-webkit-appearance:none) {
    .table-wrapper :deep(.el-table__row) {
      display: table-row;
    }

    .table-wrapper :deep(.el-table__body tr.el-table__row:nth-child(even) td.el-table__cell) {
      background-color: var(--el-table-tr-bg-color);
    }

    .table-wrapper :deep(.el-table__body-wrapper) {
      will-change: transform;
    }
  }
}

.chart-container {
  width: 100%;
  height: 500px;
}

.rank-badge {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 24px;
  height: 24px;
  border-radius: 50%;
  color: white;
  font-size: 12px;
  font-weight: bold;
}

.rank-1 {
  background: linear-gradient(135deg, #ffd700 0%, #ffb700 100%);
}

.rank-2 {
  background: linear-gradient(135deg, #c0c0c0 0%, #a8a8a8 100%);
}

.rank-3 {
  background: linear-gradient(135deg, #cd7f32 0%, #b87333 100%);
}
</style>

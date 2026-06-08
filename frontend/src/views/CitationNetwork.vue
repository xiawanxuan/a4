<template>
  <div class="citation-network">
    <el-row :gutter="20" class="main-row">
      <el-col :span="4" class="control-panel">
        <el-card shadow="never" class="control-card">
          <template #header>
            <div class="card-title">
              <el-icon><Setting /></el-icon>
              <span>控制面板</span>
            </div>
          </template>

          <div class="control-section">
            <div class="section-title">论文选择</div>
            <el-select
              v-model="selectedPaperId"
              placeholder="选择论文"
              filterable
              style="width: 100%"
              @change="handlePaperChange"
            >
              <el-option
                v-for="paper in paperOptions"
                :key="paper.id"
                :label="paper.title"
                :value="paper.id"
              />
            </el-select>
            <el-input
              v-model="searchInput"
              placeholder="或输入论文ID/DOI搜索"
              style="margin-top: 10px"
              clearable
              @keyup.enter="handleSearchPaper"
            >
              <template #append>
                <el-button @click="handleSearchPaper">
                  <el-icon><Search /></el-icon>
                </el-button>
              </template>
            </el-input>
          </div>

          <div class="control-section">
            <div class="section-title">网络深度</div>
            <el-slider
              v-model="networkDepth"
              :min="1"
              :max="3"
              :step="1"
              :marks="{ 1: '1层', 2: '2层', 3: '3层' }"
              @change="handleDepthChange"
            />
          </div>

          <div class="control-section">
            <div class="section-title">布局方式</div>
            <el-radio-group v-model="layoutType" @change="updateChart">
              <el-radio-button value="force">力导向</el-radio-button>
              <el-radio-button value="circular">环形</el-radio-button>
              <el-radio-button value="tree">层次</el-radio-button>
            </el-radio-group>
          </div>

          <div class="control-section">
            <div class="section-title">节点大小</div>
            <el-switch
              v-model="nodeSizeByCitation"
              active-text="按被引次数"
              inactive-text="固定大小"
              @change="updateChart"
            />
          </div>

          <div class="control-section">
            <div class="section-title">节点颜色</div>
            <el-radio-group v-model="nodeColorBy" @change="updateChart">
              <el-radio-button value="category">领域</el-radio-button>
              <el-radio-button value="year">年份</el-radio-button>
            </el-radio-group>
          </div>

          <div class="control-section">
            <div class="section-title">搜索高亮</div>
            <el-input
              v-model="highlightKeyword"
              placeholder="输入节点名称高亮"
              clearable
              @input="handleHighlight"
            >
              <template #prefix>
                <el-icon><Promotion /></el-icon>
              </template>
            </el-input>
          </div>

          <el-button type="primary" style="width: 100%; margin-top: 10px" @click="refreshNetwork">
            <el-icon><Refresh /></el-icon>
            刷新网络
          </el-button>
        </el-card>

        <el-card shadow="never" class="stats-card" style="margin-top: 20px">
          <template #header>
            <div class="card-title">
              <el-icon><DataAnalysis /></el-icon>
              <span>统计信息</span>
            </div>
          </template>

          <div class="stat-item">
            <div class="stat-label">网络节点数</div>
            <div class="stat-value">{{ networkData.nodes.length }}</div>
          </div>
          <div class="stat-item">
            <div class="stat-label">边数（引用关系）</div>
            <div class="stat-value">{{ networkData.links.length }}</div>
          </div>
          <div class="stat-item">
            <div class="stat-label">平均被引次数</div>
            <div class="stat-value">{{ avgCitations }}</div>
          </div>
          <div class="stat-item">
            <div class="stat-label">网络密度</div>
            <div class="stat-value">{{ networkDensity }}</div>
          </div>
        </el-card>
      </el-col>

      <el-col :span="20" class="content-panel">
        <el-card shadow="never" class="main-card">
          <el-tabs v-model="activeTab" class="main-tabs">
            <el-tab-pane label="引文网络视图" name="network">
              <div ref="networkChartRef" class="network-container"></div>
            </el-tab-pane>

            <el-tab-pane label="被引论文排行" name="ranking">
              <div class="ranking-container">
                <el-table :data="topCitedPapers" stripe style="width: 100%">
                  <el-table-column type="index" label="排名" width="80" />
                  <el-table-column prop="title" label="论文标题" min-width="300" />
                  <el-table-column prop="publicationYear" label="年份" width="100" />
                  <el-table-column prop="totalCitations" label="被引次数" width="120" sortable />
                  <el-table-column label="操作" width="120">
                    <template #default="{ row }">
                      <el-button type="primary" link @click="viewPaper(row.id)">查看</el-button>
                    </template>
                  </el-table-column>
                </el-table>
              </div>
            </el-tab-pane>

            <el-tab-pane label="参考文献列表" name="references">
              <div class="references-container">
                <el-table :data="referencesList" stripe style="width: 100%">
                  <el-table-column type="index" label="序号" width="80" />
                  <el-table-column prop="title" label="论文标题" min-width="300" />
                  <el-table-column prop="authors" label="作者" width="200">
                    <template #default="{ row }">
                      <span v-for="(author, idx) in row.authors" :key="author.id">
                        {{ author.name }}<span v-if="idx < row.authors.length - 1">, </span>
                      </span>
                    </template>
                  </el-table-column>
                  <el-table-column prop="publicationYear" label="年份" width="100" />
                  <el-table-column label="操作" width="120">
                    <template #default="{ row }">
                      <el-button type="primary" link @click="viewPaper(row.id)">查看</el-button>
                    </template>
                  </el-table-column>
                </el-table>
              </div>
            </el-tab-pane>

            <el-tab-pane label="引用时间线" name="timeline">
              <div ref="timelineChartRef" class="timeline-container"></div>
            </el-tab-pane>
          </el-tabs>
        </el-card>
      </el-col>
    </el-row>

    <el-dialog
      v-model="detailDialogVisible"
      title="论文详情"
      width="600px"
      :close-on-click-modal="false"
    >
      <div v-if="selectedPaperDetail" class="paper-detail">
        <h3 class="paper-title">{{ selectedPaperDetail.title }}</h3>
        <el-descriptions :column="2" border size="small">
          <el-descriptions-item label="发表年份">
            {{ selectedPaperDetail.publicationYear }}
          </el-descriptions-item>
          <el-descriptions-item label="被引次数">
            {{ selectedPaperDetail.totalCitations }}
          </el-descriptions-item>
          <el-descriptions-item label="期刊">
            {{ selectedPaperDetail.journalName }}
          </el-descriptions-item>
          <el-descriptions-item label="DOI">
            {{ selectedPaperDetail.doi || '-' }}
          </el-descriptions-item>
        </el-descriptions>
        <div class="paper-abstract">
          <h4>摘要</h4>
          <p>{{ selectedPaperDetail.abstractText }}</p>
        </div>
        <div class="paper-keywords">
          <h4>关键词</h4>
          <el-tag
            v-for="(kw, index) in keywordList"
            :key="index"
            type="info"
            style="margin-right: 8px; margin-bottom: 4px"
          >
            {{ kw }}
          </el-tag>
        </div>
      </div>
      <template #footer>
        <el-button @click="detailDialogVisible = false">关闭</el-button>
        <el-button type="primary" @click="goToPaperDetail">查看完整详情</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted, onBeforeUnmount, nextTick } from 'vue'
import { useRouter } from 'vue-router'
import * as echarts from 'echarts'
import { ElMessage } from 'element-plus'
import {
  Setting,
  Search,
  Refresh,
  DataAnalysis,
  Promotion
} from '@element-plus/icons-vue'
import type { CitationNetworkData, CitationNode, Paper, Author, PaperQueryParams } from '@/types'
import { getPaperList, getPaperCitationNetwork, getPaperDetail } from '@/api/paper'
import { getTopCitedPapers, getReferences } from '@/api/citation'

const router = useRouter()

const activeTab = ref('network')
const selectedPaperId = ref<number>(1)
const searchInput = ref('')
const networkDepth = ref(2)
const layoutType = ref<'force' | 'circular' | 'tree'>('force')
const nodeSizeByCitation = ref(true)
const nodeColorBy = ref<'category' | 'year'>('category')
const highlightKeyword = ref('')
const detailDialogVisible = ref(false)
const selectedPaperDetail = ref<Paper | null>(null)

const keywordList = computed(() => {
  if (!selectedPaperDetail.value?.keywords) return []
  return selectedPaperDetail.value.keywords.split(',').map(k => k.trim()).filter(k => k)
})

const networkChartRef = ref<HTMLElement>()
const timelineChartRef = ref<HTMLElement>()

let networkChart: echarts.ECharts | null = null
let timelineChart: echarts.ECharts | null = null

const paperOptions = ref<Paper[]>([])
const topCitedPapers = ref<Paper[]>([])
const referencesList = ref<Paper[]>([])
const timelineData = ref<{ year: number; count: number }[]>([])

const networkData = reactive({
  nodes: [] as CitationNode[],
  links: [] as { source: number; target: number; value: number }[],
  categories: [
    { name: '机器学习' },
    { name: '计算机视觉' },
    { name: '自然语言处理' },
    { name: '数据挖掘' },
    { name: '人工智能' }
  ]
})

const avgCitations = computed(() => {
  if (networkData.nodes.length === 0) return 0
  const total = networkData.nodes.reduce((sum, node) => sum + (node.citations || 0), 0)
  return Math.round(total / networkData.nodes.length)
})

const networkDensity = computed(() => {
  const n = networkData.nodes.length
  if (n < 2) return '0'
  const maxEdges = n * (n - 1)
  const density = (networkData.links.length / maxEdges) * 100
  return density.toFixed(2) + '%'
})

function getNodeColor(node: CitationNode): string {
  if (nodeColorBy.value === 'year') {
    const year = node.year || 2020
    const years = [2015, 2018, 2020, 2022, 2024]
    const colors = ['#91cc75', '#fac858', '#ee6666', '#73c0de', '#5470c6']
    let idx = 0
    for (let i = 0; i < years.length; i++) {
      if (year >= years[i]) idx = i
    }
    return colors[idx]
  }
  return ''
}

function updateChart() {
  if (!networkChart) return

  const categories = networkData.categories.map(c => c.name)

  const nodes = networkData.nodes.map(node => {
    const isHighlighted = highlightKeyword.value && node.title.includes(highlightKeyword.value)
    return {
      ...node,
      id: String(node.id),
      name: node.title,
      symbolSize: nodeSizeByCitation.value ? Math.sqrt(node.citations || 10) * 2.5 + 15 : 30,
      itemStyle: nodeColorBy.value === 'year'
        ? { color: getNodeColor(node as any) }
        : undefined,
      emphasis: {
        scale: true,
        itemStyle: {
          shadowBlur: 20,
          shadowColor: 'rgba(0, 0, 0, 0.3)'
        }
      },
      ...(isHighlighted ? {
        itemStyle: {
          color: '#ff6b6b',
          shadowBlur: 20,
          shadowColor: '#ff6b6b'
        }
      } : {})
    }
  })

  const option: echarts.EChartsOption = {
    tooltip: {
      formatter: (params: any) => {
        if (params.dataType === 'node') {
          return `<strong>${params.data.name}</strong><br/>被引次数: ${params.data.citations || 0}<br/>年份: ${params.data.year || '-'}`
        } else if (params.dataType === 'edge') {
          return `引用强度: ${params.data.value}`
        }
        return ''
      }
    },
    legend: [{
      data: nodeColorBy.value === 'category' ? categories : [],
      bottom: 10,
      textStyle: { fontSize: 12 }
    }],
    animationDuration: 1500,
    animationEasingUpdate: 'quinticInOut',
    series: [{
      name: '引文网络',
      type: 'graph',
      layout: layoutType.value === 'tree' ? 'force' : layoutType.value,
      data: nodes,
      links: networkData.links.map(link => ({
        source: String(link.source),
        target: String(link.target),
        value: link.value,
        lineStyle: {
          curveness: 0.2
        }
      })),
      categories: nodeColorBy.value === 'category' ? networkData.categories : undefined,
      roam: true,
      draggable: true,
      label: {
        show: true,
        position: 'right',
        fontSize: 11,
        formatter: '{b}',
        color: '#333'
      },
      lineStyle: {
        color: 'source',
        width: 2,
        opacity: 0.6,
        curveness: 0.3
      },
      edgeSymbol: ['none', 'arrow'],
      edgeSymbolSize: [4, 8],
      emphasis: {
        focus: 'adjacency',
        lineStyle: {
          width: 4,
          opacity: 1
        },
        label: {
          fontSize: 13,
          fontWeight: 'bold'
        }
      },
      force: {
        repulsion: 600,
        edgeLength: [80, 200],
        gravity: 0.08,
        friction: 0.6
      },
      circular: {
        rotateLabel: true
      }
    }]
  }

  if (layoutType.value === 'tree') {
    option.series = [{
      ...(option.series as any)[0],
      layout: 'force',
      force: {
        repulsion: 800,
        edgeLength: [100, 250],
        gravity: 0.1,
        friction: 0.5
      }
    }]
  }

  networkChart.setOption(option, true)
}

function initNetworkChart() {
  if (!networkChartRef.value) return
  networkChart = echarts.init(networkChartRef.value)

  networkChart.on('click', (params: any) => {
    if (params.dataType === 'node') {
      const nodeId = parseInt(params.data.id)
      showPaperDetail(nodeId)
    }
  })

  loadCitationNetwork()
}

function initTimelineChart() {
  if (!timelineChartRef.value) return
  timelineChart = echarts.init(timelineChartRef.value)

  const option: echarts.EChartsOption = {
    tooltip: {
      trigger: 'axis',
      formatter: '{b}年<br/>引用次数: {c}'
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
      data: timelineData.value.map(d => d.year),
      name: '年份'
    },
    yAxis: {
      type: 'value',
      name: '引用次数'
    },
    series: [{
      name: '引用次数',
      type: 'line',
      smooth: true,
      symbol: 'circle',
      symbolSize: 8,
      data: timelineData.value.map(d => d.count),
      areaStyle: {
        color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
          { offset: 0, color: 'rgba(64, 158, 255, 0.5)' },
          { offset: 1, color: 'rgba(64, 158, 255, 0.05)' }
        ])
      },
      lineStyle: {
        width: 3,
        color: '#409eff'
      },
      itemStyle: {
        color: '#409eff'
      }
    }]
  }

  timelineChart.setOption(option)
}

async function loadPaperOptions() {
  try {
    const params: PaperQueryParams = { pageNum: 1, pageSize: 20 }
    const res = await getPaperList(params)
    paperOptions.value = res.list
  } catch (error) {
    console.error('Failed to load paper options:', error)
    paperOptions.value = []
  }
}

async function loadCitationNetwork() {
  try {
    const res = await getPaperCitationNetwork(selectedPaperId.value, networkDepth.value)
    if (res.nodes && res.nodes.length > 0) {
      networkData.nodes = res.nodes
      networkData.links = res.edges.map(e => ({ source: e.source, target: e.target, value: 1 }))
    } else {
      networkData.nodes = []
      networkData.links = []
    }
  } catch (error) {
    console.error('Failed to load citation network:', error)
    networkData.nodes = []
    networkData.links = []
  }
  updateChart()
}

async function loadTopCitedPapers() {
  try {
    const res = await getTopCitedPapers(10)
    topCitedPapers.value = res
  } catch (error) {
    console.error('Failed to load top cited papers:', error)
    topCitedPapers.value = []
  }
}

async function loadReferences() {
  try {
    const res = await getReferences(selectedPaperId.value)
    referencesList.value = res
  } catch (error) {
    console.error('Failed to load references:', error)
    referencesList.value = []
  }
}

async function loadTimeline() {
  try {
    timelineData.value = []
  } catch (error) {
    console.error('Failed to load timeline:', error)
    timelineData.value = []
  }
}

async function showPaperDetail(paperId: number) {
  try {
    const res = await getPaperDetail(paperId)
    selectedPaperDetail.value = res
  } catch (error) {
    console.error('Failed to load paper detail:', error)
    selectedPaperDetail.value = null
  }
  detailDialogVisible.value = true
}

function handlePaperChange() {
  loadCitationNetwork()
  loadReferences()
  loadTimeline()
}

function handleDepthChange() {
  loadCitationNetwork()
}

function handleSearchPaper() {
  if (!searchInput.value) {
    ElMessage.warning('请输入论文ID或DOI')
    return
  }
  const id = parseInt(searchInput.value)
  if (!isNaN(id)) {
    selectedPaperId.value = id
    handlePaperChange()
  } else {
    ElMessage.info('搜索功能需要后端支持，当前使用模拟数据')
  }
}

function handleHighlight() {
  updateChart()
}

function refreshNetwork() {
  loadCitationNetwork()
  loadTopCitedPapers()
  loadReferences()
  loadTimeline()
  ElMessage.success('网络已刷新')
}

function viewPaper(id: number) {
  router.push(`/paper/${id}`)
}

function goToPaperDetail() {
  if (selectedPaperDetail.value) {
    router.push(`/paper/${selectedPaperDetail.value.id}`)
  }
}

function handleResize() {
  networkChart?.resize()
  timelineChart?.resize()
}

function handleTabChange(tabName: string) {
  nextTick(() => {
    if (tabName === 'network') {
      networkChart?.resize()
    } else if (tabName === 'timeline') {
      if (!timelineChart) {
        initTimelineChart()
      } else {
        timelineChart.resize()
      }
    }
  })
}

onMounted(async () => {
  await loadPaperOptions()
  initNetworkChart()
  await loadTopCitedPapers()
  await loadReferences()
  await loadTimeline()
  window.addEventListener('resize', handleResize)
})

onBeforeUnmount(() => {
  window.removeEventListener('resize', handleResize)
  networkChart?.dispose()
  timelineChart?.dispose()
})
</script>

<style scoped>
.citation-network {
  width: 100%;
  height: 100%;
  padding: 20px;
  box-sizing: border-box;
  background: #f5f7fa;
}

.main-row {
  height: 100%;
}

.control-panel {
  height: 100%;
}

.content-panel {
  height: 100%;
}

.control-card,
.stats-card,
.main-card {
  border-radius: 8px;
  height: 100%;
}

.control-card :deep(.el-card__body),
.stats-card :deep(.el-card__body),
.main-card :deep(.el-card__body) {
  padding: 16px;
}

.card-title {
  display: flex;
  align-items: center;
  gap: 8px;
  font-weight: 600;
  font-size: 15px;
}

.control-section {
  margin-bottom: 20px;
}

.section-title {
  font-size: 13px;
  font-weight: 500;
  color: #606266;
  margin-bottom: 10px;
}

.stat-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 10px 0;
  border-bottom: 1px solid #f0f0f0;
}

.stat-item:last-child {
  border-bottom: none;
}

.stat-label {
  font-size: 13px;
  color: #606266;
}

.stat-value {
  font-size: 18px;
  font-weight: 600;
  color: #409eff;
}

.network-container {
  width: 100%;
  height: calc(100vh - 180px);
  min-height: 500px;
}

.ranking-container,
.references-container {
  padding: 10px 0;
}

.timeline-container {
  width: 100%;
  height: 500px;
  padding-top: 20px;
}

.main-tabs :deep(.el-tabs__content) {
  height: calc(100% - 50px);
}

.main-tabs :deep(.el-tab-pane) {
  height: 100%;
}

.paper-detail .paper-title {
  margin: 0 0 16px 0;
  font-size: 18px;
  color: #303133;
  line-height: 1.5;
}

.paper-abstract {
  margin-top: 16px;
}

.paper-abstract h4,
.paper-keywords h4 {
  margin: 0 0 8px 0;
  font-size: 14px;
  color: #606266;
}

.paper-abstract p {
  margin: 0;
  line-height: 1.6;
  color: #606266;
  font-size: 13px;
}

.paper-keywords {
  margin-top: 16px;
}

@media (max-width: 1200px) {
  .control-panel {
    margin-bottom: 20px;
  }
}
</style>

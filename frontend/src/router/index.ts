import { createRouter, createWebHistory, type RouteRecordRaw } from 'vue-router'

const routes: RouteRecordRaw[] = [
  {
    path: '/',
    name: 'Dashboard',
    component: () => import('@/views/Dashboard.vue'),
    meta: { title: '首页' }
  },
  {
    path: '/papers',
    name: 'Papers',
    component: () => import('@/views/PaperList.vue'),
    meta: { title: '论文列表' }
  },
  {
    path: '/paper/:id',
    name: 'PaperDetail',
    component: () => import('@/views/PaperDetail.vue'),
    meta: { title: '论文详情' }
  },
  {
    path: '/authors',
    name: 'Authors',
    component: () => import('@/views/AuthorAnalysis.vue'),
    meta: { title: '作者分析' }
  },
  {
    path: '/institutions',
    name: 'Institutions',
    component: () => import('@/views/InstitutionAnalysis.vue'),
    meta: { title: '机构分析' }
  },
  {
    path: '/citations',
    name: 'Citations',
    component: () => import('@/views/CitationNetwork.vue'),
    meta: { title: '引文网络' }
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach((to, _from, next) => {
  if (to.meta.title) {
    document.title = `${to.meta.title} - 学术论文分析系统`
  }
  next()
})

export default router

import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import type { Paper, PageResult, PaperQueryParams, CitationNetworkData } from '@/types'
import {
  getPaperList,
  getPaperDetail,
  getPaperReferences,
  getPaperCitations,
  getPaperCitationNetwork,
  createPaper,
  updatePaper,
  deletePaper,
  batchImportPapers
} from '@/api/paper'

export const usePaperStore = defineStore('paper', () => {
  const papers = ref<Paper[]>([])
  const total = ref(0)
  const pageNum = ref(1)
  const pageSize = ref(10)
  const loading = ref(false)
  const currentPaper = ref<Paper | null>(null)
  const references = ref<Paper[]>([])
  const citations = ref<Paper[]>([])
  const citationNetwork = ref<CitationNetworkData | null>(null)

  const totalPages = computed(() => Math.ceil(total.value / pageSize.value))

  async function fetchPapers(params?: PaperQueryParams) {
    loading.value = true
    try {
      const res = await getPaperList({
        pageNum: pageNum.value,
        pageSize: pageSize.value,
        ...params
      })
      papers.value = res.list
      total.value = res.total
      if (params?.pageNum) pageNum.value = params.pageNum
      if (params?.pageSize) pageSize.value = params.pageSize
    } catch (error) {
      console.error('Failed to fetch papers:', error)
      throw error
    } finally {
      loading.value = false
    }
  }

  async function fetchPaperById(id: number) {
    loading.value = true
    try {
      const res = await getPaperDetail(id)
      currentPaper.value = res
    } catch (error) {
      console.error('Failed to fetch paper:', error)
      throw error
    } finally {
      loading.value = false
    }
  }

  async function fetchReferences(id: number) {
    try {
      const res = await getPaperReferences(id)
      references.value = res
    } catch (error) {
      console.error('Failed to fetch references:', error)
    }
  }

  async function fetchCitations(id: number) {
    try {
      const res = await getPaperCitations(id)
      citations.value = res
    } catch (error) {
      console.error('Failed to fetch citations:', error)
    }
  }

  async function fetchCitationNetwork(id: number, depth?: number) {
    try {
      const res = await getPaperCitationNetwork(id, depth)
      citationNetwork.value = res
    } catch (error) {
      console.error('Failed to fetch citation network:', error)
    }
  }

  async function addPaper(data: Partial<Paper>) {
    try {
      const res = await createPaper(data)
      return res
    } catch (error) {
      console.error('Failed to create paper:', error)
      throw error
    }
  }

  async function editPaper(id: number, data: Partial<Paper>) {
    try {
      const res = await updatePaper(id, data)
      return res
    } catch (error) {
      console.error('Failed to update paper:', error)
      throw error
    }
  }

  async function removePaper(id: number) {
    try {
      await deletePaper(id)
    } catch (error) {
      console.error('Failed to delete paper:', error)
      throw error
    }
  }

  async function importPapers(formData: FormData) {
    try {
      const res = await batchImportPapers(formData)
      return res
    } catch (error) {
      console.error('Failed to import papers:', error)
      throw error
    }
  }

  function setPage(newPage: number) {
    pageNum.value = newPage
  }

  function setPageSize(newSize: number) {
    pageSize.value = newSize
    pageNum.value = 1
  }

  function clearCurrentPaper() {
    currentPaper.value = null
    references.value = []
    citations.value = []
    citationNetwork.value = null
  }

  return {
    papers,
    total,
    pageNum,
    pageSize,
    totalPages,
    loading,
    currentPaper,
    references,
    citations,
    citationNetwork,
    fetchPapers,
    fetchPaperById,
    fetchReferences,
    fetchCitations,
    fetchCitationNetwork,
    addPaper,
    editPaper,
    removePaper,
    importPapers,
    setPage,
    setPageSize,
    clearCurrentPaper
  }
})

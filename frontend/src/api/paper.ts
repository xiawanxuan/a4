import { get, post, put, del } from './index'
import type { Paper, PageResult, PaperQueryParams, CitationNetworkData, ImportResult } from '@/types'

export function getPaperList(params?: PaperQueryParams): Promise<PageResult<Paper>> {
  return get<PageResult<Paper>>('/papers', { params })
}

export function getPaperDetail(id: number): Promise<Paper> {
  return get<Paper>(`/papers/${id}`)
}

export function getPaperByDoi(doi: string): Promise<Paper> {
  return get<Paper>(`/papers/doi/${doi}`)
}

export function createPaper(data: Partial<Paper>): Promise<Paper> {
  return post<Paper>('/papers', data)
}

export function updatePaper(id: number, data: Partial<Paper>): Promise<Paper> {
  return put<Paper>(`/papers/${id}`, data)
}

export function deletePaper(id: number): Promise<boolean> {
  return del<boolean>(`/papers/${id}`)
}

export function getPaperReferences(id: number): Promise<Paper[]> {
  return get<Paper[]>(`/papers/${id}/references`)
}

export function getPaperCitations(id: number): Promise<Paper[]> {
  return get<Paper[]>(`/papers/${id}/citations`)
}

export function getPaperStatistics(): Promise<Record<string, any>> {
  return get<Record<string, any>>('/papers/statistics')
}

export function getPaperCitationNetwork(id: number, depth?: number): Promise<CitationNetworkData> {
  return get<CitationNetworkData>(`/papers/${id}/citation-network`, { params: { depth } })
}

export function batchImportPapers(formData: FormData): Promise<ImportResult> {
  return post<ImportResult>('/import/csv', formData, {
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })
}

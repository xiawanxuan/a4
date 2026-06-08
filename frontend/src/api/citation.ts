import { get, post, del } from './index'
import type { CitationNetworkData, Paper, Citation, ImportResult } from '@/types'

export function getCitationNetwork(paperId: number, depth?: number): Promise<CitationNetworkData> {
  return get<CitationNetworkData>('/citations/network', { params: { paperId, depth } })
}

export function getTopCitedPapers(limit: number = 10): Promise<Paper[]> {
  return get<Paper[]>('/analysis/top-cited-papers', { params: { limit } })
}

export function getReferences(paperId: number): Promise<Paper[]> {
  return get<Paper[]>(`/citations/citing/${paperId}`)
}

export function getCitations(paperId: number): Promise<Paper[]> {
  return get<Paper[]>(`/citations/cited/${paperId}`)
}

export function getCitationCount(paperId: number): Promise<number> {
  return get<number>(`/citations/cited/${paperId}/count`)
}

export function addCitation(citingPaperId: number, citedPaperId: number, citationContext?: string): Promise<Citation> {
  return post<Citation>('/citations', null, {
    params: { citingPaperId, citedPaperId, citationContext }
  })
}

export function deleteCitation(id: number): Promise<void> {
  return del<void>(`/citations/${id}`)
}

export function batchImportCitations(citations: Citation[]): Promise<ImportResult> {
  return post<ImportResult>('/citations/batch', citations)
}

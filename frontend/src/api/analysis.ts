import { get } from './index'
import type {
  Paper,
  DataOverview,
  PublicationTrend,
  AuthorAnalysisData,
  InstitutionAnalysisData,
  KeywordCooccurrence,
  JournalDistribution,
  ResearchArea,
  CoreAnalysisParams
} from '@/types'

export function getOverview(): Promise<DataOverview> {
  return get<DataOverview>('/analysis/overview')
}

export function getCoreAuthors(params?: CoreAnalysisParams): Promise<AuthorAnalysisData> {
  return get<AuthorAnalysisData>('/analysis/core-authors', { params })
}

export function getCoreInstitutions(params?: CoreAnalysisParams): Promise<InstitutionAnalysisData> {
  return get<InstitutionAnalysisData>('/analysis/core-institutions', { params })
}

export function getPublicationTrend(params?: { yearStart?: number; yearEnd?: number }): Promise<PublicationTrend[]> {
  return get<PublicationTrend[]>('/analysis/publication-trend', { params })
}

export function getKeywordCooccurrence(params?: { limit?: number }): Promise<KeywordCooccurrence[]> {
  return get<KeywordCooccurrence[]>('/analysis/keyword-cooccurrence', { params })
}

export function getAuthorCollaboration(params?: { limit?: number }): Promise<AuthorAnalysisData> {
  return get<AuthorAnalysisData>('/analysis/author-collaboration', { params })
}

export function getInstitutionCollaboration(params?: { limit?: number }): Promise<InstitutionAnalysisData> {
  return get<InstitutionAnalysisData>('/analysis/institution-collaboration', { params })
}

export function getJournalDistribution(params?: { limit?: number }): Promise<JournalDistribution[]> {
  return get<JournalDistribution[]>('/analysis/journal-distribution', { params })
}

export function getResearchAreas(params?: { limit?: number }): Promise<ResearchArea[]> {
  return get<ResearchArea[]>('/analysis/research-areas', { params })
}

export function getTopCitedPapers(params?: { limit?: number }): Promise<Paper[]> {
  return get<Paper[]>('/analysis/top-cited-papers', { params })
}

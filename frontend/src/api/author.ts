import { get, post, put, del } from './index'
import type { Author, Paper, PageResult, AuthorQueryParams, AuthorAnalysisData } from '@/types'

export function getAuthorList(params?: AuthorQueryParams): Promise<PageResult<Author>> {
  return get<PageResult<Author>>('/authors', { params })
}

export function getAuthorDetail(id: number): Promise<Author> {
  return get<Author>(`/authors/${id}`)
}

export function getAuthorByOrcid(orcid: string): Promise<Author> {
  return get<Author>(`/authors/orcid/${orcid}`)
}

export function createAuthor(data: Partial<Author>): Promise<Author> {
  return post<Author>('/authors', data)
}

export function updateAuthor(id: number, data: Partial<Author>): Promise<Author> {
  return put<Author>(`/authors/${id}`, data)
}

export function deleteAuthor(id: number): Promise<void> {
  return del<void>(`/authors/${id}`)
}

export function getAuthorPapers(id: number, params?: { pageNum?: number; pageSize?: number }): Promise<PageResult<Paper>> {
  return get<PageResult<Paper>>(`/authors/${id}/papers`, { params })
}

export function getAuthorCollaboration(id: number): Promise<AuthorAnalysisData> {
  return get<AuthorAnalysisData>(`/authors/${id}/collaboration`)
}

export function getAuthorStatistics(id: number): Promise<Record<string, any>> {
  return get<Record<string, any>>(`/authors/${id}/statistics`)
}

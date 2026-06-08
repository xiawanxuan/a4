import { get, post, put, del } from './index'
import type { Institution, Author, Paper, PageResult, InstitutionQueryParams } from '@/types'

export function getInstitutionList(params?: InstitutionQueryParams): Promise<PageResult<Institution>> {
  return get<PageResult<Institution>>('/institutions', { params })
}

export function getInstitutionDetail(id: number): Promise<Institution> {
  return get<Institution>(`/institutions/${id}`)
}

export function createInstitution(data: Partial<Institution>): Promise<Institution> {
  return post<Institution>('/institutions', data)
}

export function updateInstitution(id: number, data: Partial<Institution>): Promise<Institution> {
  return put<Institution>(`/institutions/${id}`, data)
}

export function deleteInstitution(id: number): Promise<void> {
  return del<void>(`/institutions/${id}`)
}

export function getInstitutionPapers(id: number, params?: { pageNum?: number; pageSize?: number }): Promise<PageResult<Paper>> {
  return get<PageResult<Paper>>(`/institutions/${id}/papers`, { params })
}

export function getInstitutionAuthors(id: number): Promise<Author[]> {
  return get<Author[]>(`/institutions/${id}/authors`)
}

export function getInstitutionRanking(params?: { sortBy?: string; limit?: number }): Promise<Institution[]> {
  return get<Institution[]>('/institutions/ranking', { params })
}

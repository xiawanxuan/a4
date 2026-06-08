import { get, post } from './index'
import type { PaperImportData, ImportResult } from '@/types'

export function importJson(data: Partial<PaperImportData>[]): Promise<ImportResult> {
  return post<ImportResult>('/import/json', data)
}

export function importCsv(file: File): Promise<ImportResult> {
  const formData = new FormData()
  formData.append('file', file)
  return post<ImportResult>('/import/csv', formData, {
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })
}

export function importBibTeX(file: File): Promise<ImportResult> {
  const formData = new FormData()
  formData.append('file', file)
  return post<ImportResult>('/import/bibtex', formData, {
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })
}

export function importRis(file: File): Promise<ImportResult> {
  const formData = new FormData()
  formData.append('file', file)
  return post<ImportResult>('/import/ris', formData, {
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })
}

export function importEndNote(file: File): Promise<ImportResult> {
  const formData = new FormData()
  formData.append('file', file)
  return post<ImportResult>('/import/endnote', formData, {
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })
}

export function importCitations(data: { citingDoi: string; citedDoi: string; context?: string }[]): Promise<ImportResult> {
  return post<ImportResult>('/import/citations', data)
}

export function downloadTemplate(): Promise<Blob> {
  return get<Blob>('/import/template', {
    responseType: 'blob'
  })
}

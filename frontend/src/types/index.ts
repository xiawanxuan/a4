export interface Paper {
  id: number
  title: string
  titleEn?: string
  abstractText?: string
  keywords?: string
  doi?: string
  pmid?: string
  arxivId?: string
  url?: string
  pdfUrl?: string
  journalId?: number
  journalName?: string
  impactFactor?: number
  volume?: string
  issue?: string
  pages?: string
  publicationDate?: string
  publicationYear?: number
  language?: string
  documentType?: string
  totalCitations?: number
  totalReferences?: number
  authors?: Author[]
  institutions?: Institution[]
}

export interface Author {
  id: number
  name: string
  nameEn?: string
  orcid?: string
  email?: string
  homepage?: string
  hIndex?: number
  totalCitations?: number
  totalPublications?: number
  affiliationId?: number
  affiliationName?: string
  biography?: string
  researchAreas?: string[]
}

export interface Institution {
  id: number
  name: string
  nameEn?: string
  country?: string
  city?: string
  department?: string
  type?: string
  description?: string
  authorCount?: number
  paperCount?: number
  totalCitations?: number
  hIndex?: number
}

export interface Journal {
  id: number
  name: string
  nameAbbr?: string
  type?: string
  issn?: string
  eIssn?: string
  publisher?: string
  country?: string
  impactFactor?: number
  description?: string
}

export interface Citation {
  id: number
  citingPaperId: number
  citedPaperId: number
  citingPaper?: Paper
  citedPaper?: Paper
  citationContext?: string
}

export interface PageResult<T> {
  list: T[]
  total: number
  pageNum: number
  pageSize: number
}

export interface PaperQueryParams {
  keyword?: string
  authorId?: number
  institutionId?: number
  yearStart?: number
  yearEnd?: number
  pageNum?: number
  pageSize?: number
  sortBy?: string
  sortOrder?: 'asc' | 'desc'
}

export interface AuthorQueryParams {
  name?: string
  institutionId?: number
  pageNum?: number
  pageSize?: number
}

export interface InstitutionQueryParams {
  name?: string
  country?: string
  pageNum?: number
  pageSize?: number
}

export interface CitationNode {
  id: number
  title: string
  citations?: number
  year?: number
}

export interface CitationEdge {
  source: number
  target: number
}

export interface CitationNetworkData {
  nodes: CitationNode[]
  edges: CitationEdge[]
}

export interface ImportResult {
  successCount: number
  failCount: number
  errors: ImportErrorDetail[]
}

export interface ImportErrorDetail {
  rowIndex: number
  message: string
}

export interface PublicationTrend {
  year: number
  count: number
}

export interface CollaborationEdge {
  source: number
  target: number
  weight: number
}

export interface AuthorAnalysisData {
  coreAuthors: Author[]
  publicationTrend: PublicationTrend[]
  collaborationNodes: Author[]
  collaborationEdges: CollaborationEdge[]
}

export interface InstitutionAnalysisData {
  institutionRanking: Institution[]
  publicationTrend: PublicationTrend[]
  collaborationNodes: Institution[]
  collaborationEdges: CollaborationEdge[]
}

export interface DataOverview {
  totalPapers: number
  totalAuthors: number
  totalInstitutions: number
  totalCitations: number
  totalJournals: number
}

export interface KeywordCooccurrence {
  keyword1: string
  keyword2: string
  count: number
}

export interface JournalDistribution {
  journalId: number
  journalName: string
  paperCount: number
}

export interface ResearchArea {
  keyword: string
  paperCount: number
  totalCitations: number
}

export interface PaperStatistics {
  totalPapers: number
  totalCitations: number
  totalAuthors: number
  totalInstitutions: number
  yearlyTrend: PublicationTrend[]
}

export interface CoreAnalysisParams {
  sortBy?: string
  limit?: number
}

export interface CitationNetworkParams {
  paperId: number
  depth?: number
}

export interface CitationData {
  citingPaperId: number
  citedPaperId: number
  citationContext?: string
}

export interface ApiResponse<T = any> {
  code: number
  message: string
  data: T
}

export interface PaperImportData {
  title?: string
  titleEn?: string
  abstractText?: string
  keywords?: string
  doi?: string
  pmid?: string
  arxivId?: string
  url?: string
  pdfUrl?: string
  journalName?: string
  issn?: string
  volume?: string
  issue?: string
  pages?: string
  publicationDate?: string
  publicationYear?: number
  language?: string
  documentType?: string
  totalCitations?: number
  authors?: string[]
  institutions?: string[]
  references?: string[]
}

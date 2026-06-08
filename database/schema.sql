-- ============================================
-- 科研论文文献元数据管理系统数据库设计
-- ============================================

DROP DATABASE IF EXISTS research_db;
CREATE DATABASE research_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE research_db;

-- ============================================
-- 机构表
-- ============================================
CREATE TABLE institution (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL COMMENT '机构名称',
    name_en VARCHAR(255) COMMENT '英文名称',
    country VARCHAR(100) COMMENT '国家',
    city VARCHAR(100) COMMENT '城市',
    department VARCHAR(255) COMMENT '院系/部门',
    type VARCHAR(50) COMMENT '机构类型：university, institute, company, hospital',
    description TEXT COMMENT '机构简介',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uk_institution_name (name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='科研机构表';

-- ============================================
-- 作者表
-- ============================================
CREATE TABLE author (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL COMMENT '作者姓名',
    name_en VARCHAR(100) COMMENT '英文姓名',
    orcid VARCHAR(50) COMMENT 'ORCID标识符',
    email VARCHAR(255) COMMENT '邮箱',
    homepage VARCHAR(500) COMMENT '个人主页',
    h_index INT DEFAULT 0 COMMENT 'H指数',
    total_citations INT DEFAULT 0 COMMENT '总被引次数',
    total_publications INT DEFAULT 0 COMMENT '总发表论文数',
    affiliation_id BIGINT COMMENT '当前所属机构ID',
    biography TEXT COMMENT '个人简介',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    KEY idx_author_name (name),
    KEY idx_author_orcid (orcid),
    KEY idx_author_affiliation (affiliation_id),
    CONSTRAINT fk_author_institution FOREIGN KEY (affiliation_id) REFERENCES institution(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='作者表';

-- ============================================
-- 期刊/会议表
-- ============================================
CREATE TABLE journal (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL COMMENT '期刊/会议名称',
    name_abbr VARCHAR(100) COMMENT '缩写',
    type VARCHAR(20) NOT NULL DEFAULT 'journal' COMMENT '类型：journal, conference',
    issn VARCHAR(20) COMMENT 'ISSN号',
    e_issn VARCHAR(20) COMMENT '电子版ISSN',
    publisher VARCHAR(255) COMMENT '出版商',
    country VARCHAR(100) COMMENT '出版国家',
    impact_factor DECIMAL(6,3) COMMENT '影响因子',
    description TEXT COMMENT '简介',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uk_journal_name (name),
    KEY idx_journal_issn (issn)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='期刊/会议表';

-- ============================================
-- 论文表
-- ============================================
CREATE TABLE paper (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(500) NOT NULL COMMENT '论文标题',
    title_en VARCHAR(500) COMMENT '英文标题',
    abstract TEXT COMMENT '摘要',
    keywords TEXT COMMENT '关键词，逗号分隔',
    doi VARCHAR(100) COMMENT 'DOI号',
    pmid VARCHAR(20) COMMENT 'PubMed ID',
    arxiv_id VARCHAR(50) COMMENT 'arXiv ID',
    url VARCHAR(500) COMMENT '论文链接',
    pdf_url VARCHAR(500) COMMENT 'PDF链接',
    journal_id BIGINT COMMENT '期刊/会议ID',
    volume VARCHAR(50) COMMENT '卷',
    issue VARCHAR(50) COMMENT '期',
    pages VARCHAR(50) COMMENT '页码',
    publication_date DATE COMMENT '发表日期',
    publication_year INT COMMENT '发表年份',
    language VARCHAR(20) DEFAULT 'en' COMMENT '语言',
    document_type VARCHAR(50) COMMENT '文献类型：article, review, letter, editorial, etc.',
    total_citations INT DEFAULT 0 COMMENT '被引次数',
    total_references INT DEFAULT 0 COMMENT '参考文献数',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    KEY idx_paper_title (title(255)),
    KEY idx_paper_doi (doi),
    KEY idx_paper_year (publication_year),
    KEY idx_paper_journal (journal_id),
    KEY idx_paper_citations (total_citations),
    CONSTRAINT fk_paper_journal FOREIGN KEY (journal_id) REFERENCES journal(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='论文表';

-- ============================================
-- 论文-作者关联表
-- ============================================
CREATE TABLE paper_author (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    paper_id BIGINT NOT NULL COMMENT '论文ID',
    author_id BIGINT NOT NULL COMMENT '作者ID',
    author_order INT NOT NULL COMMENT '作者顺序',
    is_corresponding TINYINT(1) DEFAULT 0 COMMENT '是否通讯作者',
    affiliation_id BIGINT COMMENT '作者所属机构ID（论文发表时）',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uk_paper_author (paper_id, author_id, author_order),
    KEY idx_paper_author_paper (paper_id),
    KEY idx_paper_author_author (author_id),
    CONSTRAINT fk_pa_paper FOREIGN KEY (paper_id) REFERENCES paper(id) ON DELETE CASCADE,
    CONSTRAINT fk_pa_author FOREIGN KEY (author_id) REFERENCES author(id) ON DELETE CASCADE,
    CONSTRAINT fk_pa_institution FOREIGN KEY (affiliation_id) REFERENCES institution(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='论文作者关联表';

-- ============================================
-- 论文-机构关联表
-- ============================================
CREATE TABLE paper_institution (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    paper_id BIGINT NOT NULL COMMENT '论文ID',
    institution_id BIGINT NOT NULL COMMENT '机构ID',
    affiliation_order INT COMMENT '机构排序',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uk_paper_institution (paper_id, institution_id),
    KEY idx_pi_paper (paper_id),
    KEY idx_pi_institution (institution_id),
    CONSTRAINT fk_pi_paper FOREIGN KEY (paper_id) REFERENCES paper(id) ON DELETE CASCADE,
    CONSTRAINT fk_pi_institution FOREIGN KEY (institution_id) REFERENCES institution(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='论文机构关联表';

-- ============================================
-- 引用关系表
-- ============================================
CREATE TABLE citation (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    citing_paper_id BIGINT NOT NULL COMMENT '施引论文ID',
    cited_paper_id BIGINT NOT NULL COMMENT '被引论文ID',
    citation_context TEXT COMMENT '引用上下文',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uk_citation (citing_paper_id, cited_paper_id),
    KEY idx_citation_citing (citing_paper_id),
    KEY idx_citation_cited (cited_paper_id),
    CONSTRAINT fk_citation_citing FOREIGN KEY (citing_paper_id) REFERENCES paper(id) ON DELETE CASCADE,
    CONSTRAINT fk_citation_cited FOREIGN KEY (cited_paper_id) REFERENCES paper(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='引用关系表';

-- ============================================
-- 作者-机构关联历史表
-- ============================================
CREATE TABLE author_institution (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    author_id BIGINT NOT NULL COMMENT '作者ID',
    institution_id BIGINT NOT NULL COMMENT '机构ID',
    start_year INT COMMENT '开始年份',
    end_year INT COMMENT '结束年份',
    is_current TINYINT(1) DEFAULT 0 COMMENT '是否当前机构',
    position VARCHAR(100) COMMENT '职位',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uk_author_institution (author_id, institution_id),
    KEY idx_ai_author (author_id),
    KEY idx_ai_institution (institution_id),
    CONSTRAINT fk_ai_author FOREIGN KEY (author_id) REFERENCES author(id) ON DELETE CASCADE,
    CONSTRAINT fk_ai_institution FOREIGN KEY (institution_id) REFERENCES institution(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='作者机构关联表';

-- ============================================
-- 索引优化
-- ============================================
CREATE INDEX idx_paper_author_corresponding ON paper_author(is_corresponding);
CREATE INDEX idx_citation_date ON citation(created_at);

-- ============================================
-- 视图：论文被引次数统计
-- ============================================
CREATE VIEW v_paper_citation_stats AS
SELECT 
    p.id AS paper_id,
    p.title,
    p.publication_year,
    p.total_citations,
    p.total_references,
    COUNT(DISTINCT c.id) AS actual_citation_count
FROM paper p
LEFT JOIN citation c ON p.id = c.cited_paper_id
GROUP BY p.id;

-- ============================================
-- 视图：作者发文统计
-- ============================================
CREATE VIEW v_author_publication_stats AS
SELECT 
    a.id AS author_id,
    a.name,
    a.orcid,
    COUNT(DISTINCT pa.paper_id) AS publication_count,
    SUM(p.total_citations) AS total_citations,
    AVG(p.total_citations) AS avg_citations
FROM author a
LEFT JOIN paper_author pa ON a.id = pa.author_id
LEFT JOIN paper p ON pa.paper_id = p.id
GROUP BY a.id;

-- ============================================
-- 视图：机构发文统计
-- ============================================
CREATE VIEW v_institution_publication_stats AS
SELECT 
    i.id AS institution_id,
    i.name,
    i.country,
    COUNT(DISTINCT pi.paper_id) AS publication_count,
    COUNT(DISTINCT ai.author_id) AS author_count,
    SUM(p.total_citations) AS total_citations
FROM institution i
LEFT JOIN paper_institution pi ON i.id = pi.institution_id
LEFT JOIN paper p ON pi.paper_id = p.id
LEFT JOIN author_institution ai ON i.id = ai.institution_id AND ai.is_current = 1
GROUP BY i.id;

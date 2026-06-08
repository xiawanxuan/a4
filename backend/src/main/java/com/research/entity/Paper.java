package com.research.entity;

import lombok.Data;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "paper")
public class Paper {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "title", nullable = false, length = 500)
    private String title;

    @Column(name = "title_en", length = 500)
    private String titleEn;

    @Column(name = "abstract", columnDefinition = "TEXT")
    private String abstractText;

    @Column(name = "keywords", columnDefinition = "TEXT")
    private String keywords;

    @Column(name = "doi", length = 100)
    private String doi;

    @Column(name = "pmid", length = 20)
    private String pmid;

    @Column(name = "arxiv_id", length = 50)
    private String arxivId;

    @Column(name = "url", length = 500)
    private String url;

    @Column(name = "pdf_url", length = 500)
    private String pdfUrl;

    @Column(name = "journal_id")
    private Long journalId;

    @Column(name = "volume", length = 50)
    private String volume;

    @Column(name = "issue", length = 50)
    private String issue;

    @Column(name = "pages", length = 50)
    private String pages;

    @Column(name = "publication_date")
    private LocalDate publicationDate;

    @Column(name = "publication_year")
    private Integer publicationYear;

    @Column(name = "language", length = 20)
    private String language;

    @Column(name = "document_type", length = 50)
    private String documentType;

    @Column(name = "total_citations")
    private Integer totalCitations;

    @Column(name = "total_references")
    private Integer totalReferences;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}

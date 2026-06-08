package com.research.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
public class PaperDTO {

    private Long id;

    private String title;

    private String titleEn;

    private String abstractText;

    private String keywords;

    private String doi;

    private String pmid;

    private String arxivId;

    private String url;

    private String pdfUrl;

    private Long journalId;

    private String journalName;

    private BigDecimal impactFactor;

    private String volume;

    private String issue;

    private String pages;

    private LocalDate publicationDate;

    private Integer publicationYear;

    private String language;

    private String documentType;

    private Integer totalCitations;

    private Integer totalReferences;

    private List<AuthorDTO> authors;

    private List<InstitutionDTO> institutions;
}

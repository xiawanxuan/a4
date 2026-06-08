package com.research.dto;

import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class PaperImportDTO {

    private String title;

    private String titleEn;

    private String abstractText;

    private String keywords;

    private String doi;

    private String pmid;

    private String arxivId;

    private String url;

    private String pdfUrl;

    private String journalName;

    private String issn;

    private String volume;

    private String issue;

    private String pages;

    private LocalDate publicationDate;

    private Integer publicationYear;

    private String language;

    private String documentType;

    private Integer totalCitations;

    private List<String> authors;

    private List<String> institutions;

    private List<String> references;
}

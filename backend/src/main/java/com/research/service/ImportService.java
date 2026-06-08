package com.research.service;

import com.research.dto.ImportResultDTO;
import com.research.dto.PaperImportDTO;
import com.research.entity.*;
import com.research.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ImportService {

    private final PaperRepository paperRepository;
    private final AuthorRepository authorRepository;
    private final InstitutionRepository institutionRepository;
    private final JournalRepository journalRepository;
    private final CitationRepository citationRepository;
    private final PaperAuthorRepository paperAuthorRepository;
    private final PaperInstitutionRepository paperInstitutionRepository;
    private final BibTeXParserService bibTeXParserService;
    private final RisParserService risParserService;

    @Transactional
    public ImportResultDTO importPapersFromJson(List<PaperImportDTO> paperDTOs) {
        ImportResultDTO result = new ImportResultDTO();
        result.setSuccessCount(0);
        result.setFailCount(0);
        List<ImportResultDTO.ErrorDetail> errors = new ArrayList<>();

        for (int i = 0; i < paperDTOs.size(); i++) {
            try {
                PaperImportDTO dto = paperDTOs.get(i);
                importSinglePaper(dto);
                result.setSuccessCount(result.getSuccessCount() + 1);
            } catch (Exception e) {
                result.setFailCount(result.getFailCount() + 1);
                ImportResultDTO.ErrorDetail error = new ImportResultDTO.ErrorDetail();
                error.setRowIndex(i);
                error.setMessage(e.getMessage());
                errors.add(error);
            }
        }

        result.setErrors(errors);
        return result;
    }

    @Transactional
    public ImportResultDTO importPapersFromCsv(MultipartFile file) {
        ImportResultDTO result = new ImportResultDTO();
        result.setSuccessCount(0);
        result.setFailCount(0);
        List<ImportResultDTO.ErrorDetail> errors = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8))) {

            String headerLine = reader.readLine();
            if (headerLine == null) {
                throw new IllegalArgumentException("CSV文件为空");
            }

            String[] headers = parseCsvLine(headerLine);
            Map<String, Integer> headerIndex = new HashMap<>();
            for (int i = 0; i < headers.length; i++) {
                headerIndex.put(headers[i].trim().toLowerCase(), i);
            }

            String line;
            int rowIndex = 0;
            while ((line = reader.readLine()) != null) {
                rowIndex++;
                try {
                    String[] values = parseCsvLine(line);
                    PaperImportDTO dto = mapCsvToDto(values, headerIndex);
                    importSinglePaper(dto);
                    result.setSuccessCount(result.getSuccessCount() + 1);
                } catch (Exception e) {
                    result.setFailCount(result.getFailCount() + 1);
                    ImportResultDTO.ErrorDetail error = new ImportResultDTO.ErrorDetail();
                    error.setRowIndex(rowIndex);
                    error.setMessage(e.getMessage());
                    errors.add(error);
                }
            }
        } catch (Exception e) {
            result.setFailCount(result.getFailCount() + 1);
            ImportResultDTO.ErrorDetail error = new ImportResultDTO.ErrorDetail();
            error.setRowIndex(-1);
            error.setMessage("文件读取失败: " + e.getMessage());
            errors.add(error);
        }

        result.setErrors(errors);
        return result;
    }

    @Transactional
    public ImportResultDTO importCitations(List<Map<String, Object>> citationList) {
        ImportResultDTO result = new ImportResultDTO();
        result.setSuccessCount(0);
        result.setFailCount(0);
        List<ImportResultDTO.ErrorDetail> errors = new ArrayList<>();

        for (int i = 0; i < citationList.size(); i++) {
            try {
                Map<String, Object> citationData = citationList.get(i);
                String citingDoi = (String) citationData.get("citingDoi");
                String citedDoi = (String) citationData.get("citedDoi");

                if (citingDoi == null || citedDoi == null) {
                    throw new IllegalArgumentException("引用DOI不能为空");
                }

                Paper citingPaper = paperRepository.findByDoi(citingDoi)
                        .orElseThrow(() -> new IllegalArgumentException("施引论文不存在: " + citingDoi));
                Paper citedPaper = paperRepository.findByDoi(citedDoi)
                        .orElseThrow(() -> new IllegalArgumentException("被引论文不存在: " + citedDoi));

                Citation citation = new Citation();
                citation.setCitingPaperId(citingPaper.getId());
                citation.setCitedPaperId(citedPaper.getId());
                citation.setCitationContext((String) citationData.get("context"));
                citationRepository.save(citation);

                result.setSuccessCount(result.getSuccessCount() + 1);
            } catch (Exception e) {
                result.setFailCount(result.getFailCount() + 1);
                ImportResultDTO.ErrorDetail error = new ImportResultDTO.ErrorDetail();
                error.setRowIndex(i);
                error.setMessage(e.getMessage());
                errors.add(error);
            }
        }

        result.setErrors(errors);
        return result;
    }

    private void importSinglePaper(PaperImportDTO dto) {
        if (dto.getDoi() != null && !dto.getDoi().isEmpty()) {
            Optional<Paper> existingPaper = paperRepository.findByDoi(dto.getDoi());
            if (existingPaper.isPresent()) {
                throw new IllegalArgumentException("论文已存在 (DOI: " + dto.getDoi() + ")");
            }
        }

        if (dto.getTitle() == null || dto.getTitle().isEmpty()) {
            throw new IllegalArgumentException("论文标题不能为空");
        }

        Journal journal = null;
        if (dto.getJournalName() != null && !dto.getJournalName().isEmpty()) {
            journal = journalRepository.findByName(dto.getJournalName())
                    .orElseGet(() -> {
                        Journal newJournal = new Journal();
                        newJournal.setName(dto.getJournalName());
                        newJournal.setIssn(dto.getIssn());
                        newJournal.setType("journal");
                        return journalRepository.save(newJournal);
                    });
        }

        Paper paper = new Paper();
        paper.setTitle(dto.getTitle());
        paper.setTitleEn(dto.getTitleEn());
        paper.setAbstractText(dto.getAbstractText());
        paper.setKeywords(dto.getKeywords());
        paper.setDoi(dto.getDoi());
        paper.setPmid(dto.getPmid());
        paper.setArxivId(dto.getArxivId());
        paper.setUrl(dto.getUrl());
        paper.setPdfUrl(dto.getPdfUrl());
        if (journal != null) {
            paper.setJournalId(journal.getId());
        }
        paper.setVolume(dto.getVolume());
        paper.setIssue(dto.getIssue());
        paper.setPages(dto.getPages());
        paper.setPublicationDate(dto.getPublicationDate());
        paper.setPublicationYear(dto.getPublicationYear());
        paper.setLanguage(dto.getLanguage());
        paper.setDocumentType(dto.getDocumentType());
        paper.setTotalCitations(dto.getTotalCitations());
        if (dto.getReferences() != null) {
            paper.setTotalReferences(dto.getReferences().size());
        }
        paper = paperRepository.save(paper);

        List<String> authorNames = dto.getAuthors();
        if (authorNames != null && !authorNames.isEmpty()) {
            int order = 1;
            for (String authorName : authorNames) {
                Author author = findOrCreateAuthor(authorName);
                PaperAuthor paperAuthor = new PaperAuthor();
                paperAuthor.setPaperId(paper.getId());
                paperAuthor.setAuthorId(author.getId());
                paperAuthor.setAuthorOrder(order++);
                paperAuthorRepository.save(paperAuthor);
            }
        }

        List<String> institutionNames = dto.getInstitutions();
        if (institutionNames != null && !institutionNames.isEmpty()) {
            int order = 1;
            for (String instName : institutionNames) {
                Institution institution = findOrCreateInstitution(instName);
                PaperInstitution paperInstitution = new PaperInstitution();
                paperInstitution.setPaperId(paper.getId());
                paperInstitution.setInstitutionId(institution.getId());
                paperInstitution.setAffiliationOrder(order++);
                paperInstitutionRepository.save(paperInstitution);
            }
        }
    }

    private Author findOrCreateAuthor(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("作者姓名不能为空");
        }

        List<Author> authors = authorRepository.findAll();
        for (Author author : authors) {
            if (author.getName().equalsIgnoreCase(name.trim())) {
                return author;
            }
        }

        Author newAuthor = new Author();
        newAuthor.setName(name.trim());
        return authorRepository.save(newAuthor);
    }

    private Institution findOrCreateInstitution(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("机构名称不能为空");
        }

        List<Institution> institutions = institutionRepository.findAll();
        for (Institution inst : institutions) {
            if (inst.getName().equalsIgnoreCase(name.trim())) {
                return inst;
            }
        }

        Institution newInst = new Institution();
        newInst.setName(name.trim());
        return institutionRepository.save(newInst);
    }

    private String[] parseCsvLine(String line) {
        List<String> values = new ArrayList<>();
        boolean inQuotes = false;
        StringBuilder current = new StringBuilder();

        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);

            if (c == '"') {
                if (inQuotes && i + 1 < line.length() && line.charAt(i + 1) == '"') {
                    current.append('"');
                    i++;
                } else {
                    inQuotes = !inQuotes;
                }
            } else if (c == ',' && !inQuotes) {
                values.add(current.toString());
                current = new StringBuilder();
            } else {
                current.append(c);
            }
        }
        values.add(current.toString());

        return values.toArray(new String[0]);
    }

    private PaperImportDTO mapCsvToDto(String[] values, Map<String, Integer> headerIndex) {
        PaperImportDTO dto = new PaperImportDTO();

        dto.setTitle(getValue(values, headerIndex, "title"));
        dto.setTitleEn(getValue(values, headerIndex, "titleen", "title_en"));
        dto.setAbstractText(getValue(values, headerIndex, "abstract", "abstracttext"));
        dto.setKeywords(getValue(values, headerIndex, "keywords"));
        dto.setDoi(getValue(values, headerIndex, "doi"));
        dto.setPmid(getValue(values, headerIndex, "pmid"));
        dto.setArxivId(getValue(values, headerIndex, "arxivid", "arxiv_id"));
        dto.setUrl(getValue(values, headerIndex, "url"));
        dto.setPdfUrl(getValue(values, headerIndex, "pdfurl", "pdf_url"));
        dto.setJournalName(getValue(values, headerIndex, "journal", "journalname", "journal_name"));
        dto.setIssn(getValue(values, headerIndex, "issn"));
        dto.setVolume(getValue(values, headerIndex, "volume"));
        dto.setIssue(getValue(values, headerIndex, "issue"));
        dto.setPages(getValue(values, headerIndex, "pages"));

        String yearStr = getValue(values, headerIndex, "year", "publicationyear", "publication_year");
        if (yearStr != null && !yearStr.isEmpty()) {
            try {
                dto.setPublicationYear(Integer.parseInt(yearStr.trim()));
            } catch (NumberFormatException ignored) {
            }
        }

        String dateStr = getValue(values, headerIndex, "date", "publicationdate", "publication_date");
        if (dateStr != null && !dateStr.isEmpty()) {
            try {
                dto.setPublicationDate(LocalDate.parse(dateStr.trim(), DateTimeFormatter.ISO_LOCAL_DATE));
            } catch (Exception ignored) {
            }
        }

        dto.setLanguage(getValue(values, headerIndex, "language"));
        dto.setDocumentType(getValue(values, headerIndex, "type", "documenttype", "document_type"));

        String citationsStr = getValue(values, headerIndex, "citations", "totalcitations", "total_citations");
        if (citationsStr != null && !citationsStr.isEmpty()) {
            try {
                dto.setTotalCitations(Integer.parseInt(citationsStr.trim()));
            } catch (NumberFormatException ignored) {
            }
        }

        String authorsStr = getValue(values, headerIndex, "authors", "author");
        if (authorsStr != null && !authorsStr.isEmpty()) {
            List<String> authors = Arrays.stream(authorsStr.split("[;；]"))
                    .map(String::trim)
                    .filter(a -> !a.isEmpty())
                    .collect(Collectors.toList());
            dto.setAuthors(authors);
        }

        String institutionsStr = getValue(values, headerIndex, "institutions", "institution", "affiliations", "affiliation");
        if (institutionsStr != null && !institutionsStr.isEmpty()) {
            List<String> institutions = Arrays.stream(institutionsStr.split("[;；]"))
                    .map(String::trim)
                    .filter(i -> !i.isEmpty())
                    .collect(Collectors.toList());
            dto.setInstitutions(institutions);
        }

        String referencesStr = getValue(values, headerIndex, "references", "refs");
        if (referencesStr != null && !referencesStr.isEmpty()) {
            List<String> references = Arrays.stream(referencesStr.split("[;；]"))
                    .map(String::trim)
                    .filter(r -> !r.isEmpty())
                    .collect(Collectors.toList());
            dto.setReferences(references);
        }

        return dto;
    }

    private String getValue(String[] values, Map<String, Integer> headerIndex, String... keys) {
        for (String key : keys) {
            Integer index = headerIndex.get(key.toLowerCase());
            if (index != null && index < values.length) {
                return values[index].trim();
            }
        }
        return null;
    }

    @Transactional
    public ImportResultDTO importPapersFromBibTeX(MultipartFile file) {
        ImportResultDTO result = new ImportResultDTO();
        result.setSuccessCount(0);
        result.setFailCount(0);
        List<ImportResultDTO.ErrorDetail> errors = new ArrayList<>();

        try {
            String content = new String(file.getBytes(), StandardCharsets.UTF_8);
            List<PaperImportDTO> paperDTOs = bibTeXParserService.parse(content);

            if (paperDTOs.isEmpty()) {
                throw new IllegalArgumentException("BibTeX文件中没有解析到有效条目");
            }

            for (int i = 0; i < paperDTOs.size(); i++) {
                try {
                    importSinglePaper(paperDTOs.get(i));
                    result.setSuccessCount(result.getSuccessCount() + 1);
                } catch (Exception e) {
                    result.setFailCount(result.getFailCount() + 1);
                    ImportResultDTO.ErrorDetail error = new ImportResultDTO.ErrorDetail();
                    error.setRowIndex(i + 1);
                    error.setMessage(e.getMessage());
                    errors.add(error);
                }
            }
        } catch (Exception e) {
            result.setFailCount(result.getFailCount() + 1);
            ImportResultDTO.ErrorDetail error = new ImportResultDTO.ErrorDetail();
            error.setRowIndex(-1);
            error.setMessage("文件读取或解析失败: " + e.getMessage());
            errors.add(error);
        }

        result.setErrors(errors);
        return result;
    }

    @Transactional
    public ImportResultDTO importPapersFromRis(MultipartFile file) {
        ImportResultDTO result = new ImportResultDTO();
        result.setSuccessCount(0);
        result.setFailCount(0);
        List<ImportResultDTO.ErrorDetail> errors = new ArrayList<>();

        try {
            String content = new String(file.getBytes(), StandardCharsets.UTF_8);
            List<PaperImportDTO> paperDTOs = risParserService.parse(content);

            if (paperDTOs.isEmpty()) {
                throw new IllegalArgumentException("RIS文件中没有解析到有效条目");
            }

            for (int i = 0; i < paperDTOs.size(); i++) {
                try {
                    importSinglePaper(paperDTOs.get(i));
                    result.setSuccessCount(result.getSuccessCount() + 1);
                } catch (Exception e) {
                    result.setFailCount(result.getFailCount() + 1);
                    ImportResultDTO.ErrorDetail error = new ImportResultDTO.ErrorDetail();
                    error.setRowIndex(i + 1);
                    error.setMessage(e.getMessage());
                    errors.add(error);
                }
            }
        } catch (Exception e) {
            result.setFailCount(result.getFailCount() + 1);
            ImportResultDTO.ErrorDetail error = new ImportResultDTO.ErrorDetail();
            error.setRowIndex(-1);
            error.setMessage("文件读取或解析失败: " + e.getMessage());
            errors.add(error);
        }

        result.setErrors(errors);
        return result;
    }
}

package com.research.service;

import com.research.dto.*;
import com.research.entity.*;
import com.research.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import jakarta.persistence.criteria.*;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PaperService {

    private final PaperRepository paperRepository;
    private final PaperAuthorRepository paperAuthorRepository;
    private final PaperInstitutionRepository paperInstitutionRepository;
    private final AuthorRepository authorRepository;
    private final InstitutionRepository institutionRepository;
    private final JournalRepository journalRepository;
    private final CitationRepository citationRepository;

    @Transactional(readOnly = true)
    public PageResult<PaperDTO> queryPapers(PaperQueryDTO queryDTO) {
        int pageNum = queryDTO.getPageNum() != null && queryDTO.getPageNum() > 0 ? queryDTO.getPageNum() : 1;
        int pageSize = queryDTO.getPageSize() != null && queryDTO.getPageSize() > 0 ? queryDTO.getPageSize() : 10;

        Sort sort = buildSort(queryDTO.getSortBy(), queryDTO.getSortOrder());
        Pageable pageable = PageRequest.of(pageNum - 1, pageSize, sort);

        Specification<Paper> spec = buildSpecification(queryDTO);
        Page<Paper> paperPage = paperRepository.findAll(spec, pageable);

        List<PaperDTO> paperDTOList = paperPage.getContent().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());

        PageResult<PaperDTO> pageResult = new PageResult<>();
        pageResult.setTotal(paperPage.getTotalElements());
        pageResult.setList(paperDTOList);
        pageResult.setPageNum(pageNum);
        pageResult.setPageSize(pageSize);

        return pageResult;
    }

    @Transactional(readOnly = true)
    public PaperDTO getPaperById(Long id) {
        if (id == null) {
            return null;
        }
        return paperRepository.findById(id)
                .map(this::convertToDTO)
                .orElse(null);
    }

    @Transactional(readOnly = true)
    public PaperDTO getPaperByDoi(String doi) {
        if (!StringUtils.hasText(doi)) {
            return null;
        }
        return paperRepository.findByDoi(doi)
                .map(this::convertToDTO)
                .orElse(null);
    }

    @Transactional
    public PaperDTO createPaper(PaperDTO paperDTO) {
        Paper paper = new Paper();
        BeanUtils.copyProperties(paperDTO, paper);
        if (paper.getTotalCitations() == null) {
            paper.setTotalCitations(0);
        }
        if (paper.getTotalReferences() == null) {
            paper.setTotalReferences(0);
        }
        paper.setCreatedAt(LocalDateTime.now());
        paper.setUpdatedAt(LocalDateTime.now());

        Paper savedPaper = paperRepository.save(paper);

        savePaperAuthors(savedPaper.getId(), paperDTO.getAuthors());
        savePaperInstitutions(savedPaper.getId(), paperDTO.getInstitutions());

        return convertToDTO(savedPaper);
    }

    @Transactional
    public PaperDTO updatePaper(Long id, PaperDTO paperDTO) {
        if (id == null) {
            return null;
        }

        Paper paper = paperRepository.findById(id)
                .orElse(null);
        if (paper == null) {
            return null;
        }

        if (paperDTO.getTitle() != null) {
            paper.setTitle(paperDTO.getTitle());
        }
        if (paperDTO.getTitleEn() != null) {
            paper.setTitleEn(paperDTO.getTitleEn());
        }
        if (paperDTO.getAbstractText() != null) {
            paper.setAbstractText(paperDTO.getAbstractText());
        }
        if (paperDTO.getKeywords() != null) {
            paper.setKeywords(paperDTO.getKeywords());
        }
        if (paperDTO.getDoi() != null) {
            paper.setDoi(paperDTO.getDoi());
        }
        if (paperDTO.getPmid() != null) {
            paper.setPmid(paperDTO.getPmid());
        }
        if (paperDTO.getArxivId() != null) {
            paper.setArxivId(paperDTO.getArxivId());
        }
        if (paperDTO.getUrl() != null) {
            paper.setUrl(paperDTO.getUrl());
        }
        if (paperDTO.getPdfUrl() != null) {
            paper.setPdfUrl(paperDTO.getPdfUrl());
        }
        if (paperDTO.getJournalId() != null) {
            paper.setJournalId(paperDTO.getJournalId());
        }
        if (paperDTO.getVolume() != null) {
            paper.setVolume(paperDTO.getVolume());
        }
        if (paperDTO.getIssue() != null) {
            paper.setIssue(paperDTO.getIssue());
        }
        if (paperDTO.getPages() != null) {
            paper.setPages(paperDTO.getPages());
        }
        if (paperDTO.getPublicationDate() != null) {
            paper.setPublicationDate(paperDTO.getPublicationDate());
        }
        if (paperDTO.getPublicationYear() != null) {
            paper.setPublicationYear(paperDTO.getPublicationYear());
        }
        if (paperDTO.getLanguage() != null) {
            paper.setLanguage(paperDTO.getLanguage());
        }
        if (paperDTO.getDocumentType() != null) {
            paper.setDocumentType(paperDTO.getDocumentType());
        }
        if (paperDTO.getTotalCitations() != null) {
            paper.setTotalCitations(paperDTO.getTotalCitations());
        }
        if (paperDTO.getTotalReferences() != null) {
            paper.setTotalReferences(paperDTO.getTotalReferences());
        }

        paper.setUpdatedAt(LocalDateTime.now());

        Paper updatedPaper = paperRepository.save(paper);

        if (paperDTO.getAuthors() != null) {
            paperAuthorRepository.deleteAll(paperAuthorRepository.findByPaperIdOrderByAuthorOrderAsc(id));
            savePaperAuthors(id, paperDTO.getAuthors());
        }

        if (paperDTO.getInstitutions() != null) {
            paperInstitutionRepository.deleteAll(paperInstitutionRepository.findByPaperId(id));
            savePaperInstitutions(id, paperDTO.getInstitutions());
        }

        return convertToDTO(updatedPaper);
    }

    @Transactional
    public boolean deletePaper(Long id) {
        if (id == null || !paperRepository.existsById(id)) {
            return false;
        }

        paperAuthorRepository.deleteAll(paperAuthorRepository.findByPaperIdOrderByAuthorOrderAsc(id));
        paperInstitutionRepository.deleteAll(paperInstitutionRepository.findByPaperId(id));
        paperRepository.deleteById(id);

        return true;
    }

    @Transactional(readOnly = true)
    public List<PaperDTO> getReferences(Long paperId) {
        if (paperId == null) {
            return Collections.emptyList();
        }

        List<Citation> citations = citationRepository.findByCitingPaperId(paperId, Pageable.unpaged()).getContent();

        if (citations.isEmpty()) {
            return Collections.emptyList();
        }

        List<Long> citedPaperIds = citations.stream()
                .map(Citation::getCitedPaperId)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        if (citedPaperIds.isEmpty()) {
            return Collections.emptyList();
        }

        List<Paper> papers = paperRepository.findAllById(citedPaperIds);
        return papers.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<PaperDTO> getCitations(Long paperId) {
        if (paperId == null) {
            return Collections.emptyList();
        }

        List<Citation> citations = citationRepository.findByCitedPaperId(paperId, Pageable.unpaged()).getContent();

        if (citations.isEmpty()) {
            return Collections.emptyList();
        }

        List<Long> citingPaperIds = citations.stream()
                .map(Citation::getCitingPaperId)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        if (citingPaperIds.isEmpty()) {
            return Collections.emptyList();
        }

        List<Paper> papers = paperRepository.findAllById(citingPaperIds);
        return papers.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Map<String, Object> getStatistics() {
        Map<String, Object> stats = new HashMap<>();

        long totalPapers = paperRepository.count();
        stats.put("totalPapers", totalPapers);

        List<Paper> allPapers = paperRepository.findAll();
        int totalCitations = allPapers.stream()
                .mapToInt(p -> p.getTotalCitations() != null ? p.getTotalCitations() : 0)
                .sum();
        stats.put("totalCitations", totalCitations);

        int totalReferences = allPapers.stream()
                .mapToInt(p -> p.getTotalReferences() != null ? p.getTotalReferences() : 0)
                .sum();
        stats.put("totalReferences", totalReferences);

        long totalAuthors = authorRepository.count();
        stats.put("totalAuthors", totalAuthors);

        long totalInstitutions = institutionRepository.count();
        stats.put("totalInstitutions", totalInstitutions);

        long totalJournals = journalRepository.count();
        stats.put("totalJournals", totalJournals);

        return stats;
    }

    private Specification<Paper> buildSpecification(PaperQueryDTO queryDTO) {
        return (Root<Paper> root, CriteriaQuery<?> query, CriteriaBuilder cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (StringUtils.hasText(queryDTO.getKeyword())) {
                String keyword = "%" + queryDTO.getKeyword().trim() + "%";
                predicates.add(cb.or(
                        cb.like(root.get("title"), keyword),
                        cb.like(root.get("titleEn"), keyword),
                        cb.like(root.get("keywords"), keyword),
                        cb.like(root.get("abstractText"), keyword)
                ));
            }

            if (queryDTO.getAuthorId() != null) {
                Subquery<Long> authorSubquery = query.subquery(Long.class);
                Root<PaperAuthor> paperAuthor = authorSubquery.from(PaperAuthor.class);
                authorSubquery.select(paperAuthor.get("paperId"))
                        .where(cb.equal(paperAuthor.get("authorId"), queryDTO.getAuthorId()));
                predicates.add(root.get("id").in(authorSubquery));
            }

            if (queryDTO.getInstitutionId() != null) {
                Subquery<Long> institutionSubquery = query.subquery(Long.class);
                Root<PaperInstitution> paperInstitution = institutionSubquery.from(PaperInstitution.class);
                institutionSubquery.select(paperInstitution.get("paperId"))
                        .where(cb.equal(paperInstitution.get("institutionId"), queryDTO.getInstitutionId()));
                predicates.add(root.get("id").in(institutionSubquery));
            }

            if (queryDTO.getYearStart() != null && queryDTO.getYearEnd() != null) {
                predicates.add(cb.between(root.get("publicationYear"), queryDTO.getYearStart(), queryDTO.getYearEnd()));
            } else if (queryDTO.getYearStart() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("publicationYear"), queryDTO.getYearStart()));
            } else if (queryDTO.getYearEnd() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("publicationYear"), queryDTO.getYearEnd()));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

    private Sort buildSort(String sortBy, String sortOrder) {
        Sort.Direction direction = Sort.Direction.DESC;
        if ("asc".equalsIgnoreCase(sortOrder)) {
            direction = Sort.Direction.ASC;
        }

        if (!StringUtils.hasText(sortBy)) {
            return Sort.by(direction, "id");
        }

        switch (sortBy) {
            case "title":
                return Sort.by(direction, "title");
            case "publicationDate":
                return Sort.by(direction, "publicationDate");
            case "publicationYear":
                return Sort.by(direction, "publicationYear");
            case "totalCitations":
                return Sort.by(direction, "totalCitations");
            case "createdAt":
                return Sort.by(direction, "createdAt");
            default:
                return Sort.by(direction, "id");
        }
    }

    private PaperDTO convertToDTO(Paper paper) {
        if (paper == null) {
            return null;
        }

        PaperDTO dto = new PaperDTO();
        BeanUtils.copyProperties(paper, dto);

        if (paper.getJournalId() != null) {
            journalRepository.findById(paper.getJournalId()).ifPresent(journal -> {
                dto.setJournalName(journal.getName());
                dto.setImpactFactor(journal.getImpactFactor());
            });
        }

        List<PaperAuthor> paperAuthors = paperAuthorRepository.findByPaperIdOrderByAuthorOrderAsc(paper.getId());
        List<AuthorDTO> authorDTOList = new ArrayList<>();
        for (PaperAuthor pa : paperAuthors) {
            authorRepository.findById(pa.getAuthorId()).ifPresent(author ->
                    authorDTOList.add(convertAuthorToDTO(author))
            );
        }
        dto.setAuthors(authorDTOList);

        List<PaperInstitution> paperInstitutions = paperInstitutionRepository.findByPaperId(paper.getId());
        List<InstitutionDTO> institutionDTOList = new ArrayList<>();
        for (PaperInstitution pi : paperInstitutions) {
            institutionRepository.findById(pi.getInstitutionId()).ifPresent(institution ->
                    institutionDTOList.add(convertInstitutionToDTO(institution))
            );
        }
        dto.setInstitutions(institutionDTOList);

        return dto;
    }

    private AuthorDTO convertAuthorToDTO(Author author) {
        if (author == null) {
            return null;
        }
        AuthorDTO dto = new AuthorDTO();
        BeanUtils.copyProperties(author, dto);
        return dto;
    }

    private InstitutionDTO convertInstitutionToDTO(Institution institution) {
        if (institution == null) {
            return null;
        }
        InstitutionDTO dto = new InstitutionDTO();
        BeanUtils.copyProperties(institution, dto);
        return dto;
    }

    private void savePaperAuthors(Long paperId, List<AuthorDTO> authorDTOs) {
        if (authorDTOs == null || authorDTOs.isEmpty()) {
            return;
        }

        int order = 1;
        for (AuthorDTO authorDTO : authorDTOs) {
            if (authorDTO.getId() == null) {
                continue;
            }

            PaperAuthor paperAuthor = new PaperAuthor();
            paperAuthor.setPaperId(paperId);
            paperAuthor.setAuthorId(authorDTO.getId());
            paperAuthor.setAuthorOrder(order++);
            paperAuthor.setCreatedAt(LocalDateTime.now());
            paperAuthorRepository.save(paperAuthor);
        }
    }

    private void savePaperInstitutions(Long paperId, List<InstitutionDTO> institutionDTOs) {
        if (institutionDTOs == null || institutionDTOs.isEmpty()) {
            return;
        }

        int order = 1;
        for (InstitutionDTO institutionDTO : institutionDTOs) {
            if (institutionDTO.getId() == null) {
                continue;
            }

            PaperInstitution paperInstitution = new PaperInstitution();
            paperInstitution.setPaperId(paperId);
            paperInstitution.setInstitutionId(institutionDTO.getId());
            paperInstitution.setAffiliationOrder(order++);
            paperInstitution.setCreatedAt(LocalDateTime.now());
            paperInstitutionRepository.save(paperInstitution);
        }
    }
}

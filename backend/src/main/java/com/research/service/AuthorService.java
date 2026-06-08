package com.research.service;

import com.research.dto.AuthorAnalysisDTO;
import com.research.dto.AuthorDTO;
import com.research.dto.PageResult;
import com.research.dto.PaperDTO;
import com.research.entity.Author;
import com.research.entity.AuthorInstitution;
import com.research.entity.Institution;
import com.research.entity.Paper;
import com.research.entity.PaperAuthor;
import com.research.repository.AuthorInstitutionRepository;
import com.research.repository.AuthorRepository;
import com.research.repository.InstitutionRepository;
import com.research.repository.PaperAuthorRepository;
import com.research.repository.PaperRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuthorService {

    private final AuthorRepository authorRepository;
    private final InstitutionRepository institutionRepository;
    private final PaperRepository paperRepository;
    private final PaperAuthorRepository paperAuthorRepository;
    private final AuthorInstitutionRepository authorInstitutionRepository;

    @Transactional(readOnly = true)
    public PageResult<AuthorDTO> listAuthors(String name, Long institutionId, Pageable pageable) {
        Page<Author> page;
        if (institutionId != null) {
            page = authorRepository.findByInstitutionId(institutionId, pageable);
        } else if (name != null && !name.isEmpty()) {
            page = authorRepository.findByNameContaining(name, pageable);
        } else {
            page = authorRepository.findAll(pageable);
        }
        List<AuthorDTO> dtoList = page.getContent().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        PageResult<AuthorDTO> result = new PageResult<>();
        result.setTotal(page.getTotalElements());
        result.setList(dtoList);
        result.setPageNum(pageable.getPageNumber() + 1);
        result.setPageSize(pageable.getPageSize());
        return result;
    }

    @Transactional(readOnly = true)
    public AuthorDTO getAuthorById(Long id) {
        Author author = authorRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("作者不存在: " + id));
        AuthorDTO dto = convertToDTO(author);

        List<AuthorInstitution> authorInstitutions = authorInstitutionRepository.findByAuthorId(id);
        if (!authorInstitutions.isEmpty()) {
            Long instId = authorInstitutions.get(0).getInstitutionId();
            institutionRepository.findById(instId).ifPresent(inst -> {
                dto.setAffiliationId(instId);
                dto.setAffiliationName(inst.getName());
            });
        }

        Page<Paper> papers = paperRepository.findByAuthorId(id, Pageable.unpaged());
        dto.setTotalPublications((int) papers.getTotalElements());

        int totalCitations = papers.getContent().stream()
                .mapToInt(p -> p.getTotalCitations() != null ? p.getTotalCitations() : 0)
                .sum();
        dto.setTotalCitations(totalCitations);

        List<String> researchAreas = extractResearchAreas(papers.getContent());
        dto.setResearchAreas(researchAreas);

        return dto;
    }

    @Transactional(readOnly = true)
    public AuthorDTO getAuthorByOrcid(String orcid) {
        return authorRepository.findByOrcid(orcid)
                .map(this::convertToDTO)
                .orElse(null);
    }

    @Transactional
    public AuthorDTO createAuthor(AuthorDTO authorDTO) {
        Author author = new Author();
        BeanUtils.copyProperties(authorDTO, author);
        author.setCreatedAt(LocalDateTime.now());
        author.setUpdatedAt(LocalDateTime.now());
        Author saved = authorRepository.save(author);
        return convertToDTO(saved);
    }

    @Transactional
    public AuthorDTO updateAuthor(Long id, AuthorDTO authorDTO) {
        Author author = authorRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("作者不存在: " + id));

        if (authorDTO.getName() != null) {
            author.setName(authorDTO.getName());
        }
        if (authorDTO.getNameEn() != null) {
            author.setNameEn(authorDTO.getNameEn());
        }
        if (authorDTO.getOrcid() != null) {
            author.setOrcid(authorDTO.getOrcid());
        }
        if (authorDTO.getEmail() != null) {
            author.setEmail(authorDTO.getEmail());
        }
        if (authorDTO.getHomepage() != null) {
            author.setHomepage(authorDTO.getHomepage());
        }
        if (authorDTO.getBiography() != null) {
            author.setBiography(authorDTO.getBiography());
        }
        if (authorDTO.getAffiliationId() != null) {
            author.setAffiliationId(authorDTO.getAffiliationId());
        }
        author.setUpdatedAt(LocalDateTime.now());
        Author saved = authorRepository.save(author);
        return convertToDTO(saved);
    }

    @Transactional
    public void deleteAuthor(Long id) {
        if (!authorRepository.existsById(id)) {
            throw new IllegalArgumentException("作者不存在: " + id);
        }
        authorRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public PageResult<PaperDTO> getAuthorPapers(Long authorId, Pageable pageable) {
        if (!authorRepository.existsById(authorId)) {
            throw new IllegalArgumentException("作者不存在: " + authorId);
        }
        Page<Paper> page = paperRepository.findByAuthorId(authorId, pageable);
        List<PaperDTO> dtoList = page.getContent().stream()
                .map(this::convertPaperToDTO)
                .collect(Collectors.toList());
        PageResult<PaperDTO> result = new PageResult<>();
        result.setTotal(page.getTotalElements());
        result.setList(dtoList);
        result.setPageNum(pageable.getPageNumber() + 1);
        result.setPageSize(pageable.getPageSize());
        return result;
    }

    @Transactional(readOnly = true)
    public AuthorAnalysisDTO getAuthorCollaborationNetwork(Long authorId) {
        if (!authorRepository.existsById(authorId)) {
            throw new IllegalArgumentException("作者不存在: " + authorId);
        }

        AuthorAnalysisDTO analysis = new AuthorAnalysisDTO();

        List<Paper> authorPapers = paperRepository.findByAuthorId(authorId, Pageable.unpaged()).getContent();
        List<Long> paperIds = authorPapers.stream().map(Paper::getId).collect(Collectors.toList());

        Map<Long, Integer> collaboratorCount = new HashMap<>();
        for (Long paperId : paperIds) {
            List<PaperAuthor> paperAuthors = paperAuthorRepository.findByPaperIdOrderByAuthorOrderAsc(paperId);
            for (PaperAuthor pa : paperAuthors) {
                if (!pa.getAuthorId().equals(authorId)) {
                    collaboratorCount.merge(pa.getAuthorId(), 1, Integer::sum);
                }
            }
        }

        List<AuthorDTO> collaborationNodes = new ArrayList<>();
        List<AuthorAnalysisDTO.CollaborationEdge> collaborationEdges = new ArrayList<>();

        for (Map.Entry<Long, Integer> entry : collaboratorCount.entrySet()) {
            authorRepository.findById(entry.getKey()).ifPresent(coAuthor -> {
                collaborationNodes.add(convertToDTO(coAuthor));

                AuthorAnalysisDTO.CollaborationEdge edge = new AuthorAnalysisDTO.CollaborationEdge();
                edge.setSource(authorId);
                edge.setTarget(coAuthor.getId());
                edge.setWeight(entry.getValue());
                collaborationEdges.add(edge);
            });
        }

        analysis.setCollaborationNodes(collaborationNodes);
        analysis.setCollaborationEdges(collaborationEdges);

        return analysis;
    }

    @Transactional(readOnly = true)
    public Map<String, Object> getAuthorStatistics(Long authorId) {
        if (!authorRepository.existsById(authorId)) {
            throw new IllegalArgumentException("作者不存在: " + authorId);
        }

        Map<String, Object> stats = new HashMap<>();

        List<Paper> papers = paperRepository.findByAuthorId(authorId, Pageable.unpaged()).getContent();
        int paperCount = papers.size();
        int totalCitations = papers.stream()
                .mapToInt(p -> p.getTotalCitations() != null ? p.getTotalCitations() : 0)
                .sum();
        double avgCitations = paperCount > 0 ? (double) totalCitations / paperCount : 0;

        Integer hIndex = authorRepository.findById(authorId)
                .map(Author::getHIndex)
                .orElse(null);

        stats.put("totalPublications", paperCount);
        stats.put("totalCitations", totalCitations);
        stats.put("avgCitations", avgCitations);
        stats.put("hIndex", hIndex);

        return stats;
    }

    private AuthorDTO convertToDTO(Author author) {
        AuthorDTO dto = new AuthorDTO();
        BeanUtils.copyProperties(author, dto);
        return dto;
    }

    private PaperDTO convertPaperToDTO(Paper paper) {
        PaperDTO dto = new PaperDTO();
        BeanUtils.copyProperties(paper, dto);
        return dto;
    }

    private List<String> extractResearchAreas(List<Paper> papers) {
        Map<String, Integer> keywordCount = new HashMap<>();
        for (Paper paper : papers) {
            if (paper.getKeywords() != null && !paper.getKeywords().isEmpty()) {
                String[] keywords = paper.getKeywords().split("[,;；，]");
                for (String keyword : keywords) {
                    String trimmed = keyword.trim();
                    if (!trimmed.isEmpty()) {
                        keywordCount.merge(trimmed, 1, Integer::sum);
                    }
                }
            }
        }
        return keywordCount.entrySet().stream()
                .sorted((e1, e2) -> e2.getValue().compareTo(e1.getValue()))
                .limit(10)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }
}

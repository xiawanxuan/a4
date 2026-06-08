package com.research.service;

import com.research.dto.AuthorDTO;
import com.research.dto.InstitutionAnalysisDTO;
import com.research.dto.InstitutionDTO;
import com.research.dto.PageResult;
import com.research.dto.PaperDTO;
import com.research.entity.Author;
import com.research.entity.Institution;
import com.research.entity.Paper;
import com.research.entity.PaperInstitution;
import com.research.repository.AuthorRepository;
import com.research.repository.InstitutionRepository;
import com.research.repository.PaperInstitutionRepository;
import com.research.repository.PaperRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class InstitutionService {

    private final InstitutionRepository institutionRepository;
    private final AuthorRepository authorRepository;
    private final PaperRepository paperRepository;
    private final PaperInstitutionRepository paperInstitutionRepository;

    @Transactional(readOnly = true)
    public PageResult<InstitutionDTO> listInstitutions(String name, String country, Pageable pageable) {
        Page<Institution> page;
        if (name != null && !name.isEmpty()) {
            page = institutionRepository.findByNameContaining(name, pageable);
        } else if (country != null && !country.isEmpty()) {
            page = institutionRepository.findByCountry(country, pageable);
        } else {
            page = institutionRepository.findAll(pageable);
        }
        List<InstitutionDTO> dtoList = page.getContent().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        PageResult<InstitutionDTO> result = new PageResult<>();
        result.setTotal(page.getTotalElements());
        result.setList(dtoList);
        result.setPageNum(pageable.getPageNumber() + 1);
        result.setPageSize(pageable.getPageSize());
        return result;
    }

    @Transactional(readOnly = true)
    public InstitutionDTO getInstitutionById(Long id) {
        Institution institution = institutionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("机构不存在: " + id));
        InstitutionDTO dto = convertToDTO(institution);

        Page<Author> authors = authorRepository.findByInstitutionId(id, Pageable.unpaged());
        dto.setAuthorCount((int) authors.getTotalElements());

        Page<Paper> papers = paperRepository.findByInstitutionId(id, Pageable.unpaged());
        dto.setPaperCount((int) papers.getTotalElements());

        int totalCitations = papers.getContent().stream()
                .mapToInt(p -> p.getTotalCitations() != null ? p.getTotalCitations() : 0)
                .sum();
        dto.setTotalCitations(totalCitations);

        return dto;
    }

    @Transactional
    public InstitutionDTO createInstitution(InstitutionDTO institutionDTO) {
        Institution institution = new Institution();
        BeanUtils.copyProperties(institutionDTO, institution);
        institution.setCreatedAt(LocalDateTime.now());
        institution.setUpdatedAt(LocalDateTime.now());
        Institution saved = institutionRepository.save(institution);
        return convertToDTO(saved);
    }

    @Transactional
    public InstitutionDTO updateInstitution(Long id, InstitutionDTO institutionDTO) {
        Institution institution = institutionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("机构不存在: " + id));

        if (institutionDTO.getName() != null) {
            institution.setName(institutionDTO.getName());
        }
        if (institutionDTO.getNameEn() != null) {
            institution.setNameEn(institutionDTO.getNameEn());
        }
        if (institutionDTO.getCountry() != null) {
            institution.setCountry(institutionDTO.getCountry());
        }
        if (institutionDTO.getCity() != null) {
            institution.setCity(institutionDTO.getCity());
        }
        if (institutionDTO.getDepartment() != null) {
            institution.setDepartment(institutionDTO.getDepartment());
        }
        if (institutionDTO.getType() != null) {
            institution.setType(institutionDTO.getType());
        }
        if (institutionDTO.getDescription() != null) {
            institution.setDescription(institutionDTO.getDescription());
        }
        institution.setUpdatedAt(LocalDateTime.now());
        Institution saved = institutionRepository.save(institution);
        return convertToDTO(saved);
    }

    @Transactional
    public void deleteInstitution(Long id) {
        if (!institutionRepository.existsById(id)) {
            throw new IllegalArgumentException("机构不存在: " + id);
        }
        institutionRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public PageResult<PaperDTO> getInstitutionPapers(Long institutionId, Pageable pageable) {
        if (!institutionRepository.existsById(institutionId)) {
            throw new IllegalArgumentException("机构不存在: " + institutionId);
        }
        Page<Paper> page = paperRepository.findByInstitutionId(institutionId, pageable);
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
    public List<AuthorDTO> getInstitutionAuthors(Long institutionId) {
        if (!institutionRepository.existsById(institutionId)) {
            throw new IllegalArgumentException("机构不存在: " + institutionId);
        }
        Page<Author> page = authorRepository.findByInstitutionId(institutionId, Pageable.unpaged());
        return page.getContent().stream()
                .map(this::convertAuthorToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public InstitutionAnalysisDTO getInstitutionCollaborationNetwork(Long institutionId) {
        if (!institutionRepository.existsById(institutionId)) {
            throw new IllegalArgumentException("机构不存在: " + institutionId);
        }

        InstitutionAnalysisDTO analysis = new InstitutionAnalysisDTO();

        List<Paper> institutionPapers = paperRepository.findByInstitutionId(institutionId, Pageable.unpaged()).getContent();
        List<Long> paperIds = institutionPapers.stream().map(Paper::getId).collect(Collectors.toList());

        Map<Long, Integer> collaboratorCount = new HashMap<>();
        for (Long paperId : paperIds) {
            List<PaperInstitution> paperInstitutions = paperInstitutionRepository.findByPaperId(paperId);
            for (PaperInstitution pi : paperInstitutions) {
                if (!pi.getInstitutionId().equals(institutionId)) {
                    collaboratorCount.merge(pi.getInstitutionId(), 1, Integer::sum);
                }
            }
        }

        List<InstitutionDTO> collaborationNodes = new ArrayList<>();
        List<InstitutionAnalysisDTO.CollaborationEdge> collaborationEdges = new ArrayList<>();

        for (Map.Entry<Long, Integer> entry : collaboratorCount.entrySet()) {
            institutionRepository.findById(entry.getKey()).ifPresent(coInst -> {
                collaborationNodes.add(convertToDTO(coInst));

                InstitutionAnalysisDTO.CollaborationEdge edge = new InstitutionAnalysisDTO.CollaborationEdge();
                edge.setSource(institutionId);
                edge.setTarget(coInst.getId());
                edge.setWeight(entry.getValue());
                collaborationEdges.add(edge);
            });
        }

        analysis.setCollaborationNodes(collaborationNodes);
        analysis.setCollaborationEdges(collaborationEdges);

        return analysis;
    }

    @Transactional(readOnly = true)
    public List<InstitutionDTO> getInstitutionRanking(String sortBy, int limit) {
        List<Institution> allInstitutions = institutionRepository.findAll();
        List<InstitutionDTO> ranking = new ArrayList<>();

        for (Institution inst : allInstitutions) {
            InstitutionDTO dto = convertToDTO(inst);

            Page<Paper> papers = paperRepository.findByInstitutionId(inst.getId(), Pageable.unpaged());
            dto.setPaperCount((int) papers.getTotalElements());

            int totalCitations = papers.getContent().stream()
                    .mapToInt(p -> p.getTotalCitations() != null ? p.getTotalCitations() : 0)
                    .sum();
            dto.setTotalCitations(totalCitations);

            ranking.add(dto);
        }

        if ("citations".equals(sortBy)) {
            ranking.sort(Comparator.comparingInt(
                    (InstitutionDTO dto) -> dto.getTotalCitations() != null ? dto.getTotalCitations() : 0
            ).reversed());
        } else {
            ranking.sort(Comparator.comparingInt(
                    (InstitutionDTO dto) -> dto.getPaperCount() != null ? dto.getPaperCount() : 0
            ).reversed());
        }

        return ranking.stream().limit(limit).collect(Collectors.toList());
    }

    private InstitutionDTO convertToDTO(Institution institution) {
        InstitutionDTO dto = new InstitutionDTO();
        BeanUtils.copyProperties(institution, dto);
        return dto;
    }

    private PaperDTO convertPaperToDTO(Paper paper) {
        PaperDTO dto = new PaperDTO();
        BeanUtils.copyProperties(paper, dto);
        return dto;
    }

    private AuthorDTO convertAuthorToDTO(Author author) {
        AuthorDTO dto = new AuthorDTO();
        BeanUtils.copyProperties(author, dto);
        return dto;
    }
}

package com.research.service;

import com.research.dto.CitationEdge;
import com.research.dto.CitationNetworkDTO;
import com.research.dto.CitationNode;
import com.research.dto.ImportResultDTO;
import com.research.entity.Citation;
import com.research.entity.Paper;
import com.research.repository.CitationRepository;
import com.research.repository.PaperRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CitationService {

    private final CitationRepository citationRepository;
    private final PaperRepository paperRepository;

    @Transactional
    public Citation addCitation(Long citingPaperId, Long citedPaperId, String citationContext) {
        if (citingPaperId.equals(citedPaperId)) {
            throw new IllegalArgumentException("论文不能引用自己");
        }
        if (!paperRepository.existsById(citingPaperId)) {
            throw new IllegalArgumentException("施引论文不存在: " + citingPaperId);
        }
        if (!paperRepository.existsById(citedPaperId)) {
            throw new IllegalArgumentException("被引论文不存在: " + citedPaperId);
        }
        Citation citation = new Citation();
        citation.setCitingPaperId(citingPaperId);
        citation.setCitedPaperId(citedPaperId);
        citation.setCitationContext(citationContext);
        citation.setCreatedAt(LocalDateTime.now());
        return citationRepository.save(citation);
    }

    @Transactional
    public void deleteCitation(Long citationId) {
        if (!citationRepository.existsById(citationId)) {
            throw new IllegalArgumentException("引用关系不存在: " + citationId);
        }
        citationRepository.deleteById(citationId);
    }

    @Transactional(readOnly = true)
    public List<Paper> getReferences(Long paperId) {
        List<Citation> citations = citationRepository.findByCitingPaperId(paperId, org.springframework.data.domain.Pageable.unpaged()).getContent();
        List<Long> citedPaperIds = citations.stream()
                .map(Citation::getCitedPaperId)
                .collect(Collectors.toList());
        if (citedPaperIds.isEmpty()) {
            return Collections.emptyList();
        }
        return paperRepository.findAllById(citedPaperIds);
    }

    @Transactional(readOnly = true)
    public List<Paper> getCitations(Long paperId) {
        List<Citation> citations = citationRepository.findByCitedPaperId(paperId, org.springframework.data.domain.Pageable.unpaged()).getContent();
        List<Long> citingPaperIds = citations.stream()
                .map(Citation::getCitingPaperId)
                .collect(Collectors.toList());
        if (citingPaperIds.isEmpty()) {
            return Collections.emptyList();
        }
        return paperRepository.findAllById(citingPaperIds);
    }

    @Transactional(readOnly = true)
    public CitationNetworkDTO buildCitationNetwork(Long paperId, int layers) {
        if (layers < 0) {
            throw new IllegalArgumentException("扩展层数不能为负数");
        }
        if (!paperRepository.existsById(paperId)) {
            throw new IllegalArgumentException("论文不存在: " + paperId);
        }

        Set<Long> visited = new HashSet<>();
        List<CitationNode> nodes = new ArrayList<>();
        List<CitationEdge> edges = new ArrayList<>();
        Queue<Long> queue = new LinkedList<>();
        Map<Long, Integer> layerMap = new HashMap<>();

        queue.offer(paperId);
        visited.add(paperId);
        layerMap.put(paperId, 0);

        while (!queue.isEmpty()) {
            Long currentPaperId = queue.poll();
            int currentLayer = layerMap.get(currentPaperId);

            if (currentLayer >= layers) {
                continue;
            }

            Paper currentPaper = paperRepository.findById(currentPaperId).orElse(null);
            if (currentPaper != null && nodes.stream().noneMatch(n -> n.getId().equals(currentPaperId))) {
                nodes.add(buildCitationNode(currentPaper));
            }

            List<Citation> outgoingCitations = citationRepository.findByCitingPaperId(currentPaperId, org.springframework.data.domain.Pageable.unpaged()).getContent();
            for (Citation citation : outgoingCitations) {
                Long citedId = citation.getCitedPaperId();
                edges.add(buildCitationEdge(currentPaperId, citedId));
                if (!visited.contains(citedId)) {
                    visited.add(citedId);
                    layerMap.put(citedId, currentLayer + 1);
                    queue.offer(citedId);
                    Paper citedPaper = paperRepository.findById(citedId).orElse(null);
                    if (citedPaper != null) {
                        nodes.add(buildCitationNode(citedPaper));
                    }
                }
            }

            List<Citation> incomingCitations = citationRepository.findByCitedPaperId(currentPaperId, org.springframework.data.domain.Pageable.unpaged()).getContent();
            for (Citation citation : incomingCitations) {
                Long citingId = citation.getCitingPaperId();
                edges.add(buildCitationEdge(citingId, currentPaperId));
                if (!visited.contains(citingId)) {
                    visited.add(citingId);
                    layerMap.put(citingId, currentLayer + 1);
                    queue.offer(citingId);
                    Paper citingPaper = paperRepository.findById(citingId).orElse(null);
                    if (citingPaper != null) {
                        nodes.add(buildCitationNode(citingPaper));
                    }
                }
            }
        }

        Paper rootPaper = paperRepository.findById(paperId).orElse(null);
        if (rootPaper != null && nodes.stream().noneMatch(n -> n.getId().equals(paperId))) {
            nodes.add(0, buildCitationNode(rootPaper));
        }

        CitationNetworkDTO networkDTO = new CitationNetworkDTO();
        networkDTO.setNodes(nodes);
        networkDTO.setEdges(edges);
        return networkDTO;
    }

    @Transactional(readOnly = true)
    public Long getCitationCount(Long paperId) {
        return citationRepository.countByCitedPaperId(paperId);
    }

    @Transactional
    public ImportResultDTO batchImportCitations(List<Citation> citationList) {
        ImportResultDTO result = new ImportResultDTO();
        int successCount = 0;
        int failCount = 0;
        List<ImportResultDTO.ErrorDetail> errors = new ArrayList<>();

        for (int i = 0; i < citationList.size(); i++) {
            Citation citation = citationList.get(i);
            try {
                if (citation.getCitingPaperId() == null || citation.getCitedPaperId() == null) {
                    throw new IllegalArgumentException("施引论文ID和被引论文ID不能为空");
                }
                if (citation.getCitingPaperId().equals(citation.getCitedPaperId())) {
                    throw new IllegalArgumentException("论文不能引用自己");
                }
                if (!paperRepository.existsById(citation.getCitingPaperId())) {
                    throw new IllegalArgumentException("施引论文不存在: " + citation.getCitingPaperId());
                }
                if (!paperRepository.existsById(citation.getCitedPaperId())) {
                    throw new IllegalArgumentException("被引论文不存在: " + citation.getCitedPaperId());
                }

                Citation newCitation = new Citation();
                newCitation.setCitingPaperId(citation.getCitingPaperId());
                newCitation.setCitedPaperId(citation.getCitedPaperId());
                newCitation.setCitationContext(citation.getCitationContext());
                newCitation.setCreatedAt(LocalDateTime.now());
                citationRepository.save(newCitation);
                successCount++;
            } catch (Exception e) {
                failCount++;
                ImportResultDTO.ErrorDetail errorDetail = new ImportResultDTO.ErrorDetail();
                errorDetail.setRowIndex(i);
                errorDetail.setMessage(e.getMessage());
                errors.add(errorDetail);
            }
        }

        result.setSuccessCount(successCount);
        result.setFailCount(failCount);
        result.setErrors(errors);
        return result;
    }

    private CitationNode buildCitationNode(Paper paper) {
        CitationNode node = new CitationNode();
        node.setId(paper.getId());
        node.setTitle(paper.getTitle());
        node.setCitations(paper.getTotalCitations() != null ? paper.getTotalCitations() : 0);
        node.setYear(paper.getPublicationYear());
        return node;
    }

    private CitationEdge buildCitationEdge(Long source, Long target) {
        CitationEdge edge = new CitationEdge();
        edge.setSource(source);
        edge.setTarget(target);
        return edge;
    }
}

package com.research.controller;

import com.research.common.ApiResponse;
import com.research.dto.CitationNetworkDTO;
import com.research.dto.ImportResultDTO;
import com.research.entity.Citation;
import com.research.entity.Paper;
import com.research.service.CitationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/citations")
@RequiredArgsConstructor
public class CitationController {

    private final CitationService citationService;

    @PostMapping
    public ApiResponse<Citation> addCitation(
            @RequestParam Long citingPaperId,
            @RequestParam Long citedPaperId,
            @RequestParam(required = false) String citationContext) {
        Citation citation = citationService.addCitation(citingPaperId, citedPaperId, citationContext);
        return ApiResponse.success(citation);
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteCitation(@PathVariable Long id) {
        citationService.deleteCitation(id);
        return ApiResponse.success();
    }

    @GetMapping("/citing/{paperId}")
    public ApiResponse<List<Paper>> getReferences(@PathVariable Long paperId) {
        List<Paper> references = citationService.getReferences(paperId);
        return ApiResponse.success(references);
    }

    @GetMapping("/cited/{paperId}")
    public ApiResponse<List<Paper>> getCitations(@PathVariable Long paperId) {
        List<Paper> citations = citationService.getCitations(paperId);
        return ApiResponse.success(citations);
    }

    @GetMapping("/network")
    public ApiResponse<CitationNetworkDTO> buildCitationNetwork(
            @RequestParam Long paperId,
            @RequestParam(defaultValue = "2") Integer depth) {
        CitationNetworkDTO network = citationService.buildCitationNetwork(paperId, depth);
        return ApiResponse.success(network);
    }

    @GetMapping("/cited/{paperId}/count")
    public ApiResponse<Long> getCitationCount(@PathVariable Long paperId) {
        Long count = citationService.getCitationCount(paperId);
        return ApiResponse.success(count);
    }

    @PostMapping("/batch")
    public ApiResponse<ImportResultDTO> batchImportCitations(@RequestBody List<Citation> citationList) {
        ImportResultDTO result = citationService.batchImportCitations(citationList);
        return ApiResponse.success(result);
    }
}

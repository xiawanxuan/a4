package com.research.controller;

import com.research.common.ApiResponse;
import com.research.dto.AuthorAnalysisDTO;
import com.research.dto.InstitutionAnalysisDTO;
import com.research.dto.PaperDTO;
import com.research.service.AnalysisService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/analysis")
@RequiredArgsConstructor
public class AnalysisController {

    private final AnalysisService analysisService;

    @GetMapping("/overview")
    public ApiResponse<AnalysisService.DataOverview> getOverview() {
        return ApiResponse.success(analysisService.getDataOverview());
    }

    @GetMapping("/core-authors")
    public ApiResponse<AuthorAnalysisDTO> getCoreAuthors(
            @RequestParam(defaultValue = "hIndex") String sortBy,
            @RequestParam(defaultValue = "10") Integer limit) {
        return ApiResponse.success(analysisService.analyzeCoreAuthors(limit, sortBy));
    }

    @GetMapping("/core-institutions")
    public ApiResponse<InstitutionAnalysisDTO> getCoreInstitutions(
            @RequestParam(defaultValue = "publications") String sortBy,
            @RequestParam(defaultValue = "10") Integer limit) {
        return ApiResponse.success(analysisService.analyzeCoreInstitutions(limit, sortBy));
    }

    @GetMapping("/publication-trend")
    public ApiResponse<List<AuthorAnalysisDTO.PublicationTrend>> getPublicationTrend(
            @RequestParam(required = false) Integer yearStart,
            @RequestParam(required = false) Integer yearEnd) {
        List<AuthorAnalysisDTO.PublicationTrend> trends = analysisService.analyzePublicationTrend();
        if (yearStart != null) {
            trends = trends.stream()
                    .filter(t -> t.getYear() >= yearStart)
                    .toList();
        }
        if (yearEnd != null) {
            trends = trends.stream()
                    .filter(t -> t.getYear() <= yearEnd)
                    .toList();
        }
        return ApiResponse.success(trends);
    }

    @GetMapping("/keyword-cooccurrence")
    public ApiResponse<List<AnalysisService.KeywordCooccurrence>> getKeywordCooccurrence(
            @RequestParam(defaultValue = "20") Integer limit) {
        return ApiResponse.success(analysisService.analyzeKeywordCooccurrence(limit));
    }

    @GetMapping("/author-collaboration")
    public ApiResponse<AuthorAnalysisDTO> getAuthorCollaboration(
            @RequestParam(defaultValue = "50") Integer limit) {
        return ApiResponse.success(analysisService.analyzeAuthorCollaboration(limit));
    }

    @GetMapping("/institution-collaboration")
    public ApiResponse<InstitutionAnalysisDTO> getInstitutionCollaboration(
            @RequestParam(defaultValue = "50") Integer limit) {
        return ApiResponse.success(analysisService.analyzeInstitutionCollaboration(limit));
    }

    @GetMapping("/journal-distribution")
    public ApiResponse<List<AnalysisService.JournalDistribution>> getJournalDistribution(
            @RequestParam(defaultValue = "10") Integer limit) {
        return ApiResponse.success(analysisService.analyzeJournalDistribution(limit));
    }

    @GetMapping("/research-areas")
    public ApiResponse<List<AnalysisService.ResearchArea>> getResearchAreas(
            @RequestParam(defaultValue = "10") Integer limit) {
        return ApiResponse.success(analysisService.analyzeResearchAreas(limit));
    }

    @GetMapping("/top-cited-papers")
    public ApiResponse<List<PaperDTO>> getTopCitedPapers(
            @RequestParam(defaultValue = "10") Integer limit) {
        return ApiResponse.success(analysisService.getTopCitedPapers(limit));
    }
}

package com.research.controller;

import com.research.common.ApiResponse;
import com.research.dto.CitationNetworkDTO;
import com.research.dto.PageResult;
import com.research.dto.PaperDTO;
import com.research.dto.PaperQueryDTO;
import com.research.service.CitationService;
import com.research.service.PaperService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/papers")
@RequiredArgsConstructor
public class PaperController {

    private final PaperService paperService;
    private final CitationService citationService;

    @GetMapping
    public ApiResponse<PageResult<PaperDTO>> queryPapers(@Valid PaperQueryDTO queryDTO) {
        return ApiResponse.success(paperService.queryPapers(queryDTO));
    }

    @GetMapping("/{id}")
    public ApiResponse<PaperDTO> getPaperById(@PathVariable Long id) {
        return ApiResponse.success(paperService.getPaperById(id));
    }

    @GetMapping("/doi/{doi}")
    public ApiResponse<PaperDTO> getPaperByDoi(@PathVariable String doi) {
        return ApiResponse.success(paperService.getPaperByDoi(doi));
    }

    @PostMapping
    public ApiResponse<PaperDTO> createPaper(@RequestBody @Valid PaperDTO paperDTO) {
        return ApiResponse.success(paperService.createPaper(paperDTO));
    }

    @PutMapping("/{id}")
    public ApiResponse<PaperDTO> updatePaper(@PathVariable Long id, @RequestBody @Valid PaperDTO paperDTO) {
        return ApiResponse.success(paperService.updatePaper(id, paperDTO));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Boolean> deletePaper(@PathVariable Long id) {
        return ApiResponse.success(paperService.deletePaper(id));
    }

    @GetMapping("/{id}/references")
    public ApiResponse<List<PaperDTO>> getReferences(@PathVariable Long id) {
        return ApiResponse.success(paperService.getReferences(id));
    }

    @GetMapping("/{id}/citations")
    public ApiResponse<List<PaperDTO>> getCitations(@PathVariable Long id) {
        return ApiResponse.success(paperService.getCitations(id));
    }

    @GetMapping("/statistics")
    public ApiResponse<Map<String, Object>> getStatistics() {
        return ApiResponse.success(paperService.getStatistics());
    }

    @GetMapping("/{id}/citation-network")
    public ApiResponse<CitationNetworkDTO> getCitationNetwork(
            @PathVariable Long id,
            @RequestParam(defaultValue = "2") Integer depth) {
        CitationNetworkDTO network = citationService.buildCitationNetwork(id, depth);
        return ApiResponse.success(network);
    }
}

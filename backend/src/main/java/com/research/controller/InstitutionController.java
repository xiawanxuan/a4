package com.research.controller;

import com.research.common.ApiResponse;
import com.research.dto.AuthorDTO;
import com.research.dto.InstitutionAnalysisDTO;
import com.research.dto.InstitutionDTO;
import com.research.dto.PageResult;
import com.research.dto.PaperDTO;
import com.research.service.InstitutionService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/institutions")
@RequiredArgsConstructor
public class InstitutionController {

    private final InstitutionService institutionService;

    @GetMapping
    public ApiResponse<PageResult<InstitutionDTO>> listInstitutions(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String country,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        Pageable pageable = PageRequest.of(pageNum - 1, pageSize);
        PageResult<InstitutionDTO> result = institutionService.listInstitutions(name, country, pageable);
        return ApiResponse.success(result);
    }

    @GetMapping("/{id}")
    public ApiResponse<InstitutionDTO> getInstitutionById(@PathVariable Long id) {
        InstitutionDTO institution = institutionService.getInstitutionById(id);
        return ApiResponse.success(institution);
    }

    @PostMapping
    public ApiResponse<InstitutionDTO> createInstitution(@RequestBody InstitutionDTO institutionDTO) {
        InstitutionDTO created = institutionService.createInstitution(institutionDTO);
        return ApiResponse.success(created);
    }

    @PutMapping("/{id}")
    public ApiResponse<InstitutionDTO> updateInstitution(
            @PathVariable Long id,
            @RequestBody InstitutionDTO institutionDTO) {
        InstitutionDTO updated = institutionService.updateInstitution(id, institutionDTO);
        return ApiResponse.success(updated);
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteInstitution(@PathVariable Long id) {
        institutionService.deleteInstitution(id);
        return ApiResponse.success();
    }

    @GetMapping("/{id}/papers")
    public ApiResponse<PageResult<PaperDTO>> getInstitutionPapers(
            @PathVariable Long id,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        Pageable pageable = PageRequest.of(pageNum - 1, pageSize);
        PageResult<PaperDTO> result = institutionService.getInstitutionPapers(id, pageable);
        return ApiResponse.success(result);
    }

    @GetMapping("/{id}/authors")
    public ApiResponse<List<AuthorDTO>> getInstitutionAuthors(@PathVariable Long id) {
        List<AuthorDTO> authors = institutionService.getInstitutionAuthors(id);
        return ApiResponse.success(authors);
    }

    @GetMapping("/{id}/collaboration")
    public ApiResponse<InstitutionAnalysisDTO> getInstitutionCollaboration(@PathVariable Long id) {
        InstitutionAnalysisDTO analysis = institutionService.getInstitutionCollaborationNetwork(id);
        return ApiResponse.success(analysis);
    }

    @GetMapping("/ranking")
    public ApiResponse<List<InstitutionDTO>> getInstitutionRanking(
            @RequestParam(defaultValue = "papers") String sortBy,
            @RequestParam(defaultValue = "10") Integer limit) {
        List<InstitutionDTO> ranking = institutionService.getInstitutionRanking(sortBy, limit);
        return ApiResponse.success(ranking);
    }
}

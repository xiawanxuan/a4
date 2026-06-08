package com.research.controller;

import com.research.common.ApiResponse;
import com.research.dto.AuthorAnalysisDTO;
import com.research.dto.AuthorDTO;
import com.research.dto.PageResult;
import com.research.dto.PaperDTO;
import com.research.service.AuthorService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.Map;

@RestController
@RequestMapping("/api/authors")
@RequiredArgsConstructor
public class AuthorController {

    private final AuthorService authorService;

    @GetMapping
    public ApiResponse<PageResult<AuthorDTO>> listAuthors(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Long institutionId,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        Pageable pageable = PageRequest.of(pageNum - 1, pageSize);
        return ApiResponse.success(authorService.listAuthors(name, institutionId, pageable));
    }

    @GetMapping("/{id}")
    public ApiResponse<AuthorDTO> getAuthorById(@PathVariable Long id) {
        return ApiResponse.success(authorService.getAuthorById(id));
    }

    @GetMapping("/orcid/{orcid}")
    public ApiResponse<AuthorDTO> getAuthorByOrcid(@PathVariable String orcid) {
        return ApiResponse.success(authorService.getAuthorByOrcid(orcid));
    }

    @PostMapping
    public ApiResponse<AuthorDTO> createAuthor(@RequestBody @Valid AuthorDTO authorDTO) {
        return ApiResponse.success(authorService.createAuthor(authorDTO));
    }

    @PutMapping("/{id}")
    public ApiResponse<AuthorDTO> updateAuthor(@PathVariable Long id, @RequestBody @Valid AuthorDTO authorDTO) {
        return ApiResponse.success(authorService.updateAuthor(id, authorDTO));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteAuthor(@PathVariable Long id) {
        authorService.deleteAuthor(id);
        return ApiResponse.success();
    }

    @GetMapping("/{id}/papers")
    public ApiResponse<PageResult<PaperDTO>> getAuthorPapers(
            @PathVariable Long id,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        Pageable pageable = PageRequest.of(pageNum - 1, pageSize);
        return ApiResponse.success(authorService.getAuthorPapers(id, pageable));
    }

    @GetMapping("/{id}/collaboration")
    public ApiResponse<AuthorAnalysisDTO> getAuthorCollaboration(@PathVariable Long id) {
        return ApiResponse.success(authorService.getAuthorCollaborationNetwork(id));
    }

    @GetMapping("/{id}/statistics")
    public ApiResponse<Map<String, Object>> getAuthorStatistics(@PathVariable Long id) {
        return ApiResponse.success(authorService.getAuthorStatistics(id));
    }
}

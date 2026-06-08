package com.research.controller;

import com.research.common.ApiResponse;
import com.research.dto.ImportResultDTO;
import com.research.dto.PaperImportDTO;
import com.research.service.ImportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/import")
@RequiredArgsConstructor
public class ImportController {

    private final ImportService importService;

    @PostMapping("/json")
    public ApiResponse<ImportResultDTO> importFromJson(@RequestBody List<PaperImportDTO> papers) {
        ImportResultDTO result = importService.importPapersFromJson(papers);
        return ApiResponse.success(result);
    }

    @PostMapping("/csv")
    public ApiResponse<ImportResultDTO> importFromCsv(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ApiResponse.error(400, "上传文件不能为空");
        }
        ImportResultDTO result = importService.importPapersFromCsv(file);
        return ApiResponse.success(result);
    }

    @PostMapping("/citations")
    public ApiResponse<ImportResultDTO> importCitations(@RequestBody List<Map<String, Object>> citations) {
        ImportResultDTO result = importService.importCitations(citations);
        return ApiResponse.success(result);
    }

    @GetMapping("/template")
    public ResponseEntity<byte[]> downloadTemplate() {
        String template = "title,titleEn,abstract,keywords,doi,pmid,arxivId,url,pdfUrl,journalName,issn,volume,issue,pages,publicationYear,publicationDate,language,documentType,totalCitations,authors,institutions,references\n" +
                "示例论文标题,Example Paper Title,这是论文摘要,关键词1;关键词2;关键词3,10.1000/example,12345678,arXiv:1234.5678,https://example.com,https://example.com/pdf,示例期刊,0000-0000,Vol.10,Issue 2,100-110,2024,2024-01-15,zh,article,10,张三;李四;王五,示例大学;示例研究院,参考文献1;参考文献2\n";

        byte[] content = template.getBytes(StandardCharsets.UTF_8);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("text/csv; charset=UTF-8"));
        headers.setContentDispositionFormData("attachment", "paper_import_template.csv");
        headers.setContentLength(content.length);

        return ResponseEntity.ok()
                .headers(headers)
                .body(content);
    }
}

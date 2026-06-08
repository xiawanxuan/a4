package com.research.dto;

import lombok.Data;

import java.util.List;

@Data
public class ImportResultDTO {

    private Integer successCount;

    private Integer failCount;

    private List<ErrorDetail> errors;

    @Data
    public static class ErrorDetail {
        private Integer rowIndex;
        private String message;
    }
}

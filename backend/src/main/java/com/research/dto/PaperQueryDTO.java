package com.research.dto;

import lombok.Data;

@Data
public class PaperQueryDTO {

    private String keyword;

    private Long authorId;

    private Long institutionId;

    private Integer yearStart;

    private Integer yearEnd;

    private Integer pageNum = 1;

    private Integer pageSize = 10;

    private String sortBy;

    private String sortOrder = "desc";
}

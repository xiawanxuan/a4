package com.research.dto;

import lombok.Data;

@Data
public class CitationNode {

    private Long id;

    private String title;

    private Integer citations;

    private Integer year;
}

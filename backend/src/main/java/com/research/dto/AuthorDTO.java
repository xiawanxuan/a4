package com.research.dto;

import lombok.Data;

import java.util.List;

@Data
public class AuthorDTO {

    private Long id;

    private String name;

    private String nameEn;

    private String orcid;

    private String email;

    private String homepage;

    private Integer hIndex;

    private Integer totalCitations;

    private Integer totalPublications;

    private Long affiliationId;

    private String affiliationName;

    private String biography;

    private List<String> researchAreas;
}

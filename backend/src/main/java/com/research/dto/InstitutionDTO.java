package com.research.dto;

import lombok.Data;

@Data
public class InstitutionDTO {

    private Long id;

    private String name;

    private String nameEn;

    private String country;

    private String city;

    private String department;

    private String type;

    private String description;

    private Integer authorCount;

    private Integer paperCount;

    private Integer totalCitations;

    private Integer hIndex;
}

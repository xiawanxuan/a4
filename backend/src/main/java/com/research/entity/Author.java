package com.research.entity;

import lombok.Data;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "author")
public class Author {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "name_en", length = 100)
    private String nameEn;

    @Column(name = "orcid", length = 50)
    private String orcid;

    @Column(name = "email", length = 255)
    private String email;

    @Column(name = "homepage", length = 500)
    private String homepage;

    @Column(name = "h_index")
    private Integer hIndex;

    @Column(name = "total_citations")
    private Integer totalCitations;

    @Column(name = "total_publications")
    private Integer totalPublications;

    @Column(name = "affiliation_id")
    private Long affiliationId;

    @Column(name = "biography", columnDefinition = "TEXT")
    private String biography;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}

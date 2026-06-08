package com.research.entity;

import lombok.Data;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "author_institution")
public class AuthorInstitution {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "author_id", nullable = false)
    private Long authorId;

    @Column(name = "institution_id", nullable = false)
    private Long institutionId;

    @Column(name = "start_year")
    private Integer startYear;

    @Column(name = "end_year")
    private Integer endYear;

    @Column(name = "is_current")
    private Boolean isCurrent;

    @Column(name = "position", length = 100)
    private String position;

    @Column(name = "created_at")
    private LocalDateTime createdAt;
}

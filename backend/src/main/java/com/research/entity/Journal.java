package com.research.entity;

import lombok.Data;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "journal")
public class Journal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false, length = 255)
    private String name;

    @Column(name = "name_abbr", length = 100)
    private String nameAbbr;

    @Column(name = "type", nullable = false, length = 20)
    private String type;

    @Column(name = "issn", length = 20)
    private String issn;

    @Column(name = "e_issn", length = 20)
    private String eIssn;

    @Column(name = "publisher", length = 255)
    private String publisher;

    @Column(name = "country", length = 100)
    private String country;

    @Column(name = "impact_factor", precision = 6, scale = 3)
    private BigDecimal impactFactor;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}

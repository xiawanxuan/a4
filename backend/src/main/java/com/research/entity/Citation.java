package com.research.entity;

import lombok.Data;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "citation")
public class Citation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "citing_paper_id", nullable = false)
    private Long citingPaperId;

    @Column(name = "cited_paper_id", nullable = false)
    private Long citedPaperId;

    @Column(name = "citation_context", columnDefinition = "TEXT")
    private String citationContext;

    @Column(name = "created_at")
    private LocalDateTime createdAt;
}

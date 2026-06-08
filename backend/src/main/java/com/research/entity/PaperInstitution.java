package com.research.entity;

import lombok.Data;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "paper_institution")
public class PaperInstitution {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "paper_id", nullable = false)
    private Long paperId;

    @Column(name = "institution_id", nullable = false)
    private Long institutionId;

    @Column(name = "affiliation_order")
    private Integer affiliationOrder;

    @Column(name = "created_at")
    private LocalDateTime createdAt;
}

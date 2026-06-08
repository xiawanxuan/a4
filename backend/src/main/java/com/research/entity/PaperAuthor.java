package com.research.entity;

import lombok.Data;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "paper_author")
public class PaperAuthor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "paper_id", nullable = false)
    private Long paperId;

    @Column(name = "author_id", nullable = false)
    private Long authorId;

    @Column(name = "author_order", nullable = false)
    private Integer authorOrder;

    @Column(name = "is_corresponding")
    private Boolean isCorresponding;

    @Column(name = "affiliation_id")
    private Long affiliationId;

    @Column(name = "created_at")
    private LocalDateTime createdAt;
}

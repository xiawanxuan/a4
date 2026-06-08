package com.research.repository;

import com.research.entity.Citation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CitationRepository extends JpaRepository<Citation, Long> {

    Page<Citation> findByCitingPaperId(Long citingPaperId, Pageable pageable);

    Page<Citation> findByCitedPaperId(Long citedPaperId, Pageable pageable);

    @Query("SELECT COUNT(c) FROM Citation c WHERE c.citedPaperId = :paperId")
    Long countByCitedPaperId(@Param("paperId") Long paperId);
}

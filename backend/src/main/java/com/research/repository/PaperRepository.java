package com.research.repository;

import com.research.entity.Paper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PaperRepository extends JpaRepository<Paper, Long>, JpaSpecificationExecutor<Paper> {

    Page<Paper> findAll(Pageable pageable);

    Page<Paper> findByTitleContaining(String title, Pageable pageable);

    Optional<Paper> findByDoi(String doi);

    @Query("SELECT p FROM Paper p WHERE p.id IN (SELECT pa.paperId FROM PaperAuthor pa WHERE pa.authorId = :authorId)")
    Page<Paper> findByAuthorId(@Param("authorId") Long authorId, Pageable pageable);

    @Query("SELECT p FROM Paper p WHERE p.id IN (SELECT pi.paperId FROM PaperInstitution pi WHERE pi.institutionId = :institutionId)")
    Page<Paper> findByInstitutionId(@Param("institutionId") Long institutionId, Pageable pageable);

    Page<Paper> findByPublicationYearBetween(Integer startYear, Integer endYear, Pageable pageable);

    @Query("SELECT p FROM Paper p ORDER BY p.totalCitations DESC")
    Page<Paper> findAllOrderByTotalCitationsDesc(Pageable pageable);
}

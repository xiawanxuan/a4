package com.research.repository;

import com.research.entity.Author;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AuthorRepository extends JpaRepository<Author, Long> {

    Page<Author> findByNameContaining(String name, Pageable pageable);

    Optional<Author> findByOrcid(String orcid);

    @Query("SELECT a FROM Author a WHERE a.id IN (SELECT ai.authorId FROM AuthorInstitution ai WHERE ai.institutionId = :institutionId)")
    Page<Author> findByInstitutionId(@Param("institutionId") Long institutionId, Pageable pageable);
}

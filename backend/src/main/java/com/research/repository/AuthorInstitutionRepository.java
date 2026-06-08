package com.research.repository;

import com.research.entity.AuthorInstitution;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AuthorInstitutionRepository extends JpaRepository<AuthorInstitution, Long> {

    List<AuthorInstitution> findByAuthorId(Long authorId);

    Page<AuthorInstitution> findByInstitutionId(Long institutionId, Pageable pageable);
}

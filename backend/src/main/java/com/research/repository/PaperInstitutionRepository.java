package com.research.repository;

import com.research.entity.PaperInstitution;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PaperInstitutionRepository extends JpaRepository<PaperInstitution, Long> {

    List<PaperInstitution> findByPaperId(Long paperId);

    Page<PaperInstitution> findByInstitutionId(Long institutionId, Pageable pageable);
}

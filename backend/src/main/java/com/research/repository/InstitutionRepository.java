package com.research.repository;

import com.research.entity.Institution;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InstitutionRepository extends JpaRepository<Institution, Long> {

    Page<Institution> findByNameContaining(String name, Pageable pageable);

    Page<Institution> findByCountry(String country, Pageable pageable);
}

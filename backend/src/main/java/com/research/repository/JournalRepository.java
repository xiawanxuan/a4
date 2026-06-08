package com.research.repository;

import com.research.entity.Journal;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface JournalRepository extends JpaRepository<Journal, Long> {

    Page<Journal> findByNameContaining(String name, Pageable pageable);

    Optional<Journal> findByIssn(String issn);
}

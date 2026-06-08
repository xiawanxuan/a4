package com.research.repository;

import com.research.entity.PaperAuthor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PaperAuthorRepository extends JpaRepository<PaperAuthor, Long> {

    List<PaperAuthor> findByPaperIdOrderByAuthorOrderAsc(Long paperId);

    Page<PaperAuthor> findByAuthorId(Long authorId, Pageable pageable);
}

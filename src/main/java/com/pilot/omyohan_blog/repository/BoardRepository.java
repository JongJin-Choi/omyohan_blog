package com.pilot.omyohan_blog.repository;

import com.pilot.omyohan_blog.domain.Board;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BoardRepository extends JpaRepository<Board, Long> {
    Optional<Board> findBySlug(String slug);
    boolean existsBySlugAndIdNot(String slug, Long id);
    boolean existsBySlug(String slug);
}

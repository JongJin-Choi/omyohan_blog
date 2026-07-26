package com.pilot.omyohan_blog.repository;

import com.pilot.omyohan_blog.domain.Post;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PostRepository extends JpaRepository<Post, Long> {
    List<Post> findAllByDeletedAtIsNullOrderByUpdatedAtDesc();
    List<Post> findAllByBoardIdAndDeletedAtIsNullOrderByUpdatedAtDesc(Long boardId);
    Optional<Post> findByIdAndDeletedAtIsNull(Long id);
    List<Post> findAllByBoardIdAndIsPublishedTrueAndDeletedAtIsNullOrderByIsPinnedDescCreatedAtDesc(Long boardId);
}

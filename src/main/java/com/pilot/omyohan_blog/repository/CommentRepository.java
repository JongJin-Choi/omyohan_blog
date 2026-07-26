package com.pilot.omyohan_blog.repository;

import com.pilot.omyohan_blog.domain.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findAllByOrderByCreatedAtDesc();
    List<Comment> findAllByPostIdOrderByCreatedAtDesc(Long postId);
    List<Comment> findAllByPostIdOrderByCreatedAtAsc(Long postId);
}

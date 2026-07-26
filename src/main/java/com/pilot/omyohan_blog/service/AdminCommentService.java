package com.pilot.omyohan_blog.service;

import com.pilot.omyohan_blog.controller.ApiSchemas;
import com.pilot.omyohan_blog.domain.Comment;
import com.pilot.omyohan_blog.repository.CommentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class AdminCommentService {

    private final CommentRepository commentRepository;

    public AdminCommentService(CommentRepository commentRepository) {
        this.commentRepository = commentRepository;
    }

    public List<ApiSchemas.CommentResponse> getComments(Long postId) {
        List<Comment> comments = postId == null
                ? commentRepository.findAllByOrderByCreatedAtDesc()
                : commentRepository.findAllByPostIdOrderByCreatedAtDesc(postId);
        return comments.stream().map(this::toResponse).toList();
    }

    @Transactional
    public ApiSchemas.CommentResponse hideComment(Long commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> ServiceSupport.notFound("Comment", commentId));
        comment.setDeleted(true);
        comment.setContent("[deleted]");
        return toResponse(comment);
    }

    private ApiSchemas.CommentResponse toResponse(Comment comment) {
        return new ApiSchemas.CommentResponse(
                comment.getId(),
                comment.getPost().getId(),
                comment.getParent() == null ? null : comment.getParent().getId(),
                comment.getAuthorName(),
                comment.getContent(),
                comment.isDeleted(),
                comment.getCreatedAt(),
                comment.getUpdatedAt()
        );
    }
}

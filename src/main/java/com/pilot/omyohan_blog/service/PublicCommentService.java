package com.pilot.omyohan_blog.service;

import com.pilot.omyohan_blog.controller.ApiSchemas;
import com.pilot.omyohan_blog.domain.Comment;
import com.pilot.omyohan_blog.domain.Post;
import com.pilot.omyohan_blog.repository.CommentRepository;
import com.pilot.omyohan_blog.repository.PostRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class PublicCommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public PublicCommentService(CommentRepository commentRepository, PostRepository postRepository) {
        this.commentRepository = commentRepository;
        this.postRepository = postRepository;
    }

    public List<ApiSchemas.CommentResponse> getComments(Long postId) {
        return commentRepository.findAllByPostIdOrderByCreatedAtAsc(postId).stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public ApiSchemas.CommentResponse createComment(Long postId, ApiSchemas.AnonymousCommentCreateRequest request) {
        Post post = postRepository.findByIdAndDeletedAtIsNull(postId)
                .orElseThrow(() -> ServiceSupport.notFound("Post", postId));
        Comment comment = new Comment();
        comment.setPost(post);
        comment.setParent(request.parentId() == null ? null : commentRepository.findById(request.parentId())
                .orElseThrow(() -> ServiceSupport.notFound("Comment", request.parentId())));
        comment.setAuthorName(request.authorName());
        comment.setAuthorPassword(passwordEncoder.encode(request.authorPassword()));
        comment.setContent(request.content());
        comment.setDeleted(false);
        return toResponse(commentRepository.save(comment));
    }

    @Transactional
    public ApiSchemas.CommentResponse updateComment(Long commentId, ApiSchemas.AnonymousCommentUpdateRequest request) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> ServiceSupport.notFound("Comment", commentId));
        verifyPassword(comment, request.authorPassword());
        comment.setContent(request.content());
        return toResponse(comment);
    }

    @Transactional
    public ApiSchemas.MessageResponse deleteComment(Long commentId, ApiSchemas.AnonymousCommentDeleteRequest request) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> ServiceSupport.notFound("Comment", commentId));
        verifyPassword(comment, request.authorPassword());
        comment.setDeleted(true);
        comment.setContent("[deleted]");
        return new ApiSchemas.MessageResponse("Comment " + commentId + " deleted.");
    }

    private void verifyPassword(Comment comment, String rawPassword) {
        if (!passwordEncoder.matches(rawPassword, comment.getAuthorPassword())) {
            throw ServiceSupport.badRequest("Invalid comment password.");
        }
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

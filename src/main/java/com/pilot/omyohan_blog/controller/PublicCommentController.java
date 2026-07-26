package com.pilot.omyohan_blog.controller;

import com.pilot.omyohan_blog.service.PublicCommentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "Public Comments", description = "사용자 익명 댓글 API")
@RestController
@RequestMapping("/api")
public class PublicCommentController {

    private final PublicCommentService publicCommentService;

    public PublicCommentController(PublicCommentService publicCommentService) {
        this.publicCommentService = publicCommentService;
    }

    @Operation(summary = "댓글 목록 조회", description = "게시글의 댓글 목록을 조회합니다.")
    @GetMapping("/posts/{postId}/comments")
    public List<ApiSchemas.CommentResponse> getComments(@PathVariable Long postId) {
        return publicCommentService.getComments(postId);
    }

    @Operation(summary = "익명 댓글 작성", description = "비회원이 비밀번호를 포함해 댓글을 작성합니다.")
    @PostMapping("/posts/{postId}/comments")
    public ResponseEntity<ApiSchemas.CommentResponse> createComment(
            @PathVariable Long postId,
            @RequestBody ApiSchemas.AnonymousCommentCreateRequest request
    ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(publicCommentService.createComment(postId, request));
    }

    @Operation(summary = "익명 댓글 수정", description = "작성 시 입력한 비밀번호로 댓글을 수정합니다.")
    @PutMapping("/comments/{commentId}")
    public ApiSchemas.CommentResponse updateComment(
            @PathVariable Long commentId,
            @RequestBody ApiSchemas.AnonymousCommentUpdateRequest request
    ) {
        return publicCommentService.updateComment(commentId, request);
    }

    @Operation(summary = "익명 댓글 삭제", description = "작성 시 입력한 비밀번호로 댓글을 삭제합니다.")
    @DeleteMapping("/comments/{commentId}")
    public ApiSchemas.MessageResponse deleteComment(
            @PathVariable Long commentId,
            @RequestBody ApiSchemas.AnonymousCommentDeleteRequest request
    ) {
        return publicCommentService.deleteComment(commentId, request);
    }
}

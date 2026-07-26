package com.pilot.omyohan_blog.controller;

import com.pilot.omyohan_blog.service.AdminCommentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "Admin Comments", description = "관리자 댓글 관리 API")
@RestController
@RequestMapping("/api/admin/comments")
public class AdminCommentController {

    private final AdminCommentService adminCommentService;

    public AdminCommentController(AdminCommentService adminCommentService) {
        this.adminCommentService = adminCommentService;
    }

    @Operation(summary = "댓글 목록 조회", description = "관리자용 댓글 목록을 조회합니다.")
    @GetMapping
    public List<ApiSchemas.CommentResponse> getComments(
            @Parameter(description = "게시글 ID 필터") @RequestParam(required = false) Long postId
    ) {
        return adminCommentService.getComments(postId);
    }

    @Operation(summary = "댓글 숨김 처리", description = "관리자 기준으로 댓글을 숨김 처리합니다.")
    @DeleteMapping("/{commentId}")
    public ApiSchemas.CommentResponse hideComment(@PathVariable Long commentId) {
        return adminCommentService.hideComment(commentId);
    }
}

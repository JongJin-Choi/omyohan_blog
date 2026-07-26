package com.pilot.omyohan_blog.controller;

import com.pilot.omyohan_blog.service.AdminPostService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "Admin Posts", description = "관리자 게시글 관리 API")
@RestController
@RequestMapping("/api/admin/posts")
public class AdminPostController {

    private final AdminPostService adminPostService;

    public AdminPostController(AdminPostService adminPostService) {
        this.adminPostService = adminPostService;
    }

    @Operation(summary = "게시글 생성", description = "게시판 유형에 맞는 게시글을 생성합니다.")
    @PostMapping
    public ResponseEntity<ApiSchemas.PostResponse> createPost(@RequestBody ApiSchemas.PostCreateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(adminPostService.createPost(request));
    }

    @Operation(summary = "게시글 목록 조회", description = "관리자용 게시글 목록을 조회합니다.")
    @GetMapping
    public List<ApiSchemas.PostResponse> getPosts(
            @Parameter(description = "게시판 ID 필터") @RequestParam(required = false) Long boardId
    ) {
        return adminPostService.getPosts(boardId);
    }

    @Operation(summary = "게시글 상세 조회", description = "특정 게시글 상세 정보를 조회합니다.")
    @GetMapping("/{postId}")
    public ApiSchemas.PostResponse getPost(@PathVariable Long postId) {
        return adminPostService.getPost(postId);
    }

    @Operation(summary = "게시글 수정", description = "게시글 본문, 발행 상태, 첨부파일 등을 수정합니다.")
    @PutMapping("/{postId}")
    public ApiSchemas.PostResponse updatePost(
            @PathVariable Long postId,
            @RequestBody ApiSchemas.PostUpdateRequest request
    ) {
        return adminPostService.updatePost(postId, request);
    }

    @Operation(summary = "게시글 삭제", description = "게시글을 삭제합니다.")
    @DeleteMapping("/{postId}")
    public ApiSchemas.MessageResponse deletePost(@PathVariable Long postId) {
        return adminPostService.deletePost(postId);
    }
}

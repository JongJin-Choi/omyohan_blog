package com.pilot.omyohan_blog.controller;

import com.pilot.omyohan_blog.service.AdminBoardService;
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

@Tag(name = "Admin Boards", description = "관리자 게시판 관리 API")
@RestController
@RequestMapping("/api/admin/boards")
public class AdminBoardController {

    private final AdminBoardService adminBoardService;

    public AdminBoardController(AdminBoardService adminBoardService) {
        this.adminBoardService = adminBoardService;
    }

    @Operation(summary = "게시판 생성", description = "일반, 썸네일, 포트폴리오 게시판을 생성합니다.")
    @PostMapping
    public ResponseEntity<ApiSchemas.BoardResponse> createBoard(@RequestBody ApiSchemas.BoardCreateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(adminBoardService.createBoard(request));
    }

    @Operation(summary = "게시판 목록 조회", description = "관리자용 게시판 목록을 조회합니다.")
    @GetMapping
    public List<ApiSchemas.BoardResponse> getBoards() {
        return adminBoardService.getBoards();
    }

    @Operation(summary = "게시판 상세 조회", description = "특정 게시판 정보를 조회합니다.")
    @GetMapping("/{boardId}")
    public ApiSchemas.BoardResponse getBoard(@PathVariable Long boardId) {
        return adminBoardService.getBoard(boardId);
    }

    @Operation(summary = "게시판 수정", description = "게시판 기본 정보와 타입을 수정합니다.")
    @PutMapping("/{boardId}")
    public ApiSchemas.BoardResponse updateBoard(
            @PathVariable Long boardId,
            @RequestBody ApiSchemas.BoardUpdateRequest request
    ) {
        return adminBoardService.updateBoard(boardId, request);
    }

    @Operation(summary = "게시판 삭제", description = "게시판과 연결된 글 정책을 확인한 뒤 게시판을 삭제합니다.")
    @DeleteMapping("/{boardId}")
    public ApiSchemas.MessageResponse deleteBoard(@PathVariable Long boardId) {
        return adminBoardService.deleteBoard(boardId);
    }
}

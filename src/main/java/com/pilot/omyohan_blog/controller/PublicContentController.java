package com.pilot.omyohan_blog.controller;

import com.pilot.omyohan_blog.service.PublicContentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "Public Content", description = "사용자 메뉴/게시글 조회 API")
@RestController
@RequestMapping("/api")
public class PublicContentController {

    private final PublicContentService publicContentService;

    public PublicContentController(PublicContentService publicContentService) {
        this.publicContentService = publicContentService;
    }

    @Operation(summary = "사용자 메뉴 목록 조회", description = "노출 중인 메뉴 목록을 조회합니다.")
    @GetMapping("/menus")
    public List<ApiSchemas.MenuResponse> getVisibleMenus() {
        return publicContentService.getVisibleMenus();
    }

    @Operation(summary = "메뉴별 게시글 목록 조회", description = "선택한 메뉴에 연결된 게시판 글 목록을 조회합니다.")
    @GetMapping("/menus/{menuSlug}/posts")
    public List<ApiSchemas.PostResponse> getPostsByMenu(
            @PathVariable String menuSlug,
            @Parameter(description = "페이지당 조회 개수") @RequestParam(defaultValue = "20") int limit,
            @Parameter(description = "시작 offset") @RequestParam(defaultValue = "0") int offset
    ) {
        return publicContentService.getPostsByMenu(menuSlug).stream()
                .skip(offset)
                .limit(limit)
                .toList();
    }

    @Operation(summary = "게시글 상세 조회", description = "메뉴 기준으로 게시글 상세를 조회합니다.")
    @GetMapping("/menus/{menuSlug}/posts/{postId}")
    public ApiSchemas.PostResponse getPostDetail(
            @PathVariable String menuSlug,
            @PathVariable Long postId
    ) {
        return publicContentService.getPostDetail(menuSlug, postId);
    }
}

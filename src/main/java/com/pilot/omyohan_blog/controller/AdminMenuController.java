package com.pilot.omyohan_blog.controller;

import com.pilot.omyohan_blog.service.AdminMenuService;
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

@Tag(name = "Admin Menus", description = "관리자 메뉴 관리 API")
@RestController
@RequestMapping("/api/admin/menus")
public class AdminMenuController {

    private final AdminMenuService adminMenuService;

    public AdminMenuController(AdminMenuService adminMenuService) {
        this.adminMenuService = adminMenuService;
    }

    @Operation(summary = "메뉴 생성", description = "게시판과 연결되는 메뉴를 생성합니다.")
    @PostMapping
    public ResponseEntity<ApiSchemas.MenuResponse> createMenu(@RequestBody ApiSchemas.MenuCreateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(adminMenuService.createMenu(request));
    }

    @Operation(summary = "메뉴 목록 조회", description = "관리자용 메뉴 목록을 조회합니다.")
    @GetMapping
    public List<ApiSchemas.MenuResponse> getMenus() {
        return adminMenuService.getMenus();
    }

    @Operation(summary = "메뉴 상세 조회", description = "특정 메뉴 정보를 조회합니다.")
    @GetMapping("/{menuId}")
    public ApiSchemas.MenuResponse getMenu(@PathVariable Long menuId) {
        return adminMenuService.getMenu(menuId);
    }

    @Operation(summary = "메뉴 수정", description = "메뉴 이름, 정렬 순서, 연결 게시판 등을 수정합니다.")
    @PutMapping("/{menuId}")
    public ApiSchemas.MenuResponse updateMenu(
            @PathVariable Long menuId,
            @RequestBody ApiSchemas.MenuUpdateRequest request
    ) {
        return adminMenuService.updateMenu(menuId, request);
    }

    @Operation(summary = "메뉴 삭제", description = "메뉴를 삭제합니다.")
    @DeleteMapping("/{menuId}")
    public ApiSchemas.MessageResponse deleteMenu(@PathVariable Long menuId) {
        return adminMenuService.deleteMenu(menuId);
    }
}

package com.pilot.omyohan_blog.controller;

import com.pilot.omyohan_blog.service.AdminSettingsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Admin Settings", description = "관리자 설정 API")
@RestController
@RequestMapping("/api/admin/settings")
public class AdminSettingsController {

    private final AdminSettingsService adminSettingsService;

    public AdminSettingsController(AdminSettingsService adminSettingsService) {
        this.adminSettingsService = adminSettingsService;
    }

    @Operation(summary = "설정 조회", description = "관리자 설정 그룹을 조회합니다.")
    @GetMapping
    public ApiSchemas.SettingsResponse getSettings() {
        return adminSettingsService.getSettings();
    }

    @Operation(summary = "설정 수정", description = "관리자 설정 그룹을 저장합니다.")
    @PutMapping
    public ApiSchemas.SettingsResponse updateSettings(@RequestBody ApiSchemas.SettingsResponse request) {
        return adminSettingsService.updateSettings(request);
    }
}

package com.pilot.omyohan_blog.service;

import com.pilot.omyohan_blog.controller.ApiSchemas;
import com.pilot.omyohan_blog.domain.SiteSetting;
import com.pilot.omyohan_blog.repository.SiteSettingRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class AdminSettingsService {

    private final SiteSettingRepository siteSettingRepository;

    public AdminSettingsService(SiteSettingRepository siteSettingRepository) {
        this.siteSettingRepository = siteSettingRepository;
    }

    @Transactional(readOnly = true)
    public ApiSchemas.SettingsResponse getSettings() {
        ensureDefaults();
        return toResponse(siteSettingRepository.findAllByOrderByGroupNameAscSortOrderAscIdAsc());
    }

    @Transactional
    public ApiSchemas.SettingsResponse updateSettings(ApiSchemas.SettingsResponse request) {
        ensureDefaults();

        List<SiteSetting> current = siteSettingRepository.findAllByOrderByGroupNameAscSortOrderAscIdAsc();
        Map<String, SiteSetting> byKey = new LinkedHashMap<>();
        current.forEach(item -> byKey.put(item.getSettingKey(), item));

        for (ApiSchemas.SettingGroupResponse group : request.groups()) {
            int order = 0;
            for (ApiSchemas.SettingItemResponse item : group.items()) {
                String key = normalizeKey(group.title(), item.label());
                SiteSetting setting = byKey.computeIfAbsent(key, unused -> {
                    SiteSetting created = new SiteSetting();
                    created.setSettingKey(key);
                    return created;
                });
                setting.setGroupName(group.title());
                setting.setLabel(item.label());
                setting.setValue(item.value());
                setting.setSortOrder(order++);
            }
        }

        siteSettingRepository.saveAll(byKey.values());
        return toResponse(siteSettingRepository.findAllByOrderByGroupNameAscSortOrderAscIdAsc());
    }

    @Transactional
    public void ensureDefaults() {
        if (siteSettingRepository.count() > 0) {
            return;
        }

        siteSettingRepository.saveAll(List.of(
                create("기본 운영 설정", "site_name", "사이트 이름", "Omyohan Blog", 0),
                create("기본 운영 설정", "comment_policy", "기본 댓글 정책", "익명 댓글 허용", 1),
                create("기본 운영 설정", "default_post_status", "기본 게시글 상태", "임시 저장 후 발행", 2),
                create("콘텐츠 정책", "general_board_policy", "일반 게시판", "본문 중심", 0),
                create("콘텐츠 정책", "thumbnail_board_policy", "썸네일 게시판", "대표 이미지 필수", 1),
                create("콘텐츠 정책", "portfolio_board_policy", "포트폴리오 게시판", "기간 / 클라이언트 정보 포함", 2)
        ));
    }

    private SiteSetting create(String groupName, String key, String label, String value, int sortOrder) {
        SiteSetting setting = new SiteSetting();
        setting.setGroupName(groupName);
        setting.setSettingKey(key);
        setting.setLabel(label);
        setting.setValue(value);
        setting.setSortOrder(sortOrder);
        return setting;
    }

    private String normalizeKey(String groupTitle, String label) {
        return (groupTitle + "_" + label).toLowerCase()
                .replace(" ", "_")
                .replace("/", "_")
                .replace("-", "_");
    }

    private ApiSchemas.SettingsResponse toResponse(List<SiteSetting> settings) {
        Map<String, List<ApiSchemas.SettingItemResponse>> grouped = new LinkedHashMap<>();
        for (SiteSetting setting : settings) {
            grouped.computeIfAbsent(setting.getGroupName(), ignored -> new ArrayList<>())
                    .add(new ApiSchemas.SettingItemResponse(setting.getLabel(), setting.getValue()));
        }

        return new ApiSchemas.SettingsResponse(
                grouped.entrySet().stream()
                        .map(entry -> new ApiSchemas.SettingGroupResponse(entry.getKey(), entry.getValue()))
                        .toList()
        );
    }
}

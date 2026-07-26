package com.pilot.omyohan_blog.controller;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;

public final class ApiSchemas {

    private ApiSchemas() {
    }

    @Schema(description = "Board type")
    public enum BoardType {
        GENERAL,
        THUMBNAIL,
        PORTFOLIO
    }

    public record BoardCreateRequest(
            @Schema(example = "공지사항") String name,
            @Schema(example = "notice") String slug,
            @Schema(example = "GENERAL") BoardType boardType,
            @Schema(example = "사이트 공지 게시판") String description,
            @Schema(example = "true") Boolean isActive,
            @Schema(example = "true") Boolean useComment
    ) {
    }

    public record BoardUpdateRequest(
            @Schema(example = "포트폴리오") String name,
            @Schema(example = "portfolio") String slug,
            @Schema(example = "PORTFOLIO") BoardType boardType,
            @Schema(example = "프로젝트 포트폴리오 게시판") String description,
            @Schema(example = "true") Boolean isActive,
            @Schema(example = "true") Boolean useComment
    ) {
    }

    public record BoardResponse(
            Long id,
            String name,
            String slug,
            BoardType boardType,
            String description,
            Boolean isActive,
            Boolean useComment,
            Long postCount,
            OffsetDateTime createdAt,
            OffsetDateTime updatedAt
    ) {
    }

    public record MenuCreateRequest(
            @Schema(example = "1") Long parentId,
            @Schema(example = "2") Long boardId,
            @Schema(example = "포트폴리오") String name,
            @Schema(example = "portfolio") String slug,
            @Schema(example = "1") Integer depth,
            @Schema(example = "10") Integer sortOrder,
            @Schema(example = "true") Boolean isVisible
    ) {
    }

    public record MenuUpdateRequest(
            @Schema(example = "1") Long parentId,
            @Schema(example = "2") Long boardId,
            @Schema(example = "프로젝트") String name,
            @Schema(example = "projects") String slug,
            @Schema(example = "1") Integer depth,
            @Schema(example = "20") Integer sortOrder,
            @Schema(example = "true") Boolean isVisible
    ) {
    }

    public record MenuResponse(
            Long id,
            Long parentId,
            Long boardId,
            String name,
            String slug,
            Integer depth,
            Integer sortOrder,
            Boolean isVisible,
            OffsetDateTime createdAt,
            OffsetDateTime updatedAt
    ) {
    }

    public record PostAttachmentRequest(
            @Schema(example = "cover.png") String fileName,
            @Schema(example = "/upload/posts/1/cover.png") String filePath,
            @Schema(example = "https://cdn.example.com/posts/1/cover.png") String fileUrl,
            @Schema(example = "image/png") String mimeType,
            @Schema(example = "204800") Long fileSize,
            @Schema(example = "1") Integer sortOrder
    ) {
    }

    public record PostAttachmentResponse(
            Long id,
            String fileName,
            String filePath,
            String fileUrl,
            String mimeType,
            Long fileSize,
            Integer sortOrder
    ) {
    }

    public record PostCreateRequest(
            @Schema(example = "2") Long boardId,
            @Schema(example = "첫 번째 포트폴리오") String title,
            @Schema(example = "<p>프로젝트 소개</p>") String content,
            @Schema(example = "프로젝트 요약") String summary,
            @Schema(example = "https://cdn.example.com/thumb.png") String thumbnailUrl,
            @Schema(example = "Acme Corp") String portfolioClient,
            @Schema(example = "2026-01-01") LocalDate portfolioStartedOn,
            @Schema(example = "2026-03-31") LocalDate portfolioEndedOn,
            @Schema(example = "false") Boolean isPinned,
            @Schema(example = "true") Boolean isPublished,
            @ArraySchema(schema = @Schema(implementation = PostAttachmentRequest.class))
            List<PostAttachmentRequest> attachments
    ) {
    }

    public record PostUpdateRequest(
            @Schema(example = "수정된 제목") String title,
            @Schema(example = "<p>수정된 내용</p>") String content,
            @Schema(example = "수정된 요약") String summary,
            @Schema(example = "https://cdn.example.com/updated-thumb.png") String thumbnailUrl,
            @Schema(example = "Acme Corp") String portfolioClient,
            @Schema(example = "2026-01-01") LocalDate portfolioStartedOn,
            @Schema(example = "2026-04-10") LocalDate portfolioEndedOn,
            @Schema(example = "true") Boolean isPinned,
            @Schema(example = "true") Boolean isPublished,
            @ArraySchema(schema = @Schema(implementation = PostAttachmentRequest.class))
            List<PostAttachmentRequest> attachments
    ) {
    }

    public record PostResponse(
            Long id,
            Long boardId,
            String title,
            String content,
            String summary,
            String thumbnailUrl,
            String portfolioClient,
            LocalDate portfolioStartedOn,
            LocalDate portfolioEndedOn,
            Boolean isPinned,
            Boolean isPublished,
            Integer viewCount,
            List<PostAttachmentResponse> attachments,
            OffsetDateTime createdAt,
            OffsetDateTime updatedAt
    ) {
    }

    public record AnonymousCommentCreateRequest(
            @Schema(example = "익명1") String authorName,
            @Schema(example = "comment1234") String authorPassword,
            @Schema(example = "좋은 글 잘 봤습니다.") String content,
            @Schema(example = "1") Long parentId
    ) {
    }

    public record AnonymousCommentUpdateRequest(
            @Schema(example = "comment1234") String authorPassword,
            @Schema(example = "수정된 댓글 내용입니다.") String content
    ) {
    }

    public record AnonymousCommentDeleteRequest(
            @Schema(example = "comment1234") String authorPassword
    ) {
    }

    public record CommentResponse(
            Long id,
            Long postId,
            Long parentId,
            String authorName,
            String content,
            Boolean isDeleted,
            OffsetDateTime createdAt,
            OffsetDateTime updatedAt
    ) {
    }

    public record MessageResponse(
            String message
    ) {
    }

    public record SettingItemResponse(
            String label,
            String value
    ) {
    }

    public record SettingGroupResponse(
            String title,
            List<SettingItemResponse> items
    ) {
    }

    public record SettingsResponse(
            List<SettingGroupResponse> groups
    ) {
    }
}

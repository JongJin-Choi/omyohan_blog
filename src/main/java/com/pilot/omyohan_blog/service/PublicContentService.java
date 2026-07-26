package com.pilot.omyohan_blog.service;

import com.pilot.omyohan_blog.controller.ApiSchemas;
import com.pilot.omyohan_blog.domain.Menu;
import com.pilot.omyohan_blog.domain.Post;
import com.pilot.omyohan_blog.repository.MenuRepository;
import com.pilot.omyohan_blog.repository.PostRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class PublicContentService {

    private final MenuRepository menuRepository;
    private final PostRepository postRepository;

    public PublicContentService(MenuRepository menuRepository, PostRepository postRepository) {
        this.menuRepository = menuRepository;
        this.postRepository = postRepository;
    }

    public List<ApiSchemas.MenuResponse> getVisibleMenus() {
        return menuRepository.findAllByIsVisibleTrueOrderBySortOrderAscIdAsc().stream()
                .map(menu -> new ApiSchemas.MenuResponse(
                        menu.getId(),
                        menu.getParent() == null ? null : menu.getParent().getId(),
                        menu.getBoard() == null ? null : menu.getBoard().getId(),
                        menu.getName(),
                        menu.getSlug(),
                        menu.getDepth(),
                        menu.getSortOrder(),
                        menu.isVisible(),
                        menu.getCreatedAt(),
                        menu.getUpdatedAt()
                ))
                .toList();
    }

    public List<ApiSchemas.PostResponse> getPostsByMenu(String menuSlug) {
        Menu menu = menuRepository.findBySlugAndIsVisibleTrue(menuSlug)
                .orElseThrow(() -> ServiceSupport.badRequest("Visible menu not found: " + menuSlug));
        return postRepository.findAllByBoardIdAndIsPublishedTrueAndDeletedAtIsNullOrderByIsPinnedDescCreatedAtDesc(menu.getBoard().getId())
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public ApiSchemas.PostResponse getPostDetail(String menuSlug, Long postId) {
        Menu menu = menuRepository.findBySlugAndIsVisibleTrue(menuSlug)
                .orElseThrow(() -> ServiceSupport.badRequest("Visible menu not found: " + menuSlug));
        Post post = postRepository.findByIdAndDeletedAtIsNull(postId)
                .orElseThrow(() -> ServiceSupport.notFound("Post", postId));
        if (!post.isPublished() || !post.getBoard().getId().equals(menu.getBoard().getId())) {
            throw ServiceSupport.badRequest("Published post not found for menu: " + menuSlug);
        }
        return toResponse(post);
    }

    private ApiSchemas.PostResponse toResponse(Post post) {
        return new ApiSchemas.PostResponse(
                post.getId(),
                post.getBoard().getId(),
                post.getTitle(),
                post.getContent(),
                post.getSummary(),
                post.getThumbnailUrl(),
                post.getPortfolioClient(),
                post.getPortfolioStartedOn(),
                post.getPortfolioEndedOn(),
                post.isPinned(),
                post.isPublished(),
                post.getViewCount(),
                post.getAttachments().stream()
                        .map(item -> new ApiSchemas.PostAttachmentResponse(
                                item.getId(), item.getFileName(), item.getFilePath(), item.getFileUrl(),
                                item.getMimeType(), item.getFileSize(), item.getSortOrder()
                        ))
                        .toList(),
                post.getCreatedAt(),
                post.getUpdatedAt()
        );
    }
}

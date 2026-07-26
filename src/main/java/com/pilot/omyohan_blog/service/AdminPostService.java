package com.pilot.omyohan_blog.service;

import com.pilot.omyohan_blog.controller.ApiSchemas;
import com.pilot.omyohan_blog.domain.Board;
import com.pilot.omyohan_blog.domain.Post;
import com.pilot.omyohan_blog.domain.PostAttachment;
import com.pilot.omyohan_blog.repository.BoardRepository;
import com.pilot.omyohan_blog.repository.PostRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class AdminPostService {

    private final PostRepository postRepository;
    private final BoardRepository boardRepository;

    public AdminPostService(PostRepository postRepository, BoardRepository boardRepository) {
        this.postRepository = postRepository;
        this.boardRepository = boardRepository;
    }

    public List<ApiSchemas.PostResponse> getPosts(Long boardId) {
        List<Post> posts = boardId == null
                ? postRepository.findAllByDeletedAtIsNullOrderByUpdatedAtDesc()
                : postRepository.findAllByBoardIdAndDeletedAtIsNullOrderByUpdatedAtDesc(boardId);
        return posts.stream().map(this::toResponse).toList();
    }

    public ApiSchemas.PostResponse getPost(Long postId) {
        return toResponse(findPost(postId));
    }

    @Transactional
    public ApiSchemas.PostResponse createPost(ApiSchemas.PostCreateRequest request) {
        Post post = new Post();
        apply(post, request.boardId(), request.title(), request.content(), request.summary(), request.thumbnailUrl(),
                request.portfolioClient(), request.portfolioStartedOn(), request.portfolioEndedOn(),
                request.isPinned(), request.isPublished(), request.attachments());
        return toResponse(postRepository.save(post));
    }

    @Transactional
    public ApiSchemas.PostResponse updatePost(Long postId, ApiSchemas.PostUpdateRequest request) {
        Post post = findPost(postId);
        apply(post, post.getBoard().getId(), request.title(), request.content(), request.summary(), request.thumbnailUrl(),
                request.portfolioClient(), request.portfolioStartedOn(), request.portfolioEndedOn(),
                request.isPinned(), request.isPublished(), request.attachments());
        return toResponse(post);
    }

    @Transactional
    public ApiSchemas.MessageResponse deletePost(Long postId) {
        Post post = findPost(postId);
        post.setDeletedAt(OffsetDateTime.now());
        return new ApiSchemas.MessageResponse("Post " + postId + " deleted.");
    }

    private Post findPost(Long postId) {
        return postRepository.findByIdAndDeletedAtIsNull(postId)
                .orElseThrow(() -> ServiceSupport.notFound("Post", postId));
    }

    private Board findBoard(Long boardId) {
        return boardRepository.findById(boardId)
                .orElseThrow(() -> ServiceSupport.notFound("Board", boardId));
    }

    private void apply(
            Post post,
            Long boardId,
            String title,
            String content,
            String summary,
            String thumbnailUrl,
            String portfolioClient,
            java.time.LocalDate portfolioStartedOn,
            java.time.LocalDate portfolioEndedOn,
            Boolean isPinned,
            Boolean isPublished,
            List<ApiSchemas.PostAttachmentRequest> attachments
    ) {
        post.setBoard(findBoard(boardId));
        post.setTitle(title);
        post.setContent(content);
        post.setSummary(summary);
        post.setThumbnailUrl(thumbnailUrl);
        post.setPortfolioClient(portfolioClient);
        post.setPortfolioStartedOn(portfolioStartedOn);
        post.setPortfolioEndedOn(portfolioEndedOn);
        post.setPinned(isPinned != null && isPinned);
        post.setPublished(isPublished == null || isPublished);
        post.clearAttachments();
        if (attachments != null) {
            for (ApiSchemas.PostAttachmentRequest attachment : attachments) {
                PostAttachment item = new PostAttachment();
                item.setFileName(attachment.fileName());
                item.setFilePath(attachment.filePath());
                item.setFileUrl(attachment.fileUrl());
                item.setMimeType(attachment.mimeType());
                item.setFileSize(attachment.fileSize() == null ? 0 : attachment.fileSize());
                item.setSortOrder(attachment.sortOrder() == null ? 0 : attachment.sortOrder());
                post.addAttachment(item);
            }
        }
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
                                item.getId(),
                                item.getFileName(),
                                item.getFilePath(),
                                item.getFileUrl(),
                                item.getMimeType(),
                                item.getFileSize(),
                                item.getSortOrder()
                        ))
                        .toList(),
                post.getCreatedAt(),
                post.getUpdatedAt()
        );
    }
}

package com.pilot.omyohan_blog.service;

import com.pilot.omyohan_blog.controller.ApiSchemas;
import com.pilot.omyohan_blog.domain.Board;
import com.pilot.omyohan_blog.repository.BoardRepository;
import com.pilot.omyohan_blog.repository.PostRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class AdminBoardService {

    private final BoardRepository boardRepository;
    private final PostRepository postRepository;

    public AdminBoardService(BoardRepository boardRepository, PostRepository postRepository) {
        this.boardRepository = boardRepository;
        this.postRepository = postRepository;
    }

    public List<ApiSchemas.BoardResponse> getBoards() {
        return boardRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    public ApiSchemas.BoardResponse getBoard(Long boardId) {
        return toResponse(findBoard(boardId));
    }

    @Transactional
    public ApiSchemas.BoardResponse createBoard(ApiSchemas.BoardCreateRequest request) {
        if (boardRepository.existsBySlug(request.slug())) {
            throw ServiceSupport.badRequest("Board slug already exists: " + request.slug());
        }

        Board board = new Board();
        apply(board, request.name(), request.slug(), request.boardType(), request.description(), request.isActive(), request.useComment());
        return toResponse(boardRepository.save(board));
    }

    @Transactional
    public ApiSchemas.BoardResponse updateBoard(Long boardId, ApiSchemas.BoardUpdateRequest request) {
        Board board = findBoard(boardId);
        if (boardRepository.existsBySlugAndIdNot(request.slug(), boardId)) {
            throw ServiceSupport.badRequest("Board slug already exists: " + request.slug());
        }

        apply(board, request.name(), request.slug(), request.boardType(), request.description(), request.isActive(), request.useComment());
        return toResponse(board);
    }

    @Transactional
    public ApiSchemas.MessageResponse deleteBoard(Long boardId) {
        Board board = findBoard(boardId);
        boardRepository.delete(board);
        return new ApiSchemas.MessageResponse("Board " + boardId + " deleted.");
    }

    private Board findBoard(Long boardId) {
        return boardRepository.findById(boardId)
                .orElseThrow(() -> ServiceSupport.notFound("Board", boardId));
    }

    private void apply(Board board, String name, String slug, ApiSchemas.BoardType boardType, String description, Boolean isActive, Boolean useComment) {
        board.setName(name);
        board.setSlug(slug);
        board.setBoardType(boardType);
        board.setDescription(description);
        board.setActive(isActive == null || isActive);
        board.setUseComment(useComment == null || useComment);
    }

    private ApiSchemas.BoardResponse toResponse(Board board) {
        long postCount = postRepository.findAllByBoardIdAndDeletedAtIsNullOrderByUpdatedAtDesc(board.getId()).size();
        return new ApiSchemas.BoardResponse(
                board.getId(),
                board.getName(),
                board.getSlug(),
                board.getBoardType(),
                board.getDescription(),
                board.isActive(),
                board.isUseComment(),
                postCount,
                board.getCreatedAt(),
                board.getUpdatedAt()
        );
    }
}

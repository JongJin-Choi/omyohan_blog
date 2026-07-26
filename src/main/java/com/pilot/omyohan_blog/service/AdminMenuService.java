package com.pilot.omyohan_blog.service;

import com.pilot.omyohan_blog.controller.ApiSchemas;
import com.pilot.omyohan_blog.domain.Board;
import com.pilot.omyohan_blog.domain.Menu;
import com.pilot.omyohan_blog.repository.BoardRepository;
import com.pilot.omyohan_blog.repository.MenuRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class AdminMenuService {

    private final MenuRepository menuRepository;
    private final BoardRepository boardRepository;

    public AdminMenuService(MenuRepository menuRepository, BoardRepository boardRepository) {
        this.menuRepository = menuRepository;
        this.boardRepository = boardRepository;
    }

    public List<ApiSchemas.MenuResponse> getMenus() {
        return menuRepository.findAllByOrderBySortOrderAscIdAsc().stream()
                .map(this::toResponse)
                .toList();
    }

    public ApiSchemas.MenuResponse getMenu(Long menuId) {
        return toResponse(findMenu(menuId));
    }

    @Transactional
    public ApiSchemas.MenuResponse createMenu(ApiSchemas.MenuCreateRequest request) {
        if (menuRepository.existsBySlug(request.slug())) {
            throw ServiceSupport.badRequest("Menu slug already exists: " + request.slug());
        }
        Menu menu = new Menu();
        apply(menu, request.parentId(), request.boardId(), request.name(), request.slug(), request.depth(), request.sortOrder(), request.isVisible());
        return toResponse(menuRepository.save(menu));
    }

    @Transactional
    public ApiSchemas.MenuResponse updateMenu(Long menuId, ApiSchemas.MenuUpdateRequest request) {
        Menu menu = findMenu(menuId);
        if (menuRepository.existsBySlugAndIdNot(request.slug(), menuId)) {
            throw ServiceSupport.badRequest("Menu slug already exists: " + request.slug());
        }
        apply(menu, request.parentId(), request.boardId(), request.name(), request.slug(), request.depth(), request.sortOrder(), request.isVisible());
        return toResponse(menu);
    }

    @Transactional
    public ApiSchemas.MessageResponse deleteMenu(Long menuId) {
        menuRepository.delete(findMenu(menuId));
        return new ApiSchemas.MessageResponse("Menu " + menuId + " deleted.");
    }

    private Menu findMenu(Long menuId) {
        return menuRepository.findById(menuId)
                .orElseThrow(() -> ServiceSupport.notFound("Menu", menuId));
    }

    private Board getBoard(Long boardId) {
        return boardId == null ? null : boardRepository.findById(boardId)
                .orElseThrow(() -> ServiceSupport.notFound("Board", boardId));
    }

    private Menu getParent(Long parentId) {
        return parentId == null ? null : menuRepository.findById(parentId)
                .orElseThrow(() -> ServiceSupport.notFound("Menu", parentId));
    }

    private void apply(Menu menu, Long parentId, Long boardId, String name, String slug, Integer depth, Integer sortOrder, Boolean isVisible) {
        menu.setParent(getParent(parentId));
        menu.setBoard(getBoard(boardId));
        menu.setName(name);
        menu.setSlug(slug);
        menu.setDepth(depth);
        menu.setSortOrder(sortOrder);
        menu.setVisible(isVisible == null || isVisible);
    }

    private ApiSchemas.MenuResponse toResponse(Menu menu) {
        return new ApiSchemas.MenuResponse(
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
        );
    }
}

package com.pilot.omyohan_blog.repository;

import com.pilot.omyohan_blog.domain.Menu;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MenuRepository extends JpaRepository<Menu, Long> {
    List<Menu> findAllByOrderBySortOrderAscIdAsc();
    List<Menu> findAllByIsVisibleTrueOrderBySortOrderAscIdAsc();
    Optional<Menu> findBySlugAndIsVisibleTrue(String slug);
    boolean existsBySlugAndIdNot(String slug, Long id);
    boolean existsBySlug(String slug);
}

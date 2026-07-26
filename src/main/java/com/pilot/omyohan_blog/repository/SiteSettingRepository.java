package com.pilot.omyohan_blog.repository;

import com.pilot.omyohan_blog.domain.SiteSetting;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SiteSettingRepository extends JpaRepository<SiteSetting, Long> {
    List<SiteSetting> findAllByOrderByGroupNameAscSortOrderAscIdAsc();
}

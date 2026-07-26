package com.pilot.omyohan_blog.domain;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "posts")
public class Post extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_id", nullable = false)
    private Board board;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(nullable = false, columnDefinition = "text")
    private String content;

    @Column(length = 500)
    private String summary;

    @Column(name = "thumbnail_url", length = 500)
    private String thumbnailUrl;

    @Column(name = "portfolio_client", length = 150)
    private String portfolioClient;

    @Column(name = "portfolio_started_on")
    private LocalDate portfolioStartedOn;

    @Column(name = "portfolio_ended_on")
    private LocalDate portfolioEndedOn;

    @Column(name = "is_pinned", nullable = false)
    private boolean isPinned;

    @Column(name = "is_published", nullable = false)
    private boolean isPublished = true;

    @Column(name = "view_count", nullable = false)
    private int viewCount;

    @Column(name = "deleted_at")
    private OffsetDateTime deletedAt;

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("sortOrder asc, id asc")
    private List<PostAttachment> attachments = new ArrayList<>();

    public Long getId() {
        return id;
    }

    public Board getBoard() {
        return board;
    }

    public void setBoard(Board board) {
        this.board = board;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    public void setThumbnailUrl(String thumbnailUrl) {
        this.thumbnailUrl = thumbnailUrl;
    }

    public String getPortfolioClient() {
        return portfolioClient;
    }

    public void setPortfolioClient(String portfolioClient) {
        this.portfolioClient = portfolioClient;
    }

    public LocalDate getPortfolioStartedOn() {
        return portfolioStartedOn;
    }

    public void setPortfolioStartedOn(LocalDate portfolioStartedOn) {
        this.portfolioStartedOn = portfolioStartedOn;
    }

    public LocalDate getPortfolioEndedOn() {
        return portfolioEndedOn;
    }

    public void setPortfolioEndedOn(LocalDate portfolioEndedOn) {
        this.portfolioEndedOn = portfolioEndedOn;
    }

    public boolean isPinned() {
        return isPinned;
    }

    public void setPinned(boolean pinned) {
        isPinned = pinned;
    }

    public boolean isPublished() {
        return isPublished;
    }

    public void setPublished(boolean published) {
        isPublished = published;
    }

    public int getViewCount() {
        return viewCount;
    }

    public void setViewCount(int viewCount) {
        this.viewCount = viewCount;
    }

    public OffsetDateTime getDeletedAt() {
        return deletedAt;
    }

    public void setDeletedAt(OffsetDateTime deletedAt) {
        this.deletedAt = deletedAt;
    }

    public List<PostAttachment> getAttachments() {
        return attachments;
    }

    public void clearAttachments() {
        attachments.clear();
    }

    public void addAttachment(PostAttachment attachment) {
        attachment.setPost(this);
        attachments.add(attachment);
    }
}

-- Admin and user-facing SQL examples for PostgreSQL

-- 1. board

-- 게시판 추가
insert into boards (
    name,
    slug,
    board_type,
    description,
    is_active,
    use_comment
) values (
    :name,
    :slug,
    :boardType,
    :description,
    coalesce(:isActive, true),
    coalesce(:useComment, true)
)
returning *;

-- 게시판 목록
select
    b.id,
    b.name,
    b.slug,
    b.board_type,
    b.description,
    b.is_active,
    b.use_comment,
    count(p.id) filter (where p.deleted_at is null) as post_count,
    b.created_at,
    b.updated_at
from boards b
left join posts p on p.board_id = b.id
group by b.id
order by b.created_at desc;

-- 게시판 수정
update boards
set
    name = :name,
    slug = :slug,
    board_type = :boardType,
    description = :description,
    is_active = :isActive,
    use_comment = :useComment,
    updated_at = now()
where id = :boardId
returning *;

-- 게시판 삭제
delete from boards
where id = :boardId;

-- 2. menu

-- 메뉴 추가
insert into menus (
    parent_id,
    board_id,
    name,
    slug,
    depth,
    sort_order,
    is_visible
) values (
    :parentId,
    :boardId,
    :name,
    :slug,
    :depth,
    :sortOrder,
    coalesce(:isVisible, true)
)
returning *;

-- 메뉴 트리 조회
select
    m.id,
    m.parent_id,
    pm.name as parent_name,
    m.board_id,
    b.name as board_name,
    b.board_type,
    m.name,
    m.slug,
    m.depth,
    m.sort_order,
    m.is_visible
from menus m
left join menus pm on pm.id = m.parent_id
left join boards b on b.id = m.board_id
order by
    coalesce(m.parent_id, m.id),
    m.depth,
    m.sort_order,
    m.id;

-- 메뉴 수정
update menus
set
    parent_id = :parentId,
    board_id = :boardId,
    name = :name,
    slug = :slug,
    depth = :depth,
    sort_order = :sortOrder,
    is_visible = :isVisible,
    updated_at = now()
where id = :menuId
returning *;

-- 메뉴 삭제
delete from menus
where id = :menuId;

-- 3. post

-- 게시글 추가
insert into posts (
    board_id,
    title,
    content,
    summary,
    thumbnail_url,
    portfolio_client,
    portfolio_started_on,
    portfolio_ended_on,
    is_pinned,
    is_published
) values (
    :boardId,
    :title,
    :content,
    :summary,
    :thumbnailUrl,
    :portfolioClient,
    :portfolioStartedOn,
    :portfolioEndedOn,
    coalesce(:isPinned, false),
    coalesce(:isPublished, true)
)
returning *;

-- 게시글 목록(관리자)
select
    p.id,
    p.board_id,
    b.name as board_name,
    b.board_type,
    p.title,
    p.is_pinned,
    p.is_published,
    p.view_count,
    p.created_at,
    p.updated_at
from posts p
join boards b on b.id = p.board_id
where p.deleted_at is null
  and (:boardId is null or p.board_id = :boardId)
order by p.is_pinned desc, p.created_at desc;

-- 게시글 상세(관리자)
select
    p.*,
    b.name as board_name,
    b.board_type
from posts p
join boards b on b.id = p.board_id
where p.id = :postId
  and p.deleted_at is null;

-- 게시글 수정
update posts
set
    title = :title,
    content = :content,
    summary = :summary,
    thumbnail_url = :thumbnailUrl,
    portfolio_client = :portfolioClient,
    portfolio_started_on = :portfolioStartedOn,
    portfolio_ended_on = :portfolioEndedOn,
    is_pinned = :isPinned,
    is_published = :isPublished,
    updated_at = now()
where id = :postId
  and deleted_at is null
returning *;

-- 게시글 삭제(소프트 삭제)
update posts
set
    deleted_at = now(),
    updated_at = now()
where id = :postId
  and deleted_at is null
returning *;

-- 첨부파일 추가
insert into post_attachments (
    post_id,
    file_name,
    file_path,
    file_url,
    mime_type,
    file_size,
    sort_order
) values (
    :postId,
    :fileName,
    :filePath,
    :fileUrl,
    :mimeType,
    :fileSize,
    :sortOrder
)
returning *;

-- 게시글 첨부파일 조회
select *
from post_attachments
where post_id = :postId
order by sort_order asc, id asc;

-- 4. user page

-- 메뉴별 게시글 목록 조회
select
    p.id,
    p.title,
    p.summary,
    p.thumbnail_url,
    p.portfolio_client,
    p.portfolio_started_on,
    p.portfolio_ended_on,
    p.view_count,
    p.created_at
from menus m
join boards b on b.id = m.board_id
join posts p on p.board_id = b.id
where m.slug = :menuSlug
  and m.is_visible = true
  and b.is_active = true
  and p.is_published = true
  and p.deleted_at is null
order by p.is_pinned desc, p.created_at desc
limit :limit offset :offset;

-- 게시글 상세 조회
select
    p.*,
    b.name as board_name,
    b.board_type,
    m.name as menu_name,
    m.slug as menu_slug
from posts p
join boards b on b.id = p.board_id
join menus m on m.board_id = b.id
where p.id = :postId
  and m.slug = :menuSlug
  and m.is_visible = true
  and b.is_active = true
  and p.is_published = true
  and p.deleted_at is null;

-- 조회수 증가
update posts
set view_count = view_count + 1
where id = :postId
  and deleted_at is null;

-- 댓글 목록
select
    c.id,
    c.post_id,
    c.parent_id,
    c.author_name,
    c.content,
    c.is_deleted,
    c.created_at
from comments c
join posts p on p.id = c.post_id
where c.post_id = :postId
  and p.deleted_at is null
order by c.created_at asc, c.id asc;

-- 익명 댓글 작성
insert into comments (
    post_id,
    parent_id,
    author_name,
    author_password,
    content
) values (
    :postId,
    :parentId,
    :authorName,
    crypt(:authorPassword, gen_salt('bf')),
    :content
)
returning id, post_id, parent_id, author_name, content, created_at;

-- 익명 댓글 수정
update comments
set
    content = :content,
    updated_at = now()
where id = :commentId
  and author_password = crypt(:authorPassword, author_password)
returning id, post_id, parent_id, author_name, content, updated_at;

-- 익명 댓글 삭제
update comments
set
    is_deleted = true,
    content = '[deleted]',
    updated_at = now()
where id = :commentId
  and author_password = crypt(:authorPassword, author_password)
returning id, is_deleted, updated_at;

-- 5. optional extension for crypt()
create extension if not exists pgcrypto;

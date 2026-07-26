-- PostgreSQL schema for blog admin/user features

create table if not exists boards (
    id bigserial primary key,
    name varchar(100) not null,
    slug varchar(100) not null unique,
    board_type varchar(20) not null,
    description varchar(500),
    is_active boolean not null default true,
    use_comment boolean not null default true,
    created_at timestamptz not null default now(),
    updated_at timestamptz not null default now(),
    constraint ck_boards_type
        check (board_type in ('GENERAL', 'THUMBNAIL', 'PORTFOLIO'))
);

create table if not exists menus (
    id bigserial primary key,
    parent_id bigint references menus(id) on delete set null,
    board_id bigint references boards(id) on delete set null,
    name varchar(100) not null,
    slug varchar(100) not null unique,
    depth integer not null default 1,
    sort_order integer not null default 0,
    is_visible boolean not null default true,
    created_at timestamptz not null default now(),
    updated_at timestamptz not null default now(),
    constraint ck_menus_depth
        check (depth between 1 and 3)
);

create table if not exists posts (
    id bigserial primary key,
    board_id bigint not null references boards(id) on delete cascade,
    title varchar(200) not null,
    content text not null,
    summary varchar(500),
    thumbnail_url varchar(500),
    portfolio_client varchar(150),
    portfolio_started_on date,
    portfolio_ended_on date,
    is_pinned boolean not null default false,
    is_published boolean not null default true,
    view_count integer not null default 0,
    created_at timestamptz not null default now(),
    updated_at timestamptz not null default now(),
    deleted_at timestamptz,
    constraint ck_posts_view_count check (view_count >= 0),
    constraint ck_posts_portfolio_period
        check (
            portfolio_started_on is null
            or portfolio_ended_on is null
            or portfolio_started_on <= portfolio_ended_on
        )
);

create table if not exists post_attachments (
    id bigserial primary key,
    post_id bigint not null references posts(id) on delete cascade,
    file_name varchar(255) not null,
    file_path varchar(500) not null,
    file_url varchar(500),
    mime_type varchar(100),
    file_size bigint not null default 0,
    sort_order integer not null default 0,
    created_at timestamptz not null default now(),
    constraint ck_post_attachments_file_size check (file_size >= 0)
);

create table if not exists comments (
    id bigserial primary key,
    post_id bigint not null references posts(id) on delete cascade,
    parent_id bigint references comments(id) on delete cascade,
    author_name varchar(100) not null,
    author_password varchar(255) not null,
    content varchar(2000) not null,
    is_deleted boolean not null default false,
    created_at timestamptz not null default now(),
    updated_at timestamptz not null default now()
);

create table if not exists site_settings (
    id bigserial primary key,
    group_name varchar(100) not null,
    setting_key varchar(100) not null unique,
    label varchar(150) not null,
    value varchar(1000) not null,
    sort_order integer not null default 0,
    created_at timestamptz not null default now(),
    updated_at timestamptz not null default now()
);

create index if not exists idx_menus_parent_sort
    on menus(parent_id, sort_order);

create index if not exists idx_menus_board_id
    on menus(board_id);

create index if not exists idx_posts_board_published_created
    on posts(board_id, is_published, created_at desc)
    where deleted_at is null;

create index if not exists idx_posts_board_pinned_created
    on posts(board_id, is_pinned desc, created_at desc)
    where deleted_at is null;

create index if not exists idx_comments_post_created
    on comments(post_id, created_at asc);

create index if not exists idx_attachments_post_sort
    on post_attachments(post_id, sort_order);

create index if not exists idx_site_settings_group_sort
    on site_settings(group_name, sort_order);

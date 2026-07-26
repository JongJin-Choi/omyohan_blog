export const summaryStats = [
  { label: "운영 중 게시판", value: "3", description: "일반 / 썸네일 / 포트폴리오" },
  { label: "노출 메뉴", value: "5", description: "숨김 메뉴 1개" },
  { label: "게시글 수", value: "24", description: "이번 주 신규 작성 5개" },
  { label: "검토 댓글", value: "2", description: "익명 댓글 확인 필요" }
];

export const boards = [
  { id: 1, name: "공지사항", slug: "notice", type: "GENERAL", state: "운영중", posts: 12, comments: true, updatedAt: "2026-07-26" },
  { id: 2, name: "썸네일 갤러리", slug: "gallery", type: "THUMBNAIL", state: "운영중", posts: 8, comments: true, updatedAt: "2026-07-25" },
  { id: 3, name: "포트폴리오", slug: "portfolio", type: "PORTFOLIO", state: "기획중", posts: 4, comments: false, updatedAt: "2026-07-24" }
];

export const menus = [
  { id: 1, name: "공지사항", slug: "notice", board: "공지사항", depth: 1, visible: true, sortOrder: 1 },
  { id: 2, name: "프로젝트", slug: "portfolio", board: "포트폴리오", depth: 1, visible: true, sortOrder: 2 },
  { id: 3, name: "갤러리", slug: "gallery", board: "썸네일 갤러리", depth: 1, visible: true, sortOrder: 3 },
  { id: 4, name: "아카이브", slug: "archive", board: "공지사항", depth: 2, visible: false, sortOrder: 4 }
];

export const posts = [
  { id: 101, title: "여름 리뉴얼 공지", board: "공지사항", type: "GENERAL", published: true, pinned: true, author: "admin", updatedAt: "2026-07-26" },
  { id: 102, title: "메인 썸네일 샘플 모음", board: "썸네일 갤러리", type: "THUMBNAIL", published: true, pinned: false, author: "editor", updatedAt: "2026-07-25" },
  { id: 103, title: "브랜딩 프로젝트 소개", board: "포트폴리오", type: "PORTFOLIO", published: false, pinned: false, author: "admin", updatedAt: "2026-07-24" }
];

export const comments = [
  { id: 201, author: "익명A", post: "브랜딩 프로젝트 소개", content: "포트폴리오 대표 이미지가 좋습니다.", state: "대기", createdAt: "2026-07-26 09:20" },
  { id: 202, author: "익명B", post: "여름 리뉴얼 공지", content: "댓글 수정 기능도 있으면 좋겠습니다.", state: "확인", createdAt: "2026-07-26 10:05" }
];

export const quickTasks = [
  "포트폴리오 게시판 상태 확정",
  "비노출 메뉴 공개 여부 검토",
  "신규 댓글 2건 처리",
  "메인 공지글 상단 고정 유지 확인"
];

export const settingGroups = [
  {
    title: "기본 운영 설정",
    items: [
      { label: "사이트 이름", value: "Omyohan Blog" },
      { label: "기본 댓글 정책", value: "익명 댓글 허용" },
      { label: "기본 게시글 상태", value: "임시 저장 후 발행" }
    ]
  },
  {
    title: "콘텐츠 정책",
    items: [
      { label: "일반 게시판", value: "본문 중심" },
      { label: "썸네일 게시판", value: "대표 이미지 필수" },
      { label: "포트폴리오 게시판", value: "기간 / 클라이언트 정보 포함" }
    ]
  }
];

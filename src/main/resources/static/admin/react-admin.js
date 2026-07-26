(function () {
    const e = React.createElement;

    const boards = [
        { id: 1, name: "공지사항", type: "GENERAL", posts: 12, comments: true, state: "운영중" },
        { id: 2, name: "썸네일 갤러리", type: "THUMBNAIL", posts: 8, comments: true, state: "운영중" },
        { id: 3, name: "포트폴리오", type: "PORTFOLIO", posts: 4, comments: false, state: "검수중" }
    ];

    const menus = [
        { id: 1, name: "공지사항", board: "공지사항", depth: 1, visible: true },
        { id: 2, name: "프로젝트", board: "포트폴리오", depth: 1, visible: true },
        { id: 3, name: "썸네일", board: "썸네일 갤러리", depth: 2, visible: false }
    ];

    const comments = [
        { id: 1, author: "익명1", post: "첫 번째 포트폴리오", text: "레이아웃이 인상적입니다.", status: "신규" },
        { id: 2, author: "익명2", post: "공지 업데이트", text: "댓글 수정 기능도 필요합니다.", status: "확인" }
    ];

    function boardTag(type) {
        if (type === "PORTFOLIO") return "tag portfolio";
        if (type === "THUMBNAIL") return "tag thumbnail";
        return "tag general";
    }

    function App() {
        return e("div", { className: "admin-shell" },
            e("aside", { className: "sidebar" },
                e("div", { className: "sidebar-card panel" },
                    e("div", { className: "app-title" },
                        e("p", { className: "eyebrow" }, "React Admin"),
                        e("h1", null, "블로그 운영실"),
                        e("p", { className: "hero-copy" }, "게시판, 메뉴, 댓글 흐름을 한 화면에서 관리하는 React 시안")
                    ),
                    e("ul", { className: "nav-list" },
                        e("li", { className: "nav-item active" }, "대시보드"),
                        e("li", { className: "nav-item" }, "게시판 관리"),
                        e("li", { className: "nav-item" }, "메뉴 관리"),
                        e("li", { className: "nav-item" }, "게시글 작성"),
                        e("li", { className: "nav-item" }, "댓글 검토")
                    ),
                    e("div", { className: "panel" },
                        e("div", { className: "row between" },
                            e("strong", null, "오늘 처리할 일"),
                            e("span", { className: "status-pill active" }, "6건")
                        ),
                        e("ul", { className: "mini-list" },
                            e("li", { className: "mini-item" }, "포트폴리오 게시판 소개글 수정"),
                            e("li", { className: "mini-item" }, "비노출 메뉴 정리"),
                            e("li", { className: "mini-item" }, "신규 댓글 2건 확인")
                        )
                    )
                )
            ),
            e("main", { className: "content-stack" },
                e("section", { className: "hero-panel panel" },
                    e("div", null,
                        e("p", { className: "eyebrow" }, "Admin Overview"),
                        e("h1", null, "메뉴와 게시판 구조를 먼저 다듬는 관리자 화면"),
                        e("p", { className: "hero-copy" }, "일반 게시판, 썸네일 게시판, 포트폴리오 게시판을 분리해서 운영하고 메뉴 노출 여부를 즉시 확인할 수 있는 구조입니다."),
                        e("div", { className: "hero-actions" },
                            e("button", { className: "button primary" }, "새 게시판 추가"),
                            e("button", { className: "button secondary" }, "메뉴 정렬 열기")
                        )
                    ),
                    e("div", { className: "panel" },
                        e("strong", null, "배포 전 체크"),
                        e("ul", { className: "mini-list" },
                            e("li", { className: "mini-item" }, "공지사항 메뉴 연결 확인"),
                            e("li", { className: "mini-item" }, "포트폴리오 대표 썸네일 점검"),
                            e("li", { className: "mini-item" }, "익명 댓글 비밀번호 정책 확인")
                        )
                    )
                ),
                e("section", { className: "stats-grid" },
                    statCard("전체 게시판", "3", "운영중 2 / 검수중 1"),
                    statCard("전체 메뉴", "7", "비노출 메뉴 1"),
                    statCard("게시글 수", "24", "이번 주 작성 5"),
                    statCard("신규 댓글", "2", "익명 댓글만 허용")
                ),
                e("section", { className: "board-grid" },
                    boards.map(function (board) {
                        return e("article", { className: "board-card", key: board.id },
                            e("div", { className: "row between" },
                                e("span", { className: boardTag(board.type) }, board.type),
                                e("span", { className: "status-pill " + (board.state === "운영중" ? "active" : "hidden") }, board.state)
                            ),
                            e("h3", null, board.name),
                            e("p", { className: "muted" }, "게시글 ", board.posts, "개 / 댓글 ", board.comments ? "사용" : "미사용"),
                            e("div", { className: "inline-actions" },
                                e("button", { className: "button soft" }, "수정"),
                                e("button", { className: "button warn" }, "삭제")
                            )
                        );
                    })
                ),
                e("section", { className: "menu-grid" },
                    e("div", { className: "panel" },
                        e("div", { className: "section-header" },
                            e("div", null,
                                e("h2", null, "메뉴 트리"),
                                e("p", { className: "section-copy" }, "게시판과 연결된 사용자 메뉴 구조")
                            ),
                            e("button", { className: "button secondary" }, "메뉴 추가")
                        ),
                        e("ul", { className: "menu-tree" },
                            menus.map(function (menu) {
                                return e("li", { className: "menu-node", key: menu.id },
                                    e("div", null,
                                        e("strong", null, menu.name),
                                        e("p", { className: "muted" }, menu.board, " / depth ", menu.depth)
                                    ),
                                    e("span", { className: "status-pill " + (menu.visible ? "active" : "hidden") }, menu.visible ? "노출" : "숨김")
                                );
                            })
                        )
                    ),
                    e("div", { className: "panel editor-panel" },
                        e("h2", null, "게시글 작성"),
                        inputField("제목", "프로젝트 소개 페이지"),
                        selectField("게시판 타입", ["GENERAL", "THUMBNAIL", "PORTFOLIO"]),
                        inputField("썸네일 URL", "https://cdn.example.com/cover.png"),
                        textareaField("본문", "포트폴리오 소개 내용을 입력합니다."),
                        e("div", { className: "form-actions" },
                            e("button", { className: "button primary" }, "임시 저장"),
                            e("button", { className: "button secondary" }, "발행")
                        )
                    )
                ),
                e("section", { className: "comment-grid" },
                    comments.map(function (comment) {
                        return e("article", { className: "comment-card", key: comment.id },
                            e("div", { className: "row between" },
                                e("strong", null, comment.author),
                                e("span", { className: "status-pill " + (comment.status === "신규" ? "active" : "thumbnail") }, comment.status)
                            ),
                            e("p", { className: "muted" }, comment.post),
                            e("p", null, comment.text),
                            e("div", { className: "inline-actions" },
                                e("button", { className: "button soft" }, "승인"),
                                e("button", { className: "button warn" }, "숨김")
                            )
                        );
                    })
                )
            )
        );
    }

    function statCard(title, value, description) {
        return e("article", { className: "panel" },
            e("p", { className: "eyebrow" }, title),
            e("p", { className: "stat-value" }, value),
            e("p", { className: "muted" }, description)
        );
    }

    function inputField(label, value) {
        return e("div", { className: "field" },
            e("label", null, label),
            e("input", { defaultValue: value })
        );
    }

    function selectField(label, values) {
        return e("div", { className: "field" },
            e("label", null, label),
            e("select", null, values.map(function (value) {
                return e("option", { key: value, value: value }, value);
            }))
        );
    }

    function textareaField(label, value) {
        return e("div", { className: "field" },
            e("label", null, label),
            e("textarea", { defaultValue: value })
        );
    }

    ReactDOM.createRoot(document.getElementById("root")).render(e(App));
})();

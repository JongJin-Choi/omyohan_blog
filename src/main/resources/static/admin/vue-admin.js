const { createApp } = Vue;

createApp({
    data() {
        return {
            boards: [
                { id: 1, name: "공지사항", type: "GENERAL", state: "운영중", posts: 12 },
                { id: 2, name: "썸네일 갤러리", type: "THUMBNAIL", state: "운영중", posts: 8 },
                { id: 3, name: "포트폴리오", type: "PORTFOLIO", state: "기획중", posts: 4 }
            ],
            menus: [
                { id: 1, name: "공지사항", board: "공지사항", sort: 1, visible: true },
                { id: 2, name: "프로젝트", board: "포트폴리오", sort: 2, visible: true },
                { id: 3, name: "비공개 메뉴", board: "썸네일 갤러리", sort: 3, visible: false }
            ],
            comments: [
                { id: 1, author: "익명A", text: "포트폴리오 썸네일이 좋아요.", post: "포트폴리오 소개" },
                { id: 2, author: "익명B", text: "메뉴 구성이 깔끔합니다.", post: "공지 업데이트" }
            ]
        };
    },
    methods: {
        typeClass(type) {
            if (type === "PORTFOLIO") return "tag portfolio";
            if (type === "THUMBNAIL") return "tag thumbnail";
            return "tag general";
        },
        statusClass(state) {
            return state === "운영중" ? "status-pill active" : "status-pill hidden";
        }
    },
    template: `
        <div class="admin-shell">
            <aside class="sidebar">
                <div class="sidebar-card panel">
                    <div class="app-title">
                        <p class="eyebrow">Vue Admin</p>
                        <h1>콘텐츠 제어판</h1>
                        <p class="hero-copy">메뉴 구조와 게시판 종류를 빠르게 편집하는 Vue 시안</p>
                    </div>
                    <ul class="nav-list">
                        <li class="nav-item active">콘텐츠 요약</li>
                        <li class="nav-item">게시판 편집</li>
                        <li class="nav-item">메뉴 노출 관리</li>
                        <li class="nav-item">댓글 응답</li>
                    </ul>
                </div>
            </aside>

            <main class="content-stack">
                <section class="hero-panel panel">
                    <div>
                        <p class="eyebrow">Vue Workflow</p>
                        <h1>운영 흐름 중심 관리자 화면</h1>
                        <p class="hero-copy">게시판 추가, 메뉴 연결, 게시글 작성, 익명 댓글 검토까지 한 흐름으로 연결했습니다.</p>
                        <div class="hero-actions">
                            <button class="button primary">새 메뉴 만들기</button>
                            <button class="button secondary">포트폴리오 등록</button>
                        </div>
                    </div>
                    <div class="panel">
                        <strong>이번 주 운영 포인트</strong>
                        <ul class="mini-list">
                            <li class="mini-item">썸네일 게시판 대표 이미지 교체</li>
                            <li class="mini-item">비노출 메뉴 공개 여부 결정</li>
                            <li class="mini-item">댓글 신고 처리 화면 추가 검토</li>
                        </ul>
                    </div>
                </section>

                <section class="stats-grid">
                    <article class="panel">
                        <p class="eyebrow">관리 중 게시판</p>
                        <p class="stat-value">{{ boards.length }}</p>
                        <p class="muted">일반, 썸네일, 포트폴리오 분리 운영</p>
                    </article>
                    <article class="panel">
                        <p class="eyebrow">메뉴 노출 상태</p>
                        <p class="stat-value">{{ menus.filter(menu => menu.visible).length }}</p>
                        <p class="muted">비노출 메뉴 {{ menus.filter(menu => !menu.visible).length }}개</p>
                    </article>
                    <article class="panel">
                        <p class="eyebrow">신규 댓글</p>
                        <p class="stat-value">{{ comments.length }}</p>
                        <p class="muted">익명 댓글 검토 필요</p>
                    </article>
                </section>

                <section class="menu-grid">
                    <div class="panel">
                        <div class="section-header">
                            <div>
                                <h2>게시판 관리</h2>
                                <p class="section-copy">유형별 게시판 상태와 글 수 확인</p>
                            </div>
                            <button class="button soft">게시판 추가</button>
                        </div>
                        <div class="board-grid">
                            <article class="board-card" v-for="board in boards" :key="board.id">
                                <div class="row between">
                                    <span :class="typeClass(board.type)">{{ board.type }}</span>
                                    <span :class="statusClass(board.state)">{{ board.state }}</span>
                                </div>
                                <h3>{{ board.name }}</h3>
                                <p class="muted">게시글 {{ board.posts }}개</p>
                                <div class="inline-actions">
                                    <button class="button secondary">수정</button>
                                    <button class="button warn">삭제</button>
                                </div>
                            </article>
                        </div>
                    </div>

                    <div class="panel editor-panel">
                        <h2>메뉴 편집</h2>
                        <div class="field">
                            <label>메뉴명</label>
                            <input value="포트폴리오">
                        </div>
                        <div class="split-fields">
                            <div class="field">
                                <label>연결 게시판</label>
                                <select>
                                    <option>포트폴리오</option>
                                    <option>공지사항</option>
                                    <option>썸네일 갤러리</option>
                                </select>
                            </div>
                            <div class="field">
                                <label>노출 여부</label>
                                <select>
                                    <option>노출</option>
                                    <option>숨김</option>
                                </select>
                            </div>
                        </div>
                        <div class="field">
                            <label>설명</label>
                            <textarea>프로젝트 포트폴리오를 사용자에게 보여주는 대표 메뉴입니다.</textarea>
                        </div>
                        <div class="form-actions">
                            <button class="button primary">저장</button>
                            <button class="button secondary">정렬 변경</button>
                        </div>
                    </div>
                </section>

                <section class="comment-grid">
                    <article class="comment-card" v-for="comment in comments" :key="comment.id">
                        <div class="row between">
                            <strong>{{ comment.author }}</strong>
                            <span class="status-pill thumbnail">대기</span>
                        </div>
                        <p class="muted">{{ comment.post }}</p>
                        <p>{{ comment.text }}</p>
                        <div class="inline-actions">
                            <button class="button soft">답변 준비</button>
                            <button class="button warn">숨김 처리</button>
                        </div>
                    </article>
                </section>
            </main>
        </div>
    `
}).mount("#app");

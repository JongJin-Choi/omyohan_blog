<script setup>
import { computed, onMounted, reactive, ref } from "vue";
import { api } from "../api/client";
import { useAsyncState } from "../composables/useAsyncState";
import PageHeader from "../components/PageHeader.vue";

const postsState = useAsyncState([]);
const boardsState = useAsyncState([]);

const initialValues = {
  id: null,
  boardId: null,
  title: "",
  content: "",
  summary: "",
  thumbnailUrl: "",
  portfolioClient: "",
  portfolioStartedOn: "",
  portfolioEndedOn: "",
  isPinned: false,
  isPublished: true,
  attachments: []
};

const form = reactive({ ...initialValues });
const errors = reactive({
  submit: "",
  boardId: "",
  title: "",
  summary: "",
  content: "",
  thumbnailUrl: "",
  portfolioClient: "",
  portfolioStartedOn: "",
  portfolioEndedOn: ""
});

const saving = ref(false);
const boardFilter = ref("");

const boardMap = computed(() => Object.fromEntries(boardsState.data.value.map((board) => [board.id, board])));
const selectedBoard = computed(() => boardMap.value[form.boardId] ?? null);
const hasBoards = computed(() => boardsState.data.value.length > 0);
const isImageBoard = computed(() => selectedBoard.value?.boardType === "THUMBNAIL");
const isPortfolioBoard = computed(() => selectedBoard.value?.boardType === "PORTFOLIO");

function boardBadge(type) {
  if (type === "PORTFOLIO") return "bg-amber-100 text-amber-700 ring-1 ring-amber-200";
  if (type === "THUMBNAIL") return "bg-sky-100 text-sky-700 ring-1 ring-sky-200";
  return "bg-emerald-100 text-emerald-700 ring-1 ring-emerald-200";
}

function clearErrors() {
  Object.keys(errors).forEach((key) => {
    errors[key] = "";
  });
}

function applyFormValues(values = {}) {
  form.id = values.id ?? null;
  form.boardId = values.boardId ?? boardsState.data.value[0]?.id ?? null;
  form.title = values.title ?? "";
  form.content = values.content ?? "";
  form.summary = values.summary ?? "";
  form.thumbnailUrl = values.thumbnailUrl ?? "";
  form.portfolioClient = values.portfolioClient ?? "";
  form.portfolioStartedOn = values.portfolioStartedOn ?? "";
  form.portfolioEndedOn = values.portfolioEndedOn ?? "";
  form.isPinned = values.isPinned ?? false;
  form.isPublished = values.isPublished ?? true;
  form.attachments = values.attachments ?? [];
}

function resetForm() {
  clearErrors();
  applyFormValues(initialValues);
}

function normalizeText(value) {
  return typeof value === "string" ? value.trim() : "";
}

function isValidUrl(value) {
  try {
    new URL(value);
    return true;
  } catch {
    return false;
  }
}

function validateForm() {
  clearErrors();

  if (!form.boardId) {
    errors.boardId = "게시판을 선택해주세요.";
  }
  if (!normalizeText(form.title)) {
    errors.title = "제목을 입력해주세요.";
  }
  if (!normalizeText(form.summary)) {
    errors.summary = "요약을 입력해주세요.";
  }
  if (!normalizeText(form.content)) {
    errors.content = "본문을 입력해주세요.";
  }
  if (isImageBoard.value && !normalizeText(form.thumbnailUrl)) {
    errors.thumbnailUrl = "이미지 게시판은 썸네일 URL이 필요합니다.";
  } else if (normalizeText(form.thumbnailUrl) && !isValidUrl(normalizeText(form.thumbnailUrl))) {
    errors.thumbnailUrl = "올바른 URL 형식으로 입력해주세요.";
  }
  if (isPortfolioBoard.value && !normalizeText(form.portfolioClient)) {
    errors.portfolioClient = "포트폴리오 게시판은 클라이언트명을 입력해야 합니다.";
  }
  if (form.portfolioStartedOn && form.portfolioEndedOn && form.portfolioStartedOn > form.portfolioEndedOn) {
    errors.portfolioEndedOn = "종료일은 시작일보다 빠를 수 없습니다.";
  }

  return !Object.values(errors).some(Boolean);
}

async function loadPosts() {
  const boardId = boardFilter.value ? Number(boardFilter.value) : undefined;
  await postsState.run(() => api.getPosts(boardId));
}

async function loadBoards() {
  await boardsState.run(() => api.getBoards());
}

function editPost(post) {
  clearErrors();
  applyFormValues(post);
}

async function submitPost() {
  if (!validateForm()) return;

  saving.value = true;
  errors.submit = "";

  try {
    const payload = {
      boardId: form.boardId,
      title: normalizeText(form.title),
      content: normalizeText(form.content),
      summary: normalizeText(form.summary),
      thumbnailUrl: normalizeText(form.thumbnailUrl) || null,
      portfolioClient: normalizeText(form.portfolioClient) || null,
      portfolioStartedOn: form.portfolioStartedOn || null,
      portfolioEndedOn: form.portfolioEndedOn || null,
      isPinned: form.isPinned,
      isPublished: form.isPublished,
      attachments: []
    };

    if (form.id) {
      await api.updatePost(form.id, payload);
    } else {
      await api.createPost(payload);
    }

    resetForm();
    await loadPosts();
  } catch (error) {
    errors.submit = error instanceof Error ? error.message : "게시글 저장 중 오류가 발생했습니다.";
  } finally {
    saving.value = false;
  }
}

async function removePost(postId) {
  clearErrors();
  try {
    await api.deletePost(postId);
    if (form.id === postId) resetForm();
    await loadPosts();
  } catch (error) {
    errors.submit = error instanceof Error ? error.message : "게시글 삭제 중 오류가 발생했습니다.";
  }
}

onMounted(async () => {
  await Promise.all([loadBoards(), loadPosts()]);
  resetForm();
});
</script>

<template>
  <PageHeader
    eyebrow="Posts"
    title="게시글 관리"
    description="일반, 이미지, 포트폴리오 게시글을 작성하고 수정합니다."
  >
    <template #actions>
      <button class="rounded-full bg-stone-950 px-5 py-3 text-sm font-semibold text-white hover:bg-stone-800" @click="resetForm">
        새 게시글
      </button>
      <button class="rounded-full border border-stone-300 bg-white px-5 py-3 text-sm font-semibold text-stone-700 hover:bg-stone-50" @click="loadPosts">
        목록 새로고침
      </button>
    </template>
    <template #side>
      <div class="space-y-4">
        <h3 class="text-sm font-bold text-stone-900">연동 API</h3>
        <ul class="space-y-2 text-sm text-stone-600">
          <li class="rounded-2xl border border-white/60 bg-white/80 px-4 py-3">GET /api/admin/posts</li>
          <li class="rounded-2xl border border-white/60 bg-white/80 px-4 py-3">POST /api/admin/posts</li>
          <li class="rounded-2xl border border-white/60 bg-white/80 px-4 py-3">PUT /api/admin/posts/{id}</li>
          <li class="rounded-2xl border border-white/60 bg-white/80 px-4 py-3">DELETE /api/admin/posts/{id}</li>
        </ul>
      </div>
    </template>
  </PageHeader>

  <section v-if="postsState.error.value || boardsState.error.value" class="rounded-[28px] border border-rose-200 bg-rose-50 p-6 text-rose-700">
    <strong class="text-base font-bold">API 오류</strong>
    <p v-if="postsState.error.value" class="mt-2 text-sm">게시글 목록: {{ postsState.error.value }}</p>
    <p v-if="boardsState.error.value" class="mt-2 text-sm">게시판 목록: {{ boardsState.error.value }}</p>
  </section>

  <section class="rounded-[28px] border border-stone-200/80 bg-white/80 p-5 shadow-[0_20px_60px_rgba(40,28,16,0.08)]">
    <div class="grid gap-4 sm:grid-cols-[240px_1fr] sm:items-end">
      <label class="space-y-2">
        <span class="text-sm font-semibold text-stone-700">게시판 필터</span>
        <select v-model="boardFilter" class="w-full rounded-2xl border border-stone-300 bg-white px-4 py-3 text-sm outline-none transition focus:border-stone-500" @change="loadPosts">
          <option value="">전체</option>
          <option v-for="board in boardsState.data.value" :key="board.id" :value="board.id">
            {{ board.name }}
          </option>
        </select>
      </label>
      <p v-if="boardsState.loading.value" class="text-sm text-stone-500">게시판 목록을 불러오는 중입니다.</p>
      <p v-else-if="!hasBoards" class="text-sm text-amber-700">등록된 게시판이 없습니다. 먼저 게시판을 생성해주세요.</p>
    </div>
  </section>

  <section class="grid gap-4 xl:grid-cols-[minmax(0,1.1fr)_440px]">
    <div class="rounded-[32px] border border-stone-200/80 bg-white/80 p-6 shadow-[0_20px_60px_rgba(40,28,16,0.08)]">
      <div class="mb-5">
        <h3 class="text-xl font-bold text-stone-950">게시글 목록</h3>
        <p class="mt-1 text-sm text-stone-600">실데이터 기준 게시글 현황입니다.</p>
      </div>

      <div v-if="postsState.loading.value" class="rounded-[24px] border border-dashed border-stone-300 bg-stone-50 p-6 text-sm text-stone-500">
        게시글 목록을 불러오는 중입니다.
      </div>

      <div v-else-if="postsState.data.value.length === 0" class="rounded-[24px] border border-dashed border-stone-300 bg-stone-50 p-6 text-sm text-stone-500">
        조회된 게시글이 없습니다.
      </div>

      <div v-else class="space-y-3">
        <article
          v-for="post in postsState.data.value"
          :key="post.id"
          class="rounded-[24px] border border-stone-200 bg-stone-50/80 p-5"
        >
          <div class="flex flex-col gap-4 md:flex-row md:items-start md:justify-between">
            <div class="min-w-0">
              <div class="flex flex-wrap items-center gap-2">
                <span class="rounded-full px-3 py-1 text-xs font-semibold" :class="boardBadge(boardMap[post.boardId]?.boardType ?? 'GENERAL')">
                  {{ boardMap[post.boardId]?.boardType ?? "GENERAL" }}
                </span>
                <span :class="post.isPublished ? 'bg-emerald-100 text-emerald-700' : 'bg-stone-200 text-stone-600'" class="rounded-full px-3 py-1 text-xs font-semibold">
                  {{ post.isPublished ? "발행" : "임시저장" }}
                </span>
              </div>
              <h4 class="mt-4 text-lg font-bold text-stone-950">{{ post.title }}</h4>
              <p class="mt-2 text-sm text-stone-600">{{ boardMap[post.boardId]?.name ?? "게시판 미확인" }} / 조회수 {{ post.viewCount ?? 0 }}</p>
            </div>
            <div class="flex flex-wrap gap-2">
              <button class="rounded-full bg-emerald-100 px-4 py-2 text-sm font-semibold text-emerald-700 hover:bg-emerald-200" @click="editPost(post)">
                수정
              </button>
              <button class="rounded-full bg-rose-100 px-4 py-2 text-sm font-semibold text-rose-700 hover:bg-rose-200" @click="removePost(post.id)">
                삭제
              </button>
            </div>
          </div>
        </article>
      </div>
    </div>

    <div class="rounded-[32px] border border-stone-200/80 bg-white/80 p-6 shadow-[0_20px_60px_rgba(40,28,16,0.08)]">
      <div class="mb-5">
        <h3 class="text-xl font-bold text-stone-950">{{ form.id ? "게시글 수정" : "게시글 등록" }}</h3>
        <p class="mt-1 text-sm text-stone-600">선택한 게시판 유형에 맞는 필수값을 검증합니다.</p>
      </div>

      <div v-if="errors.submit" class="mb-4 rounded-2xl border border-rose-200 bg-rose-50 px-4 py-3 text-sm text-rose-700">
        {{ errors.submit }}
      </div>

      <div class="space-y-4">
        <label class="space-y-2">
          <span class="text-sm font-semibold text-stone-700">제목</span>
          <input v-model="form.title" class="w-full rounded-2xl border px-4 py-3 text-sm outline-none transition focus:border-stone-500" :class="errors.title ? 'border-rose-300 bg-rose-50' : 'border-stone-300 bg-white'" />
          <p v-if="errors.title" class="text-sm text-rose-600">{{ errors.title }}</p>
        </label>

        <label class="space-y-2">
          <span class="text-sm font-semibold text-stone-700">게시판</span>
          <select
            v-model.number="form.boardId"
            class="w-full rounded-2xl border px-4 py-3 text-sm outline-none transition focus:border-stone-500"
            :class="errors.boardId ? 'border-rose-300 bg-rose-50' : 'border-stone-300 bg-white'"
            :disabled="boardsState.loading.value || !hasBoards"
          >
            <option :value="null" disabled>
              {{ boardsState.loading.value ? "게시판 불러오는 중..." : hasBoards ? "게시판을 선택하세요" : "게시판이 없습니다" }}
            </option>
            <option v-for="board in boardsState.data.value" :key="board.id" :value="board.id">
              {{ board.name }} ({{ board.boardType }})
            </option>
          </select>
          <p v-if="errors.boardId" class="text-sm text-rose-600">{{ errors.boardId }}</p>
          <p v-else-if="boardsState.error.value" class="text-sm text-rose-600">게시판 목록을 가져오지 못했습니다. 게시판 API 상태를 확인해주세요.</p>
          <p v-else-if="!hasBoards" class="text-sm text-amber-700">게시글을 등록하려면 먼저 게시판을 생성해야 합니다.</p>
        </label>

        <label class="space-y-2">
          <span class="text-sm font-semibold text-stone-700">요약</span>
          <input v-model="form.summary" class="w-full rounded-2xl border px-4 py-3 text-sm outline-none transition focus:border-stone-500" :class="errors.summary ? 'border-rose-300 bg-rose-50' : 'border-stone-300 bg-white'" />
          <p v-if="errors.summary" class="text-sm text-rose-600">{{ errors.summary }}</p>
        </label>

        <div class="grid gap-4 sm:grid-cols-2">
          <label class="space-y-2">
            <span class="text-sm font-semibold text-stone-700">클라이언트명</span>
            <input v-model="form.portfolioClient" class="w-full rounded-2xl border px-4 py-3 text-sm outline-none transition focus:border-stone-500" :class="errors.portfolioClient ? 'border-rose-300 bg-rose-50' : 'border-stone-300 bg-white'" />
            <p v-if="errors.portfolioClient" class="text-sm text-rose-600">{{ errors.portfolioClient }}</p>
          </label>
          <label class="space-y-2">
            <span class="text-sm font-semibold text-stone-700">썸네일 URL</span>
            <input v-model="form.thumbnailUrl" class="w-full rounded-2xl border px-4 py-3 text-sm outline-none transition focus:border-stone-500" :class="errors.thumbnailUrl ? 'border-rose-300 bg-rose-50' : 'border-stone-300 bg-white'" />
            <p v-if="errors.thumbnailUrl" class="text-sm text-rose-600">{{ errors.thumbnailUrl }}</p>
          </label>
        </div>

        <div class="grid gap-4 sm:grid-cols-2">
          <label class="space-y-2">
            <span class="text-sm font-semibold text-stone-700">시작일</span>
            <input v-model="form.portfolioStartedOn" type="date" class="w-full rounded-2xl border border-stone-300 bg-white px-4 py-3 text-sm outline-none transition focus:border-stone-500" />
          </label>
          <label class="space-y-2">
            <span class="text-sm font-semibold text-stone-700">종료일</span>
            <input v-model="form.portfolioEndedOn" type="date" class="w-full rounded-2xl border px-4 py-3 text-sm outline-none transition focus:border-stone-500" :class="errors.portfolioEndedOn ? 'border-rose-300 bg-rose-50' : 'border-stone-300 bg-white'" />
            <p v-if="errors.portfolioEndedOn" class="text-sm text-rose-600">{{ errors.portfolioEndedOn }}</p>
          </label>
        </div>

        <div class="grid gap-4 sm:grid-cols-2">
          <label class="space-y-2">
            <span class="text-sm font-semibold text-stone-700">상단 고정</span>
            <select v-model="form.isPinned" class="w-full rounded-2xl border border-stone-300 bg-white px-4 py-3 text-sm outline-none transition focus:border-stone-500">
              <option :value="true">사용</option>
              <option :value="false">미사용</option>
            </select>
          </label>
          <label class="space-y-2">
            <span class="text-sm font-semibold text-stone-700">발행 상태</span>
            <select v-model="form.isPublished" class="w-full rounded-2xl border border-stone-300 bg-white px-4 py-3 text-sm outline-none transition focus:border-stone-500">
              <option :value="true">발행</option>
              <option :value="false">임시저장</option>
            </select>
          </label>
        </div>

        <label class="space-y-2">
          <span class="text-sm font-semibold text-stone-700">본문</span>
          <textarea v-model="form.content" class="min-h-40 w-full rounded-[24px] border px-4 py-3 text-sm outline-none transition focus:border-stone-500" :class="errors.content ? 'border-rose-300 bg-rose-50' : 'border-stone-300 bg-white'"></textarea>
          <p v-if="errors.content" class="text-sm text-rose-600">{{ errors.content }}</p>
        </label>

        <div class="flex flex-wrap gap-3 pt-2">
          <button class="rounded-full bg-stone-950 px-5 py-3 text-sm font-semibold text-white hover:bg-stone-800 disabled:opacity-60" :disabled="saving || !hasBoards" @click="submitPost">
            {{ form.id ? "게시글 수정" : "게시글 저장" }}
          </button>
          <button class="rounded-full border border-stone-300 bg-white px-5 py-3 text-sm font-semibold text-stone-700 hover:bg-stone-50" @click="resetForm">
            입력 초기화
          </button>
        </div>
      </div>
    </div>
  </section>
</template>

<script setup>
import { onMounted, reactive, ref } from "vue";
import { api } from "../api/client";
import { useAsyncState } from "../composables/useAsyncState";
import PageHeader from "../components/PageHeader.vue";

const boardsState = useAsyncState([]);

const initialValues = {
  id: null,
  name: "",
  slug: "",
  boardType: "GENERAL",
  description: "",
  isActive: true,
  useComment: true
};

const form = reactive({ ...initialValues });
const saving = ref(false);
const errors = reactive({
  submit: "",
  name: "",
  slug: "",
  description: ""
});

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

function resetForm() {
  clearErrors();
  Object.assign(form, initialValues);
}

function normalizeText(value) {
  return typeof value === "string" ? value.trim() : "";
}

function validateForm() {
  clearErrors();

  if (!normalizeText(form.name)) {
    errors.name = "게시판명을 입력해주세요.";
  }
  if (!normalizeText(form.slug)) {
    errors.slug = "slug를 입력해주세요.";
  } else if (!/^[a-z0-9-]+$/.test(normalizeText(form.slug))) {
    errors.slug = "slug는 영문 소문자, 숫자, 하이픈만 사용할 수 있습니다.";
  }
  if (!normalizeText(form.description)) {
    errors.description = "게시판 설명을 입력해주세요.";
  }

  return !Object.values(errors).some(Boolean);
}

async function loadBoards() {
  await boardsState.run(() => api.getBoards());
}

function editBoard(board) {
  clearErrors();
  form.id = board.id;
  form.name = board.name;
  form.slug = board.slug;
  form.boardType = board.boardType;
  form.description = board.description ?? "";
  form.isActive = board.isActive;
  form.useComment = board.useComment;
}

async function submitBoard() {
  if (!validateForm()) return;

  saving.value = true;
  errors.submit = "";

  try {
    const payload = {
      name: normalizeText(form.name),
      slug: normalizeText(form.slug),
      boardType: form.boardType,
      description: normalizeText(form.description),
      isActive: form.isActive,
      useComment: form.useComment
    };

    if (form.id) {
      await api.updateBoard(form.id, payload);
    } else {
      await api.createBoard(payload);
    }

    resetForm();
    await loadBoards();
  } catch (error) {
    errors.submit = error instanceof Error ? error.message : "게시판 저장 중 오류가 발생했습니다.";
  } finally {
    saving.value = false;
  }
}

async function removeBoard(boardId) {
  clearErrors();
  try {
    await api.deleteBoard(boardId);
    if (form.id === boardId) resetForm();
    await loadBoards();
  } catch (error) {
    errors.submit = error instanceof Error ? error.message : "게시판 삭제 중 오류가 발생했습니다.";
  }
}

onMounted(loadBoards);
</script>

<template>
  <PageHeader
    eyebrow="Boards"
    title="게시판 관리"
    description="일반, 썸네일, 포트폴리오 게시판을 생성하고 상태를 관리합니다."
  >
    <template #actions>
      <button class="rounded-full bg-stone-950 px-5 py-3 text-sm font-semibold text-white hover:bg-stone-800" @click="resetForm">
        새 게시판
      </button>
      <button class="rounded-full border border-stone-300 bg-white px-5 py-3 text-sm font-semibold text-stone-700 hover:bg-stone-50" @click="loadBoards">
        목록 새로고침
      </button>
    </template>
    <template #side>
      <div class="space-y-4">
        <h3 class="text-sm font-bold text-stone-900">연동 API</h3>
        <ul class="space-y-2 text-sm text-stone-600">
          <li class="rounded-2xl border border-white/60 bg-white/80 px-4 py-3">GET /api/admin/boards</li>
          <li class="rounded-2xl border border-white/60 bg-white/80 px-4 py-3">POST /api/admin/boards</li>
          <li class="rounded-2xl border border-white/60 bg-white/80 px-4 py-3">PUT /api/admin/boards/{id}</li>
          <li class="rounded-2xl border border-white/60 bg-white/80 px-4 py-3">DELETE /api/admin/boards/{id}</li>
        </ul>
      </div>
    </template>
  </PageHeader>

  <section v-if="boardsState.error.value" class="rounded-[28px] border border-rose-200 bg-rose-50 p-6 text-rose-700">
    <strong class="text-base font-bold">API 오류</strong>
    <p class="mt-2 text-sm">{{ boardsState.error.value }}</p>
  </section>

  <section class="grid gap-4 xl:grid-cols-[minmax(0,1.1fr)_420px]">
    <div class="rounded-[32px] border border-stone-200/80 bg-white/80 p-6 shadow-[0_20px_60px_rgba(40,28,16,0.08)]">
      <div class="mb-5 flex items-center justify-between">
        <div>
          <h3 class="text-xl font-bold text-stone-950">게시판 목록</h3>
          <p class="mt-1 text-sm text-stone-600">실데이터 기준 게시판 상태입니다.</p>
        </div>
      </div>

      <div v-if="boardsState.loading.value" class="rounded-[24px] border border-dashed border-stone-300 bg-stone-50 p-6 text-sm text-stone-500">
        게시판 목록을 불러오는 중입니다.
      </div>

      <div v-else-if="boardsState.data.value.length === 0" class="rounded-[24px] border border-dashed border-stone-300 bg-stone-50 p-6 text-sm text-stone-500">
        등록된 게시판이 없습니다.
      </div>

      <div v-else class="grid gap-4 md:grid-cols-2">
        <article
          v-for="board in boardsState.data.value"
          :key="board.id"
          class="rounded-[24px] border border-stone-200 bg-stone-50/80 p-5"
        >
          <div class="flex items-center justify-between gap-3">
            <span class="rounded-full px-3 py-1 text-xs font-semibold" :class="boardBadge(board.boardType)">
              {{ board.boardType }}
            </span>
            <span :class="board.isActive ? 'bg-emerald-100 text-emerald-700' : 'bg-stone-200 text-stone-600'" class="rounded-full px-3 py-1 text-xs font-semibold">
              {{ board.isActive ? "운영중" : "비활성" }}
            </span>
          </div>
          <h4 class="mt-4 text-lg font-bold text-stone-950">{{ board.name }}</h4>
          <p class="mt-2 text-sm text-stone-500">slug: {{ board.slug }}</p>
          <p class="mt-1 text-sm text-stone-600">게시글 {{ board.postCount ?? 0 }}개 / 댓글 {{ board.useComment ? "사용" : "미사용" }}</p>
          <div class="mt-4 flex flex-wrap gap-2">
            <button class="rounded-full bg-emerald-100 px-4 py-2 text-sm font-semibold text-emerald-700 hover:bg-emerald-200" @click="editBoard(board)">
              수정
            </button>
            <button class="rounded-full bg-rose-100 px-4 py-2 text-sm font-semibold text-rose-700 hover:bg-rose-200" @click="removeBoard(board.id)">
              삭제
            </button>
          </div>
        </article>
      </div>
    </div>

    <div class="rounded-[32px] border border-stone-200/80 bg-white/80 p-6 shadow-[0_20px_60px_rgba(40,28,16,0.08)]">
      <div class="mb-5">
        <h3 class="text-xl font-bold text-stone-950">{{ form.id ? "게시판 수정" : "게시판 등록" }}</h3>
        <p class="mt-1 text-sm text-stone-600">게시판 정보 저장 전에 필수값을 확인합니다.</p>
      </div>

      <div v-if="errors.submit" class="mb-4 rounded-2xl border border-rose-200 bg-rose-50 px-4 py-3 text-sm text-rose-700">
        {{ errors.submit }}
      </div>

      <div class="space-y-4">
        <div class="grid gap-4 sm:grid-cols-2">
          <label class="space-y-2">
            <span class="text-sm font-semibold text-stone-700">게시판명</span>
            <input v-model="form.name" class="w-full rounded-2xl border px-4 py-3 text-sm outline-none transition focus:border-stone-500" :class="errors.name ? 'border-rose-300 bg-rose-50' : 'border-stone-300 bg-white'" />
            <p v-if="errors.name" class="text-sm text-rose-600">{{ errors.name }}</p>
          </label>
          <label class="space-y-2">
            <span class="text-sm font-semibold text-stone-700">slug</span>
            <input v-model="form.slug" class="w-full rounded-2xl border px-4 py-3 text-sm outline-none transition focus:border-stone-500" :class="errors.slug ? 'border-rose-300 bg-rose-50' : 'border-stone-300 bg-white'" />
            <p v-if="errors.slug" class="text-sm text-rose-600">{{ errors.slug }}</p>
          </label>
        </div>

        <div class="grid gap-4 sm:grid-cols-2">
          <label class="space-y-2">
            <span class="text-sm font-semibold text-stone-700">게시판 종류</span>
            <select v-model="form.boardType" class="w-full rounded-2xl border border-stone-300 bg-white px-4 py-3 text-sm outline-none transition focus:border-stone-500">
              <option>GENERAL</option>
              <option>THUMBNAIL</option>
              <option>PORTFOLIO</option>
            </select>
          </label>
          <label class="space-y-2">
            <span class="text-sm font-semibold text-stone-700">댓글 사용</span>
            <select v-model="form.useComment" class="w-full rounded-2xl border border-stone-300 bg-white px-4 py-3 text-sm outline-none transition focus:border-stone-500">
              <option :value="true">사용</option>
              <option :value="false">미사용</option>
            </select>
          </label>
        </div>

        <label class="space-y-2">
          <span class="text-sm font-semibold text-stone-700">설명</span>
          <textarea v-model="form.description" class="min-h-36 w-full rounded-[24px] border px-4 py-3 text-sm outline-none transition focus:border-stone-500" :class="errors.description ? 'border-rose-300 bg-rose-50' : 'border-stone-300 bg-white'"></textarea>
          <p v-if="errors.description" class="text-sm text-rose-600">{{ errors.description }}</p>
        </label>

        <div class="grid gap-4 sm:grid-cols-2">
          <label class="space-y-2">
            <span class="text-sm font-semibold text-stone-700">운영 상태</span>
            <select v-model="form.isActive" class="w-full rounded-2xl border border-stone-300 bg-white px-4 py-3 text-sm outline-none transition focus:border-stone-500">
              <option :value="true">운영중</option>
              <option :value="false">비활성</option>
            </select>
          </label>
        </div>

        <div class="flex flex-wrap gap-3 pt-2">
          <button class="rounded-full bg-stone-950 px-5 py-3 text-sm font-semibold text-white hover:bg-stone-800 disabled:opacity-60" :disabled="saving" @click="submitBoard">
            {{ form.id ? "게시판 수정" : "게시판 저장" }}
          </button>
          <button class="rounded-full border border-stone-300 bg-white px-5 py-3 text-sm font-semibold text-stone-700 hover:bg-stone-50" @click="resetForm">
            입력 초기화
          </button>
        </div>
      </div>
    </div>
  </section>
</template>

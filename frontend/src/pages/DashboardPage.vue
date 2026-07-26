<script setup>
import { computed, onMounted } from "vue";
import { api } from "../api/client";
import { useAsyncState } from "../composables/useAsyncState";
import PageHeader from "../components/PageHeader.vue";

const boardsState = useAsyncState([]);
const commentsState = useAsyncState([]);
const healthState = useAsyncState({ status: "checking" });

const summaryStats = computed(() => [
  {
    label: "운영 중 게시판",
    value: String(boardsState.data.value.length),
    description: "관리자 게시판 API 기준"
  },
  {
    label: "포트폴리오 게시판",
    value: String(boardsState.data.value.filter((item) => item.boardType === "PORTFOLIO").length),
    description: "유형별 게시판 수"
  },
  {
    label: "검토 댓글",
    value: String(commentsState.data.value.length),
    description: "관리자 댓글 API 기준"
  },
  {
    label: "백엔드 상태",
    value: healthState.data.value.status ?? "unknown",
    description: "헬스체크 API 응답"
  }
]);

function boardBadge(type) {
  if (type === "PORTFOLIO") {
    return "bg-amber-100 text-amber-700 ring-1 ring-amber-200";
  }
  if (type === "THUMBNAIL") {
    return "bg-sky-100 text-sky-700 ring-1 ring-sky-200";
  }
  return "bg-emerald-100 text-emerald-700 ring-1 ring-emerald-200";
}

onMounted(async () => {
  await Promise.all([
    boardsState.run(() => api.getBoards()),
    commentsState.run(() => api.getComments()),
    healthState.run(() => api.health())
  ]);
});
</script>

<template>
  <PageHeader
    eyebrow="Dashboard"
    title="운영 현황"
    description="게시판, 댓글, 서버 상태를 한 화면에서 확인하는 대시보드입니다."
  >
    <template #actions>
      <button class="rounded-full bg-stone-950 px-5 py-3 text-sm font-semibold text-white transition hover:bg-stone-800">
        새 게시글 작성
      </button>
      <button class="rounded-full border border-stone-300 bg-white px-5 py-3 text-sm font-semibold text-stone-700 transition hover:border-stone-400 hover:bg-stone-50">
        메뉴 구조 보기
      </button>
    </template>
    <template #side>
      <div class="space-y-4">
        <h3 class="text-sm font-bold text-stone-900">오늘 체크할 항목</h3>
        <ul class="space-y-2">
          <li class="rounded-2xl border border-white/60 bg-white/80 px-4 py-3 text-sm text-stone-600">포트폴리오 발행 상태 확인</li>
          <li class="rounded-2xl border border-white/60 bg-white/80 px-4 py-3 text-sm text-stone-600">썸네일 대표 이미지 점검</li>
          <li class="rounded-2xl border border-white/60 bg-white/80 px-4 py-3 text-sm text-stone-600">신규 댓글 검토</li>
        </ul>
      </div>
    </template>
  </PageHeader>

  <section class="grid gap-4 md:grid-cols-2 2xl:grid-cols-4">
    <article
      v-for="item in summaryStats"
      :key="item.label"
      class="rounded-[28px] border border-stone-200/80 bg-white/80 p-6 shadow-[0_20px_60px_rgba(40,28,16,0.08)]"
    >
      <p class="text-xs font-semibold uppercase tracking-[0.22em] text-stone-500">{{ item.label }}</p>
      <p class="mt-4 text-4xl font-black tracking-tight text-stone-950">{{ item.value }}</p>
      <p class="mt-2 text-sm leading-6 text-stone-600">{{ item.description }}</p>
    </article>
  </section>

  <section v-if="boardsState.error.value || commentsState.error.value || healthState.error.value" class="rounded-[28px] border border-rose-200 bg-rose-50 p-6 text-rose-700">
    <h3 class="text-base font-bold">API 오류</h3>
    <p v-if="boardsState.error.value" class="mt-2 text-sm">게시판: {{ boardsState.error.value }}</p>
    <p v-if="commentsState.error.value" class="mt-2 text-sm">댓글: {{ commentsState.error.value }}</p>
    <p v-if="healthState.error.value" class="mt-2 text-sm">헬스체크: {{ healthState.error.value }}</p>
  </section>

  <section class="grid gap-4 xl:grid-cols-[minmax(0,1.2fr)_420px]">
    <div class="rounded-[32px] border border-stone-200/80 bg-white/80 p-6 shadow-[0_20px_60px_rgba(40,28,16,0.08)]">
      <div class="mb-5 flex items-center justify-between gap-3">
        <div>
          <h3 class="text-xl font-bold text-stone-950">게시판 요약</h3>
          <p class="mt-1 text-sm text-stone-600">현재 저장된 게시판과 유형 상태</p>
        </div>
      </div>

      <div class="grid gap-4 md:grid-cols-2">
        <article
          v-for="board in boardsState.data.value"
          :key="board.id"
          class="rounded-[24px] border border-stone-200 bg-stone-50/80 p-5"
        >
          <div class="flex items-center justify-between gap-3">
            <span class="rounded-full px-3 py-1 text-xs font-semibold" :class="boardBadge(board.boardType)">
              {{ board.boardType }}
            </span>
            <span
              class="rounded-full px-3 py-1 text-xs font-semibold"
              :class="board.isActive ? 'bg-emerald-100 text-emerald-700' : 'bg-stone-200 text-stone-600'"
            >
              {{ board.isActive ? "운영중" : "비활성" }}
            </span>
          </div>
          <h4 class="mt-4 text-lg font-bold text-stone-950">{{ board.name }}</h4>
          <p class="mt-2 text-sm text-stone-500">slug: {{ board.slug }}</p>
          <p class="mt-1 text-sm text-stone-600">게시글 {{ board.postCount ?? 0 }}개</p>
        </article>
      </div>
    </div>

    <div class="rounded-[32px] border border-stone-200/80 bg-white/80 p-6 shadow-[0_20px_60px_rgba(40,28,16,0.08)]">
      <div class="mb-5">
        <h3 class="text-xl font-bold text-stone-950">최근 댓글</h3>
        <p class="mt-1 text-sm text-stone-600">관리자 기준 최근 댓글 목록</p>
      </div>

      <div class="space-y-3">
        <article
          v-for="comment in commentsState.data.value"
          :key="comment.id"
          class="rounded-[24px] border border-stone-200 bg-stone-50/80 p-4"
        >
          <div class="flex items-start justify-between gap-3">
            <div>
              <p class="text-sm font-semibold text-stone-900">{{ comment.authorName }}</p>
              <p class="mt-1 text-xs text-stone-500">postId {{ comment.postId }} / {{ comment.createdAt }}</p>
            </div>
            <span
              class="rounded-full px-3 py-1 text-xs font-semibold"
              :class="comment.isDeleted ? 'bg-stone-200 text-stone-600' : 'bg-sky-100 text-sky-700'"
            >
              {{ comment.isDeleted ? "숨김" : "표시중" }}
            </span>
          </div>
          <p class="mt-3 text-sm leading-6 text-stone-700">{{ comment.content }}</p>
        </article>
      </div>
    </div>
  </section>
</template>

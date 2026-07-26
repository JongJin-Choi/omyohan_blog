<script setup>
import { computed, onMounted, ref } from "vue";
import { api } from "../api/client";
import { useAsyncState } from "../composables/useAsyncState";
import PageHeader from "../components/PageHeader.vue";

const commentsState = useAsyncState([]);
const postId = ref("");
const mode = ref("all");

const filteredComments = computed(() => {
  if (mode.value === "hidden") return commentsState.data.value.filter((comment) => comment.isDeleted);
  if (mode.value === "visible") return commentsState.data.value.filter((comment) => !comment.isDeleted);
  return commentsState.data.value;
});

async function loadComments() {
  await commentsState.run(() => api.getComments(postId.value ? Number(postId.value) : undefined));
}

async function hideComment(commentId) {
  await api.hideComment(commentId);
  await loadComments();
}

onMounted(loadComments);
</script>

<template>
  <PageHeader
    eyebrow="Comments"
    title="댓글 관리"
    description="관리자 댓글 목록 조회와 숨김 처리를 Tailwind 화면에서 수행합니다."
  >
    <template #actions>
      <button class="rounded-full bg-stone-950 px-5 py-3 text-sm font-semibold text-white hover:bg-stone-800" @click="mode = 'visible'">
        표시중 댓글
      </button>
      <button class="rounded-full border border-stone-300 bg-white px-5 py-3 text-sm font-semibold text-stone-700 hover:bg-stone-50" @click="mode = 'hidden'">
        숨김 댓글
      </button>
      <button class="rounded-full border border-stone-300 bg-white px-5 py-3 text-sm font-semibold text-stone-700 hover:bg-stone-50" @click="mode = 'all'">
        전체 댓글
      </button>
    </template>
    <template #side>
      <div class="space-y-4">
        <h3 class="text-sm font-bold text-stone-900">연결 API</h3>
        <ul class="space-y-2 text-sm text-stone-600">
          <li class="rounded-2xl border border-white/60 bg-white/80 px-4 py-3">GET /api/admin/comments</li>
          <li class="rounded-2xl border border-white/60 bg-white/80 px-4 py-3">DELETE /api/admin/comments/{id}</li>
        </ul>
      </div>
    </template>
  </PageHeader>

  <section v-if="commentsState.error.value" class="rounded-[28px] border border-rose-200 bg-rose-50 p-6 text-rose-700">
    <strong class="text-base font-bold">API 오류</strong>
    <p class="mt-2 text-sm">{{ commentsState.error.value }}</p>
  </section>

  <section class="rounded-[28px] border border-stone-200/80 bg-white/80 p-5 shadow-[0_20px_60px_rgba(40,28,16,0.08)]">
    <div class="grid gap-4 sm:grid-cols-[220px_180px_auto] sm:items-end">
      <label class="space-y-2">
        <span class="text-sm font-semibold text-stone-700">조회할 postId</span>
        <input v-model="postId" type="number" class="w-full rounded-2xl border border-stone-300 bg-white px-4 py-3 text-sm outline-none transition focus:border-stone-500" />
      </label>
      <label class="space-y-2">
        <span class="text-sm font-semibold text-stone-700">현재 필터</span>
        <input :value="mode" readonly class="w-full rounded-2xl border border-stone-300 bg-stone-50 px-4 py-3 text-sm text-stone-500" />
      </label>
      <button class="rounded-full bg-stone-950 px-5 py-3 text-sm font-semibold text-white hover:bg-stone-800" @click="loadComments">
        댓글 조회
      </button>
    </div>
  </section>

  <section class="grid gap-4 lg:grid-cols-2">
    <article
      v-for="comment in filteredComments"
      :key="comment.id"
      class="rounded-[28px] border border-stone-200/80 bg-white/80 p-5 shadow-[0_20px_60px_rgba(40,28,16,0.08)]"
    >
      <div class="flex items-start justify-between gap-4">
        <div>
          <h3 class="text-lg font-bold text-stone-950">{{ comment.authorName }}</h3>
          <p class="mt-1 text-sm text-stone-500">postId {{ comment.postId }} / {{ comment.createdAt }}</p>
        </div>
        <span :class="comment.isDeleted ? 'bg-stone-200 text-stone-600' : 'bg-sky-100 text-sky-700'" class="rounded-full px-3 py-1 text-xs font-semibold">
          {{ comment.isDeleted ? "숨김" : "표시중" }}
        </span>
      </div>

      <p class="mt-4 text-sm leading-7 text-stone-700">{{ comment.content }}</p>

      <div class="mt-5 flex flex-wrap gap-2">
        <button class="rounded-full bg-rose-100 px-4 py-2 text-sm font-semibold text-rose-700 hover:bg-rose-200 disabled:cursor-not-allowed disabled:opacity-50" :disabled="comment.isDeleted" @click="hideComment(comment.id)">
          숨김 처리
        </button>
      </div>
    </article>
  </section>
</template>

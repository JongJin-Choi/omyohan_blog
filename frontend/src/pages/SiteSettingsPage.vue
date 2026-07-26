<script setup>
import { onMounted } from "vue";
import { api } from "../api/client";
import { useAsyncState } from "../composables/useAsyncState";
import PageHeader from "../components/PageHeader.vue";

const settingsState = useAsyncState({ groups: [] });

async function loadSettings() {
  await settingsState.run(() => api.getSettings());
}

async function saveSettings() {
  await settingsState.run(() => api.updateSettings(settingsState.data.value));
}

onMounted(loadSettings);
</script>

<template>
  <PageHeader
    eyebrow="Settings"
    title="사이트 설정"
    description="블로그 운영 정책과 기본값을 Tailwind 기반 설정 화면에서 관리합니다."
  >
    <template #actions>
      <button class="rounded-full bg-stone-950 px-5 py-3 text-sm font-semibold text-white hover:bg-stone-800" @click="saveSettings">
        설정 저장
      </button>
      <button class="rounded-full border border-stone-300 bg-white px-5 py-3 text-sm font-semibold text-stone-700 hover:bg-stone-50" @click="loadSettings">
        다시 불러오기
      </button>
    </template>
    <template #side>
      <div class="space-y-4">
        <h3 class="text-sm font-bold text-stone-900">연결 API</h3>
        <ul class="space-y-2 text-sm text-stone-600">
          <li class="rounded-2xl border border-white/60 bg-white/80 px-4 py-3">GET /api/admin/settings</li>
          <li class="rounded-2xl border border-white/60 bg-white/80 px-4 py-3">PUT /api/admin/settings</li>
        </ul>
      </div>
    </template>
  </PageHeader>

  <section v-if="settingsState.error.value" class="rounded-[28px] border border-rose-200 bg-rose-50 p-6 text-rose-700">
    <strong class="text-base font-bold">API 오류</strong>
    <p class="mt-2 text-sm">{{ settingsState.error.value }}</p>
  </section>

  <section class="grid gap-4 xl:grid-cols-2">
    <article
      v-for="group in settingsState.data.value.groups"
      :key="group.title"
      class="rounded-[32px] border border-stone-200/80 bg-white/80 p-6 shadow-[0_20px_60px_rgba(40,28,16,0.08)]"
    >
      <div class="mb-5">
        <h3 class="text-xl font-bold text-stone-950">{{ group.title }}</h3>
      </div>

      <div class="space-y-3">
        <label
          v-for="item in group.items"
          :key="item.label"
          class="flex flex-col gap-2 rounded-[24px] border border-stone-200 bg-stone-50/80 p-4"
        >
          <span class="text-sm font-semibold text-stone-700">{{ item.label }}</span>
          <input
            v-model="item.value"
            class="w-full rounded-2xl border border-stone-300 bg-white px-4 py-3 text-sm outline-none transition focus:border-stone-500"
          />
        </label>
      </div>
    </article>
  </section>
</template>

<script setup>
import { computed } from "vue";
import { RouterLink, RouterView, useRoute } from "vue-router";

const route = useRoute();

const navItems = [
  { label: "대시보드", to: "/dashboard", icon: "DS" },
  { label: "게시판 관리", to: "/boards", icon: "BD" },
  { label: "메뉴 관리", to: "/menus", icon: "MN" },
  { label: "게시글 관리", to: "/posts", icon: "PT" },
  { label: "댓글 관리", to: "/comments", icon: "CM" },
  { label: "설정", to: "/settings", icon: "ST" }
];

const quickTasks = [
  "포트폴리오 게시판 구조 확인",
  "메뉴 노출 순서 검토",
  "신규 댓글 처리",
  "설정값 저장 여부 점검"
];

const pageTitle = computed(() => {
  const current = navItems.find((item) => item.to === route.path);
  return current ? current.label : "관리자";
});
</script>

<template>
  <div class="min-h-screen bg-[linear-gradient(180deg,#f7f4ee_0%,#efe5d6_45%,#efe8de_100%)] text-stone-900">
    <div class="mx-auto flex min-h-screen max-w-[1600px] flex-col gap-6 px-4 py-4 lg:flex-row lg:px-6">
      <aside class="w-full shrink-0 lg:sticky lg:top-0 lg:h-screen lg:w-[320px] lg:py-4">
        <div class="flex h-full flex-col gap-5 rounded-[32px] border border-stone-200/70 bg-white/70 p-6 shadow-[0_24px_80px_rgba(40,28,16,0.12)] backdrop-blur">
          <div class="space-y-3">
            <span class="inline-flex rounded-full bg-emerald-50 px-3 py-1 text-xs font-semibold uppercase tracking-[0.24em] text-emerald-700">
              Omyohan CMS
            </span>
            <div class="space-y-2">
              <h1 class="text-3xl font-black tracking-tight text-stone-950">관리자 센터</h1>
              <p class="text-sm leading-6 text-stone-600">
                Spring Boot Swagger API와 연결된 블로그 CMS 관리자 화면입니다.
              </p>
            </div>
          </div>

          <nav class="grid gap-2">
            <RouterLink
              v-for="item in navItems"
              :key="item.to"
              :to="item.to"
              class="group flex items-center gap-3 rounded-2xl border px-4 py-3 text-sm font-medium transition"
              :class="route.path === item.to
                ? 'border-stone-900 bg-stone-950 text-white shadow-lg'
                : 'border-stone-200 bg-stone-50/80 text-stone-700 hover:border-stone-300 hover:bg-white'"
            >
              <span
                class="inline-flex h-9 w-9 items-center justify-center rounded-xl text-[11px] font-bold tracking-wide"
                :class="route.path === item.to ? 'bg-white/15 text-white' : 'bg-stone-900 text-stone-100'"
              >
                {{ item.icon }}
              </span>
              <span>{{ item.label }}</span>
            </RouterLink>
          </nav>

          <div class="mt-auto rounded-[28px] border border-stone-200 bg-stone-50/90 p-5">
            <div class="mb-4 flex items-center justify-between gap-3">
              <span class="text-sm font-semibold text-stone-900">현재 페이지</span>
              <span class="rounded-full bg-emerald-100 px-3 py-1 text-xs font-semibold text-emerald-700">
                {{ pageTitle }}
              </span>
            </div>
            <ul class="space-y-2">
              <li
                v-for="task in quickTasks"
                :key="task"
                class="rounded-2xl border border-stone-200 bg-white px-4 py-3 text-sm text-stone-600"
              >
                {{ task }}
              </li>
            </ul>
          </div>
        </div>
      </aside>

      <main class="min-w-0 flex-1 py-1 lg:py-4">
        <div class="space-y-6">
          <RouterView />
        </div>
      </main>
    </div>
  </div>
</template>

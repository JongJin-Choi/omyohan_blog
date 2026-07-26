<script setup>
import { computed, onMounted, reactive, ref } from "vue";
import { api } from "../api/client";
import { useAsyncState } from "../composables/useAsyncState";
import PageHeader from "../components/PageHeader.vue";

const menusState = useAsyncState([]);
const boardsState = useAsyncState([]);

const initialValues = {
  id: null,
  parentId: null,
  boardId: null,
  name: "",
  slug: "",
  depth: 1,
  sortOrder: 1,
  isVisible: true
};

const form = reactive({ ...initialValues });
const saving = ref(false);
const errors = reactive({
  submit: "",
  boardId: "",
  name: "",
  slug: "",
  depth: "",
  sortOrder: ""
});

const hasBoards = computed(() => boardsState.data.value.length > 0);
const boardNameMap = computed(() => Object.fromEntries(boardsState.data.value.map((board) => [board.id, board.name])));

function clearErrors() {
  Object.keys(errors).forEach((key) => {
    errors[key] = "";
  });
}

function resetForm() {
  clearErrors();
  Object.assign(form, initialValues, {
    boardId: boardsState.data.value[0]?.id ?? null
  });
}

function normalizeText(value) {
  return typeof value === "string" ? value.trim() : "";
}

function validateForm() {
  clearErrors();

  if (!form.boardId) {
    errors.boardId = "연결할 게시판을 선택해주세요.";
  }
  if (!normalizeText(form.name)) {
    errors.name = "메뉴명을 입력해주세요.";
  }
  if (!normalizeText(form.slug)) {
    errors.slug = "slug를 입력해주세요.";
  } else if (!/^[a-z0-9-]+$/.test(normalizeText(form.slug))) {
    errors.slug = "slug는 영문 소문자, 숫자, 하이픈만 사용할 수 있습니다.";
  }
  if (!Number.isInteger(form.depth) || form.depth < 1) {
    errors.depth = "depth는 1 이상의 정수여야 합니다.";
  }
  if (!Number.isInteger(form.sortOrder) || form.sortOrder < 1) {
    errors.sortOrder = "정렬 순서는 1 이상의 정수여야 합니다.";
  }

  return !Object.values(errors).some(Boolean);
}

async function loadMenus() {
  await menusState.run(() => api.getMenus());
}

async function loadBoards() {
  await boardsState.run(() => api.getBoards());
}

function editMenu(menu) {
  clearErrors();
  form.id = menu.id;
  form.parentId = menu.parentId;
  form.boardId = menu.boardId;
  form.name = menu.name;
  form.slug = menu.slug;
  form.depth = menu.depth;
  form.sortOrder = menu.sortOrder;
  form.isVisible = menu.isVisible;
}

async function submitMenu() {
  if (!validateForm()) return;

  saving.value = true;
  errors.submit = "";

  try {
    const payload = {
      parentId: form.parentId,
      boardId: form.boardId,
      name: normalizeText(form.name),
      slug: normalizeText(form.slug),
      depth: form.depth,
      sortOrder: form.sortOrder,
      isVisible: form.isVisible
    };

    if (form.id) {
      await api.updateMenu(form.id, payload);
    } else {
      await api.createMenu(payload);
    }

    resetForm();
    await loadMenus();
  } catch (error) {
    errors.submit = error instanceof Error ? error.message : "메뉴 저장 중 오류가 발생했습니다.";
  } finally {
    saving.value = false;
  }
}

async function removeMenu(menuId) {
  clearErrors();
  try {
    await api.deleteMenu(menuId);
    if (form.id === menuId) resetForm();
    await loadMenus();
  } catch (error) {
    errors.submit = error instanceof Error ? error.message : "메뉴 삭제 중 오류가 발생했습니다.";
  }
}

onMounted(async () => {
  await Promise.all([loadMenus(), loadBoards()]);
  resetForm();
});
</script>

<template>
  <PageHeader
    eyebrow="Menus"
    title="메뉴 관리"
    description="메뉴 구조와 연결 게시판을 실데이터 기준으로 관리합니다."
  >
    <template #actions>
      <button class="rounded-full bg-stone-950 px-5 py-3 text-sm font-semibold text-white hover:bg-stone-800" @click="resetForm">
        새 메뉴
      </button>
      <button class="rounded-full border border-stone-300 bg-white px-5 py-3 text-sm font-semibold text-stone-700 hover:bg-stone-50" @click="loadMenus">
        목록 새로고침
      </button>
    </template>
    <template #side>
      <div class="space-y-4">
        <h3 class="text-sm font-bold text-stone-900">연동 API</h3>
        <ul class="space-y-2 text-sm text-stone-600">
          <li class="rounded-2xl border border-white/60 bg-white/80 px-4 py-3">GET /api/admin/menus</li>
          <li class="rounded-2xl border border-white/60 bg-white/80 px-4 py-3">POST /api/admin/menus</li>
          <li class="rounded-2xl border border-white/60 bg-white/80 px-4 py-3">PUT /api/admin/menus/{id}</li>
          <li class="rounded-2xl border border-white/60 bg-white/80 px-4 py-3">DELETE /api/admin/menus/{id}</li>
        </ul>
      </div>
    </template>
  </PageHeader>

  <section v-if="menusState.error.value || boardsState.error.value" class="rounded-[28px] border border-rose-200 bg-rose-50 p-6 text-rose-700">
    <strong class="text-base font-bold">API 오류</strong>
    <p v-if="menusState.error.value" class="mt-2 text-sm">메뉴 목록: {{ menusState.error.value }}</p>
    <p v-if="boardsState.error.value" class="mt-2 text-sm">게시판 목록: {{ boardsState.error.value }}</p>
  </section>

  <section class="grid gap-4 xl:grid-cols-[minmax(0,1.1fr)_420px]">
    <div class="rounded-[32px] border border-stone-200/80 bg-white/80 p-6 shadow-[0_20px_60px_rgba(40,28,16,0.08)]">
      <div class="mb-5">
        <h3 class="text-xl font-bold text-stone-950">메뉴 목록</h3>
        <p class="mt-1 text-sm text-stone-600">노출 상태와 정렬 순서를 확인할 수 있습니다.</p>
      </div>

      <div v-if="menusState.loading.value" class="rounded-[24px] border border-dashed border-stone-300 bg-stone-50 p-6 text-sm text-stone-500">
        메뉴 목록을 불러오는 중입니다.
      </div>

      <div v-else-if="menusState.data.value.length === 0" class="rounded-[24px] border border-dashed border-stone-300 bg-stone-50 p-6 text-sm text-stone-500">
        등록된 메뉴가 없습니다.
      </div>

      <div v-else class="space-y-3">
        <article
          v-for="menu in menusState.data.value"
          :key="menu.id"
          class="flex flex-col gap-4 rounded-[24px] border border-stone-200 bg-stone-50/80 p-5 md:flex-row md:items-center md:justify-between"
        >
          <div>
            <h4 class="text-lg font-bold text-stone-950">{{ menu.name }}</h4>
            <p class="mt-1 text-sm text-stone-500">{{ boardNameMap[menu.boardId] ?? `boardId ${menu.boardId}` }} / {{ menu.slug }} / depth {{ menu.depth }}</p>
          </div>
          <div class="flex flex-wrap items-center gap-2">
            <span class="rounded-full bg-sky-100 px-3 py-1 text-xs font-semibold text-sky-700">정렬 {{ menu.sortOrder }}</span>
            <span :class="menu.isVisible ? 'bg-emerald-100 text-emerald-700' : 'bg-stone-200 text-stone-600'" class="rounded-full px-3 py-1 text-xs font-semibold">
              {{ menu.isVisible ? "노출" : "숨김" }}
            </span>
            <button class="rounded-full bg-emerald-100 px-4 py-2 text-sm font-semibold text-emerald-700 hover:bg-emerald-200" @click="editMenu(menu)">
              수정
            </button>
            <button class="rounded-full bg-rose-100 px-4 py-2 text-sm font-semibold text-rose-700 hover:bg-rose-200" @click="removeMenu(menu.id)">
              삭제
            </button>
          </div>
        </article>
      </div>
    </div>

    <div class="rounded-[32px] border border-stone-200/80 bg-white/80 p-6 shadow-[0_20px_60px_rgba(40,28,16,0.08)]">
      <div class="mb-5">
        <h3 class="text-xl font-bold text-stone-950">{{ form.id ? "메뉴 수정" : "메뉴 등록" }}</h3>
        <p class="mt-1 text-sm text-stone-600">메뉴 저장 전 필수값과 연결 게시판 상태를 확인합니다.</p>
      </div>

      <div v-if="errors.submit" class="mb-4 rounded-2xl border border-rose-200 bg-rose-50 px-4 py-3 text-sm text-rose-700">
        {{ errors.submit }}
      </div>

      <div class="space-y-4">
        <label class="space-y-2">
          <span class="text-sm font-semibold text-stone-700">메뉴명</span>
          <input v-model="form.name" class="w-full rounded-2xl border px-4 py-3 text-sm outline-none transition focus:border-stone-500" :class="errors.name ? 'border-rose-300 bg-rose-50' : 'border-stone-300 bg-white'" />
          <p v-if="errors.name" class="text-sm text-rose-600">{{ errors.name }}</p>
        </label>

        <div class="grid gap-4 sm:grid-cols-2">
          <label class="space-y-2">
            <span class="text-sm font-semibold text-stone-700">연결 게시판</span>
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
            <p v-else-if="boardsState.error.value" class="text-sm text-rose-600">게시판 목록을 가져오지 못했습니다.</p>
          </label>
          <label class="space-y-2">
            <span class="text-sm font-semibold text-stone-700">노출 상태</span>
            <select v-model="form.isVisible" class="w-full rounded-2xl border border-stone-300 bg-white px-4 py-3 text-sm outline-none transition focus:border-stone-500">
              <option :value="true">노출</option>
              <option :value="false">숨김</option>
            </select>
          </label>
        </div>

        <div class="grid gap-4 sm:grid-cols-2">
          <label class="space-y-2">
            <span class="text-sm font-semibold text-stone-700">정렬 순서</span>
            <input v-model.number="form.sortOrder" type="number" class="w-full rounded-2xl border px-4 py-3 text-sm outline-none transition focus:border-stone-500" :class="errors.sortOrder ? 'border-rose-300 bg-rose-50' : 'border-stone-300 bg-white'" />
            <p v-if="errors.sortOrder" class="text-sm text-rose-600">{{ errors.sortOrder }}</p>
          </label>
          <label class="space-y-2">
            <span class="text-sm font-semibold text-stone-700">depth</span>
            <input v-model.number="form.depth" type="number" class="w-full rounded-2xl border px-4 py-3 text-sm outline-none transition focus:border-stone-500" :class="errors.depth ? 'border-rose-300 bg-rose-50' : 'border-stone-300 bg-white'" />
            <p v-if="errors.depth" class="text-sm text-rose-600">{{ errors.depth }}</p>
          </label>
        </div>

        <div class="grid gap-4 sm:grid-cols-2">
          <label class="space-y-2">
            <span class="text-sm font-semibold text-stone-700">부모 메뉴 ID</span>
            <input v-model.number="form.parentId" type="number" class="w-full rounded-2xl border border-stone-300 bg-white px-4 py-3 text-sm outline-none transition focus:border-stone-500" />
          </label>
          <label class="space-y-2">
            <span class="text-sm font-semibold text-stone-700">slug</span>
            <input v-model="form.slug" class="w-full rounded-2xl border px-4 py-3 text-sm outline-none transition focus:border-stone-500" :class="errors.slug ? 'border-rose-300 bg-rose-50' : 'border-stone-300 bg-white'" />
            <p v-if="errors.slug" class="text-sm text-rose-600">{{ errors.slug }}</p>
          </label>
        </div>

        <div class="flex flex-wrap gap-3 pt-2">
          <button class="rounded-full bg-stone-950 px-5 py-3 text-sm font-semibold text-white hover:bg-stone-800 disabled:opacity-60" :disabled="saving || !hasBoards" @click="submitMenu">
            {{ form.id ? "메뉴 수정" : "메뉴 저장" }}
          </button>
          <button class="rounded-full border border-stone-300 bg-white px-5 py-3 text-sm font-semibold text-stone-700 hover:bg-stone-50" @click="resetForm">
            입력 초기화
          </button>
        </div>
      </div>
    </div>
  </section>
</template>

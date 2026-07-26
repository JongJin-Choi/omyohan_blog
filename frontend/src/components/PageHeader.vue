<script setup>
defineProps({
  eyebrow: String,
  title: String,
  description: String
});
</script>

<template>
  <section class="grid gap-4 xl:grid-cols-[minmax(0,1.5fr)_360px]">
    <div class="rounded-[32px] border border-stone-200/80 bg-white/75 p-7 shadow-[0_24px_80px_rgba(40,28,16,0.10)] backdrop-blur">
      <div class="space-y-5">
        <div class="space-y-3">
          <span class="inline-flex rounded-full bg-stone-900 px-3 py-1 text-xs font-semibold uppercase tracking-[0.24em] text-stone-100">
            {{ eyebrow }}
          </span>
          <div class="space-y-2">
            <h2 class="text-3xl font-black tracking-tight text-stone-950 md:text-4xl">{{ title }}</h2>
            <p class="max-w-3xl text-sm leading-7 text-stone-600 md:text-base">
              {{ description }}
            </p>
          </div>
        </div>
        <div class="flex flex-wrap gap-3">
          <slot name="actions" />
        </div>
      </div>
    </div>

    <div class="rounded-[32px] border border-stone-200/80 bg-gradient-to-br from-emerald-50 via-white to-amber-50 p-6 shadow-[0_24px_80px_rgba(40,28,16,0.10)]">
      <slot name="side" />
    </div>
  </section>
</template>

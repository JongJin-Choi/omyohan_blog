import { ref } from "vue";

export function useAsyncState(initialValue) {
  const data = ref(initialValue);
  const loading = ref(false);
  const error = ref("");

  async function run(loader) {
    loading.value = true;
    error.value = "";
    try {
      data.value = await loader();
    } catch (err) {
      error.value = err instanceof Error ? err.message : "알 수 없는 오류가 발생했습니다.";
    } finally {
      loading.value = false;
    }
  }

  return {
    data,
    loading,
    error,
    run
  };
}

function toErrorMessage(path, status, payload, fallbackText) {
  if (payload && typeof payload === "object") {
    if (typeof payload.message === "string" && payload.message.trim()) {
      return payload.message;
    }
    if (typeof payload.error === "string" && payload.error.trim()) {
      return payload.error;
    }
    if (Array.isArray(payload.errors) && payload.errors.length > 0) {
      return payload.errors.join(", ");
    }
  }

  if (fallbackText) {
    return fallbackText;
  }

  return `${path} 요청이 실패했습니다. (status: ${status})`;
}

async function request(path, options = {}) {
  const response = await fetch(path, {
    headers: {
      "Content-Type": "application/json",
      ...(options.headers || {})
    },
    ...options
  });

  const rawText = await response.text();
  let payload = null;

  if (rawText) {
    try {
      payload = JSON.parse(rawText);
    } catch {
      payload = null;
    }
  }

  if (!response.ok) {
    throw new Error(toErrorMessage(path, response.status, payload, rawText.trim()));
  }

  if (response.status === 204 || !rawText) {
    return null;
  }

  return payload ?? rawText;
}

export const api = {
  health() {
    return request("/api/health");
  },
  getBoards() {
    return request("/api/admin/boards");
  },
  getBoard(boardId) {
    return request(`/api/admin/boards/${boardId}`);
  },
  createBoard(payload) {
    return request("/api/admin/boards", { method: "POST", body: JSON.stringify(payload) });
  },
  updateBoard(boardId, payload) {
    return request(`/api/admin/boards/${boardId}`, { method: "PUT", body: JSON.stringify(payload) });
  },
  deleteBoard(boardId) {
    return request(`/api/admin/boards/${boardId}`, { method: "DELETE" });
  },
  getMenus() {
    return request("/api/admin/menus");
  },
  getMenu(menuId) {
    return request(`/api/admin/menus/${menuId}`);
  },
  createMenu(payload) {
    return request("/api/admin/menus", { method: "POST", body: JSON.stringify(payload) });
  },
  updateMenu(menuId, payload) {
    return request(`/api/admin/menus/${menuId}`, { method: "PUT", body: JSON.stringify(payload) });
  },
  deleteMenu(menuId) {
    return request(`/api/admin/menus/${menuId}`, { method: "DELETE" });
  },
  getPosts(boardId) {
    const query = boardId ? `?boardId=${boardId}` : "";
    return request(`/api/admin/posts${query}`);
  },
  getPost(postId) {
    return request(`/api/admin/posts/${postId}`);
  },
  createPost(payload) {
    return request("/api/admin/posts", { method: "POST", body: JSON.stringify(payload) });
  },
  updatePost(postId, payload) {
    return request(`/api/admin/posts/${postId}`, { method: "PUT", body: JSON.stringify(payload) });
  },
  deletePost(postId) {
    return request(`/api/admin/posts/${postId}`, { method: "DELETE" });
  },
  getComments(postId) {
    const query = postId ? `?postId=${postId}` : "";
    return request(`/api/admin/comments${query}`);
  },
  hideComment(commentId) {
    return request(`/api/admin/comments/${commentId}`, { method: "DELETE" });
  },
  getSettings() {
    return request("/api/admin/settings");
  },
  updateSettings(payload) {
    return request("/api/admin/settings", { method: "PUT", body: JSON.stringify(payload) });
  }
};

import { createRouter, createWebHistory } from "vue-router";
import DashboardPage from "../pages/DashboardPage.vue";
import BoardListPage from "../pages/BoardListPage.vue";
import MenuListPage from "../pages/MenuListPage.vue";
import PostListPage from "../pages/PostListPage.vue";
import CommentListPage from "../pages/CommentListPage.vue";
import SiteSettingsPage from "../pages/SiteSettingsPage.vue";

const routes = [
  { path: "/", redirect: "/dashboard" },
  { path: "/dashboard", component: DashboardPage },
  { path: "/boards", component: BoardListPage },
  { path: "/menus", component: MenuListPage },
  { path: "/posts", component: PostListPage },
  { path: "/comments", component: CommentListPage },
  { path: "/settings", component: SiteSettingsPage }
];

export default createRouter({
  history: createWebHistory(),
  routes
});

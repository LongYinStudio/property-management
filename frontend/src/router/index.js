import { createRouter, createWebHistory } from "vue-router";
import Cookies from "js-cookie";

const routes = [
  {
    path: "/login",
    name: "Login",
    component: () => import("@/views/Login.vue"),
    meta: { title: "登录" },
  },
  {
    path: "/",
    component: () => import("@/layout/MainLayout.vue"),
    redirect: "/dashboard",
    children: [
      {
        path: "dashboard",
        name: "Dashboard",
        component: () => import("@/views/Dashboard.vue"),
        meta: { title: "首页", icon: "HomeFilled" },
      },
      {
        path: "user",
        name: "User",
        component: () => import("@/views/user/Index.vue"),
        meta: { title: "用户管理", icon: "User" },
      },
      {
        path: "repair",
        name: "Repair",
        component: () => import("@/views/repair/Index.vue"),
        meta: { title: "报修管理", icon: "Tools" },
      },
      {
        path: "cleaning",
        name: "Cleaning",
        component: () => import("@/views/cleaning/Index.vue"),
        meta: { title: "清洁管理", icon: "Brush" },
      },
      {
        path: "fee",
        name: "PropertyFee",
        component: () => import("@/views/fee/Index.vue"),
        meta: { title: "物业费管理", icon: "Wallet" },
      },
      {
        path: "complaint",
        name: "Complaint",
        component: () => import("@/views/complaint/Index.vue"),
        meta: { title: "投诉建议", icon: "ChatDotSquare" },
      },
      {
        path: "statistics",
        name: "Statistics",
        component: () => import("@/views/statistics/Index.vue"),
        meta: { title: "统计报表", icon: "DataAnalysis" },
      },
    ],
  },
];

const router = createRouter({
  history: createWebHistory(),
  routes,
});

// 路由守卫
router.beforeEach((to, from, next) => {
  document.title = to.meta.title
    ? `${to.meta.title} - 物业管理系统`
    : "物业管理系统";

  const token = Cookies.get("token");

  if (to.path === "/login") {
    if (token) {
      next("/dashboard");
    } else {
      next();
    }
  } else {
    if (token) {
      next();
    } else {
      next("/login");
    }
  }
});

export default router;

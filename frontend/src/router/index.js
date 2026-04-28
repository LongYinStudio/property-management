import { createRouter, createWebHistory } from "vue-router";
import Cookies from "js-cookie";
import { useUserStore } from "@/stores/user";

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
        path: "inspection",
        name: "EquipmentInspection",
        component: () => import("@/views/inspection/Index.vue"),
        meta: { title: "设备巡检", icon: "Operation" },
      },
      {
        path: "fee",
        name: "PropertyFee",
        component: () => import("@/views/fee/Index.vue"),
        meta: { title: "物业费管理", icon: "Wallet" },
      },
      {
        path: "parking",
        name: "Parking",
        component: () => import("@/views/parking/Index.vue"),
        meta: { title: "车位管理", icon: "Location" },
      },
      {
        path: "complaint",
        name: "Complaint",
        component: () => import("@/views/complaint/Index.vue"),
        meta: { title: "投诉建议", icon: "ChatDotSquare" },
      },
      {
        path: "vote",
        name: "Vote",
        component: () => import("@/views/vote/Index.vue"),
        meta: { title: "投票/问卷", icon: "Tickets" },
      },
      {
        path: "contract",
        name: "OwnerContract",
        component: () => import("@/views/contract/Index.vue"),
        meta: { title: "合同管理", icon: "Document" },
      },
      {
        path: "statistics",
        name: "Statistics",
        component: () => import("@/views/statistics/Index.vue"),
        meta: { title: "统计报表", icon: "DataAnalysis" },
      },
      {
        path: "profile",
        name: "Profile",
        component: () => import("@/views/Profile.vue"),
        meta: { title: "个人信息" },
      },
      {
        path: "change-password",
        name: "ChangePassword",
        component: () => import("@/views/ChangePassword.vue"),
        meta: { title: "修改密码" },
      },
    ],
  },
];

const router = createRouter({
  history: createWebHistory(),
  routes,
});

// 路由守卫
router.beforeEach(async (to, from, next) => {
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
      const userStore = useUserStore();
      // 如果 userInfo 为空，则获取用户信息
      if (!userStore.userInfo) {
        try {
          await userStore.getUserInfoAction();
        } catch (error) {
          // 获取用户信息失败，清除 token 并跳转登录页
          userStore.clearUserInfo();
          next("/login");
          return;
        }
      }
      next();
    } else {
      next("/login");
    }
  }
});

export default router;

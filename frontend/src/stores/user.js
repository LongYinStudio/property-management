import { defineStore } from "pinia";
import { ref } from "vue";
import Cookies from "js-cookie";
import { login, logout, getUserInfo } from "@/api/auth";

export const useUserStore = defineStore("user", () => {
  const token = ref(Cookies.get("token") || "");
  const userInfo = ref(null);

  // 登录
  const loginAction = async (loginForm) => {
    const res = await login(loginForm);
    token.value = res.data.token;
    userInfo.value = res.data.userInfo;
    Cookies.set("token", res.data.token, { expires: 1 });
    return res;
  };

  // 获取用户信息
  const getUserInfoAction = async () => {
    const res = await getUserInfo();
    userInfo.value = res.data;
    return res;
  };

  // 退出登录
  const logoutAction = async () => {
    await logout();
    token.value = "";
    userInfo.value = null;
    Cookies.remove("token");
  };

  // 清除用户信息
  const clearUserInfo = () => {
    token.value = "";
    userInfo.value = null;
    Cookies.remove("token");
  };

  return {
    token,
    userInfo,
    loginAction,
    getUserInfoAction,
    logoutAction,
    clearUserInfo,
  };
});

import request from "@/utils/request";

// 登录
export function login(data) {
  return request({
    url: "/auth/login",
    method: "post",
    data,
  });
}

// 注册
export function register(data) {
  return request({
    url: "/auth/register",
    method: "post",
    data,
  });
}

// 获取用户信息
export function getUserInfo() {
  return request({
    url: "/auth/info",
    method: "get",
  });
}

// 更新个人信息
export function updateProfile(data) {
  return request({
    url: "/auth/info",
    method: "put",
    data,
  });
}

// 修改密码
export function changePassword(data) {
  return request({
    url: "/auth/password",
    method: "put",
    data,
  });
}

// 退出登录
export function logout() {
  return Promise.resolve();
}

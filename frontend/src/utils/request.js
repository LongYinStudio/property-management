import axios from "axios";
import { ElMessage } from "element-plus";
import Cookies from "js-cookie";
import router from "@/router";

const normalizeUploadUrl = (value) => {
  if (typeof value !== "string") {
    return value;
  }
  const uploadsIndex = value.indexOf("/uploads/");
  if (uploadsIndex >= 0) {
    return value.slice(uploadsIndex);
  }
  return value;
};

const normalizeResponseData = (value) => {
  if (Array.isArray(value)) {
    return value.map(normalizeResponseData);
  }
  if (value && typeof value === "object") {
    return Object.fromEntries(
      Object.entries(value).map(([key, item]) => [key, normalizeResponseData(item)]),
    );
  }
  return normalizeUploadUrl(value);
};

// 创建axios实例
const request = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL || "/api",
  timeout: 10000,
});

// 请求拦截器
request.interceptors.request.use(
  (config) => {
    const token = Cookies.get("token");
    if (token) {
      config.headers["Authorization"] = `Bearer ${token}`;
    }
    return config;
  },
  (error) => {
    return Promise.reject(error);
  },
);

// 响应拦截器
request.interceptors.response.use(
  (response) => {
    // 如果是blob类型（文件下载），直接返回数据
    if (response.data instanceof Blob) {
      return response.data;
    }
    const res = normalizeResponseData(response.data);
    if (res.code === 200) {
      return res;
    } else if (res.code === 401) {
      ElMessage.error("登录已过期，请重新登录");
      Cookies.remove("token");
      router.push("/login");
      return Promise.reject(new Error(res.message));
    } else {
      ElMessage.error(res.message || "请求失败");
      return Promise.reject(new Error(res.message));
    }
  },
  (error) => {
    let message = "网络错误";
    if (error.response) {
      switch (error.response.status) {
        case 401:
          message = "登录已过期，请重新登录";
          Cookies.remove("token");
          router.push("/login");
          break;
        case 403:
          message = "权限不足";
          break;
        case 404:
          message = "请求资源不存在";
          break;
        case 500:
          message = "服务器错误";
          break;
        default:
          message = error.response.data?.message || "请求失败";
      }
    }
    ElMessage.error(message);
    return Promise.reject(error);
  },
);

export default request;

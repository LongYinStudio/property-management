import request from "@/utils/request";

// 获取首页统计数据
export function getDashboardStats() {
  return request({
    url: "/statistics/dashboard",
    method: "get",
  });
}

// 获取费用类型统计
export function getFeeTypeStats(params) {
  return request({
    url: "/statistics/fee-type",
    method: "get",
    params,
  });
}

// 获取报修类型统计
export function getRepairTypeStats() {
  return request({
    url: "/statistics/repair-type",
    method: "get",
  });
}

// 获取月度收入统计
export function getMonthlyIncome(params) {
  return request({
    url: "/statistics/monthly-income",
    method: "get",
    params,
  });
}

// 获取报修状态统计
export function getRepairStatusStats() {
  return request({
    url: "/statistics/repair-status",
    method: "get",
  });
}

// 获取投诉类型统计
export function getComplaintTypeStats() {
  return request({
    url: "/statistics/complaint-type",
    method: "get",
  });
}

// 获取清洁任务统计
export function getCleaningStats() {
  return request({
    url: "/statistics/cleaning",
    method: "get",
  });
}

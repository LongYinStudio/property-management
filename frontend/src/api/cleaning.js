import request from "@/utils/request";

// 新增清洁任务
export function createCleaning(data) {
  return request({
    url: "/cleaning",
    method: "post",
    data,
  });
}

// 分页查询清洁任务列表
export function getCleaningPage(params) {
  return request({
    url: "/cleaning/page",
    method: "get",
    params,
  });
}

// 获取清洁任务详情
export function getCleaningById(id) {
  return request({
    url: `/cleaning/${id}`,
    method: "get",
  });
}

// 删除清洁任务
export function deleteCleaning(id) {
  return request({
    url: `/cleaning/${id}`,
    method: "delete",
  });
}

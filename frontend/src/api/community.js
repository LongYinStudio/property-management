import request from "@/utils/request";

// 新增小区
export function createCommunity(data) {
  return request({
    url: "/community",
    method: "post",
    data,
  });
}

// 更新小区
export function updateCommunity(id, data) {
  return request({
    url: `/community/${id}`,
    method: "put",
    data,
  });
}

// 分页查询小区
export function getCommunityPage(params) {
  return request({
    url: "/community/page",
    method: "get",
    params,
  });
}

// 获取小区详情
export function getCommunityById(id) {
  return request({
    url: `/community/${id}`,
    method: "get",
  });
}

// 获取小区列表
export function getCommunityList() {
  return request({
    url: "/community/list",
    method: "get",
  });
}

// 删除小区
export function deleteCommunity(id) {
  return request({
    url: `/community/${id}`,
    method: "delete",
  });
}

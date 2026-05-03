import request from "@/utils/request";

// 新增公告
export function createNotice(data) {
  return request({
    url: "/notice",
    method: "post",
    data,
  });
}

// 更新公告
export function updateNotice(id, data) {
  return request({
    url: `/notice/${id}`,
    method: "put",
    data,
  });
}

// 分页查询公告
export function getNoticePage(params) {
  return request({
    url: "/notice/page",
    method: "get",
    params,
  });
}

// 获取公告详情
export function getNoticeById(id) {
  return request({
    url: `/notice/${id}`,
    method: "get",
  });
}

// 发布公告
export function publishNotice(id) {
  return request({
    url: `/notice/${id}/publish`,
    method: "post",
  });
}

// 撤回公告
export function revokeNotice(id) {
  return request({
    url: `/notice/${id}/revoke`,
    method: "post",
  });
}

// 删除公告
export function deleteNotice(id) {
  return request({
    url: `/notice/${id}`,
    method: "delete",
  });
}

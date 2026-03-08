import request from "@/utils/request";

// 新增投诉/建议
export function createComplaint(data) {
  return request({
    url: "/complaint",
    method: "post",
    data,
  });
}

// 分页查询投诉/建议列表
export function getComplaintPage(params) {
  return request({
    url: "/complaint/page",
    method: "get",
    params,
  });
}

// 获取投诉/建议详情
export function getComplaintById(id) {
  return request({
    url: `/complaint/${id}`,
    method: "get",
  });
}

// 删除投诉/建议
export function deleteComplaint(id) {
  return request({
    url: `/complaint/${id}`,
    method: "delete",
  });
}

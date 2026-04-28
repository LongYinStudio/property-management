import request from "@/utils/request";

// 新增访客登记
export function createVisitor(data) {
  return request({
    url: "/visitor",
    method: "post",
    data,
  });
}

// 分页查询访客记录
export function getVisitorPage(params) {
  return request({
    url: "/visitor/page",
    method: "get",
    params,
  });
}

// 获取访客详情
export function getVisitorById(id) {
  return request({
    url: `/visitor/${id}`,
    method: "get",
  });
}

// 核销通行证
export function verifyVisitor(id) {
  return request({
    url: `/visitor/${id}/verify`,
    method: "post",
  });
}

// 取消访客登记
export function cancelVisitor(id) {
  return request({
    url: `/visitor/${id}/cancel`,
    method: "post",
  });
}

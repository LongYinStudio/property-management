import request from "@/utils/request";

// 新增报修
export function createRepair(data) {
  return request({
    url: "/repair",
    method: "post",
    data,
  });
}

// 分页查询报修列表
export function getRepairPage(params) {
  return request({
    url: "/repair/page",
    method: "get",
    params,
  });
}

// 获取报修详情
export function getRepairById(id) {
  return request({
    url: `/repair/${id}`,
    method: "get",
  });
}

// 删除报修
export function deleteRepair(id) {
  return request({
    url: `/repair/${id}`,
    method: "delete",
  });
}

// 指派维修人员
export function assignRepair(id, data) {
  return request({
    url: `/repair/${id}/assign`,
    method: "post",
    data,
  });
}

// 完成报修处理
export function completeRepair(id, data) {
  return request({
    url: `/repair/${id}/complete`,
    method: "post",
    data,
  });
}

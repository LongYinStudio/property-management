import request from "@/utils/request";

// 新增设备巡检记录
export function createInspection(data) {
  return request({
    url: "/equipment-inspection",
    method: "post",
    data,
  });
}

// 分页查询设备巡检记录
export function getInspectionPage(params) {
  return request({
    url: "/equipment-inspection/page",
    method: "get",
    params,
  });
}

// 获取设备巡检记录详情
export function getInspectionById(id) {
  return request({
    url: `/equipment-inspection/${id}`,
    method: "get",
  });
}

// 删除设备巡检记录
export function deleteInspection(id) {
  return request({
    url: `/equipment-inspection/${id}`,
    method: "delete",
  });
}

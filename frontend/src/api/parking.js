import request from "@/utils/request";

// ========== 车位管理 ==========

// 新增车位
export function createParkingSpace(data) {
  return request({
    url: "/parking",
    method: "post",
    data,
  });
}

// 更新车位
export function updateParkingSpace(id, data) {
  return request({
    url: `/parking/${id}`,
    method: "put",
    data,
  });
}

// 分页查询车位列表
export function getParkingSpacePage(params) {
  return request({
    url: "/parking/page",
    method: "get",
    params,
  });
}

// 获取车位详情
export function getParkingSpaceById(id) {
  return request({
    url: `/parking/${id}`,
    method: "get",
  });
}

// 删除车位
export function deleteParkingSpace(id) {
  return request({
    url: `/parking/${id}`,
    method: "delete",
  });
}

// 分配车位（出售）
export function assignParkingSpace(id, userId) {
  return request({
    url: `/parking/${id}/assign`,
    method: "post",
    params: { userId },
  });
}

// ========== 租用管理 ==========

// 租用车位
export function rentParkingSpace(data) {
  return request({
    url: "/parking/rent",
    method: "post",
    data,
  });
}

// 续费车位
export function renewParkingSpace(rentalId, data) {
  return request({
    url: `/parking/rent/${rentalId}/renew`,
    method: "post",
    data,
  });
}

// 分页查询租用记录
export function getParkingRentalPage(params) {
  return request({
    url: "/parking/rent/page",
    method: "get",
    params,
  });
}

// 支付租用费用
export function payParkingRental(id) {
  return request({
    url: `/parking/rent/${id}/pay`,
    method: "post",
  });
}

// 取消租用
export function cancelParkingRental(id) {
  return request({
    url: `/parking/rent/${id}`,
    method: "delete",
  });
}

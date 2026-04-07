import request from "@/utils/request";

// 新增物业费
export function createPropertyFee(data) {
  return request({
    url: "/property-fee",
    method: "post",
    data,
  });
}

// 分页查询物业费列表
export function getPropertyFeePage(params) {
  return request({
    url: "/property-fee/page",
    method: "get",
    params,
  });
}

// 获取物业费详情
export function getPropertyFeeById(id) {
  return request({
    url: `/property-fee/${id}`,
    method: "get",
  });
}

// 支付物业费
export function payPropertyFee(id) {
  return request({
    url: `/property-fee/${id}/pay`,
    method: "post",
  });
}

// 删除物业费
export function deletePropertyFee(id) {
  return request({
    url: `/property-fee/${id}`,
    method: "delete",
  });
}

// 导出物业费Excel
export function exportPropertyFee(params) {
  return request({
    url: "/property-fee/export",
    method: "get",
    params,
    responseType: "blob",
  });
}

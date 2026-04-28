import request from "@/utils/request";

// 新增业主合同
export function createOwnerContract(data) {
  return request({
    url: "/owner-contract",
    method: "post",
    data,
  });
}

// 更新业主合同
export function updateOwnerContract(id, data) {
  return request({
    url: `/owner-contract/${id}`,
    method: "put",
    data,
  });
}

// 分页查询业主合同
export function getOwnerContractPage(params) {
  return request({
    url: "/owner-contract/page",
    method: "get",
    params,
  });
}

// 获取业主合同详情
export function getOwnerContractById(id) {
  return request({
    url: `/owner-contract/${id}`,
    method: "get",
  });
}

// 删除业主合同
export function deleteOwnerContract(id) {
  return request({
    url: `/owner-contract/${id}`,
    method: "delete",
  });
}

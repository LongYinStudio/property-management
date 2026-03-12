import request from "@/utils/request";

// 新增用户
export function createUser(data) {
  return request({
    url: "/user",
    method: "post",
    data,
  });
}

// 更新用户
export function updateUser(id, data) {
  return request({
    url: `/user/${id}`,
    method: "put",
    data,
  });
}

// 分页查询用户列表
export function getUserPage(params) {
  return request({
    url: "/user/page",
    method: "get",
    params,
  });
}

// 获取用户详情
export function getUserById(id) {
  return request({
    url: `/user/${id}`,
    method: "get",
  });
}

// 删除用户
export function deleteUser(id) {
  return request({
    url: `/user/${id}`,
    method: "delete",
  });
}

// 更新用户状态
export function updateUserStatus(id, status) {
  return request({
    url: `/user/${id}/status`,
    method: "put",
    params: { status },
  });
}

// 获取业主列表
export function getOwnerList() {
  return request({
    url: "/user/owners",
    method: "get",
  });
}

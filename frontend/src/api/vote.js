import request from "@/utils/request";

// 创建投票/问卷活动
export function createVoteActivity(data) {
  return request({
    url: "/vote",
    method: "post",
    data,
  });
}

// 分页查询投票/问卷活动
export function getVotePage(params) {
  return request({
    url: "/vote/page",
    method: "get",
    params,
  });
}

// 获取投票/问卷详情
export function getVoteById(id) {
  return request({
    url: `/vote/${id}`,
    method: "get",
  });
}

// 提交投票/问卷
export function submitVote(id, data) {
  return request({
    url: `/vote/${id}/submit`,
    method: "post",
    data,
  });
}

// 结束活动
export function closeVote(id) {
  return request({
    url: `/vote/${id}/close`,
    method: "post",
  });
}

// 删除活动
export function deleteVote(id) {
  return request({
    url: `/vote/${id}`,
    method: "delete",
  });
}

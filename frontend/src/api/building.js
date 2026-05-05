import request from "@/utils/request";

export function createBuilding(data) {
  return request({
    url: "/building",
    method: "post",
    data,
  });
}

export function updateBuilding(id, data) {
  return request({
    url: `/building/${id}`,
    method: "put",
    data,
  });
}

export function getBuildingPage(params) {
  return request({
    url: "/building/page",
    method: "get",
    params,
  });
}

export function getBuildingById(id) {
  return request({
    url: `/building/${id}`,
    method: "get",
  });
}

export function getBuildingList(params) {
  return request({
    url: "/building/list",
    method: "get",
    params,
  });
}

export function deleteBuilding(id) {
  return request({
    url: `/building/${id}`,
    method: "delete",
  });
}

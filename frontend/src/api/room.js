import request from "@/utils/request";

export function createRoom(data) {
  return request({
    url: "/room",
    method: "post",
    data,
  });
}

export function updateRoom(id, data) {
  return request({
    url: `/room/${id}`,
    method: "put",
    data,
  });
}

export function getRoomPage(params) {
  return request({
    url: "/room/page",
    method: "get",
    params,
  });
}

export function getRoomById(id) {
  return request({
    url: `/room/${id}`,
    method: "get",
  });
}

export function getRoomList(params) {
  return request({
    url: "/room/list",
    method: "get",
    params,
  });
}

export function deleteRoom(id) {
  return request({
    url: `/room/${id}`,
    method: "delete",
  });
}

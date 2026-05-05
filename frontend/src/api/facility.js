import request from "@/utils/request";

export function createFacility(data) {
  return request({
    url: "/facility",
    method: "post",
    data,
  });
}

export function updateFacility(id, data) {
  return request({
    url: `/facility/${id}`,
    method: "put",
    data,
  });
}

export function getFacilityPage(params) {
  return request({
    url: "/facility/page",
    method: "get",
    params,
  });
}

export function getFacilityById(id) {
  return request({
    url: `/facility/${id}`,
    method: "get",
  });
}

export function getFacilityList(params) {
  return request({
    url: "/facility/list",
    method: "get",
    params,
  });
}

export function deleteFacility(id) {
  return request({
    url: `/facility/${id}`,
    method: "delete",
  });
}

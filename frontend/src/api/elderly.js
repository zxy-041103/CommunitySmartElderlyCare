import request from "@/utils/request";

export function getElderlyList(params) {
  return request({
    url: "/user/list",
    method: "get",
    params: {
      ...params,
      roleType: "elderly",
    },
  });
}

export function getElderlyDetail(id) {
  return request({
    url: `/user/${id}`,
    method: "get",
  });
}

export function createElderly(data) {
  console.log("Creating elderly with data:", data);
  return request({
    url: "/user",
    method: "post",
    data: {
      ...data,
      roleType: "elderly",
    },
  });
}

export function updateElderly(id, data) {
  return request({
    url: `/user/${id}`,
    method: "put",
    data,
  });
}

export function deleteElderly(id) {
  return request({
    url: `/user/${id}`,
    method: "delete",
  });
}

export function getElderlyStatistics() {
  return request({
    url: "/user/statistics",
    method: "get",
    params: {
      roleType: "elderly",
    },
  });
}

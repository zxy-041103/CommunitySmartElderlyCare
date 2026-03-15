import request from "@/utils/request";

export function queryHealthData(params) {
  return request({
    url: "/elderly/health-data",
    method: "get",
    params,
  });
}

export function inputHealthData(data) {
  return request({
    url: "/elderly/health-data",
    method: "post",
    data,
  });
}

export function deleteHealthData(id) {
  return request({
    url: `/elderly/health-data/${id}`,
    method: "delete",
  });
}

import request from "@/utils/request";

export function getHealthList(params) {
  return request({
    url: "/health/list",
    method: "get",
    params,
  });
}

export function getHealthDetail(id) {
  return request({
    url: `/health/${id}`,
    method: "get",
  });
}

export function createHealth(data) {
  return request({
    url: "/health/input",
    method: "post",
    data,
  });
}

export function updateHealth(id, data) {
  return request({
    url: `/health/${id}`,
    method: "put",
    data,
  });
}

export function deleteHealth(id) {
  return request({
    url: `/health/${id}`,
    method: "delete",
  });
}

export function getHealthStatistics(params) {
  return request({
    url: "/health/statistics",
    method: "get",
    params,
  });
}

export function getHealthTrend(elderlyId, params) {
  return request({
    url: `/health/trend/${elderlyId}`,
    method: "get",
    params,
  });
}

export function getAllHealthTrend(params) {
  return request({
    url: "/health/trend/all",
    method: "get",
    params,
  });
}

export function getAbnormalHealthList(params) {
  return request({
    url: "/health/warnings",
    method: "get",
    params,
  });
}

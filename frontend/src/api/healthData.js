import request from "@/utils/request";

export function inputHealthData(data) {
  return request({
    url: "/health/input",
    method: "post",
    data,
  });
}

export function queryHealthDataByTimeRange(data) {
  return request({
    url: "/health/query",
    method: "post",
    data,
  });
}

export function configureWarningThreshold(data) {
  return request({
    url: "/health/threshold/configure",
    method: "post",
    data,
  });
}

export function getCurrentWarningThresholds() {
  return request({
    url: "/health/threshold/current",
    method: "get",
  });
}

export function getHealthWarnings(params) {
  return request({
    url: "/health/warnings",
    method: "get",
    params,
  });
}

export function getFamilyElderlyHealth(familyId) {
  return request({
    url: `/health/family/${familyId}/elderly`,
    method: "get",
  });
}

export function getLatestHealthData(userId) {
  return request({
    url: `/health/latest/${userId}`,
    method: "get",
  });
}

export function getHealthStatistics() {
  return request({
    url: "/health/statistics",
    method: "get",
  });
}

export function getHealthDataList(params) {
  return request({
    url: "/health/list",
    method: "get",
    params,
  });
}

export function getHealthTrend(userId, params) {
  return request({
    url: `/health/trend/${userId}`,
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

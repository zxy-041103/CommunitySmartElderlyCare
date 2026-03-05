import request from "@/utils/request";

export function getEmergencyList(params) {
  return request({
    url: "/emergency-help/list",
    method: "get",
    params,
  });
}

export function getEmergencyDetail(id) {
  return request({
    url: `/emergency-help/${id}`,
    method: "get",
  });
}

export function createEmergency(data) {
  return request({
    url: "/emergency-help",
    method: "post",
    data,
  });
}

export function updateEmergency(id, data) {
  return request({
    url: `/emergency-help/${id}/status`,
    method: "put",
    data,
  });
}

export function deleteEmergency(id) {
  return request({
    url: `/emergency-help/${id}`,
    method: "delete",
  });
}

export function handleEmergency(id, data) {
  return request({
    url: `/emergency-help/${id}/assign`,
    method: "post",
    data,
  });
}

export function getEmergencyStatistics(params) {
  return request({
    url: "/emergency-help/statistics",
    method: "get",
    params,
  });
}

export function getEmergencyTrend(params) {
  return request({
    url: "/emergency-help/trend",
    method: "get",
    params,
  });
}

export function getPendingEmergencies(params) {
  return request({
    url: "/emergency-help/admin/pending",
    method: "get",
    params,
  });
}

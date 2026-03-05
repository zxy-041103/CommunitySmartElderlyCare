import request from "../request";

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

export function submitEmergencyHelp(data) {
  return request({
    url: "/emergency-help",
    method: "post",
    data,
  });
}

export function assignCaregiver(helpId) {
  return request({
    url: `/emergency-help/${helpId}/assign`,
    method: "post",
  });
}

export function updateHelpStatus(helpId, data) {
  return request({
    url: `/emergency-help/${helpId}/status`,
    method: "put",
    data,
  });
}

export function getHelpRecords(params) {
  return request({
    url: "/emergency-help",
    method: "get",
    params,
  });
}

export function getHelpDetail(helpId) {
  return request({
    url: `/emergency-help/${helpId}`,
    method: "get",
  });
}

export function getAllHelpRecords(params) {
  return request({
    url: "/emergency-help/admin/all",
    method: "get",
    params,
  });
}

export function getPendingHelpRecords(params) {
  return request({
    url: "/emergency-help/admin/pending",
    method: "get",
    params,
  });
}

export function getHandledHelpRecords(params) {
  return request({
    url: "/emergency-help/caregiver/handled",
    method: "get",
    params,
  });
}

export function getHelpStatistics() {
  return request({
    url: "/emergency-help/statistics",
    method: "get",
  });
}

export function getHelpList(params) {
  return request({
    url: "/emergency-help/list",
    method: "get",
    params,
  });
}

export function getHelpTrend(days) {
  return request({
    url: "/emergency-help/trend",
    method: "get",
    params: { days },
  });
}
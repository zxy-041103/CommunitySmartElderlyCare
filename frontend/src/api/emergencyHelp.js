import request from "@/utils/request";

export function submitEmergencyHelp(data) {
  return request({
    url: "/api/emergency-help",
    method: "post",
    data,
  });
}

export function assignCaregiver(helpId) {
  return request({
    url: `/api/emergency-help/${helpId}/assign`,
    method: "post",
  });
}

export function updateHelpStatus(helpId, data) {
  return request({
    url: `/api/emergency-help/${helpId}/status`,
    method: "put",
    data,
  });
}

export function getHelpRecords(params) {
  return request({
    url: "/api/emergency-help",
    method: "get",
    params,
  });
}

export function getHelpDetail(helpId) {
  return request({
    url: `/api/emergency-help/${helpId}`,
    method: "get",
  });
}

export function getAllHelpRecords(params) {
  return request({
    url: "/api/emergency-help/admin/all",
    method: "get",
    params,
  });
}

export function getPendingHelpRecords(params) {
  return request({
    url: "/api/emergency-help/admin/pending",
    method: "get",
    params,
  });
}

export function getHandledHelpRecords(params) {
  return request({
    url: "/api/emergency-help/caregiver/handled",
    method: "get",
    params,
  });
}

export function getHelpStatistics() {
  return request({
    url: "/api/emergency-help/statistics",
    method: "get",
  });
}

export function getHelpList(params) {
  return request({
    url: "/api/emergency-help/list",
    method: "get",
    params,
  });
}

export function getHelpTrend(days) {
  return request({
    url: "/api/emergency-help/trend",
    method: "get",
    params: { days },
  });
}

import request from "../request";

export function getCaregiverTasks(params) {
  return request({
    url: "/service-order/caregiver/pending",
    method: "get",
    params,
  });
}

export function getTaskDetail(id) {
  return request({
    url: `/service-order/${id}`,
    method: "get",
  });
}

export function updateTaskStatus(id, data) {
  return request({
    url: `/service-order/${id}/status`,
    method: "put",
    data,
  });
}

export function getPendingEmergencies() {
  return request({
    url: "/emergency-help/admin/pending",
    method: "get",
  });
}

export function handleEmergency(id, data) {
  return request({
    url: `/emergency-help/${id}/status`,
    method: "put",
    data,
  });
}
import request from "@/utils/request";

export function getEmergencyList(params) {
  return request({
    url: "/elderly/emergency",
    method: "get",
    params,
  });
}

export function createEmergency(data) {
  return request({
    url: "/elderly/emergency",
    method: "post",
    data,
  });
}

export function cancelEmergency(id) {
  return request({
    url: `/elderly/emergency/${id}/cancel`,
    method: "post",
  });
}

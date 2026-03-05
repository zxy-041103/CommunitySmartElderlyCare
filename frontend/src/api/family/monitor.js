import request from "../request";

export function getElderlyHealthData(elderlyId, params) {
  return request({
    url: `/health/elderly/${elderlyId}`,
    method: "get",
    params,
  });
}

export function getElderlyServiceOrders(elderlyId, params) {
  return request({
    url: `/service-order/elderly/${elderlyId}`,
    method: "get",
    params,
  });
}

export function getElderlyEmergencies(elderlyId, params) {
  return request({
    url: `/emergency-help/elderly/${elderlyId}`,
    method: "get",
    params,
  });
}
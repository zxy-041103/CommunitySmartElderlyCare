import request from "@/utils/request";

export function createServiceOrder(data) {
  return request({
    url: "/api/service-order",
    method: "post",
    data,
  });
}

export function getServiceOrderProgress(params) {
  return request({
    url: "/api/service-order/progress",
    method: "get",
    params,
  });
}

export function verifyService(data) {
  return request({
    url: "/api/service-order/verify",
    method: "post",
    data,
  });
}

export function submitEvaluation(data) {
  return request({
    url: "/api/service-order/evaluation",
    method: "post",
    data,
  });
}

export function getOrderDetail(orderId) {
  return request({
    url: `/api/service-order/${orderId}`,
    method: "get",
  });
}

export function cancelOrder(orderId) {
  return request({
    url: `/api/service-order/${orderId}/cancel`,
    method: "put",
  });
}

export function getAllOrders(params) {
  return request({
    url: "/api/service-order/admin/all",
    method: "get",
    params,
  });
}

export function getPendingOrders(params) {
  return request({
    url: "/api/service-order/caregiver/pending",
    method: "get",
    params,
  });
}

export function getCaregiverOrders(params) {
  return request({
    url: "/api/service-order/caregiver/my",
    method: "get",
    params,
  });
}

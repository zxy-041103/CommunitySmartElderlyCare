import request from "../request";

export function createServiceOrder(data) {
  return request({
    url: "/service-order",
    method: "post",
    data,
  });
}

export function getServiceOrders(params) {
  return request({
    url: "/service-order/list",
    method: "get",
    params,
  });
}

export function getServiceOrderDetail(id) {
  return request({
    url: `/service-order/${id}`,
    method: "get",
  });
}

export function cancelServiceOrder(id) {
  return request({
    url: `/service-order/${id}/cancel`,
    method: "put",
  });
}

export function getServiceTypes() {
  return request({
    url: "/service-order/types",
    method: "get",
  });
}
import request from "../request";

export function submitServiceRecord(data) {
  return request({
    url: "/service-order/record",
    method: "post",
    data,
  });
}

export function getServiceRecords(params) {
  return request({
    url: "/service-order/caregiver/records",
    method: "get",
    params,
  });
}

export function getRecordDetail(id) {
  return request({
    url: `/service-order/record/${id}`,
    method: "get",
  });
}

export function verifyService(data) {
  return request({
    url: "/service-order/verify",
    method: "post",
    data,
  });
}
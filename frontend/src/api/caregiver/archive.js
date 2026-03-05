import request from "../request";

export function getElderlyArchives(params) {
  return request({
    url: "/user/elderly/archives",
    method: "get",
    params,
  });
}

export function getElderlyArchiveDetail(id) {
  return request({
    url: `/user/elderly/${id}/archive`,
    method: "get",
  });
}

export function updateElderlyArchive(id, data) {
  return request({
    url: `/user/elderly/${id}/archive`,
    method: "put",
    data,
  });
}

export function addElderlyHealthRecord(id, data) {
  return request({
    url: `/user/elderly/${id}/health-record`,
    method: "post",
    data,
  });
}
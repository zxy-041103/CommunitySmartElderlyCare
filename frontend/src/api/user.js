import request from "@/utils/request";

export function login(data) {
  return request({
    url: "/user/login",
    method: "post",
    data,
  });
}

export function getCaptcha() {
  return request({
    url: "/captcha",
    method: "get",
  });
}

export function logout() {
  return request({
    url: "/user/logout",
    method: "post",
  });
}

export function getUserInfo(id) {
  return request({
    url: `/user/${id}`,
    method: "get",
  });
}

export function updateUserInfo(id, data) {
  return request({
    url: `/user/${id}`,
    method: "put",
    data,
  });
}

export function changePassword(data) {
  return request({
    url: "/user/change-password",
    method: "post",
    data,
  });
}

export function getPendingRelations() {
  return request({
    url: "/user/relation/pending",
    method: "get",
  });
}

export function auditFamilyRelation(relationId, data) {
  return request({
    url: `/user/relation/${relationId}/audit`,
    method: "put",
    data,
  });
}

import request from "../request";

export function login(data) {
  return request({
    url: "/user/login",
    method: "post",
    data,
  });
}

export function logout() {
  return request({
    url: "/user/logout",
    method: "post",
  });
}

export function getCaptcha() {
  return request({
    url: "/user/captcha",
    method: "get",
  });
}

export function refreshToken() {
  return request({
    url: "/user/refresh-token",
    method: "post",
  });
}

export function getCurrentUser() {
  return request({
    url: "/user/current",
    method: "get",
  });
}
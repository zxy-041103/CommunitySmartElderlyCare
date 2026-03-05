import request from "../request";

export function getUsers(params) {
  return request({
    url: "/admin/users",
    method: "get",
    params,
  });
}

export function getUserDetail(id) {
  return request({
    url: `/user/${id}`,
    method: "get",
  });
}

export function createUser(data) {
  return request({
    url: "/user",
    method: "post",
    data,
  });
}

export function updateUser(id, data) {
  return request({
    url: `/user/${id}`,
    method: "put",
    data,
  });
}

export function deleteUser(id) {
  return request({
    url: `/admin/users/${id}`,
    method: "delete",
  });
}

export function updateUserStatus(id, status) {
  return request({
    url: `/user/${id}/status`,
    method: "put",
    data: { status },
  });
}

export function getUsersByRole(roleType) {
  return request({
    url: "/user/role",
    method: "get",
    params: { roleType },
  });
}
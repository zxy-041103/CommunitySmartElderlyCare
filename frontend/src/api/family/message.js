import request from "../request";

export function getMessages(params) {
  return request({
    url: "/message/list",
    method: "get",
    params,
  });
}

export function getMessageDetail(id) {
  return request({
    url: `/message/${id}`,
    method: "get",
  });
}

export function markMessageAsRead(id) {
  return request({
    url: `/message/${id}/read`,
    method: "put",
  });
}

export function markAllMessagesAsRead() {
  return request({
    url: "/message/read-all",
    method: "put",
  });
}

export function deleteMessage(id) {
  return request({
    url: `/message/${id}`,
    method: "delete",
  });
}
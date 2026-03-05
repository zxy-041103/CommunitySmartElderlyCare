import request from "../request";

export function getAnnouncements(params) {
  return request({
    url: "/admin/activities",
    method: "get",
    params,
  });
}

export function getAnnouncementDetail(id) {
  return request({
    url: `/admin/activities/${id}`,
    method: "get",
  });
}

export function createAnnouncement(data) {
  return request({
    url: "/admin/activities",
    method: "post",
    data,
  });
}

export function updateAnnouncement(id, data) {
  return request({
    url: `/admin/activities/${id}`,
    method: "put",
    data,
  });
}

export function deleteAnnouncement(id) {
  return request({
    url: `/admin/activities/${id}`,
    method: "delete",
  });
}

export function publishAnnouncement(id) {
  return request({
    url: `/admin/activities/${id}/publish`,
    method: "put",
  });
}

export function getAnnouncementCategories() {
  return request({
    url: "/admin/activities/categories",
    method: "get",
  });
}
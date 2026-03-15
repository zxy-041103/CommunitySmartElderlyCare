import request from "../request";

export function getAnnouncements() {
  return request({
    url: "/elderly/announcements",
    method: "get",
  });
}

export function getFamilyMembers() {
  return request({
    url: "/elderly/family-members",
    method: "get",
  });
}

export function getHealthOverview() {
  return request({
    url: "/elderly/health-overview",
    method: "get",
  });
}

export function getElderlyHealthTrend(days) {
  return request({
    url: "/elderly/health-trend",
    method: "get",
    params: { days },
  });
}
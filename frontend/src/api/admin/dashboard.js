import request from "../request";

export function getDashboardStatistics() {
  return request({
    url: "/admin/dashboard/statistics",
    method: "get",
  });
}

export function getHealthAbnormalRate(params) {
  return request({
    url: "/admin/dashboard/chart/health",
    method: "get",
    params,
  });
}

export function getCaregiverWorkload(params) {
  return request({
    url: "/admin/dashboard/chart/workload",
    method: "get",
    params,
  });
}

export function getServiceTypeDistribution() {
  return request({
    url: "/admin/dashboard/chart/serviceType",
    method: "get",
  });
}

export function getEmergencyTrend(params) {
  return request({
    url: "/admin/dashboard/chart/healthStatus",
    method: "get",
    params,
  });
}
import request from "../request";

export function getSystemConfig() {
  return request({
    url: "/system/config/backup",
    method: "get",
  });
}

export function updateSystemConfig(data) {
  return request({
    url: "/system/config/backup",
    method: "post",
    data,
  });
}

export function getHealthWarningThresholds() {
  return request({
    url: "/health/warning-thresholds",
    method: "get",
  });
}

export function updateHealthWarningThresholds(data) {
  return request({
    url: "/health/warning-thresholds",
    method: "put",
    data,
  });
}

export function getVoiceKeywords() {
  return request({
    url: "/system/config/voice/keywords",
    method: "get",
  });
}

export function addVoiceKeyword(data) {
  return request({
    url: "/system/config/voice/keyword",
    method: "post",
    data,
  });
}

export function updateVoiceKeyword(data) {
  return request({
    url: "/system/config/voice/keyword",
    method: "put",
    data,
  });
}

export function deleteVoiceKeyword(id) {
  return request({
    url: `/system/config/voice/keyword/${id}`,
    method: "delete",
  });
}

export function backupSystemData() {
  return request({
    url: "/system/config/backup/manual",
    method: "post",
  });
}
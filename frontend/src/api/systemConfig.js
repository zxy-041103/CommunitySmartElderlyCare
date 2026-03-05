import request from '@/utils/request';

export const getBackupConfig = () => {
  return request({
    url: '/system/config/backup',
    method: 'get'
  });
};

export const updateBackupConfig = (data) => {
  return request({
    url: '/system/config/backup',
    method: 'post',
    data
  });
};

export const manualBackup = () => {
  return request({
    url: '/system/config/backup/manual',
    method: 'post'
  });
};

export const getVoiceKeywords = () => {
  return request({
    url: '/system/config/voice/keywords',
    method: 'get'
  });
};

export const addVoiceKeyword = (data) => {
  return request({
    url: '/system/config/voice/keyword',
    method: 'post',
    data
  });
};

export const removeVoiceKeyword = (id) => {
  return request({
    url: `/system/config/voice/keyword/${id}`,
    method: 'delete'
  });
};

export const updateVoiceKeyword = (data) => {
  return request({
    url: '/system/config/voice/keyword',
    method: 'put',
    data
  });
};

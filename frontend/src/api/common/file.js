import request from "../request";

export function uploadFile(formData) {
  return request({
    url: "/file/upload",
    method: "post",
    data: formData,
    headers: {
      "Content-Type": "multipart/form-data",
    },
  });
}

export function uploadAvatar(formData) {
  return request({
    url: "/file/upload/avatar",
    method: "post",
    data: formData,
    headers: {
      "Content-Type": "multipart/form-data",
    },
  });
}

export function deleteFile(fileId) {
  return request({
    url: `/file/${fileId}`,
    method: "delete",
  });
}

export function getFileList(params) {
  return request({
    url: "/file/list",
    method: "get",
    params,
  });
}
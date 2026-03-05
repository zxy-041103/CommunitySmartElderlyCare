import request from "../request";

export function submitEvaluation(data) {
  return request({
    url: "/service-evaluation",
    method: "post",
    data,
  });
}

export function getEvaluations(params) {
  return request({
    url: "/service-evaluation/list",
    method: "get",
    params,
  });
}

export function getEvaluationDetail(id) {
  return request({
    url: `/service-evaluation/${id}`,
    method: "get",
  });
}

export function updateEvaluation(id, data) {
  return request({
    url: `/service-evaluation/${id}`,
    method: "put",
    data,
  });
}

export function deleteEvaluation(id) {
  return request({
    url: `/service-evaluation/${id}`,
    method: "delete",
  });
}
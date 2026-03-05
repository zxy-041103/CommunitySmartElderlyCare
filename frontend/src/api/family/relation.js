import request from "../request";

export function addFamilyRelation(data) {
  return request({
    url: "/user/relation",
    method: "post",
    params: {
      familyId: data.familyId,
      elderlyId: data.elderlyId,
      relationType: data.relationType,
      relationName: data.relationName,
      proofMaterials: data.proofMaterials
    },
  });
}

export function getFamilyRelations(params) {
  if (params.familyId) {
    return request({
      url: `/user/relation/family/${params.familyId}`,
      method: "get",
    });
  } else if (params.elderlyId) {
    return request({
      url: `/user/relation/elderly/${params.elderlyId}`,
      method: "get",
    });
  } else {
    return request({
      url: "/user/relation/pending",
      method: "get",
    });
  }
}

export function getRelationDetail(id) {
  return request({
    url: `/user/relation/${id}`,
    method: "get",
  });
}

export function updateRelationStatus(id, data) {
  return request({
    url: `/user/relation/${id}/audit`,
    method: "put",
    data,
  });
}

export function deleteFamilyRelation(id) {
  return request({
    url: `/user/relation/${id}`,
    method: "delete",
  });
}
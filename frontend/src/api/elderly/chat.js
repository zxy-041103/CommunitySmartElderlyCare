import request from "../request";

/**
 * 获取聊天记录
 * @param {number} familyId - 家属ID
 * @returns {Promise}
 */
export function getChatHistory(familyId) {
  return request({
    url: `/api/chat/history/${familyId}`,
    method: "get",
  });
}

/**
 * 标记消息为已读
 * @param {number} familyId - 家属ID
 * @returns {Promise}
 */
export function markAsRead(familyId) {
  return request({
    url: `/api/chat/read/${familyId}`,
    method: "put",
  });
}

/**
 * 获取未读消息数量
 * @returns {Promise}
 */
export function getUnreadCount() {
  return request({
    url: "/api/chat/unread/count",
    method: "get",
  });
}

/**
 * 清空聊天记录
 * @param {number} familyId - 家属ID
 * @returns {Promise}
 */
export function clearChatHistory(familyId) {
  return request({
    url: `/api/chat/history/${familyId}`,
    method: "delete",
  });
}

/**
 * 获取快捷话术模板
 * @param {string} category - 分类：daily-日常报平安，sick-身体不适，need-生活需求，emotion-情感关怀，emergency-紧急情况
 * @returns {Promise}
 */
export function getQuickPhrasesByCategory(category) {
  return request({
    url: `/api/quick-phrases/category/${category}`,
    method: "get",
  });
}

/**
 * 获取所有快捷话术模板
 * @returns {Promise}
 */
export function getAllQuickPhrases() {
  return request({
    url: "/api/quick-phrases/all",
    method: "get",
  });
}

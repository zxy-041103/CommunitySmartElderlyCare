/**
 * 脱敏工具函数
 * 用于敏感信息的脱敏处理
 */

/**
 * 手机号脱敏
 * @param {string} phone - 手机号
 * @returns {string} 脱敏后的手机号
 */
export function desensitizePhone(phone) {
  if (!phone || phone.length < 11) return phone;
  return phone.replace(/(\d{3})\d{4}(\d{4})/, '$1****$2');
}

/**
 * 身份证号脱敏
 * @param {string} idCard - 身份证号
 * @returns {string} 脱敏后的身份证号
 */
export function desensitizeIdCard(idCard) {
  if (!idCard || idCard.length < 18) return idCard;
  return idCard.replace(/(\d{6})\d{8}(\d{4})/, '$1********$2');
}

/**
 * 姓名脱敏
 * @param {string} name - 姓名
 * @returns {string} 脱敏后的姓名
 */
export function desensitizeName(name) {
  if (!name || name.length < 2) return name;
  if (name.length === 2) {
    return name[0] + '*';
  }
  return name[0] + '*'.repeat(name.length - 2) + name[name.length - 1];
}

/**
 * 地址脱敏
 * @param {string} address - 地址
 * @returns {string} 脱敏后的地址
 */
export function desensitizeAddress(address) {
  if (!address || address.length < 8) return address;
  const len = address.length;
  const start = address.substring(0, 4);
  const end = address.substring(len - 2);
  return start + '****' + end;
}

/**
 * 银行卡号脱敏
 * @param {string} bankCard - 银行卡号
 * @returns {string} 脱敏后的银行卡号
 */
export function desensitizeBankCard(bankCard) {
  if (!bankCard || bankCard.length < 16) return bankCard;
  return bankCard.replace(/(\d{4})\d{8,12}(\d{4})/, '$1********$2');
}

export default {
  desensitizePhone,
  desensitizeIdCard,
  desensitizeName,
  desensitizeAddress,
  desensitizeBankCard,
};
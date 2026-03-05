package com.community.elderly.utils;

import org.apache.commons.lang3.StringUtils;

public class XssUtil {

    private static final String[] XSS_KEYWORDS = {
        "<script", "</script", "javascript:", "vbscript:", "onload", "onerror",
        "onclick", "onmouseover", "onfocus", "onblur", "onsubmit", "onreset",
        "onchange", "onselect", "ondblclick", "onkeydown", "onkeyup", "onkeypress",
        "onmousedown", "onmouseup", "onmousemove", "onmouseout", "eval(", "expression("
    };

    /**
     * 清理XSS攻击字符串
     *
     * @param value 待清理的字符串
     * @return 清理后的字符串
     */
    public static String clean(String value) {
        if (StringUtils.isBlank(value)) {
            return value;
        }

        String cleanedValue = value;

        for (String keyword : XSS_KEYWORDS) {
            cleanedValue = cleanedValue.replace(keyword, "");
        }

        cleanedValue = cleanedValue.replaceAll("<", "&lt;")
                .replaceAll(">", "&gt;")
                .replaceAll("\"", "&quot;")
                .replaceAll("'", "&#x27;")
                .replaceAll("/", "&#x2F;");

        return cleanedValue;
    }

    /**
     * 检查字符串是否包含XSS攻击特征
     *
     * @param value 待检查的字符串
     * @return 如果包含XSS攻击特征返回true，否则返回false
     */
    public static boolean containsXss(String value) {
        if (StringUtils.isBlank(value)) {
            return false;
        }

        String lowerValue = value.toLowerCase();
        for (String keyword : XSS_KEYWORDS) {
            if (lowerValue.contains(keyword.toLowerCase())) {
                return true;
            }
        }

        return false;
    }

    /**
     * 严格清理XSS攻击字符串（移除所有HTML标签）
     *
     * @param value 待清理的字符串
     * @return 清理后的字符串
     */
    public static String strictClean(String value) {
        if (StringUtils.isBlank(value)) {
            return value;
        }

        return value.replaceAll("<[^>]*>", "").trim();
    }
}
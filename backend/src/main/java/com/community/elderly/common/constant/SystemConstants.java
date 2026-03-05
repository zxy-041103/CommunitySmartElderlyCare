package com.community.elderly.common.constant;

public class SystemConstants {
    public static final String SYSTEM_NAME = "智慧养老平台";
    public static final String SYSTEM_VERSION = "1.0.0";
    public static final String SYSTEM_AUTHOR = "Community Team";
    
    public static final int PAGE_SIZE = 10;
    public static final int MAX_PAGE_SIZE = 100;
    
    public static final String DEFAULT_ENCODING = "UTF-8";
    public static final String DEFAULT_TIMEZONE = "Asia/Shanghai";
    
    public static final String UPLOAD_PATH = "d:/CommunitySmartElderlyCareMonitoringandManagementPlatform/uploads";
    public static final String ALLOWED_IMAGE_TYPES = "jpg,jpeg,png,gif,bmp";
    public static final long MAX_FILE_SIZE = 10 * 1024 * 1024; // 10MB
    
    public static final String CACHE_KEY_PREFIX = "elderly:care:platform:";
    public static final int CACHE_EXPIRATION = 3600; // 1小时
    
    public static final String JWT_SECRET = "elderly-care-platform-secret-key-2024-for-jwt-token-generation-must-be-at-least-512-bits-long";
    public static final long JWT_EXPIRATION = 86400000; // 24小时
}

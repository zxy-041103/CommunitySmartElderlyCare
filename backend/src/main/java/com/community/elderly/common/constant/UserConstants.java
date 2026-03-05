package com.community.elderly.common.constant;

public class UserConstants {
    public static final String USERNAME_REGEX = "^[a-zA-Z0-9_]{4,16}$";
    public static final String PASSWORD_REGEX = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)[a-zA-Z\\d@$!%*?&]{8,}$";
    public static final String PHONE_REGEX = "^1[3-9]\\d{9}$";
    public static final String EMAIL_REGEX = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
    
    public static final int USERNAME_MIN_LENGTH = 4;
    public static final int USERNAME_MAX_LENGTH = 16;
    public static final int PASSWORD_MIN_LENGTH = 8;
    
    public static final String DEFAULT_AVATAR = "/static/avatar/default.png";
    public static final String USER_ROLE_ELDERLY = "elderly";
    public static final String USER_ROLE_FAMILY = "family";
    public static final String USER_ROLE_CAREGIVER = "caregiver";
    public static final String USER_ROLE_ADMIN = "admin";
}

package com.community.elderly.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;

@ApiModel(description = "用户登录响应")
public class LoginResponse {

    @ApiModelProperty(value = "访问令牌")
    private String token;

    @ApiModelProperty(value = "令牌类型")
    private String tokenType = "Bearer";

    @ApiModelProperty(value = "用户信息")
    private UserInfo userInfo;

    @ApiModelProperty(value = "菜单列表")
    private List<MenuInfo> menus;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getTokenType() {
        return tokenType;
    }

    public void setTokenType(String tokenType) {
        this.tokenType = tokenType;
    }

    public UserInfo getUserInfo() {
        return userInfo;
    }

    public void setUserInfo(UserInfo userInfo) {
        this.userInfo = userInfo;
    }

    public List<MenuInfo> getMenus() {
        return menus;
    }

    public void setMenus(List<MenuInfo> menus) {
        this.menus = menus;
    }

    @ApiModel(description = "用户信息")
    public static class UserInfo {
        @ApiModelProperty(value = "用户ID")
        private Long id;

        @ApiModelProperty(value = "用户名")
        private String username;

        @ApiModelProperty(value = "真实姓名")
        private String realName;

        @ApiModelProperty(value = "身份证号（脱敏）")
        private String idCard;

        @ApiModelProperty(value = "手机号（脱敏）")
        private String phone;

        @ApiModelProperty(value = "角色类型")
        private String roleType;

        @ApiModelProperty(value = "性别：1-男，0-女")
        private Integer gender;

        @ApiModelProperty(value = "年龄")
        private Integer age;

        @ApiModelProperty(value = "地址")
        private String address;

        @ApiModelProperty(value = "头像")
        private String avatar;

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getRealName() {
            return realName;
        }

        public void setRealName(String realName) {
            this.realName = realName;
        }

        public String getIdCard() {
            return idCard;
        }

        public void setIdCard(String idCard) {
            this.idCard = idCard;
        }

        public String getPhone() {
            return phone;
        }

        public void setPhone(String phone) {
            this.phone = phone;
        }

        public String getRoleType() {
            return roleType;
        }

        public void setRoleType(String roleType) {
            this.roleType = roleType;
        }

        public Integer getGender() {
            return gender;
        }

        public void setGender(Integer gender) {
            this.gender = gender;
        }

        public Integer getAge() {
            return age;
        }

        public void setAge(Integer age) {
            this.age = age;
        }

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        public String getAvatar() {
            return avatar;
        }

        public void setAvatar(String avatar) {
            this.avatar = avatar;
        }
    }

    @ApiModel(description = "菜单信息")
    public static class MenuInfo {
        @ApiModelProperty(value = "菜单ID")
        private Long id;

        @ApiModelProperty(value = "菜单名称")
        private String name;

        @ApiModelProperty(value = "菜单路径")
        private String path;

        @ApiModelProperty(value = "组件路径")
        private String component;

        @ApiModelProperty(value = "菜单图标")
        private String icon;

        @ApiModelProperty(value = "排序")
        private Integer sort;

        @ApiModelProperty(value = "子菜单")
        private List<MenuInfo> children;

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getPath() {
            return path;
        }

        public void setPath(String path) {
            this.path = path;
        }

        public String getComponent() {
            return component;
        }

        public void setComponent(String component) {
            this.component = component;
        }

        public String getIcon() {
            return icon;
        }

        public void setIcon(String icon) {
            this.icon = icon;
        }

        public Integer getSort() {
            return sort;
        }

        public void setSort(Integer sort) {
            this.sort = sort;
        }

        public List<MenuInfo> getChildren() {
            return children;
        }

        public void setChildren(List<MenuInfo> children) {
            this.children = children;
        }
    }
}

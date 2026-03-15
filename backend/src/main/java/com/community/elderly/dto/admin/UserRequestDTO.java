package com.community.elderly.dto.admin;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * 用户管理请求DTO - 接收前端传递的用户数据
 */
@ApiModel(description = "用户管理请求")
public class UserRequestDTO {

    @ApiModelProperty(value = "用户ID", example = "1")
    private Long id;

    @ApiModelProperty(value = "用户名", example = "zhangsan")
    private String username;

    @ApiModelProperty(value = "真实姓名", example = "张三")
    private String realName;

    @ApiModelProperty(value = "身份证号", example = "110101199001011234")
    private String idCard;

    @ApiModelProperty(value = "手机号", example = "13800138000")
    private String phone;

    @ApiModelProperty(value = "密码", example = "123456")
    private String password;

    @ApiModelProperty(value = "角色类型：elderly-老人, family-家属, caregiver-护工, admin-管理员", example = "elderly")
    private String roleType;

    @ApiModelProperty(value = "性别：male-男, female-女", example = "male")
    private String gender;

    @ApiModelProperty(value = "年龄", example = "75")
    private Integer age;

    @ApiModelProperty(value = "地址", example = "北京市朝阳区建国路88号")
    private String address;

    @ApiModelProperty(value = "是否启用：1-启用, 0-禁用", example = "1")
    private Integer isEnabled;

    // Getters and Setters
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRoleType() {
        return roleType;
    }

    public void setRoleType(String roleType) {
        this.roleType = roleType;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
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

    public Integer getIsEnabled() {
        return isEnabled;
    }

    public void setIsEnabled(Integer isEnabled) {
        this.isEnabled = isEnabled;
    }

    /**
     * 将字符串性别转换为整数
     * male -> 1 (男)
     * female -> 0 (女)
     * @return Integer 性别代码
     */
    public Integer getGenderAsInteger() {
        if (gender == null) {
            return null;
        }
        return "male".equalsIgnoreCase(gender) ? 1 : 0;
    }
}

package com.community.elderly.dto.family;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.NotNull;

@ApiModel(description = "家属-老人关联审核请求")
public class AuditFamilyRelationRequest {

    @NotNull(message = "审核状态不能为空")
    @ApiModelProperty(value = "审核状态：approved-已通过，rejected-已拒绝", required = true, example = "approved")
    private String auditStatus;

    @ApiModelProperty(value = "审核备注", example = "审核通过")
    private String auditRemark;

    @ApiModelProperty(value = "审核人ID", example = "1")
    private Long auditorId;

    public String getAuditStatus() {
        return auditStatus;
    }

    public void setAuditStatus(String auditStatus) {
        this.auditStatus = auditStatus;
    }

    public String getAuditRemark() {
        return auditRemark;
    }

    public void setAuditRemark(String auditRemark) {
        this.auditRemark = auditRemark;
    }

    public Long getAuditorId() {
        return auditorId;
    }

    public void setAuditorId(Long auditorId) {
        this.auditorId = auditorId;
    }
}

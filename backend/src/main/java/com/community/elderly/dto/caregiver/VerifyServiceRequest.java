package com.community.elderly.dto.caregiver;

import javax.validation.constraints.NotBlank;

public class VerifyServiceRequest {

    @NotBlank(message = "核销码不能为空")
    private String verificationCode;

    private String serviceContent;

    private String elderlyFeedback;

    public String getVerificationCode() {
        return verificationCode;
    }

    public void setVerificationCode(String verificationCode) {
        this.verificationCode = verificationCode;
    }

    public String getServiceContent() {
        return serviceContent;
    }

    public void setServiceContent(String serviceContent) {
        this.serviceContent = serviceContent;
    }

    public String getElderlyFeedback() {
        return elderlyFeedback;
    }

    public void setElderlyFeedback(String elderlyFeedback) {
        this.elderlyFeedback = elderlyFeedback;
    }
}

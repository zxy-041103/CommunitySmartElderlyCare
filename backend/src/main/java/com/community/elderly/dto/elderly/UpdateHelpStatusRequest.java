package com.community.elderly.dto.elderly;

import javax.validation.constraints.NotNull;

public class UpdateHelpStatusRequest {

    @NotNull(message = "状态不能为空")
    private String helpStatus;

    private String handleResult;

    public String getHelpStatus() {
        return helpStatus;
    }

    public void setHelpStatus(String helpStatus) {
        this.helpStatus = helpStatus;
    }

    public String getHandleResult() {
        return handleResult;
    }

    public void setHandleResult(String handleResult) {
        this.handleResult = handleResult;
    }
}

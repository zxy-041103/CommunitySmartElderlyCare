package com.community.elderly.dto.elderly;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Future;
import java.time.LocalDate;

public class CreateServiceOrderRequest {

    @NotNull(message = "服务类型不能为空")
    private String serviceType;

    @NotBlank(message = "服务名称不能为空")
    private String serviceName;

    @NotNull(message = "服务日期不能为空")
    @Future(message = "服务日期必须是未来时间")
    private LocalDate serviceDate;

    @NotBlank(message = "服务时间不能为空")
    private String serviceTime;

    private String serviceAddress;

    private String serviceDescription;

    public String getServiceType() {
        return serviceType;
    }

    public void setServiceType(String serviceType) {
        this.serviceType = serviceType;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public LocalDate getServiceDate() {
        return serviceDate;
    }

    public void setServiceDate(LocalDate serviceDate) {
        this.serviceDate = serviceDate;
    }

    public String getServiceTime() {
        return serviceTime;
    }

    public void setServiceTime(String serviceTime) {
        this.serviceTime = serviceTime;
    }

    public String getServiceAddress() {
        return serviceAddress;
    }

    public void setServiceAddress(String serviceAddress) {
        this.serviceAddress = serviceAddress;
    }

    public String getServiceDescription() {
        return serviceDescription;
    }

    public void setServiceDescription(String serviceDescription) {
        this.serviceDescription = serviceDescription;
    }
}

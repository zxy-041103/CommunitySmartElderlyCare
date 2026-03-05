package com.community.elderly.dto.elderly;

import lombok.Data;

@Data
public class HealthWarningThresholdRequest {
    private Long id;
    private String indicatorType;
    private Double minValue;
    private Double maxValue;
    private Integer isActive;
}

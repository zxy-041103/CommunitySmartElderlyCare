package com.community.elderly.dto.elderly;

import lombok.Data;

@Data
public class HealthDataInputRequest {
    private Long userId;
    private Double bloodPressureSystolic;
    private Double bloodPressureDiastolic;
    private Double heartRate;
    private Double bloodSugar;
    private Double bodyTemperature;
    private Double weight;
    private String note;
}

package com.community.elderly.dto.elderly;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class HealthDataQueryRequest {
    private Long userId;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Integer pageSize;
    private Integer pageNum;
}

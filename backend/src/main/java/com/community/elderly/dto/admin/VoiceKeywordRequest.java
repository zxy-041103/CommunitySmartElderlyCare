package com.community.elderly.dto.admin;

import lombok.Data;

@Data
public class VoiceKeywordRequest {
    private Long id;
    private String keyword;
    private String actionType;
    private String actionParams;
    private Integer sort;
    private Integer isEnabled;
}

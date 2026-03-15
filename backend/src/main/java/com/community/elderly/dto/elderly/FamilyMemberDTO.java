package com.community.elderly.dto.elderly;

import lombok.Data;

@Data
public class FamilyMemberDTO {
    private Long id;
    private Long familyId;
    private Long elderlyId;
    private String relationType;
    private String relationName;
    private String familyName;
    private String familyPhone;
    private String auditStatus;
}
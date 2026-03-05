package com.community.elderly.service.elderly;

import com.baomidou.mybatisplus.extension.service.IService;
import com.community.elderly.dto.family.AuditFamilyRelationRequest;
import com.community.elderly.dto.elderly.LoginRequest;
import com.community.elderly.dto.elderly.UpdateUserRequest;
import com.community.elderly.entity.FamilyElderlyRelation;
import com.community.elderly.entity.User;
import com.community.elderly.vo.LoginResponse;

import java.util.List;
import java.util.Map;

public interface ElderlyProfileService extends IService<User> {

    LoginResponse login(LoginRequest request);

    User getUserById(Long id);

    User updateUser(Long id, UpdateUserRequest request);

    User createUser(User user);

    boolean deleteUser(Long id);

    Map<String, Object> getUserList(Integer page, Integer size, String name, String phone, Integer status, String roleType);

    Map<String, Object> getUserStatistics(String roleType);

    boolean createFamilyRelation(Long familyId, Long elderlyId, String relationType, String relationName, String proofMaterials);

    boolean auditFamilyRelation(Long relationId, AuditFamilyRelationRequest request);

    List<FamilyElderlyRelation> getFamilyRelationsByFamilyId(Long familyId);

    List<FamilyElderlyRelation> getFamilyRelationsByElderlyId(Long elderlyId);

    List<FamilyElderlyRelation> getPendingRelations();

    boolean updateRelationProofMaterials(Long relationId, String proofMaterials);
}
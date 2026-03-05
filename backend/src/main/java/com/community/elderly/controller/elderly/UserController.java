package com.community.elderly.controller.elderly;

import com.community.elderly.common.Result;
import com.community.elderly.dto.family.AuditFamilyRelationRequest;
import com.community.elderly.dto.elderly.LoginRequest;
import com.community.elderly.dto.elderly.UpdateUserRequest;
import com.community.elderly.entity.FamilyElderlyRelation;
import com.community.elderly.entity.User;
import com.community.elderly.service.elderly.ElderlyProfileService;
import com.community.elderly.vo.LoginResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/user")
@Api(tags = "用户管理")
public class UserController {

    @Autowired
    private ElderlyProfileService elderlyProfileService;

    @PostMapping("/login")
    @ApiOperation(value = "用户登录", notes = "用户登录接口，返回token和用户信息，按角色返回不同权限菜单")
    public Result<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        try {
            LoginResponse response = elderlyProfileService.login(request);
            return Result.success(response);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @GetMapping("/{id}")
    @ApiOperation(value = "查询个人信息", notes = "根据用户ID查询个人信息，敏感信息已脱敏")
    public Result<User> getUserById(
            @ApiParam(value = "用户ID", required = true) @PathVariable Long id) {
        try {
            User user = elderlyProfileService.getUserById(id);
            if (user == null) {
                return Result.error("用户不存在");
            }
            return Result.success(user);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    @ApiOperation(value = "修改个人信息", notes = "修改用户个人信息，包括手机号、地址、头像等")
    public Result<User> updateUser(
            @ApiParam(value = "用户ID", required = true) @PathVariable Long id,
            @Valid @RequestBody UpdateUserRequest request) {
        try {
            User user = elderlyProfileService.updateUser(id, request);
            return Result.success(user);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @PostMapping
    @ApiOperation(value = "创建用户", notes = "创建新用户，支持不同角色类型")
    public Result<User> createUser(@Valid @RequestBody User user) {
        try {
            User createdUser = elderlyProfileService.createUser(user);
            return Result.success(createdUser);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    @ApiOperation(value = "删除用户", notes = "根据用户ID删除用户")
    public Result<Boolean> deleteUser(
            @ApiParam(value = "用户ID", required = true) @PathVariable Long id) {
        try {
            boolean result = elderlyProfileService.deleteUser(id);
            return Result.success(result);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @GetMapping("/list")
    @ApiOperation(value = "获取用户列表", notes = "获取用户列表，支持分页和按角色过滤")
    public Result<Map<String, Object>> getUserList(
            @ApiParam(value = "页码") @RequestParam(defaultValue = "1") Integer page,
            @ApiParam(value = "每页大小") @RequestParam(defaultValue = "10") Integer size,
            @ApiParam(value = "姓名") @RequestParam(required = false) String name,
            @ApiParam(value = "电话") @RequestParam(required = false) String phone,
            @ApiParam(value = "状态") @RequestParam(required = false) Integer status,
            @ApiParam(value = "角色类型") @RequestParam(required = false) String roleType) {
        try {
            Map<String, Object> result = elderlyProfileService.getUserList(page, size, name, phone, status, roleType);
            return Result.success(result);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @GetMapping("/statistics")
    @ApiOperation(value = "获取用户统计信息", notes = "获取用户统计信息，支持按角色过滤")
    public Result<Map<String, Object>> getUserStatistics(
            @ApiParam(value = "角色类型") @RequestParam(required = false) String roleType) {
        try {
            Map<String, Object> statistics = elderlyProfileService.getUserStatistics(roleType);
            return Result.success(statistics);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @PostMapping("/relation")
    @ApiOperation(value = "创建家属-老人关联关系", notes = "家属创建与老人的关联关系，等待审核")
    public Result<Boolean> createFamilyRelation(
            @ApiParam(value = "家属用户ID", required = true) @RequestParam Long familyId,
            @ApiParam(value = "老人用户ID", required = true) @RequestParam Long elderlyId,
            @ApiParam(value = "关系类型：spouse-配偶，child-子女，grandchild-孙子女，sibling-兄弟姐妹，other-其他", required = true) @RequestParam String relationType,
            @ApiParam(value = "关系名称（如：儿子、女儿等）") @RequestParam(required = false) String relationName,
            @ApiParam(value = "审核材料（JSON格式存储文件路径）") @RequestParam(required = false) String proofMaterials) {
        try {
            boolean result = elderlyProfileService.createFamilyRelation(familyId, elderlyId, relationType, relationName, proofMaterials);
            return Result.success(result);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @PutMapping("/relation/{relationId}/audit")
    @ApiOperation(value = "审核家属-老人关联关系", notes = "管理员审核家属与老人的关联关系")
    public Result<Boolean> auditFamilyRelation(
            @ApiParam(value = "关联关系ID", required = true) @PathVariable Long relationId,
            @Valid @RequestBody AuditFamilyRelationRequest request) {
        try {
            // 从 SecurityContextHolder 中获取当前登录用户的 ID
            Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            if (principal != null && principal instanceof String) {
                // 由于我们在 JwtAuthenticationFilter 中设置的是 UsernamePasswordAuthenticationToken，其中 principal 是 userId.toString()
                Long auditorId = Long.parseLong((String) principal);
                request.setAuditorId(auditorId);
            }
            boolean result = elderlyProfileService.auditFamilyRelation(relationId, request);
            return Result.success(result);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @GetMapping("/relation/family/{familyId}")
    @ApiOperation(value = "查询家属的关联关系", notes = "查询某家属的所有老人关联关系")
    public Result<List<FamilyElderlyRelation>> getRelationsByFamilyId(
            @ApiParam(value = "家属用户ID", required = true) @PathVariable Long familyId) {
        try {
            List<FamilyElderlyRelation> relations = elderlyProfileService.getFamilyRelationsByFamilyId(familyId);
            return Result.success(relations);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @GetMapping("/relation/elderly/{elderlyId}")
    @ApiOperation(value = "查询老人的关联关系", notes = "查询某老人的所有家属关联关系")
    public Result<List<FamilyElderlyRelation>> getRelationsByElderlyId(
            @ApiParam(value = "老人用户ID", required = true) @PathVariable Long elderlyId) {
        try {
            List<FamilyElderlyRelation> relations = elderlyProfileService.getFamilyRelationsByElderlyId(elderlyId);
            return Result.success(relations);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @GetMapping("/relation/pending")
    @ApiOperation(value = "查询待审核的关联关系", notes = "查询所有待审核的家属-老人关联关系")
    public Result<List<FamilyElderlyRelation>> getPendingRelations() {
        try {
            List<FamilyElderlyRelation> relations = elderlyProfileService.getPendingRelations();
            return Result.success(relations);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @PutMapping("/relation/{relationId}/update-proof")
    @ApiOperation(value = "更新关联关系证明材料", notes = "更新关联关系的证明材料信息")
    public Result<Boolean> updateRelationProofMaterials(
            @ApiParam(value = "关联关系ID", required = true) @PathVariable Long relationId,
            @ApiParam(value = "审核材料（JSON格式存储文件路径）", required = true) @RequestParam String proofMaterials) {
        try {
            boolean result = elderlyProfileService.updateRelationProofMaterials(relationId, proofMaterials);
            return Result.success(result);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
}
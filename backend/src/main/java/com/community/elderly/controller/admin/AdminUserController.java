package com.community.elderly.controller.admin;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.community.elderly.common.Result;
import com.community.elderly.dto.admin.UserRequestDTO;
import com.community.elderly.entity.FamilyElderlyRelation;
import com.community.elderly.entity.User;
import com.community.elderly.mapper.family.FamilyRelationMapper;
import com.community.elderly.mapper.elderly.ElderlyProfileMapper;
import com.community.elderly.utils.AesUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/admin")
@Api(tags = "管理员接口")
public class AdminUserController {

    @Autowired
    private ElderlyProfileMapper elderlyProfileMapper;

    @Autowired
    private FamilyRelationMapper familyRelationMapper;

    @Autowired
    private com.community.elderly.service.elderly.ElderlyProfileService userService;
    
    @Autowired
    private com.community.elderly.service.admin.AnnouncementService announcementService;
    
    @Autowired
    private AesUtil aesUtil;

    /**
     * 获取活动分类统计
     */
    @GetMapping("/dashboard/activity-stats")
    @ApiOperation(value = "获取活动分类统计", notes = "获取活动分类统计数据")
    public Result<List<com.community.elderly.common.dto.ActivityCategoryStats>> getActivityStats() {
        return Result.success(announcementService.getActivityCategoryStats());
    }

    /**
     * 获取用户列表
     */
    @GetMapping("/users")
    @ApiOperation(value = "获取用户列表", notes = "分页获取用户列表，支持按角色和关键词搜索")
    public Result<Map<String, Object>> getUserList(
            @ApiParam("页码") @RequestParam(defaultValue = "1") Integer page,
            @ApiParam("每页大小") @RequestParam(defaultValue = "10") Integer size,
            @ApiParam("角色类型") @RequestParam(required = false) String roleType,
            @ApiParam("搜索关键词") @RequestParam(required = false) String keyword) {
        
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getIsDeleted, 0);
        
        if (roleType != null && !roleType.isEmpty()) {
            wrapper.eq(User::getRoleType, roleType);
        }
        
        if (keyword != null && !keyword.isEmpty()) {
            wrapper.and(w -> w.like(User::getUsername, keyword)
                    .or()
                    .like(User::getRealName, keyword));
        }
        
        wrapper.orderByDesc(User::getCreateTime);
        
        IPage<User> userPage = new Page<>(page, size);
        userPage = elderlyProfileMapper.selectPage(userPage, wrapper);
        
        List<User> records = userPage.getRecords();
        for (User user : records) {
            if (user.getPhone() != null && !user.getPhone().isEmpty()) {
                String decryptedPhone = aesUtil.decrypt(user.getPhone(), "elderlycare2024!");
                user.setPhone(decryptedPhone);
            }
        }
        
        Map<String, Object> stats = new HashMap<>();
        stats.put("all", elderlyProfileMapper.selectCount(new LambdaQueryWrapper<User>().eq(User::getIsDeleted, 0)));
        stats.put("elderly", elderlyProfileMapper.selectCount(new LambdaQueryWrapper<User>().eq(User::getIsDeleted, 0).eq(User::getRoleType, "elderly")));
        stats.put("family", elderlyProfileMapper.selectCount(new LambdaQueryWrapper<User>().eq(User::getIsDeleted, 0).eq(User::getRoleType, "family")));
        stats.put("caregiver", elderlyProfileMapper.selectCount(new LambdaQueryWrapper<User>().eq(User::getIsDeleted, 0).eq(User::getRoleType, "caregiver")));
        stats.put("admin", elderlyProfileMapper.selectCount(new LambdaQueryWrapper<User>().eq(User::getIsDeleted, 0).eq(User::getRoleType, "admin")));
        
        Map<String, Object> result = new HashMap<>();
        result.put("records", records);
        result.put("total", userPage.getTotal());
        result.put("current", userPage.getCurrent());
        result.put("size", userPage.getSize());
        result.put("pages", userPage.getPages());
        result.put("stats", stats);
        
        return Result.success(result);
    }

    /**
     * 删除用户
     */
    @DeleteMapping("/users/{id}")
    @ApiOperation(value = "删除用户", notes = "逻辑删除用户")
    public Result<Boolean> deleteUser(@PathVariable Long id) {
        try {
            User user = elderlyProfileMapper.selectById(id);
            if (user == null) {
                return Result.error("用户不存在");
            }
            
            user.setIsDeleted(1);
            user.setUpdateTime(LocalDateTime.now());
            elderlyProfileMapper.updateById(user);
            return Result.success("删除成功", true);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 获取待审核的关联列表
     */
    @GetMapping("/family-elderly-relations/pending")
    @ApiOperation(value = "获取待审核关联列表", notes = "获取所有待审核的家属-老人关联申请")
    public Result<List<Map<String, Object>>> getPendingRelations() {
        LambdaQueryWrapper<FamilyElderlyRelation> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(FamilyElderlyRelation::getAuditStatus, "pending");
        wrapper.eq(FamilyElderlyRelation::getIsDeleted, 0);
        wrapper.orderByDesc(FamilyElderlyRelation::getCreateTime);
        
        List<FamilyElderlyRelation> relations = familyRelationMapper.selectList(wrapper);
        List<Map<String, Object>> result = new ArrayList<>();
        
        for (FamilyElderlyRelation relation : relations) {
            Map<String, Object> item = new HashMap<>();
            item.put("id", relation.getId());
            item.put("relationType", relation.getRelationType());
            item.put("relationName", relation.getRelationName());
            item.put("auditStatus", relation.getAuditStatus());
            item.put("applyTime", relation.getCreateTime());
            
            // 获取家属信息（使用 userService.getUserById 来解密手机号）
            User family = userService.getUserById(relation.getFamilyId());
            if (family != null) {
                item.put("familyId", family.getId());
                item.put("familyName", family.getRealName());
                item.put("familyPhone", family.getPhone());
            }
            
            // 获取老人信息（使用 userService.getUserById 来解密手机号）
            User elderly = userService.getUserById(relation.getElderlyId());
            if (elderly != null) {
                item.put("elderlyId", elderly.getId());
                item.put("elderlyName", elderly.getRealName());
                item.put("elderlyPhone", elderly.getPhone());
            }
            
            // 添加审核材料
            item.put("proofMaterials", relation.getProofMaterials());
            
            result.add(item);
        }
        
        return Result.success(result);
    }

    /**
     * 通过关联申请
     */
    @PutMapping("/family-elderly-relations/{id}/approve")
    @ApiOperation(value = "通过关联申请", notes = "审核通过家属-老人关联申请")
    public Result<?> approveRelation(@PathVariable Long id) {
        FamilyElderlyRelation relation = new FamilyElderlyRelation();
        relation.setId(id);
        relation.setAuditStatus("approved");
        relation.setAuditTime(LocalDateTime.now());
        familyRelationMapper.updateById(relation);
        return Result.success("审核通过");
    }

    @PutMapping("/family-elderly-relations/{id}/reject")
    @ApiOperation(value = "拒绝关联申请", notes = "拒绝家属-老人关联申请")
    public Result<?> rejectRelation(@PathVariable Long id) {
        FamilyElderlyRelation relation = new FamilyElderlyRelation();
        relation.setId(id);
        relation.setAuditStatus("rejected");
        relation.setAuditTime(LocalDateTime.now());
        familyRelationMapper.updateById(relation);
        return Result.success("已拒绝");
    }

    /**
     * 新增用户
     */
    @PostMapping("/users")
    @ApiOperation(value = "新增用户", notes = "新增用户")
    public Result<User> createUser(@RequestBody UserRequestDTO userDTO) {
        try {
            // 将DTO转换为Entity
            User user = new User();
            user.setUsername(userDTO.getUsername());
            user.setRealName(userDTO.getRealName());
            user.setPhone(userDTO.getPhone());
            user.setIdCard(userDTO.getIdCard());
            user.setPassword(userDTO.getPassword());
            user.setRoleType(userDTO.getRoleType());
            user.setGender(userDTO.getGenderAsInteger()); // 转换性别
            user.setAge(userDTO.getAge());
            user.setAddress(userDTO.getAddress());
            user.setIsEnabled(userDTO.getIsEnabled());
            
            User createdUser = userService.createUser(user);
            return Result.success(createdUser);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 编辑用户
     */
    @PutMapping("/users/{id}")
    @ApiOperation(value = "编辑用户", notes = "编辑用户信息")
    public Result<User> updateUser(@PathVariable Long id, @RequestBody UserRequestDTO userDTO) {
        try {
            // 查询现有用户
            User existingUser = elderlyProfileMapper.selectById(id);
            if (existingUser == null) {
                return Result.error("用户不存在");
            }
            
            // 更新用户信息
            existingUser.setUsername(userDTO.getUsername());
            existingUser.setRealName(userDTO.getRealName());
            existingUser.setIdCard(userDTO.getIdCard());
            existingUser.setGender(userDTO.getGenderAsInteger()); // 转换性别
            existingUser.setAge(userDTO.getAge());
            existingUser.setAddress(userDTO.getAddress());
            existingUser.setUpdateTime(LocalDateTime.now());
            
            // 加密手机号
            if (userDTO.getPhone() != null && !userDTO.getPhone().isEmpty()) {
                String encryptedPhone = aesUtil.encrypt(userDTO.getPhone());
                existingUser.setPhone(encryptedPhone);
            }
            
            // 更新密码（如果提供了）
            if (userDTO.getPassword() != null && !userDTO.getPassword().isEmpty()) {
                org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder encoder = 
                    new org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder();
                existingUser.setPassword(encoder.encode(userDTO.getPassword()));
            }
            
            // 保存更新
            elderlyProfileMapper.updateById(existingUser);
            
            // 返回更新后的用户（解密手机号）
            User resultUser = elderlyProfileMapper.selectById(id);
            if (resultUser.getPhone() != null && !resultUser.getPhone().isEmpty()) {
                String decryptedPhone = aesUtil.decrypt(resultUser.getPhone());
                resultUser.setPhone(decryptedPhone);
            }
            
            return Result.success(resultUser);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 更新用户状态
     */
    @PutMapping("/users/{id}/status")
    @ApiOperation(value = "更新用户状态", notes = "更新用户启用/禁用状态")
    public Result<?> updateUserStatus(@PathVariable Long id, @RequestBody Map<String, Integer> request) {
        try {
            User user = new User();
            user.setId(id);
            user.setIsEnabled(request.get("isEnabled"));
            user.setUpdateTime(LocalDateTime.now());
            elderlyProfileMapper.updateById(user);
            return Result.success("状态更新成功");
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
}

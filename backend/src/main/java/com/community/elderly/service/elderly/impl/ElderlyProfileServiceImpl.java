package com.community.elderly.service.elderly.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.community.elderly.dto.family.AuditFamilyRelationRequest;
import com.community.elderly.dto.elderly.LoginRequest;
import com.community.elderly.dto.elderly.UpdateUserRequest;
import com.community.elderly.entity.FamilyElderlyRelation;
import com.community.elderly.entity.User;
import com.community.elderly.mapper.elderly.ElderlyProfileMapper;
import com.community.elderly.mapper.family.FamilyRelationMapper;
import com.community.elderly.service.elderly.ElderlyProfileService;
import com.community.elderly.utils.CaptchaService;
import com.community.elderly.utils.AesUtil;
import com.community.elderly.utils.DesensitizeUtil;
import com.community.elderly.utils.JwtUtil;
import com.community.elderly.vo.LoginResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class ElderlyProfileServiceImpl extends ServiceImpl<ElderlyProfileMapper, User> implements ElderlyProfileService {

    @Autowired
    private ElderlyProfileMapper userMapper;

    @Autowired
    private FamilyRelationMapper familyElderlyRelationMapper;

    @Autowired
    private JwtUtil jwtUtil;

    // 直接创建AesUtil实例，避免依赖注入失败
    private final AesUtil aesUtil = new AesUtil();

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Override
    public LoginResponse login(LoginRequest request) {
        // 验证码验证
        if (!CaptchaService.verifyCaptcha(request.getCaptchaId(), request.getCaptcha())) {
            throw new RuntimeException("验证码错误");
        }

        User user = userMapper.selectByUsername(request.getUsername());
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }

        if (user.getIsEnabled() == 0) {
            throw new RuntimeException("账号已被禁用");
        }

        log.info("Login attempt - Username: {}, Input password: {}, Stored password: {}", 
            request.getUsername(), request.getPassword(), user.getPassword());
        
        // 测试BCrypt验证
        boolean matches = passwordEncoder.matches(request.getPassword(), user.getPassword());
        log.info("Password verification result: {}", matches);
        
        // 临时生成一个新的哈希值用于测试
        String newHash = passwordEncoder.encode("123456");
        log.info("Generated hash for '123456': {}", newHash);
        log.info("Test verify '123456' against new hash: {}", passwordEncoder.matches("123456", newHash));
        
        if (!matches) {
            log.error("Password mismatch for user: {}", request.getUsername());
            throw new RuntimeException("密码错误");
        }

        log.info("Login successful for user: {}", request.getUsername());

        String token = jwtUtil.generateToken(user.getId(), user.getUsername(), user.getRoleType());

        LoginResponse response = new LoginResponse();
        response.setToken(token);
        response.setTokenType("Bearer");

        LoginResponse.UserInfo userInfo = new LoginResponse.UserInfo();
        userInfo.setId(user.getId());
        userInfo.setUsername(user.getUsername());
        userInfo.setRealName(user.getRealName());
        userInfo.setIdCard(DesensitizeUtil.desensitizeIdCard(user.getIdCard()));
        // 解密手机号，让前端能存储完整手机号
        try {
            if (user.getPhone() != null) {
                userInfo.setPhone(aesUtil.decrypt(user.getPhone()));
            }
        } catch (Exception e) {
            log.error("解密手机号失败", e);
            userInfo.setPhone(user.getPhone());
        }
        userInfo.setRoleType(user.getRoleType());
        userInfo.setGender(user.getGender());
        userInfo.setAge(user.getAge());
        userInfo.setAddress(user.getAddress());
        userInfo.setAvatar(user.getAvatar());

        response.setUserInfo(userInfo);
        response.setMenus(getMenusByRole(user.getRoleType()));

        return response;
    }

    @Override
    public User getUserById(Long id) {
        User user = userMapper.selectById(id);
        if (user != null) {
            user.setIdCard(DesensitizeUtil.desensitizeIdCard(user.getIdCard()));
            // 解密手机号，让前端能显示完整手机号
            log.info("解密手机号前: {}", user.getPhone());
            try {
                if (user.getPhone() != null) {
                    String decryptedPhone = aesUtil.decrypt(user.getPhone());
                    log.info("解密手机号后: {}", decryptedPhone);
                    user.setPhone(decryptedPhone);
                }
            } catch (Exception e) {
                log.error("解密手机号失败", e);
            }
        }
        return user;
    }

    @Override
    @Transactional
    public User updateUser(Long id, UpdateUserRequest request) {
        User existingUser = userMapper.selectById(id);
        if (existingUser == null) {
            throw new RuntimeException("用户不存在");
        }

        if (request.getPhone() != null && !request.getPhone().isEmpty()) {
            // 加密手机号后再查询
            try {
                log.info("开始加密手机号，原始手机号: {}", request.getPhone());
                log.info("手机号长度: {}", request.getPhone().length());
                log.info("手机号格式是否正确: {}", request.getPhone().matches("^1[3-9]\\d{9}$"));
                String encryptedPhone = aesUtil.encrypt(request.getPhone());
                log.info("加密成功，加密后手机号: {}", encryptedPhone);
                User phoneUser = userMapper.selectByPhone(encryptedPhone);
                if (phoneUser != null && !phoneUser.getId().equals(id)) {
                    throw new RuntimeException("手机号已被使用");
                }
            } catch (Exception e) {
                log.error("加密手机号失败，原始手机号: {}", request.getPhone(), e);
                throw new RuntimeException("手机号格式错误");
            }
        }

        User updateUser = new User();
        BeanUtils.copyProperties(request, updateUser);
        updateUser.setId(id);

        // 加密手机号
        try {
            if (request.getPhone() != null && !request.getPhone().isEmpty()) {
                log.info("开始加密手机号(存储用)，原始手机号: {}", request.getPhone());
                String encryptedPhone = aesUtil.encrypt(request.getPhone());
                log.info("加密成功(存储用)，加密后手机号: {}", encryptedPhone);
                updateUser.setPhone(encryptedPhone);
            }
        } catch (Exception e) {
            log.error("加密手机号失败(存储用)，原始手机号: {}", request.getPhone(), e);
            throw new RuntimeException("手机号格式错误");
        }

        if (request.getPassword() != null && !request.getPassword().isEmpty()) {
            updateUser.setPassword(passwordEncoder.encode(request.getPassword()));
        }

        // 手动设置updateTime为当前时间
        updateUser.setUpdateTime(java.time.LocalDateTime.now());

        userMapper.updateById(updateUser);
        return getUserById(id);
    }

    @Override
    @Transactional
    public boolean createFamilyRelation(Long familyId, Long elderlyId, String relationType, String relationName, String proofMaterials) {
        FamilyElderlyRelation existingRelation = familyElderlyRelationMapper.selectByFamilyAndElderly(familyId, elderlyId);
        if (existingRelation != null) {
            throw new RuntimeException("关联关系已存在");
        }

        FamilyElderlyRelation relation = new FamilyElderlyRelation();
        relation.setFamilyId(familyId);
        relation.setElderlyId(elderlyId);
        relation.setRelationType(relationType);
        relation.setRelationName(relationName);
        relation.setProofMaterials(proofMaterials);
        relation.setAuditStatus("pending");

        return familyElderlyRelationMapper.insert(relation) > 0;
    }

    @Override
    @Transactional
    public boolean auditFamilyRelation(Long relationId, AuditFamilyRelationRequest request) {
        FamilyElderlyRelation relation = familyElderlyRelationMapper.selectById(relationId);
        if (relation == null) {
            throw new RuntimeException("关联关系不存在");
        }

        if (!"pending".equals(relation.getAuditStatus())) {
            throw new RuntimeException("该关联关系已审核");
        }

        return familyElderlyRelationMapper.updateAuditStatus(
                relationId,
                request.getAuditStatus(),
                request.getAuditRemark(),
                request.getAuditorId()
        ) > 0;
    }

    @Override
    public List<FamilyElderlyRelation> getFamilyRelationsByFamilyId(Long familyId) {
        return familyElderlyRelationMapper.selectByFamilyId(familyId);
    }

    @Override
    public List<FamilyElderlyRelation> getFamilyRelationsByElderlyId(Long elderlyId) {
        return familyElderlyRelationMapper.selectByElderlyId(elderlyId);
    }

    @Override
    public List<FamilyElderlyRelation> getPendingRelations() {
        LambdaQueryWrapper<FamilyElderlyRelation> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(FamilyElderlyRelation::getAuditStatus, "pending");
        return familyElderlyRelationMapper.selectList(wrapper);
    }

    @Override
    @Transactional
    public boolean updateRelationProofMaterials(Long relationId, String proofMaterials) {
        FamilyElderlyRelation relation = familyElderlyRelationMapper.selectById(relationId);
        if (relation == null) {
            throw new RuntimeException("关联关系不存在");
        }

        relation.setProofMaterials(proofMaterials);
        relation.setUpdateTime(java.time.LocalDateTime.now());

        return familyElderlyRelationMapper.updateById(relation) > 0;
    }

    @Override
    @Transactional
    public User createUser(User user) {
        // 检查用户名是否已存在
        User existingUser = userMapper.selectByUsername(user.getUsername());
        if (existingUser != null) {
            throw new RuntimeException("用户名已存在");
        }

        // 检查手机号是否已存在
        if (user.getPhone() != null && !user.getPhone().isEmpty()) {
            try {
                String encryptedPhone = aesUtil.encrypt(user.getPhone());
                User existingPhoneUser = userMapper.selectByPhone(encryptedPhone);
                if (existingPhoneUser != null) {
                    throw new RuntimeException("手机号已被使用");
                }
            } catch (Exception e) {
                log.error("加密手机号失败", e);
                throw new RuntimeException("手机号格式错误");
            }
        }

        // 加密密码
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        // 加密手机号
        try {
            if (user.getPhone() != null && !user.getPhone().isEmpty()) {
                user.setPhone(aesUtil.encrypt(user.getPhone()));
            }
        } catch (Exception e) {
            log.error("加密手机号失败", e);
            throw new RuntimeException("手机号格式错误");
        }

        // 设置默认值
        user.setIsEnabled(1);
        user.setIsDeleted(0);
        user.setCreateTime(java.time.LocalDateTime.now());
        user.setUpdateTime(java.time.LocalDateTime.now());

        // 保存用户
        userMapper.insert(user);

        return getUserById(user.getId());
    }

    @Override
    @Transactional
    public boolean deleteUser(Long id) {
        User user = userMapper.selectById(id);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }

        // 逻辑删除用户
        user.setIsDeleted(1);
        user.setUpdateTime(java.time.LocalDateTime.now());

        return userMapper.updateById(user) > 0;
    }

    @Override
    public java.util.Map<String, Object> getUserList(Integer page, Integer size, String name, String phone, Integer status, String roleType) {
        java.util.Map<String, Object> result = new java.util.HashMap<>();

        // 构建查询条件
        com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<User> wrapper = new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<>();
        wrapper.eq(User::getIsDeleted, 0);

        if (name != null && !name.isEmpty()) {
            wrapper.like(User::getRealName, name);
        }

        if (phone != null && !phone.isEmpty()) {
            try {
                String encryptedPhone = aesUtil.encrypt(phone);
                wrapper.like(User::getPhone, encryptedPhone);
            } catch (Exception e) {
                log.error("加密手机号失败", e);
            }
        }

        if (status != null) {
            wrapper.eq(User::getIsEnabled, status);
        }

        if (roleType != null && !roleType.isEmpty()) {
            wrapper.eq(User::getRoleType, roleType);
        }

        // 计算总数
        long total = userMapper.selectCount(wrapper);

        // 分页查询
        int offset = (page - 1) * size;
        wrapper.last("LIMIT " + offset + ", " + size);
        java.util.List<User> users = userMapper.selectList(wrapper);

        // 解密手机号
        for (User user : users) {
            try {
                if (user.getPhone() != null) {
                    user.setPhone(aesUtil.decrypt(user.getPhone()));
                }
            } catch (Exception e) {
                log.error("解密手机号失败", e);
            }
            user.setPassword(null);
        }

        result.put("records", users);
        result.put("total", total);
        result.put("current", page);
        result.put("size", size);

        return result;
    }

    @Override
    public java.util.Map<String, Object> getUserStatistics(String roleType) {
        java.util.Map<String, Object> statistics = new java.util.HashMap<>();

        // 构建查询条件
        com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<User> wrapper = new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<>();
        wrapper.eq(User::getIsDeleted, 0);

        if (roleType != null && !roleType.isEmpty()) {
            wrapper.eq(User::getRoleType, roleType);
        }

        // 总用户数
        long total = userMapper.selectCount(wrapper);
        statistics.put("total", total);

        // 启用用户数
        wrapper.eq(User::getIsEnabled, 1);
        long enabled = userMapper.selectCount(wrapper);
        statistics.put("enabled", enabled);

        // 禁用用户数
        wrapper.clear();
        wrapper.eq(User::getIsDeleted, 0);
        if (roleType != null && !roleType.isEmpty()) {
            wrapper.eq(User::getRoleType, roleType);
        }
        wrapper.eq(User::getIsEnabled, 0);
        long disabled = userMapper.selectCount(wrapper);
        statistics.put("disabled", disabled);

        return statistics;
    }

    private List<LoginResponse.MenuInfo> getMenusByRole(String roleType) {
        List<LoginResponse.MenuInfo> menus = new ArrayList<>();

        switch (roleType) {
            case "elderly":
                menus.add(createMenu("1", "首页", "/home", "el-icon-house"));
                menus.add(createMenu("2", "健康监测", "/health", "el-icon-monitor"));
                menus.add(createMenu("3", "服务预约", "/service", "el-icon-service"));
                menus.add(createMenu("4", "紧急求助", "/emergency", "el-icon-warning"));
                menus.add(createMenu("5", "个人中心", "/profile", "el-icon-user"));
                break;
            case "family":
                menus.add(createMenu("1", "首页", "/home", "el-icon-house"));
                menus.add(createMenu("2", "家人健康", "/family-health", "el-icon-monitor"));
                menus.add(createMenu("3", "服务预约", "/service", "el-icon-service"));
                menus.add(createMenu("4", "关联管理", "/relation", "el-icon-link"));
                menus.add(createMenu("5", "个人中心", "/profile", "el-icon-user"));
                break;
            case "caregiver":
                menus.add(createMenu("1", "首页", "/home", "el-icon-house"));
                menus.add(createMenu("2", "服务管理", "/service-manage", "el-icon-service"));
                menus.add(createMenu("3", "紧急求助", "/emergency", "el-icon-warning"));
                menus.add(createMenu("4", "健康数据", "/health-data", "el-icon-monitor"));
                menus.add(createMenu("5", "个人中心", "/profile", "el-icon-user"));
                break;
            case "admin":
                menus.add(createMenu("1", "首页", "/home", "el-icon-house"));
                menus.add(createMenu("2", "用户管理", "/user-manage", "el-icon-user"));
                menus.add(createMenu("3", "角色管理", "/role-manage", "el-icon-s-custom"));
                menus.add(createMenu("4", "服务管理", "/service-manage", "el-icon-service"));
                menus.add(createMenu("5", "健康数据", "/health-data", "el-icon-monitor"));
                menus.add(createMenu("6", "公告管理", "/announcement", "el-icon-bell"));
                menus.add(createMenu("7", "关联审核", "/relation-audit", "el-icon-link"));
                menus.add(createMenu("8", "系统设置", "/settings", "el-icon-setting"));
                break;
            default:
                break;
        }

        return menus;
    }

    private LoginResponse.MenuInfo createMenu(String id, String name, String path, String icon) {
        LoginResponse.MenuInfo menu = new LoginResponse.MenuInfo();
        menu.setId(Long.parseLong(id));
        menu.setName(name);
        menu.setPath(path);
        menu.setIcon(icon);
        return menu;
    }
}
package com.community.elderly.service.elderly.impl;

import com.community.elderly.dto.family.AuditFamilyRelationRequest;
import com.community.elderly.dto.elderly.LoginRequest;
import com.community.elderly.dto.elderly.UpdateUserRequest;
import com.community.elderly.entity.FamilyElderlyRelation;
import com.community.elderly.entity.User;
import com.community.elderly.mapper.elderly.ElderlyProfileMapper;
import com.community.elderly.mapper.family.FamilyRelationMapper;
import com.community.elderly.utils.CaptchaService;
import com.community.elderly.utils.JwtUtil;
import com.community.elderly.vo.LoginResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private ElderlyProfileMapper userMapper;

    @Mock
    private FamilyRelationMapper familyRelationMapper;

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private ElderlyProfileServiceImpl userService;

    private LoginRequest loginRequest;
    private UpdateUserRequest updateUserRequest;
    private AuditFamilyRelationRequest auditRequest;
    private User testUser;
    private FamilyElderlyRelation testRelation;

    @BeforeEach
    void setUp() {
        loginRequest = new LoginRequest();
        loginRequest.setUsername("testuser");
        loginRequest.setPassword("123456");
        loginRequest.setCaptcha("1234");
        loginRequest.setCaptchaId("test-captcha-id");

        updateUserRequest = new UpdateUserRequest();
        updateUserRequest.setPhone("13800138000");
        updateUserRequest.setAddress("测试地址");
        updateUserRequest.setAvatar("http://test.com/avatar.jpg");

        auditRequest = new AuditFamilyRelationRequest();
        auditRequest.setAuditStatus("approved");
        auditRequest.setAuditRemark("审核通过");
        auditRequest.setAuditorId(1L);

        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");
        testUser.setPassword(new BCryptPasswordEncoder().encode("123456"));
        testUser.setRealName("测试用户");
        testUser.setIdCard("123456789012345678");
        testUser.setPhone("13800138000");
        testUser.setRoleType("elderly");
        testUser.setGender(1);
        testUser.setAge(65);
        testUser.setAddress("测试地址");
        testUser.setAvatar("http://test.com/avatar.jpg");
        testUser.setIsEnabled(1);

        testRelation = new FamilyElderlyRelation();
        testRelation.setId(1L);
        testRelation.setFamilyId(1L);
        testRelation.setElderlyId(2L);
        testRelation.setRelationType("child");
        testRelation.setRelationName("儿子");
        testRelation.setAuditStatus("pending");
    }

    @Test
    void testLogin_Success() {
        try (MockedStatic<CaptchaService> mockedCaptchaService = mockStatic(CaptchaService.class)) {
            mockedCaptchaService.when(() -> CaptchaService.verifyCaptcha(anyString(), anyString())).thenReturn(true);
            when(userMapper.selectByUsername("testuser")).thenReturn(testUser);
            when(jwtUtil.generateToken(anyLong(), anyString(), anyString())).thenReturn("test-token");

            LoginResponse response = userService.login(loginRequest);

            assertNotNull(response);
            assertEquals("test-token", response.getToken());
            assertEquals("Bearer", response.getTokenType());
            assertNotNull(response.getUserInfo());
            assertEquals("testuser", response.getUserInfo().getUsername());
            assertEquals("测试用户", response.getUserInfo().getRealName());
            assertEquals("elderly", response.getUserInfo().getRoleType());
            assertNotNull(response.getMenus());
            assertFalse(response.getMenus().isEmpty());
        }
    }

    @Test
    void testLogin_CaptchaError() {
        try (MockedStatic<CaptchaService> mockedCaptchaService = mockStatic(CaptchaService.class)) {
            mockedCaptchaService.when(() -> CaptchaService.verifyCaptcha(anyString(), anyString())).thenReturn(false);

            assertThrows(RuntimeException.class, () -> userService.login(loginRequest));
        }
    }

    @Test
    void testLogin_UserNotFound() {
        try (MockedStatic<CaptchaService> mockedCaptchaService = mockStatic(CaptchaService.class)) {
            mockedCaptchaService.when(() -> CaptchaService.verifyCaptcha(anyString(), anyString())).thenReturn(true);
            when(userMapper.selectByUsername("testuser")).thenReturn(null);

            assertThrows(RuntimeException.class, () -> userService.login(loginRequest));
        }
    }

    @Test
    void testLogin_AccountDisabled() {
        try (MockedStatic<CaptchaService> mockedCaptchaService = mockStatic(CaptchaService.class)) {
            mockedCaptchaService.when(() -> CaptchaService.verifyCaptcha(anyString(), anyString())).thenReturn(true);
            testUser.setIsEnabled(0);
            when(userMapper.selectByUsername("testuser")).thenReturn(testUser);

            assertThrows(RuntimeException.class, () -> userService.login(loginRequest));
        }
    }

    @Test
    void testLogin_PasswordError() {
        try (MockedStatic<CaptchaService> mockedCaptchaService = mockStatic(CaptchaService.class)) {
            mockedCaptchaService.when(() -> CaptchaService.verifyCaptcha(anyString(), anyString())).thenReturn(true);
            testUser.setPassword(new BCryptPasswordEncoder().encode("wrongpassword"));
            when(userMapper.selectByUsername("testuser")).thenReturn(testUser);

            assertThrows(RuntimeException.class, () -> userService.login(loginRequest));
        }
    }

    @Test
    void testGetUserById_Success() {
        when(userMapper.selectById(1L)).thenReturn(testUser);

        User result = userService.getUserById(1L);

        assertNotNull(result);
        assertEquals("testuser", result.getUsername());
        assertEquals("测试用户", result.getRealName());
        assertEquals("elderly", result.getRoleType());
        assertEquals("123456********5678", result.getIdCard());
        assertEquals("138****8000", result.getPhone());
    }

    @Test
    void testGetUserById_UserNotFound() {
        when(userMapper.selectById(999L)).thenReturn(null);

        User result = userService.getUserById(999L);

        assertNull(result);
    }

    @Test
    void testUpdateUser_Success() {
        when(userMapper.selectById(1L)).thenReturn(testUser);
        when(userMapper.selectByPhone("13800138000")).thenReturn(null);
        when(userMapper.updateById(any(User.class))).thenReturn(1);

        User result = userService.updateUser(1L, updateUserRequest);

        assertNotNull(result);
        assertEquals("testuser", result.getUsername());
        verify(userMapper, times(1)).updateById(any(User.class));
    }

    @Test
    void testUpdateUser_UserNotFound() {
        when(userMapper.selectById(999L)).thenReturn(null);

        assertThrows(RuntimeException.class, () -> userService.updateUser(999L, updateUserRequest));
    }

    @Test
    void testUpdateUser_PhoneAlreadyUsed() {
        User anotherUser = new User();
        anotherUser.setId(2L);
        anotherUser.setUsername("anotheruser");
        anotherUser.setPhone("13800138000");

        when(userMapper.selectById(1L)).thenReturn(testUser);
        when(userMapper.selectByPhone("13800138000")).thenReturn(anotherUser);

        assertThrows(RuntimeException.class, () -> userService.updateUser(1L, updateUserRequest));
    }

    @Test
    void testUpdateUser_WithPassword() {
        updateUserRequest.setPassword("newpassword123");

        when(userMapper.selectById(1L)).thenReturn(testUser);
        when(userMapper.selectByPhone("13800138000")).thenReturn(null);
        when(userMapper.updateById(any(User.class))).thenReturn(1);

        User result = userService.updateUser(1L, updateUserRequest);

        assertNotNull(result);
        verify(userMapper, times(1)).updateById(argThat(user -> {
            BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
            return encoder.matches("newpassword123", user.getPassword());
        }));
    }

    @Test
    void testCreateFamilyRelation_Success() {
        when(familyRelationMapper.selectByFamilyAndElderly(1L, 2L)).thenReturn(null);
        when(familyRelationMapper.insert(any(FamilyElderlyRelation.class))).thenReturn(1);

        String proofMaterials = "{\"url\": \"/uploads/test.jpg\"}";
        boolean result = userService.createFamilyRelation(1L, 2L, "child", "儿子", proofMaterials);

        assertTrue(result);
        verify(familyRelationMapper, times(1)).insert(argThat(relation -> {
            return relation.getFamilyId().equals(1L) &&
                   relation.getElderlyId().equals(2L) &&
                   relation.getRelationType().equals("child") &&
                   relation.getRelationName().equals("儿子") &&
                   relation.getProofMaterials().equals(proofMaterials) &&
                   relation.getAuditStatus().equals("pending");
        }));
    }

    @Test
    void testCreateFamilyRelation_RelationAlreadyExists() {
        when(familyRelationMapper.selectByFamilyAndElderly(1L, 2L)).thenReturn(testRelation);

        assertThrows(RuntimeException.class, () -> userService.createFamilyRelation(1L, 2L, "child", "儿子", "{\"url\": \"/uploads/test.jpg\"}"));
    }

    @Test
    void testAuditFamilyRelation_Success() {
        when(familyRelationMapper.selectById(1L)).thenReturn(testRelation);
        when(familyRelationMapper.updateAuditStatus(eq(1L), eq("approved"), eq("审核通过"), eq(1L))).thenReturn(1);

        boolean result = userService.auditFamilyRelation(1L, auditRequest);

        assertTrue(result);
        verify(familyRelationMapper, times(1)).updateAuditStatus(1L, "approved", "审核通过", 1L);
    }

    @Test
    void testAuditFamilyRelation_RelationNotFound() {
        when(familyRelationMapper.selectById(999L)).thenReturn(null);

        assertThrows(RuntimeException.class, () -> userService.auditFamilyRelation(999L, auditRequest));
    }

    @Test
    void testAuditFamilyRelation_AlreadyAudited() {
        testRelation.setAuditStatus("approved");
        when(familyRelationMapper.selectById(1L)).thenReturn(testRelation);

        assertThrows(RuntimeException.class, () -> userService.auditFamilyRelation(1L, auditRequest));
    }

    @Test
    void testGetFamilyRelationsByFamilyId_Success() {
        List<FamilyElderlyRelation> relations = new ArrayList<>();
        relations.add(testRelation);

        when(familyRelationMapper.selectByFamilyId(1L)).thenReturn(relations);

        List<FamilyElderlyRelation> result = userService.getFamilyRelationsByFamilyId(1L);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).getFamilyId());
    }

    @Test
    void testGetFamilyRelationsByElderlyId_Success() {
        List<FamilyElderlyRelation> relations = new ArrayList<>();
        relations.add(testRelation);

        when(familyRelationMapper.selectByElderlyId(2L)).thenReturn(relations);

        List<FamilyElderlyRelation> result = userService.getFamilyRelationsByElderlyId(2L);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(2L, result.get(0).getElderlyId());
    }

    @Test
    void testGetPendingRelations_Success() {
        List<FamilyElderlyRelation> relations = new ArrayList<>();
        relations.add(testRelation);

        when(familyRelationMapper.selectList(any())).thenReturn(relations);

        List<FamilyElderlyRelation> result = userService.getPendingRelations();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("pending", result.get(0).getAuditStatus());
    }
}

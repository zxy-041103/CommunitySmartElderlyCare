package com.community.elderly.controller.elderly;

import com.community.elderly.common.Result;
import com.community.elderly.dto.family.AuditFamilyRelationRequest;
import com.community.elderly.dto.elderly.LoginRequest;
import com.community.elderly.dto.elderly.UpdateUserRequest;
import com.community.elderly.entity.FamilyElderlyRelation;
import com.community.elderly.entity.User;
import com.community.elderly.service.elderly.ElderlyProfileService;
import com.community.elderly.utils.CaptchaService;
import com.community.elderly.vo.LoginResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @Mock
    private ElderlyProfileService userService;

    @InjectMocks
    private UserController userController;

    private LoginRequest loginRequest;
    private UpdateUserRequest updateUserRequest;
    private AuditFamilyRelationRequest auditRequest;
    private User testUser;
    private LoginResponse loginResponse;

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
        testUser.setRealName("测试用户");
        testUser.setIdCard("123456789012345678");
        testUser.setPhone("13800138000");
        testUser.setRoleType("elderly");
        testUser.setGender(1);
        testUser.setAge(65);
        testUser.setAddress("测试地址");
        testUser.setAvatar("http://test.com/avatar.jpg");
        testUser.setIsEnabled(1);

        loginResponse = new LoginResponse();
        loginResponse.setToken("test-token");
        loginResponse.setTokenType("Bearer");
        loginResponse.setUserInfo(new LoginResponse.UserInfo());
        loginResponse.setMenus(new ArrayList<>());
    }

    @Test
    void testLogin_Success() {
        when(userService.login(any(LoginRequest.class))).thenReturn(loginResponse);

        Result<LoginResponse> result = userController.login(loginRequest);

        assertNotNull(result);
        assertEquals(200, result.getCode());
        assertEquals("操作成功", result.getMessage());
        assertNotNull(result.getData());
        assertEquals("test-token", result.getData().getToken());
        verify(userService, times(1)).login(any(LoginRequest.class));
    }

    @Test
    void testLogin_CaptchaError() {
        try (MockedStatic<CaptchaService> mockedCaptchaService = mockStatic(CaptchaService.class)) {
            mockedCaptchaService.when(() -> CaptchaService.verifyCaptcha(anyString(), anyString())).thenReturn(false);

            when(userService.login(any(LoginRequest.class))).thenThrow(new RuntimeException("验证码错误"));

            Result<LoginResponse> result = userController.login(loginRequest);

            assertNotNull(result);
            assertEquals(500, result.getCode());
            assertEquals("验证码错误", result.getMessage());
            assertNull(result.getData());
        }
    }

    @Test
    void testLogin_UserNotFound() {
        when(userService.login(any(LoginRequest.class))).thenThrow(new RuntimeException("用户不存在"));

        Result<LoginResponse> result = userController.login(loginRequest);

        assertNotNull(result);
        assertEquals(500, result.getCode());
        assertEquals("用户不存在", result.getMessage());
        assertNull(result.getData());
    }

    @Test
    void testLogin_PasswordError() {
        when(userService.login(any(LoginRequest.class))).thenThrow(new RuntimeException("密码错误"));

        Result<LoginResponse> result = userController.login(loginRequest);

        assertNotNull(result);
        assertEquals(500, result.getCode());
        assertEquals("密码错误", result.getMessage());
        assertNull(result.getData());
    }

    @Test
    void testLogin_AccountDisabled() {
        when(userService.login(any(LoginRequest.class))).thenThrow(new RuntimeException("账号已被禁用"));

        Result<LoginResponse> result = userController.login(loginRequest);

        assertNotNull(result);
        assertEquals(500, result.getCode());
        assertEquals("账号已被禁用", result.getMessage());
        assertNull(result.getData());
    }

    @Test
    void testGetUserById_Success() {
        when(userService.getUserById(1L)).thenReturn(testUser);

        Result<User> result = userController.getUserById(1L);

        assertNotNull(result);
        assertEquals(200, result.getCode());
        assertEquals("操作成功", result.getMessage());
        assertNotNull(result.getData());
        assertEquals("testuser", result.getData().getUsername());
        assertEquals("测试用户", result.getData().getRealName());
        verify(userService, times(1)).getUserById(1L);
    }

    @Test
    void testGetUserById_UserNotFound() {
        when(userService.getUserById(999L)).thenReturn(null);

        Result<User> result = userController.getUserById(999L);

        assertNotNull(result);
        assertEquals(500, result.getCode());
        assertEquals("用户不存在", result.getMessage());
        assertNull(result.getData());
    }

    @Test
    void testUpdateUser_Success() {
        when(userService.updateUser(eq(1L), any(UpdateUserRequest.class))).thenReturn(testUser);

        Result<User> result = userController.updateUser(1L, updateUserRequest);

        assertNotNull(result);
        assertEquals(200, result.getCode());
        assertEquals("操作成功", result.getMessage());
        assertNotNull(result.getData());
        verify(userService, times(1)).updateUser(eq(1L), any(UpdateUserRequest.class));
    }

    @Test
    void testUpdateUser_UserNotFound() {
        when(userService.updateUser(eq(999L), any(UpdateUserRequest.class)))
                .thenThrow(new RuntimeException("用户不存在"));

        Result<User> result = userController.updateUser(999L, updateUserRequest);

        assertNotNull(result);
        assertEquals(500, result.getCode());
        assertEquals("用户不存在", result.getMessage());
        assertNull(result.getData());
    }

    @Test
    void testUpdateUser_PhoneAlreadyUsed() {
        when(userService.updateUser(eq(1L), any(UpdateUserRequest.class)))
                .thenThrow(new RuntimeException("手机号已被使用"));

        Result<User> result = userController.updateUser(1L, updateUserRequest);

        assertNotNull(result);
        assertEquals(500, result.getCode());
        assertEquals("手机号已被使用", result.getMessage());
        assertNull(result.getData());
    }

    @Test
    void testCreateFamilyRelation_Success() {
        when(userService.createFamilyRelation(anyLong(), anyLong(), anyString(), anyString(), anyString())).thenReturn(true);

        Result<Boolean> result = userController.createFamilyRelation(1L, 2L, "child", "儿子", "{\"url\": \"/uploads/test.jpg\"}");

        assertNotNull(result);
        assertEquals(200, result.getCode());
        assertEquals("操作成功", result.getMessage());
        assertTrue(result.getData());
        verify(userService, times(1)).createFamilyRelation(1L, 2L, "child", "儿子", "{\"url\": \"/uploads/test.jpg\"}");
    }

    @Test
    void testCreateFamilyRelation_RelationAlreadyExists() {
        when(userService.createFamilyRelation(anyLong(), anyLong(), anyString(), anyString(), anyString()))
                .thenThrow(new RuntimeException("关联关系已存在"));

        Result<Boolean> result = userController.createFamilyRelation(1L, 2L, "child", "儿子", "{\"url\": \"/uploads/test.jpg\"}");

        assertNotNull(result);
        assertEquals(500, result.getCode());
        assertEquals("关联关系已存在", result.getMessage());
        assertNull(result.getData());
    }

    @Test
    void testAuditFamilyRelation_Success() {
        when(userService.auditFamilyRelation(eq(1L), any(AuditFamilyRelationRequest.class))).thenReturn(true);

        Result<Boolean> result = userController.auditFamilyRelation(1L, auditRequest);

        assertNotNull(result);
        assertEquals(200, result.getCode());
        assertEquals("操作成功", result.getMessage());
        assertTrue(result.getData());
        verify(userService, times(1)).auditFamilyRelation(eq(1L), any(AuditFamilyRelationRequest.class));
    }

    @Test
    void testAuditFamilyRelation_RelationNotFound() {
        when(userService.auditFamilyRelation(eq(999L), any(AuditFamilyRelationRequest.class)))
                .thenThrow(new RuntimeException("关联关系不存在"));

        Result<Boolean> result = userController.auditFamilyRelation(999L, auditRequest);

        assertNotNull(result);
        assertEquals(500, result.getCode());
        assertEquals("关联关系不存在", result.getMessage());
        assertNull(result.getData());
    }

    @Test
    void testAuditFamilyRelation_AlreadyAudited() {
        when(userService.auditFamilyRelation(eq(1L), any(AuditFamilyRelationRequest.class)))
                .thenThrow(new RuntimeException("该关联关系已审核"));

        Result<Boolean> result = userController.auditFamilyRelation(1L, auditRequest);

        assertNotNull(result);
        assertEquals(500, result.getCode());
        assertEquals("该关联关系已审核", result.getMessage());
        assertNull(result.getData());
    }

    @Test
    void testGetRelationsByFamilyId_Success() {
        List<FamilyElderlyRelation> relations = new ArrayList<>();
        FamilyElderlyRelation relation = new FamilyElderlyRelation();
        relation.setId(1L);
        relation.setFamilyId(1L);
        relation.setElderlyId(2L);
        relation.setRelationType("child");
        relation.setRelationName("儿子");
        relation.setAuditStatus("approved");
        relations.add(relation);

        when(userService.getFamilyRelationsByFamilyId(1L)).thenReturn(relations);

        Result<List<FamilyElderlyRelation>> result = userController.getRelationsByFamilyId(1L);

        assertNotNull(result);
        assertEquals(200, result.getCode());
        assertEquals("操作成功", result.getMessage());
        assertNotNull(result.getData());
        assertEquals(1, result.getData().size());
        verify(userService, times(1)).getFamilyRelationsByFamilyId(1L);
    }

    @Test
    void testGetRelationsByElderlyId_Success() {
        List<FamilyElderlyRelation> relations = new ArrayList<>();
        FamilyElderlyRelation relation = new FamilyElderlyRelation();
        relation.setId(1L);
        relation.setFamilyId(1L);
        relation.setElderlyId(2L);
        relation.setRelationType("child");
        relation.setRelationName("儿子");
        relation.setAuditStatus("approved");
        relations.add(relation);

        when(userService.getFamilyRelationsByElderlyId(2L)).thenReturn(relations);

        Result<List<FamilyElderlyRelation>> result = userController.getRelationsByElderlyId(2L);

        assertNotNull(result);
        assertEquals(200, result.getCode());
        assertEquals("操作成功", result.getMessage());
        assertNotNull(result.getData());
        assertEquals(1, result.getData().size());
        verify(userService, times(1)).getFamilyRelationsByElderlyId(2L);
    }

    @Test
    void testGetPendingRelations_Success() {
        List<FamilyElderlyRelation> relations = new ArrayList<>();
        FamilyElderlyRelation relation = new FamilyElderlyRelation();
        relation.setId(1L);
        relation.setFamilyId(1L);
        relation.setElderlyId(2L);
        relation.setRelationType("child");
        relation.setRelationName("儿子");
        relation.setAuditStatus("pending");
        relations.add(relation);

        when(userService.getPendingRelations()).thenReturn(relations);

        Result<List<FamilyElderlyRelation>> result = userController.getPendingRelations();

        assertNotNull(result);
        assertEquals(200, result.getCode());
        assertEquals("操作成功", result.getMessage());
        assertNotNull(result.getData());
        assertEquals(1, result.getData().size());
        assertEquals("pending", result.getData().get(0).getAuditStatus());
        verify(userService, times(1)).getPendingRelations();
    }
}

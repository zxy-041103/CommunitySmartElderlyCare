<template>
  <div class="user-manage">
    <div class="page-header">
      <h2 class="page-title">用户管理</h2>
      <el-button type="primary" size="large" @click="openAddUserDialog">
        <el-icon><Plus /></el-icon>
        新增用户
      </el-button>
    </div>

    <!-- 统计卡片 -->
    <el-row :gutter="20" class="stats-row">
      <el-col :xs="12" :sm="6" v-for="stat in userStats" :key="stat.type">
        <el-card class="stat-card" shadow="hover" @click="filterByRole(stat.type)">
          <div class="stat-content">
            <div class="stat-icon" :class="stat.type">
              <el-icon :size="24"><component :is="stat.icon" /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ stat.value }}</div>
              <div class="stat-label">{{ stat.label }}</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 用户列表 -->
    <el-card class="user-list-card" shadow="hover">
      <template #header>
        <div class="list-header">
          <div class="filter-section">
            <el-radio-group v-model="activeRole" size="small" @change="handleRoleChange">
              <el-radio-button label="all">全部</el-radio-button>
              <el-radio-button label="elderly">老人</el-radio-button>
              <el-radio-button label="family">家属</el-radio-button>
              <el-radio-button label="caregiver">护工</el-radio-button>
              <el-radio-button label="admin">管理员</el-radio-button>
            </el-radio-group>
          </div>
          <div class="search-section">
            <el-input
              v-model="searchKeyword"
              placeholder="搜索用户名/姓名/手机号"
              clearable
              style="width: 250px"
              @input="handleSearch"
            >
              <template #prefix>
                <el-icon><Search /></el-icon>
              </template>
            </el-input>
            <el-button :icon="Refresh" @click="refreshUsers" :loading="loading">
              刷新
            </el-button>
          </div>
        </div>
      </template>

      <el-table :data="filteredUsers" stripe v-loading="loading" style="width: 100%">
        <el-table-column type="index" width="50" />
        <el-table-column prop="username" label="用户名" width="120" />
        <el-table-column prop="realName" label="姓名" width="100" />
        <el-table-column prop="phone" label="手机号" width="130" />
        <el-table-column prop="roleType" label="角色" width="100">
          <template #default="scope">
            <el-tag :type="getRoleType(scope.row.roleType)" size="small">
              {{ getRoleLabel(scope.row.roleType) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="gender" label="性别" width="80">
          <template #default="scope">
            {{ scope.row.gender === 1 || scope.row.gender === 'male' ? '男' : '女' }}
          </template>
        </el-table-column>
        <el-table-column prop="age" label="年龄" width="80" />
        <el-table-column prop="isEnabled" label="状态" width="100">
          <template #default="scope">
            <el-switch
              v-model="scope.row.isEnabled"
              :active-value="1"
              :inactive-value="0"
              @change="updateUserStatus(scope.row)"
            />
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="160">
          <template #default="scope">
            {{ formatDateTime(scope.row.createTime) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="scope">
            <el-button type="primary" size="small" @click="editUser(scope.row)">
              编辑
            </el-button>
            <el-button type="danger" size="small" @click="deleteUser(scope.row)">
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination-section">
        <el-pagination
          :current-page="currentPage"
          :page-size="pageSize"
          :page-sizes="[10, 20, 50]"
          :total="total"
          layout="total, sizes, prev, pager, next, jumper"
          @update:current-page="handlePageChange"
          @update:page-size="handleSizeChange"
        />
      </div>
    </el-card>

    <!-- 家属-老人关联审核 -->
    <el-card class="relation-card" shadow="hover">
      <template #header>
        <div class="relation-header">
          <span class="relation-title">家属-老人关联审核</span>
          <el-badge :value="pendingRelations.length" :hidden="pendingRelations.length === 0" />
        </div>
      </template>

      <el-table :data="pendingRelations" stripe v-loading="relationLoading">
        <el-table-column prop="familyName" label="家属姓名" width="120" />
        <el-table-column prop="familyPhone" label="家属手机号" width="130" />
        <el-table-column prop="elderlyName" label="老人姓名" width="120" />
        <el-table-column prop="elderlyPhone" label="老人手机号" width="130" />
        <el-table-column prop="relationType" label="关系" width="100">
          <template #default="scope">
            {{ getRelationTypeText(scope.row.relationType) }}
          </template>
        </el-table-column>
        <el-table-column prop="relationName" label="关系名称" width="100" />
        <el-table-column prop="applyTime" label="申请时间" width="160">
          <template #default="scope">
            {{ formatDateTime(scope.row.applyTime) }}
          </template>
        </el-table-column>
        <el-table-column label="审核材料" width="100">
          <template #default="scope">
            <el-button 
              type="info" 
              size="small" 
              @click="viewProofMaterials(scope.row)"
            >
              查看
            </el-button>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="scope">
            <el-button type="success" size="small" @click="approveRelation(scope.row)">
              通过
            </el-button>
            <el-button type="danger" size="small" @click="rejectRelation(scope.row)">
              拒绝
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <div v-if="pendingRelations.length === 0" class="empty-relations">
        <el-empty description="暂无待审核的关联申请" :image-size="80" />
      </div>
    </el-card>

    <!-- 新增/编辑用户弹窗 -->
    <el-dialog
      v-model="userDialogVisible"
      :title="isEditMode ? '编辑用户' : '新增用户'"
      width="600px"
      destroy-on-close
    >
      <el-form
        ref="userFormRef"
        :model="userForm"
        :rules="userRules"
        label-width="100px"
      >
        <el-form-item label="用户名" prop="username">
          <el-input v-model="userForm.username" placeholder="请输入用户名" />
        </el-form-item>
        <el-form-item label="姓名" prop="realName">
          <el-input v-model="userForm.realName" placeholder="请输入姓名" />
        </el-form-item>
        <el-form-item label="手机号" prop="phone">
          <el-input v-model="userForm.phone" placeholder="请输入手机号" />
        </el-form-item>
        <el-form-item label="身份证号" prop="idCard">
          <el-input v-model="userForm.idCard" placeholder="请输入身份证号" />
        </el-form-item>
        <el-form-item v-if="!isEditMode" label="密码" prop="password">
          <el-input v-model="userForm.password" type="password" placeholder="请输入密码" />
        </el-form-item>
        <el-form-item label="角色" prop="roleType">
          <el-select v-model="userForm.roleType" placeholder="请选择角色" style="width: 100%">
            <el-option
              v-for="role in roleOptions"
              :key="role.value"
              :label="role.label"
              :value="role.value"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="性别">
          <el-radio-group v-model="userForm.gender">
            <el-radio label="male">男</el-radio>
            <el-radio label="female">女</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="年龄">
          <el-input-number v-model="userForm.age" :min="1" :max="150" style="width: 100%" />
        </el-form-item>
        <el-form-item label="地址">
          <el-input v-model="userForm.address" type="textarea" :rows="2" placeholder="请输入地址" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="userDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitUserForm" :loading="submitting">
          确定
        </el-button>
      </template>
    </el-dialog>

    <!-- 审核材料查看弹窗 -->
    <el-dialog
      v-model="proofMaterialsVisible"
      title="审核材料"
      width="800px"
    >
      <div v-if="parsedProofMaterials && parsedProofMaterials.length > 0" class="proof-materials">
        <div class="materials-list">
          <div
            v-for="(material, index) in parsedProofMaterials"
            :key="index"
            class="material-item"
          >
            <el-image
              :src="getFullImageUrl(material.url)"
              fit="cover"
              style="width: 200px; height: 200px; cursor: pointer"
              @click="previewImage(getFullImageUrl(material.url))"
            >
              <template #error>
                <div class="image-error">
                  <el-icon><Picture /></el-icon>
                  <span>图片加载失败</span>
                </div>
              </template>
            </el-image>
            <div class="material-name">{{ material.name }}</div>
          </div>
        </div>
      </div>
      <div v-else class="no-materials">
        <el-empty description="暂无审核材料" :image-size="80" />
      </div>
    </el-dialog>

    <!-- 图片预览弹窗 -->
    <el-dialog
      v-model="imagePreviewVisible"
      title="材料预览"
      width="800px"
    >
      <el-image
        :src="previewImageUrl"
        fit="contain"
        style="width: 100%; height: 600px"
      />
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue';
import { Plus, Search, Refresh, User, UserFilled, FirstAidKit, Setting, Picture } from '@element-plus/icons-vue';
import { ElMessage, ElMessageBox } from 'element-plus';
import request from '@/utils/request';

// 页面名称
defineOptions({
  name: 'AdminUserManage'
});

// 角色选项
const roleOptions = [
  { label: '老人', value: 'elderly', icon: 'User' },
  { label: '家属', value: 'family', icon: 'UserFilled' },
  { label: '护工', value: 'caregiver', icon: 'FirstAidKit' },
  { label: '管理员', value: 'admin', icon: 'Setting' }
];

// 统计数据
const userStats = ref([
  { type: 'all', label: '全部用户', value: 0, icon: 'User' },
  { type: 'elderly', label: '老人', value: 0, icon: 'User' },
  { type: 'family', label: '家属', value: 0, icon: 'UserFilled' },
  { type: 'caregiver', label: '护工', value: 0, icon: 'FirstAidKit' }
]);

// 用户列表数据
const users = ref([]);
const loading = ref(false);
const activeRole = ref('all');
const searchKeyword = ref('');
const currentPage = ref(1);
const pageSize = ref(10);
const total = ref(0);

// 关联审核数据
const pendingRelations = ref([]);
const relationLoading = ref(false);

// 审核材料查看相关
const proofMaterialsVisible = ref(false);
const currentRelation = ref(null);
const imagePreviewVisible = ref(false);
const previewImageUrl = ref('');

// 解析审核材料
const parsedProofMaterials = computed(() => {
  if (!currentRelation.value?.proofMaterials) return [];
  try {
    return JSON.parse(currentRelation.value.proofMaterials);
  } catch (error) {
    console.error('解析审核材料失败:', error);
    return [];
  }
});

// 弹窗数据
const userDialogVisible = ref(false);
const isEditMode = ref(false);
const submitting = ref(false);
const userFormRef = ref(null);

const userForm = ref({
  id: null,
  username: '',
  realName: '',
  phone: '',
  idCard: '',
  password: '',
  roleType: '',
  gender: 'male',
  age: null,
  address: '',
  isEnabled: 1
});

const userRules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  realName: [{ required: true, message: '请输入姓名', trigger: 'blur' }],
  phone: [
    { required: true, message: '请输入手机号', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '手机号格式不正确', trigger: 'blur' }
  ],
  idCard: [
    { required: true, message: '请输入身份证号', trigger: 'blur' },
    { pattern: /^\d{17}[\dXx]$/, message: '身份证号格式不正确', trigger: 'blur' }
  ],
  password: [{ required: true, message: '请输入密码', trigger: 'blur', min: 6 }],
  roleType: [{ required: true, message: '请选择角色', trigger: 'change' }]
};

// 过滤后的用户列表
const filteredUsers = computed(() => {
  let result = users.value;
  
  if (activeRole.value !== 'all') {
    result = result.filter(user => user.roleType === activeRole.value);
  }
  
  if (searchKeyword.value) {
    const keyword = searchKeyword.value.toLowerCase();
    result = result.filter(user => 
      user.username?.toLowerCase().includes(keyword) ||
      user.realName?.toLowerCase().includes(keyword) ||
      user.phone?.includes(keyword)
    );
  }
  
  return result;
});

// 获取用户列表
const fetchUsers = async () => {
  loading.value = true;
  try {
    const response = await request({
      url: '/admin/users',
      method: 'get',
      params: {
        page: currentPage.value,
        size: pageSize.value,
        roleType: activeRole.value === 'all' ? '' : activeRole.value,
        keyword: searchKeyword.value
      }
    });

    if (response.code === 200 && response.data) {
      users.value = response.data.records || [];
      total.value = response.data.total || 0;
      updateStats(response.data.stats);
    }
  } catch (error) {
    console.error('获取用户列表失败:', error);
    ElMessage.error('获取用户列表失败，请稍后重试');
  } finally {
    loading.value = false;
  }
};

// 获取待审核关联列表
const fetchPendingRelations = async () => {
  relationLoading.value = true;
  console.log('开始获取待审核关联列表...');
  try {
    const response = await request({
      url: '/admin/family-elderly-relations/pending',
      method: 'get'
    });

    console.log('获取待审核关联列表响应:', response);

    if (response.code === 200 && response.data) {
      pendingRelations.value = response.data || [];
      console.log('待审核关联列表:', pendingRelations.value);
      console.log('待审核关联列表长度:', pendingRelations.value.length);
    } else {
      console.log('响应数据为空或code不为200:', response);
    }
  } catch (error) {
    console.error('获取关联审核列表失败:', error);
    ElMessage.error('获取关联审核列表失败，请稍后重试');
  } finally {
    relationLoading.value = false;
    console.log('获取待审核关联列表完成');
  }
};

// 更新统计
const updateStats = (stats) => {
  userStats.value = [
    { type: 'all', label: '全部用户', value: stats?.all || 0, icon: 'User' },
    { type: 'elderly', label: '老人', value: stats?.elderly || 0, icon: 'User' },
    { type: 'family', label: '家属', value: stats?.family || 0, icon: 'UserFilled' },
    { type: 'caregiver', label: '护工', value: stats?.caregiver || 0, icon: 'FirstAidKit' }
  ];
};

// 角色筛选
const filterByRole = (role) => {
  activeRole.value = role;
  currentPage.value = 1;
  fetchUsers();
};

const handleRoleChange = () => {
  currentPage.value = 1;
  fetchUsers();
};

// 搜索
const handleSearch = () => {
  currentPage.value = 1;
  fetchUsers();
};

// 刷新
const refreshUsers = () => {
  fetchUsers();
  fetchPendingRelations();
};

// 分页
const handlePageChange = (newPage) => {
  currentPage.value = newPage;
  fetchUsers();
};

const handleSizeChange = (newSize) => {
  pageSize.value = newSize;
  currentPage.value = 1;
  fetchUsers();
};

// 打开新增用户弹窗
const openAddUserDialog = () => {
  isEditMode.value = false;
  resetForm();
  userDialogVisible.value = true;
};

// 编辑用户
const editUser = (user) => {
  isEditMode.value = true;
  // 将后端的整数gender转换为前端字符串
  const genderStr = user.gender === 1 ? 'male' : (user.gender === 0 ? 'female' : 'male');
  userForm.value = { 
    ...user,
    gender: genderStr
  };
  userDialogVisible.value = true;
};

// 删除用户
const deleteUser = async (user) => {
  try {
    await ElMessageBox.confirm(
      `确定要删除用户 "${user.realName}" 吗？此操作不可恢复！`,
      '确认删除',
      { type: 'warning' }
    );

    const response = await request({
      url: `/admin/users/${user.id}`,
      method: 'delete'
    });

    if (response.code === 200) {
      ElMessage.success('用户已删除');
      fetchUsers();
    } else {
      ElMessage.error(response.message || '删除失败');
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除用户失败:', error);
      ElMessage.error('删除失败');
    }
  }
};

// 提交用户表单
const submitUserForm = async () => {
  if (!userFormRef.value) return;

  await userFormRef.value.validate(async (valid) => {
    if (!valid) return;

    submitting.value = true;
    try {
      const url = isEditMode.value 
        ? `/admin/users/${userForm.value.id}` 
        : '/admin/users';
      const method = isEditMode.value ? 'put' : 'post';

      const response = await request({
        url,
        method,
        data: userForm.value
      });

      if (response.code === 200) {
        ElMessage.success(isEditMode.value ? '用户已更新' : '用户已新增');
        userDialogVisible.value = false;
        fetchUsers();
      } else {
        ElMessage.error(response.message || '操作失败');
      }
    } catch (error) {
      console.error('提交用户表单失败:', error);
      ElMessage.error('操作失败，请稍后重试');
    } finally {
      submitting.value = false;
    }
  });
};

// 更新用户状态
const updateUserStatus = async (user) => {
  try {
    const response = await request({
      url: `/admin/users/${user.id}/status`,
      method: 'put',
      data: { isEnabled: user.isEnabled }
    });

    if (response.code === 200) {
      ElMessage.success(`用户状态已更新为: ${user.isEnabled === 1 ? '启用' : '禁用'}`);
    } else {
      ElMessage.error(response.message || '更新失败');
    }
  } catch (error) {
    console.error('更新用户状态失败:', error);
    ElMessage.error('状态更新失败，请稍后重试');
    }
};

// 审核关联 - 通过
const approveRelation = async (relation) => {
  try {
    const response = await request({
      url: `/admin/family-elderly-relations/${relation.id}/approve`,
      method: 'put'
    });

    if (response.code === 200) {
      ElMessage.success('关联申请已通过');
      fetchPendingRelations();
    } else {
      ElMessage.error(response.message || '操作失败');
    }
  } catch (error) {
    console.error('审核关联失败:', error);
    ElMessage.error('审核失败，请稍后重试');
  }
};

// 审核关联 - 拒绝
const rejectRelation = async (relation) => {
  try {
    const response = await request({
      url: `/admin/family-elderly-relations/${relation.id}/reject`,
      method: 'put'
    });

    if (response.code === 200) {
      ElMessage.success('关联申请已拒绝');
      fetchPendingRelations();
    } else {
      ElMessage.error(response.message || '操作失败');
    }
  } catch (error) {
    console.error('拒绝关联失败:', error);
    ElMessage.error('拒绝失败，请稍后重试');
  }
};

// 查看审核材料
const viewProofMaterials = (relation) => {
  console.log('查看审核材料:', relation);
  console.log('proofMaterials:', relation.proofMaterials);
  currentRelation.value = relation;
  proofMaterialsVisible.value = true;
};

// 预览图片
const previewImage = (url) => {
  previewImageUrl.value = url;
  imagePreviewVisible.value = true;
};

// 获取完整的图片URL
const getFullImageUrl = (url) => {
  if (!url) return '';
  // 如果URL已经是完整的http地址，直接返回
  if (url.startsWith('http')) {
    return url;
  }
  // 否则添加后端基础URL
  const baseUrl = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080/api';
  // 移除/api后缀，因为图片路径是相对于根目录的
  const serverUrl = baseUrl.replace('/api', '');
  return `${serverUrl}${url}`;
};

// 重置表单
const resetForm = () => {
  userForm.value = {
    id: null,
    username: '',
    realName: '',
    phone: '',
    idCard: '',
    password: '',
    roleType: '',
    gender: 'male',
    age: null,
    address: '',
    isEnabled: 1
  };
  userFormRef.value?.resetFields();
};

// 获取角色类型
const getRoleType = (role) => {
  const typeMap = { elderly: 'info', family: 'primary', caregiver: 'success', admin: 'danger' };
  return typeMap[role] || 'info';
};

// 获取角色标签
const getRoleLabel = (role) => {
  const labelMap = { elderly: '老人', family: '家属', caregiver: '护工', admin: '管理员' };
  return labelMap[role] || role;
};

// 获取关系类型文本
const getRelationTypeText = (type) => {
  const textMap = { 
    spouse: '配偶', 
    child: '子女', 
    grandchild: '孙子女', 
    sibling: '兄弟姐妹', 
    other: '其他' 
  };
  return textMap[type] || type;
};

// 格式化日期时间
const formatDateTime = (dateTime) => {
  if (!dateTime) return '--';
  try {
    // 处理不同格式的时间数据
    let date;
    if (typeof dateTime === 'string') {
      // 处理 ISO 格式的时间字符串
      if (dateTime.includes('T')) {
        date = new Date(dateTime.replace('T', ' '));
      } else {
        date = new Date(dateTime);
      }
    } else {
      date = new Date(dateTime);
    }
    
    // 检查日期是否有效
    if (isNaN(date.getTime())) {
      return dateTime;
    }
    
    return date.toLocaleString('zh-CN');
  } catch (error) {
    console.error('时间格式化错误:', error);
    return dateTime;
  }
};

onMounted(() => {
  console.log('UserManage 页面已加载');
  fetchUsers();
  fetchPendingRelations();
});
</script>

<style lang="scss" scoped>
.user-manage {
  padding: 30px;
  background-color: #f5f7fa;
  min-height: 100vh;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 30px;
}

.page-title {
  font-size: 28px;
  font-weight: bold;
  color: #303133;
  margin: 0;
}

// 统计卡片
.stats-row {
  margin-bottom: 30px;
}

.stat-card {
  border-radius: 12px;
  cursor: pointer;
  transition: all 0.3s ease;
  
  &:hover {
    transform: translateY(-5px);
    box-shadow: 0 8px 16px rgba(0, 0, 0, 0.1);
  }
}

.stat-content {
  display: flex;
  align-items: center;
  gap: 15px;
  padding: 10px;
}

.stat-icon {
  width: 50px;
  height: 50px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #ffffff;
  
  &.all { background: linear-gradient(135deg, #409eff, #64b5f6); }
  &.elderly { background: linear-gradient(135deg, #67c23a, #81c784); }
  &.family { background: linear-gradient(135deg, #e6a23c, #ffb74d); }
  &.caregiver { background: linear-gradient(135deg, #f56c6c, #e57373); }
}

.stat-info {
  flex: 1;
}

.stat-value {
  font-size: 24px;
  font-weight: bold;
  color: #303133;
  margin-bottom: 5px;
}

.stat-label {
  font-size: 14px;
  color: #909399;
}

// 用户列表
.user-list-card {
  border-radius: 12px;
  margin-bottom: 30px;
}

.list-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  flex-wrap: wrap;
  gap: 15px;
}

.filter-section {
  display: flex;
  gap: 10px;
}

.search-section {
  display: flex;
  gap: 10px;
}

.pagination-section {
  margin-top: 20px;
  display: flex;
  justify-content: center;
}

// 关联审核
.relation-card {
  border-radius: 12px;
}

.relation-header {
  display: flex;
  align-items: center;
  gap: 10px;
}

.relation-title {
  font-size: 16px;
  font-weight: bold;
  color: #303133;
}

.empty-relations {
  padding: 40px 0;
}

.proof-materials {
  padding: 20px 0;
}

.materials-list {
  display: flex;
  flex-wrap: wrap;
  gap: 20px;
}

.material-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
}

.material-name {
  font-size: 14px;
  color: #606266;
  max-width: 200px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.image-error {
  width: 200px;
  height: 200px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  background-color: #f5f7fa;
  color: #909399;
  gap: 8px;
}

.no-materials {
  padding: 40px 0;
  text-align: center;
}

@media (max-width: 768px) {
  .user-manage {
    padding: 20px;
  }
  
  .page-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 15px;
  }
  
  .list-header {
    flex-direction: column;
    align-items: flex-start;
  }
  
  .search-section {
    width: 100%;
  }
  
  .stat-content {
    flex-direction: column;
    text-align: center;
  }
}
</style>

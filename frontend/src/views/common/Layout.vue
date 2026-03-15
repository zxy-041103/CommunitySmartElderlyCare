<template>
  <div>
    <el-container class="layout-container">
      <el-aside width="200px">
        <div class="logo">智慧养老平台</div>
        <el-menu
          :default-active="activeMenu"
          router
          background-color="#304156"
          text-color="#bfcbd9"
          active-text-color="#409EFF"
        >
          <!-- 老年用户端菜单 -->
          <template v-if="userStore.roleType === 1">
            <el-menu-item index="/elderly/home">
              <el-icon><House /></el-icon>
              <span>首页</span>
            </el-menu-item>
            <el-menu-item index="/elderly/health-report">
              <el-icon><Monitor /></el-icon>
              <span>健康报告</span>
            </el-menu-item>
            <el-menu-item index="/elderly/health-monitor">
              <el-icon><Monitor /></el-icon>
              <span>健康监测</span>
            </el-menu-item>
            <el-menu-item index="/elderly/service-booking">
              <el-icon><Service /></el-icon>
              <span>服务预约</span>
            </el-menu-item>
            <el-menu-item index="/elderly/emergency">
              <el-icon><Warning /></el-icon>
              <span>紧急求助</span>
            </el-menu-item>
            <el-menu-item index="/elderly/family-contact">
              <el-icon><Phone /></el-icon>
              <span>联系家属</span>
            </el-menu-item>
          </template>

          <!-- 家属用户端菜单 -->
          <template v-else-if="userStore.roleType === 2">
            <el-menu-item index="/family/monitoring">
              <el-icon><Monitor /></el-icon>
              <span>监护中心</span>
            </el-menu-item>
            <el-menu-item index="/family/message">
              <el-icon><Message /></el-icon>
              <span>消息中心</span>
            </el-menu-item>
            <el-menu-item index="/family/add-relation">
              <el-icon><Link /></el-icon>
              <span>添加关联</span>
            </el-menu-item>
            <el-menu-item index="/health">
              <el-icon><Monitor /></el-icon>
              <span>健康监测</span>
            </el-menu-item>
            <el-menu-item index="/emergency">
              <el-icon><Warning /></el-icon>
              <span>紧急求助</span>
            </el-menu-item>
          </template>

          <!-- 护工用户端菜单 -->
          <template v-else-if="userStore.roleType === 3">
            <el-menu-item index="/caregiver/task">
              <el-icon><Check /></el-icon>
              <span>任务首页</span>
            </el-menu-item>
            <el-menu-item index="/caregiver/service-record">
              <el-icon><Document /></el-icon>
              <span>服务记录</span>
            </el-menu-item>
            <el-menu-item index="/health">
              <el-icon><Monitor /></el-icon>
              <span>健康监测</span>
            </el-menu-item>
            <el-menu-item index="/emergency">
              <el-icon><Warning /></el-icon>
              <span>紧急求助</span>
            </el-menu-item>
          </template>

          <!-- 管理员端菜单 -->
          <template v-else-if="userStore.roleType === 4">
            <el-menu-item index="/admin/dashboard">
              <el-icon><House /></el-icon>
              <span>首页</span>
            </el-menu-item>
            <el-menu-item index="/admin/data-count">
              <el-icon><DataAnalysis /></el-icon>
              <span>数据统计详情</span>
            </el-menu-item>
            <el-menu-item index="/admin/user-manage">
              <el-icon><UserFilled /></el-icon>
              <span>用户管理</span>
            </el-menu-item>
            <el-menu-item index="/admin/activity-manage">
              <el-icon><Calendar /></el-icon>
              <span>活动管理</span>
            </el-menu-item>
            <el-menu-item index="/admin/system-config">
              <el-icon><Setting /></el-icon>
              <span>系统配置</span>
            </el-menu-item>
            <el-menu-item index="/health">
              <el-icon><Monitor /></el-icon>
              <span>健康监测</span>
            </el-menu-item>
            <el-menu-item index="/emergency">
              <el-icon><Warning /></el-icon>
              <span>紧急求助</span>
            </el-menu-item>
          </template>

          <!-- 默认菜单 -->
          <template v-else>
            <el-menu-item index="/health">
              <el-icon><Monitor /></el-icon>
              <span>健康监测</span>
            </el-menu-item>
            <el-menu-item index="/emergency">
              <el-icon><Warning /></el-icon>
              <span>紧急求助</span>
            </el-menu-item>
          </template>
        </el-menu>
      </el-aside>
      <el-container>
        <el-header>
          <div class="header-left">
            <span class="page-title">{{ pageTitle }}</span>
          </div>
        </el-header>
        <el-main>
          <router-view />
        </el-main>
      </el-container>
      <div
        class="admin-account"
        style="position: fixed; bottom: 20px; left: 20px"
      >
        <el-dropdown
          @command="handleAccountCommand"
          @click="console.log('Dropdown clicked')"
        >
          <div
            class="account-icon"
            style="cursor: pointer; padding: 10px; border-radius: 4px"
          >
            <el-icon><i class="el-icon-user"></i></el-icon>
            <span>{{ userStore.userName || "管理员" }}</span>
          </div>
          <template #dropdown>
            <el-dropdown-menu>
              <el-dropdown-item command="profile">个人信息</el-dropdown-item>
              <el-dropdown-item command="logout" divided
                >退出登录</el-dropdown-item
              >
            </el-dropdown-menu>
          </template>
        </el-dropdown>
      </div>
    </el-container>

    <!-- 个人信息编辑弹窗 -->
    <el-dialog v-model="profileDialogVisible" title="个人信息" width="600px">
      <div class="profile-header">
        <div class="avatar-container">
          <div class="avatar-wrapper" @click="triggerFileInput">
            <el-avatar :size="100" :src="avatarUrl" class="avatar">
              <span
                v-if="avatarUrl === '/src/assets/images/avatar/initial.jpg'"
                >{{ editUserForm.realName?.charAt(0) || "用" }}</span
              >
            </el-avatar>
            <input
              ref="fileInput"
              type="file"
              accept="image/*"
              style="display: none"
              @change="handleFileUpload"
            />
          </div>
          <div class="avatar-actions">
            <el-button type="primary" size="small" @click="triggerFileInput">
              <el-icon><Upload /></el-icon>
              从本地上传
            </el-button>
            <el-button type="success" size="small" @click="openImageLibrary">
              <el-icon><Picture /></el-icon>
              从图片库选择
            </el-button>
          </div>
        </div>
      </div>
      <el-form
        ref="editUserFormRef"
        :model="editUserForm"
        :rules="editUserRules"
      >
        <!-- 基本信息 - 所有角色都显示 -->
        <el-form-item label="用户名">
          <el-input v-model="editUserForm.username" disabled />
        </el-form-item>
        <el-form-item label="角色类型">
          <el-input :value="getRoleName(editUserForm.roleType)" disabled />
        </el-form-item>
        <el-form-item label="真实姓名" prop="realName">
          <el-input
            v-model="editUserForm.realName"
            placeholder="请输入真实姓名"
          />
        </el-form-item>
        <el-form-item label="手机号" prop="phone">
          <el-input v-model="editUserForm.phone" placeholder="请输入手机号" />
        </el-form-item>
        <el-form-item label="身份证号">
          <el-input v-model="editUserForm.idCard" disabled />
        </el-form-item>
        <el-form-item label="性别" prop="gender">
          <el-radio-group v-model="editUserForm.gender">
            <el-radio :label="1">男</el-radio>
            <el-radio :label="0">女</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="年龄" prop="age">
          <el-input
            v-model.number="editUserForm.age"
            type="number"
            placeholder="请输入年龄"
          />
        </el-form-item>
        <el-form-item label="地址" prop="address">
          <el-input v-model="editUserForm.address" placeholder="请输入地址" />
        </el-form-item>
        <el-form-item label="账号状态">
          <el-tag :type="editUserForm.isEnabled === 1 ? 'success' : 'danger'">
            {{ editUserForm.isEnabled === 1 ? '正常' : '禁用' }}
          </el-tag>
        </el-form-item>
        <el-form-item label="注册时间">
          <el-input :value="formatDateTime(editUserForm.createTime)" disabled />
        </el-form-item>
        <el-form-item v-if="false" label="头像URL" prop="avatar">
          <el-input v-model="editUserForm.avatar" placeholder="请输入头像URL" />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="profileDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitEditUserForm">确定</el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 图片库选择弹窗 -->
    <el-dialog v-model="imageLibraryVisible" title="选择头像" width="500px">
      <div class="image-library">
        <div class="image-grid">
          <div
            v-for="(avatar, index) in defaultAvatars"
            :key="index"
            class="image-item"
            @click="selectDefaultAvatar(avatar)"
          >
            <img
              :src="`/src/assets/images/${avatar}`"
              :alt="`Avatar ${index + 1}`"
              class="avatar-image"
            />
          </div>
        </div>
      </div>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="imageLibraryVisible = false">取消</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { computed, ref } from "vue";
import { useRoute, useRouter } from "vue-router";
import { useUserStore } from "@/store/user";
import { ElMessage } from "element-plus";
import {
  House,
  Service,
  Message,
  User,
  UserFilled,
  Monitor,
  Warning,
  Calendar,
  Setting,
  DataAnalysis,
  Document,
  Upload,
  Check,
  Picture,
  Link,
  Phone,
} from "@element-plus/icons-vue";
import { getUserInfo, updateUserInfo } from "@/api/user";
import { onMounted } from "vue";

const route = useRoute();
const router = useRouter();
const userStore = useUserStore();

// 组件挂载时获取用户信息
onMounted(async () => {
  if (userStore.userId && !userStore.userInfo?.realName) {
    try {
      await userStore.getUserInfo();
    } catch (error) {
      console.error("获取用户信息失败:", error);
    }
  }
});

// 个人信息编辑弹窗
const profileDialogVisible = ref(false);
const userInfo = ref(null);
const editUserForm = ref({});
const editUserFormRef = ref(null);
const editUserRules = {
  realName: [{ required: true, message: "请输入真实姓名", trigger: "blur" }],
  phone: [{ required: true, message: "请输入手机号", trigger: "blur" }],
  gender: [{ required: true, message: "请选择性别", trigger: "change" }],
  age: [{ required: true, message: "请输入年龄", trigger: "blur" }],
};

// 头像处理
const avatarUrl = computed(() => {
  if (editUserForm.value.avatar) {
    // 检查是否是完整的URL
    if (
      editUserForm.value.avatar.startsWith("http://") ||
      editUserForm.value.avatar.startsWith("https://")
    ) {
      return editUserForm.value.avatar;
    } else {
      // 假设是相对路径，从assets/images目录加载
      return `/src/assets/images/${editUserForm.value.avatar}`;
    }
  }
  // 如果没有头像，显示默认头像
  return "/src/assets/images/avatar/initial.jpg";
});

// 图片库选择弹窗
const imageLibraryVisible = ref(false);
const defaultAvatars = ref([
  "avatar/default/avatar1.jpg",
  "avatar/default/avatar2.jpg",
  "avatar/default/avatar3.jpg",
  "avatar/default/avatar4.jpg",
  "avatar/default/avatar5.jpg",
]);

// 文件输入引用
const fileInput = ref(null);

// 触发文件输入
const triggerFileInput = () => {
  if (fileInput.value) {
    fileInput.value.click();
  }
};

// 处理文件上传
const handleFileUpload = (event) => {
  const file = event.target.files[0];
  if (file) {
    // 使用用户名作为文件名
    const username =
      editUserForm.value.username || userStore.userName || "user";
    const fileExtension = file.name.split(".").pop();
    const fileName = `avatar/${username}.${fileExtension}`;
    // 简单设置头像路径，实际项目中需要处理文件上传
    editUserForm.value.avatar = fileName;
    ElMessage.success("头像已更新");
    // 清空文件输入，以便可以重复选择同一个文件
    event.target.value = "";

    // 注意：实际项目中，这里需要将文件上传到服务器
    // 然后服务器会将文件保存到 frontend/src/assets/images/avatar 目录下
    console.log("头像文件将保存为:", fileName);
  }
};

// 打开图片库
const openImageLibrary = () => {
  imageLibraryVisible.value = true;
};

// 选择默认头像
const selectDefaultAvatar = (avatarPath) => {
  editUserForm.value.avatar = avatarPath;
  imageLibraryVisible.value = false;
  ElMessage.success("头像已更新");
};

const activeMenu = computed(() => route.path);
const pageTitle = computed(() => route.meta.title || "首页");

const handleAccountCommand = (command) => {
  console.log("handleAccountCommand被执行，command:", command);
  if (command === "logout") {
    userStore.logout();
    ElMessage.success("退出成功");
    // 使用window.location.href直接刷新到登录页面，确保页面完全重新加载
    window.location.href = "/login";
  } else if (command === "profile") {
    console.log("准备调用handleEditUserInfo函数");
    handleEditUserInfo();
  }
};

// 编辑个人信息
const handleEditUserInfo = async () => {
  try {
    console.log("开始获取个人信息");
    // 从多个来源获取用户ID
    let userId = userStore.userId;
    console.log("从userStore.userId获取:", userId);

    // 如果userId为空，尝试从userInfo中获取
    if (!userId && userStore.userInfo?.id) {
      userId = userStore.userInfo.id;
      console.log("从userStore.userInfo.id获取:", userId);
    }

    console.log("最终使用的userId:", userId);

    if (userId) {
      console.log("准备调用getUserInfo API，userId:", userId);
      try {
        console.log("调用getUserInfo前");
        // 使用getUserInfo API函数，自动处理CORS和认证
        console.log("getUserInfo函数:", getUserInfo);
        console.log("userId类型:", typeof userId);
        console.log("userId值:", userId);

        const res = await getUserInfo(userId);
        console.log("getUserInfo API返回结果:", res);
        console.log("res类型:", typeof res);
        console.log("res.data:", res.data);
        console.log("res.data类型:", typeof res.data);

        if (res && res.data) {
          console.log("res.data.phone:", res.data.phone);
          console.log("res.data.phone类型:", typeof res.data.phone);
          console.log("res.data.phone长度:", res.data.phone.length);
          userInfo.value = res.data;
          editUserForm.value = { ...res.data };
          console.log("设置到editUserForm的值:", editUserForm.value);
          console.log("手机号值:", editUserForm.value.phone);
          console.log("手机号长度:", editUserForm.value.phone.length);
          console.log(
            "手机号是否为加密格式:",
            editUserForm.value.phone.length > 11 ||
              !/^1[3-9]\d{9}$/.test(editUserForm.value.phone),
          );
          profileDialogVisible.value = true;
          console.log("个人信息获取成功，弹窗已显示");
        } else {
          console.error("获取个人信息失败：返回数据格式错误", res);
          ElMessage.error(
            `获取个人信息失败：${res?.message || "返回数据格式错误"}`,
          );
        }
      } catch (apiError) {
        console.error("调用getUserInfo API失败:", apiError);
        console.error("API错误详情:", apiError.message, apiError.response);
        ElMessage.error(`调用getUserInfo API失败：${apiError.message}`);
      }
    } else {
      console.error("获取用户信息失败：未找到用户ID，请重新登录");
      ElMessage.error("未找到用户信息，请重新登录");
    }
  } catch (error) {
    console.error("获取个人信息失败:", error);
    ElMessage.error("获取个人信息失败");
  }
};

// 提交个人信息编辑
const submitEditUserForm = async () => {
  console.log("开始提交个人信息修改");
  if (!editUserFormRef.value) {
    console.error("修改个人信息失败：表单引用不存在");
    ElMessage.error("表单初始化失败");
    return;
  }

  try {
    console.log("开始表单验证");
    await editUserFormRef.value.validate();
    console.log("表单验证成功");

    // 从多个来源获取用户ID
    let userId = userStore.userId;
    console.log("从userStore.userId获取:", userId);
    if (!userId && userStore.userInfo?.id) {
      userId = userStore.userInfo.id;
      console.log("从userStore.userInfo.id获取:", userId);
    }
    if (!userId && userInfo.value?.id) {
      userId = userInfo.value.id;
      console.log("从userInfo.value.id获取:", userId);
    }

    console.log("提交时使用的userId:", userId);

    if (!userId) {
      console.error("修改个人信息失败：未找到用户ID");
      ElMessage.error("未找到用户信息，请重新登录");
      return;
    }

    // 移除idCard字段，因为它是脱敏的，不应该被提交
    const formData = { ...editUserForm.value };
    console.log("原始formData:", formData);
    delete formData.idCard;
    console.log("删除idCard后:", formData);
    // 移除password字段，因为它是加密的，不应该被提交
    delete formData.password;
    console.log("删除password后:", formData);
    // 移除id字段，因为它是路径参数，不应该在请求体中
    delete formData.id;
    console.log("删除id后:", formData);
    // 检查手机号是否为加密格式，如果是则不提交
    if (
      formData.phone &&
      (formData.phone.length > 11 || !/^1[3-9]\d{9}$/.test(formData.phone))
    ) {
      console.log("手机号为加密格式，删除:", formData.phone);
      delete formData.phone;
    }
    console.log("最终提交的formData:", formData);

    console.log("准备调用updateUserInfo API");
    console.log("updateUserInfo函数:", updateUserInfo);
    const res = await updateUserInfo(userId, formData);
    console.log("updateUserInfo API返回结果:", res);

    userInfo.value = res.data;
    userStore.userInfo = res.data;
    profileDialogVisible.value = false;
    ElMessage.success("个人信息修改成功");
  } catch (error) {
    console.error("修改个人信息失败:", error);
    console.error("错误详情:", error.message, error.response);
    // 显示更详细的错误信息
    const errorMessage =
      error.message || error.response?.data?.message || "修改个人信息失败";
    ElMessage.error(`修改个人信息失败: ${errorMessage}`);
  }
};

// 获取角色名称
const getRoleName = (roleType) => {
  const roleMap = {
    'elderly': '老人',
    'family': '家属',
    'caregiver': '护工',
    'admin': '管理员'
  };
  return roleMap[roleType] || roleType;
};

// 格式化日期时间
const formatDateTime = (dateTime) => {
  if (!dateTime) return '-';
  const date = new Date(dateTime);
  return date.toLocaleString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit',
    second: '2-digit'
  });
};
</script>

<style lang="scss" scoped>
.layout-container {
  height: 100vh;
}

.el-aside {
  background-color: #304156;
  color: #fff;

  .logo {
    height: 60px;
    line-height: 60px;
    text-align: center;
    font-size: 18px;
    font-weight: bold;
    color: #fff;
    border-bottom: 1px solid #1f2d3d;
  }
}

.el-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  background-color: #fff;
  box-shadow: 0 1px 4px rgba(0, 21, 41, 0.08);

  .header-left {
    .page-title {
      font-size: 18px;
      font-weight: bold;
    }
  }

  .header-right {
    display: flex;
    align-items: center;
    gap: 20px;

    .user-info {
      display: flex;
      align-items: center;
      cursor: pointer;
      font-size: 14px;
    }
  }
}

.el-main {
  background-color: #f0f2f5;
  padding: 20px;
}

.admin-account {
  .account-icon {
    display: flex;
    align-items: center;
    padding: 10px 15px;
    background: rgba(255, 255, 255, 0.9);
    border-radius: 20px;
    box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
    cursor: pointer;
    transition: all 0.3s ease;

    &:hover {
      background: rgba(255, 255, 255, 1);
      box-shadow: 0 4px 16px 0 rgba(0, 0, 0, 0.15);
    }

    span {
      margin-left: 8px;
      font-size: 14px;
      font-weight: 500;
    }
  }
}

.profile-header {
  display: flex;
  justify-content: center;
  margin-bottom: 30px;
  padding: 20px;
  background-color: #f9f9f9;
  border-radius: 8px;
}

.avatar-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 15px;
}

.avatar {
  border: 3px solid #fff;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
  transition: all 0.3s ease;
  cursor: pointer;

  &:hover {
    transform: scale(1.05);
  }
}

.avatar-wrapper {
  position: relative;
  cursor: pointer;
}

.avatar-actions {
  display: flex;
  gap: 10px;
  margin-top: 15px;
}

/* 图片库样式 */
.image-library {
  padding: 20px 0;
}

.image-grid {
  display: grid;
  grid-template-columns: repeat(5, 1fr);
  gap: 15px;
}

.image-item {
  border: 2px solid #e0e0e0;
  border-radius: 8px;
  padding: 5px;
  cursor: pointer;
  transition: all 0.3s ease;

  &:hover {
    border-color: #409eff;
    box-shadow: 0 2px 12px 0 rgba(64, 158, 255, 0.3);
  }
}

.avatar-image {
  width: 100%;
  height: 80px;
  object-fit: cover;
  border-radius: 4px;
}
</style>

<template>
  <div class="login-container">
    <div class="login-background"></div>
    <div class="login-overlay"></div>
    <h1 class="login-title">社区智慧养老监护管理平台</h1>
    <p class="login-subtitle">专业的养老服务管理系统</p>
    <el-card class="login-card">
      <el-form
        ref="loginFormRef"
        :model="loginForm"
        :rules="rules"
        class="login-form"
      >
        <el-form-item prop="username">
          <el-input
            v-model="loginForm.username"
            placeholder="请输入用户名"
            prefix-icon="User"
            size="large"
            @keyup.enter="handleLogin"
          />
        </el-form-item>
        <el-form-item prop="password">
          <el-input
            v-model="loginForm.password"
            type="password"
            placeholder="请输入密码"
            prefix-icon="Lock"
            size="large"
            show-password
            @keyup.enter="handleLogin"
          />
        </el-form-item>

        <el-form-item prop="captcha">
          <el-row :gutter="12">
            <el-col :span="16">
              <el-input
                v-model="loginForm.captcha"
                placeholder="请输入验证码"
                prefix-icon="Picture"
                size="large"
                @keyup.enter="handleLogin"
              />
            </el-col>
            <el-col :span="8">
              <div class="captcha-container">
                <img
                  :src="
                    captchaImage ? 'data:image/png;base64,' + captchaImage : ''
                  "
                  class="captcha-image"
                  alt="验证码"
                  @click="refreshCaptcha"
                />
              </div>
            </el-col>
          </el-row>
          <div class="forgot-password">
            忘记密码请联系管理员
            <span class="admin-email">2073793528@qq.com</span>
          </div>
        </el-form-item>
        <el-form-item>
          <el-button
            type="primary"
            size="large"
            style="width: 100%"
            @click="handleLogin"
            >登录</el-button
          >
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from "vue";
import { useRouter } from "vue-router";
import { useUserStore } from "@/store/user";
import { getCaptcha } from "@/api/user";
import { ElMessage } from "element-plus";

const router = useRouter();
const userStore = useUserStore();
const loginFormRef = ref();
const captchaImage = ref("");

const loginForm = reactive({
  username: "",
  password: "",
  captcha: "",
  captchaId: "",
});

const rules = {
  username: [{ required: true, message: "请输入用户名", trigger: "blur" }],
  password: [{ required: true, message: "请输入密码", trigger: "blur" }],
  captcha: [{ required: true, message: "请输入验证码", trigger: "blur" }],
};

const loadCaptcha = async () => {
  try {
    console.log("开始加载验证码...");
    const response = await getCaptcha();
    console.log("验证码请求响应:", response);
    // 由于响应拦截器已经处理了code=200的情况，直接使用response
    // 注意：后端返回的image和captchaId在data对象内部
    if (response && response.data) {
      console.log("验证码数据:", response.data);
      console.log("验证码ID:", response.data.captchaId);
      console.log(
        "验证码图片长度:",
        response.data.image ? response.data.image.length : 0,
      );
      loginForm.captchaId = response.data.captchaId;
      captchaImage.value = response.data.image;
      console.log("验证码加载成功");
    } else {
      console.error("验证码响应数据格式错误:", response);
      ElMessage.error("获取验证码失败");
    }
  } catch (error) {
    console.error("Failed to get captcha:", error);
    ElMessage.error("获取验证码失败");
  }
};

const refreshCaptcha = () => {
  loadCaptcha();
};

const handleLogin = async () => {
  await loginFormRef.value.validate();
  try {
    await userStore.login(loginForm);
    ElMessage.success("登录成功");
    // 根据用户角色跳转到对应的首页
    switch (userStore.roleType) {
      case 1: // 老人
        router.push("elderly/home");
        break;
      case 2: // 家属
        router.push("family/monitoring");
        break;
      case 3: // 护工
        router.push("caregiver/task");
        break;
      case 4: // 管理员
        router.push("admin/dashboard");
        break;
      default:
        router.push("health");
    }
  } catch (error) {
    console.error("Login failed:", error);
    // 登录失败后刷新验证码
    refreshCaptcha();
  }
};

// 组件挂载时加载验证码
onMounted(() => {
  loadCaptcha();
});
</script>

<style lang="scss" scoped>
.login-container {
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  padding: 60px 20px;
  position: relative;
  overflow: hidden;
}

.login-background {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-size: cover;
  background-position: center;
  background-image: url("@/assets/images/login-bg.jpg");
  z-index: 1;
}

.login-overlay {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(0, 0, 0, 0.4);
  z-index: 2;
}

.login-title {
  text-align: center;
  margin-bottom: 8px;
  color: #ffffff;
  font-size: 36px;
  font-weight: bold;
  letter-spacing: 2px;
  z-index: 3;
  text-shadow: 0 2px 4px rgba(0, 0, 0, 0.5);
}

.login-subtitle {
  text-align: center;
  margin-bottom: 7px;
  color: #f0f0f0;
  font-size: 16px;
  z-index: 3;
  text-shadow: 0 1px 2px rgba(0, 0, 0, 0.5);
}

:deep(.el-form-item__label) {
  color: rgba(255, 255, 255, 0.9);
  font-size: 14px;
  font-weight: 500;
}

:deep(.el-select .el-input__wrapper) {
  background: rgba(255, 255, 255, 0.2) !important;
  border: 1px solid rgba(255, 255, 255, 0.3);

  &:hover {
    border-color: rgba(255, 255, 255, 0.5) !important;
  }

  &.is-focus {
    border-color: rgba(255, 255, 255, 0.8) !important;
    box-shadow: 0 0 0 2px rgba(255, 255, 255, 0.2) !important;
  }
}

:deep(.el-select .el-input__inner) {
  color: #ffffff;

  &::placeholder {
    color: rgba(255, 255, 255, 0.7);
  }
}

:deep(.el-select__caret) {
  color: rgba(255, 255, 255, 0.8);
}

.login-card {
  width: 320px;
  padding: 1px 18px;
  background: rgba(255, 255, 255, 0.2);
  border-radius: 12px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.2);
  backdrop-filter: blur(5px);
  z-index: 3;
  transition: all 0.3s ease;
  border: 1px solid rgba(255, 255, 255, 0.3);

  &:hover {
    box-shadow: 0 12px 48px rgba(0, 0, 0, 0.3);
    background: rgba(255, 255, 255, 0.25);
  }
}

.login-form {
  margin-top: 15px;
}

:deep(.el-input__wrapper) {
  height: 48px;
  border-radius: 8px;
  background: rgba(255, 255, 255, 0.2) !important;
  border: 1px solid rgba(255, 255, 255, 0.3);

  &:hover {
    border-color: rgba(255, 255, 255, 0.5) !important;
  }

  &.is-focus {
    border-color: rgba(255, 255, 255, 0.8) !important;
    box-shadow: 0 0 0 2px rgba(255, 255, 255, 0.2) !important;
  }
}

:deep(.el-input__inner) {
  font-size: 16px;
  color: #ffffff;

  &::placeholder {
    color: rgba(255, 255, 255, 0.7);
  }
}

:deep(.el-input__prefix-inner) {
  color: rgba(255, 255, 255, 0.9);
}

:deep(.el-input__suffix-inner) {
  color: rgba(255, 255, 255, 0.9);
}

:deep(.el-button--primary) {
  height: 48px;
  font-size: 16px;
  border-radius: 8px;
  background: rgba(255, 255, 255, 0.3);
  border: 1px solid rgba(255, 255, 255, 0.5);
  color: #ffffff;
  transition: all 0.3s ease;

  &:hover {
    background: rgba(255, 255, 255, 0.4);
    transform: translateY(-2px);
    box-shadow: 0 4px 16px rgba(255, 255, 255, 0.3);
    border-color: rgba(255, 255, 255, 0.7);
  }
}

.captcha-container {
  display: flex;
  align-items: center;
  height: 48px;
}

.captcha-image {
  width: 100%;
  height: 100%;
  cursor: pointer;
  border-radius: 8px;
  transition: all 0.3s ease;

  &:hover {
    transform: scale(1.02);
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  }
}

.forgot-password {
  text-align: right;
  margin-top: 8px;
  font-size: 12px;
  color: rgba(255, 255, 255, 0.7);
  position: relative;
  cursor: default;

  .admin-email {
    display: none;
    position: absolute;
    right: 0;
    top: 100%;
    margin-top: 5px;
    padding: 8px 12px;
    background: rgba(0, 0, 0, 0.8);
    color: #ffffff;
    border-radius: 4px;
    font-size: 12px;
    white-space: nowrap;
    z-index: 1000;

    &::before {
      content: "";
      position: absolute;
      right: 10px;
      top: -5px;
      border-width: 0 5px 5px 5px;
      border-style: solid;
      border-color: transparent transparent rgba(0, 0, 0, 0.8) transparent;
    }
  }

  &:hover {
    color: rgba(255, 255, 255, 0.9);

    .admin-email {
      display: block;
    }
  }
}
</style>

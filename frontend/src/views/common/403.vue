<template>
  <div class="error-403">
    <div class="error-container">
      <div class="error-icon">
        <el-icon :size="80"><Warning /></el-icon>
      </div>
      <h1 class="error-title">403</h1>
      <p class="error-message">您没有权限访问此页面</p>
      <el-button type="primary" size="large" @click="goHome">
        <el-icon><House /></el-icon>
        返回首页
      </el-button>
    </div>
  </div>
</template>

<script setup>
import { House, Warning } from "@element-plus/icons-vue";
import { useRouter } from "vue-router";

const router = useRouter();

const goHome = () => {
  // 根据角色跳转到对应的首页
  const roleType = localStorage.getItem("roleType");
  if (roleType === "1") {
    router.push("/elderly/home");
  } else if (roleType === "2") {
    router.push("/family/monitoring");
  } else if (roleType === "3") {
    router.push("/caregiver/task");
  } else if (roleType === "4") {
    router.push("/admin/dashboard");
  } else {
    router.push("/login");
  }
};
</script>

<style lang="scss" scoped>
.error-403 {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  background-color: #f5f7fa;
}

.error-container {
  text-align: center;
  padding: 60px 40px;
  background-color: #ffffff;
  border-radius: 16px;
  box-shadow: 0 10px 40px rgba(0, 0, 0, 0.1);
  max-width: 500px;
  width: 90%;
  transition: all 0.3s ease;

  &:hover {
    box-shadow: 0 15px 60px rgba(0, 0, 0, 0.15);
    transform: translateY(-5px);
  }
}

.error-icon {
  color: #e6a23c;
  margin-bottom: 30px;
  animation: pulse 2s infinite;
}

.error-title {
  font-size: 72px;
  font-weight: bold;
  color: #303133;
  margin: 0 0 20px;
  line-height: 1;
}

.error-message {
  font-size: 20px;
  color: #606266;
  margin: 0 0 40px;
  line-height: 1.5;
}

@keyframes pulse {
  0% {
    transform: scale(1);
  }
  50% {
    transform: scale(1.05);
  }
  100% {
    transform: scale(1);
  }
}
</style>

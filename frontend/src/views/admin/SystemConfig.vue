<template>
  <div class="system-config">
    <el-card class="config-card">
      <template #header>
        <div class="card-header">
          <h2 class="page-title">系统配置</h2>
        </div>
      </template>

      <!-- 健康预警阈值配置 -->
      <div class="config-section">
        <h3 class="section-title">
          <el-icon :size="20"><Warning /></el-icon>
          健康预警阈值配置
        </h3>
        <el-form
          ref="healthFormRef"
          :model="healthConfig"
          :rules="healthRules"
          label-width="200px"
        >
          <el-row :gutter="20">
            <el-col :span="8">
              <el-form-item label="血压上限 (mmHg)" prop="bloodPressureMax">
                <el-input-number
                  v-model="healthConfig.bloodPressureMax"
                  :min="100"
                  :max="200"
                  :step="1"
                  size="large"
                  style="width: 100%"
                />
              </el-form-item>
            </el-col>
            <el-col :span="8">
              <el-form-item label="血压下限 (mmHg)" prop="bloodPressureMin">
                <el-input-number
                  v-model="healthConfig.bloodPressureMin"
                  :min="60"
                  :max="100"
                  :step="1"
                  size="large"
                  style="width: 100%"
                />
              </el-form-item>
            </el-col>
            <el-col :span="8">
              <el-form-item label="血糖上限 (mmol/L)" prop="bloodSugarMax">
                <el-input-number
                  v-model="healthConfig.bloodSugarMax"
                  :min="4"
                  :max="10"
                  :step="0.1"
                  size="large"
                  style="width: 100%"
                />
              </el-form-item>
            </el-col>
          </el-row>
          <el-row :gutter="20">
            <el-col :span="8">
              <el-form-item label="血糖下限 (mmol/L)" prop="bloodSugarMin">
                <el-input-number
                  v-model="healthConfig.bloodSugarMin"
                  :min="3"
                  :max="6"
                  :step="0.1"
                  size="large"
                  style="width: 100%"
                />
              </el-form-item>
            </el-col>
            <el-col :span="8">
              <el-form-item label="心率上限 (bpm)" prop="heartRateMax">
                <el-input-number
                  v-model="healthConfig.heartRateMax"
                  :min="60"
                  :max="150"
                  :step="1"
                  size="large"
                  style="width: 100%"
                />
              </el-form-item>
            </el-col>
            <el-col :span="8">
              <el-form-item label="心率下限 (bpm)" prop="heartRateMin">
                <el-input-number
                  v-model="healthConfig.heartRateMin"
                  :min="40"
                  :max="80"
                  :step="1"
                  size="large"
                  style="width: 100%"
                />
              </el-form-item>
            </el-col>
          </el-row>
          <el-form-item>
            <el-button type="primary" size="large" @click="saveHealthConfig">
              <el-icon><Check /></el-icon>
              保存健康配置
            </el-button>
          </el-form-item>
        </el-form>
      </div>

      <!-- 数据备份 -->
      <div class="config-section">
        <h3 class="section-title">
          <el-icon :size="20"><Document /></el-icon>
          数据备份
        </h3>
        <div class="backup-section">
          <el-card class="backup-card">
            <div class="backup-content">
              <div class="backup-info">
                <h4 class="backup-title">自动备份设置</h4>
                <p class="backup-desc">系统默认每天凌晨 2 点自动备份数据</p>
                <el-form :model="backupConfig" label-width="120px">
                  <el-form-item label="备份频率">
                    <el-select
                      v-model="backupConfig.frequency"
                      placeholder="选择备份频率"
                      size="large"
                    >
                      <el-option label="每天" value="daily" />
                      <el-option label="每周" value="weekly" />
                      <el-option label="每月" value="monthly" />
                    </el-select>
                  </el-form-item>
                  <el-form-item label="备份时间">
                    <el-time-picker
                      v-model="backupConfig.time"
                      placeholder="选择时间"
                      format="HH:mm"
                      value-format="HH:mm"
                      size="large"
                      style="width: 100%"
                    />
                  </el-form-item>
                  <el-form-item>
                    <el-button type="primary" @click="saveBackupConfig">
                      <el-icon><Check /></el-icon>
                      保存备份配置
                    </el-button>
                  </el-form-item>
                </el-form>
              </div>
              <div class="backup-actions">
                <el-button type="primary" size="large" @click="handleManualBackup">
                  <el-icon><Download /></el-icon>
                  手动备份
                </el-button>
              </div>
            </div>
          </el-card>
        </div>
      </div>

      <!-- 语音关键词维护 -->
      <div class="config-section">
        <h3 class="section-title">
          <el-icon :size="20"><Mic /></el-icon>
          语音关键词维护
        </h3>
        <div class="voice-section">
          <el-card class="voice-card">
            <div class="voice-content">
              <div class="voice-input">
                <el-input
                  v-model="newKeyword"
                  placeholder="请输入新的语音关键词"
                  size="large"
                  @keyup.enter="addKeyword"
                >
                  <template #append>
                    <el-button type="primary" @click="addKeyword">
                      <el-icon><Plus /></el-icon>
                      添加
                    </el-button>
                  </template>
                </el-input>
              </div>
              <div class="keyword-list">
                <el-tag
                  v-for="keyword in voiceKeywords"
                  :key="keyword.id"
                  closable
                  class="keyword-tag"
                  @close="removeKeyword(keyword.id)"
                >
                  {{ keyword.word }}
                </el-tag>
              </div>
              <div class="voice-tips">
                <h4>语音关键词说明：</h4>
                <ul>
                  <li>系统会根据语音关键词触发相应的功能</li>
                  <li>例如："打开健康报告"、"我要紧急求助"等</li>
                  <li>关键词应简洁明了，便于老人发音</li>
                </ul>
              </div>
            </div>
          </el-card>
        </div>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from "vue";
import {
  Warning,
  Document,
  Mic,
  Check,
  Download,
  Upload,
  Plus,
} from "@element-plus/icons-vue";
import { ElMessage } from "element-plus";
import {
  getBackupConfig,
  updateBackupConfig,
  manualBackup as apiManualBackup,
  getVoiceKeywords,
  addVoiceKeyword,
  removeVoiceKeyword,
} from "@/api/systemConfig";
import {
  getCurrentWarningThresholds,
  configureWarningThreshold,
} from "@/api/healthData";

// 页面名称
defineOptions({
  name: 'AdminSystemConfig'
});

onMounted(() => {
  loadHealthConfig();
  loadBackupConfig();
  loadVoiceKeywords();
});

const healthFormRef = ref(null);

const healthConfig = reactive({
  bloodPressureMax: 140,
  bloodPressureMin: 90,
  bloodSugarMax: 7.0,
  bloodSugarMin: 3.9,
  heartRateMax: 90,
  heartRateMin: 60,
});

const healthRules = {
  bloodPressureMax: [
    { required: true, message: "请输入血压上限", trigger: "blur" },
  ],
  bloodPressureMin: [
    { required: true, message: "请输入血压下限", trigger: "blur" },
  ],
  bloodSugarMax: [
    { required: true, message: "请输入血糖上限", trigger: "blur" },
  ],
  bloodSugarMin: [
    { required: true, message: "请输入血糖下限", trigger: "blur" },
  ],
  heartRateMax: [
    { required: true, message: "请输入心率上限", trigger: "blur" },
  ],
  heartRateMin: [
    { required: true, message: "请输入心率下限", trigger: "blur" },
  ],
};

const backupConfig = reactive({
  id: null,
  frequency: "daily",
  time: "02:00",
});

const voiceKeywords = ref([]);
const newKeyword = ref("");

const loadHealthConfig = async () => {
  try {
    const res = await getCurrentWarningThresholds();
    if (res.code === 200 && res.data) {
      res.data.forEach((item) => {
        if (item.indicatorType === "bloodPressureSystolic") {
          healthConfig.bloodPressureMax = item.maxValue;
        } else if (item.indicatorType === "bloodPressureDiastolic") {
          healthConfig.bloodPressureMin = item.minValue;
        } else if (item.indicatorType === "bloodSugar") {
          healthConfig.bloodSugarMax = item.maxValue;
          healthConfig.bloodSugarMin = item.minValue;
        } else if (item.indicatorType === "heartRate") {
          healthConfig.heartRateMax = item.maxValue;
          healthConfig.heartRateMin = item.minValue;
        }
      });
    }
  } catch (error) {
    console.error("加载健康配置失败:", error);
  }
};

const saveHealthConfig = async () => {
  if (!healthFormRef.value) return;

  try {
    await healthFormRef.value.validate();
    const requests = [
      {
        indicatorType: "bloodPressureSystolic",
        minValue: 0,
        maxValue: healthConfig.bloodPressureMax,
      },
      {
        indicatorType: "bloodPressureDiastolic",
        minValue: healthConfig.bloodPressureMin,
        maxValue: 0,
      },
      {
        indicatorType: "bloodSugar",
        minValue: healthConfig.bloodSugarMin,
        maxValue: healthConfig.bloodSugarMax,
      },
      {
        indicatorType: "heartRate",
        minValue: healthConfig.heartRateMin,
        maxValue: healthConfig.heartRateMax,
      },
    ];

    for (const req of requests) {
      await configureWarningThreshold(req);
    }

    ElMessage.success("健康预警阈值配置已保存");
  } catch (error) {
    console.error("保存健康配置失败:", error);
    ElMessage.error("保存健康配置失败");
  }
};

const loadBackupConfig = async () => {
  try {
    const res = await getBackupConfig();
    if (res.code === 200 && res.data) {
      backupConfig.id = res.data.id;
      backupConfig.frequency = res.data.backupFrequency;
      backupConfig.time = res.data.backupTime;
    }
  } catch (error) {
    console.error("加载备份配置失败:", error);
  }
};

const saveBackupConfig = async () => {
  try {
    const res = await updateBackupConfig({
      id: backupConfig.id,
      backupFrequency: backupConfig.frequency,
      backupTime: backupConfig.time,
    });
    if (res.code === 200) {
      ElMessage.success("备份配置已保存");
    } else {
      ElMessage.error(res.msg || "备份配置保存失败");
    }
  } catch (error) {
    console.error("保存备份配置失败:", error);
    ElMessage.error("保存备份配置失败");
  }
};

const handleManualBackup = async () => {
  try {
    const res = await apiManualBackup();
    if (res.code === 200) {
      ElMessage.success("开始手动备份数据，请稍候...");
      setTimeout(() => {
        ElMessage.success("数据备份成功");
      }, 2000);
    } else {
      ElMessage.error(res.msg || "手动备份失败");
    }
  } catch (error) {
    console.error("手动备份失败:", error);
    ElMessage.error("手动备份失败");
  }
};

const loadVoiceKeywords = async () => {
  try {
    const res = await getVoiceKeywords();
    if (res.code === 200 && res.data) {
      voiceKeywords.value = res.data.map((item) => ({
        id: item.id,
        word: item.keyword,
      }));
    }
  } catch (error) {
    console.error("加载语音关键词失败:", error);
  }
};

const addKeyword = async () => {
  if (!newKeyword.value.trim()) {
    ElMessage.error("请输入关键词");
    return;
  }

  if (
    voiceKeywords.value.some(
      (keyword) => keyword.word === newKeyword.value.trim(),
    )
  ) {
    ElMessage.error("关键词已存在");
    return;
  }

  try {
    const res = await addVoiceKeyword({
      keyword: newKeyword.value.trim(),
      actionType: "other",
      sort: voiceKeywords.value.length,
      isEnabled: 1,
    });

    if (res.code === 200) {
      ElMessage.success("关键词添加成功");
      newKeyword.value = "";
      await loadVoiceKeywords();
    } else {
      ElMessage.error(res.msg || "关键词添加失败");
    }
  } catch (error) {
    console.error("添加关键词失败:", error);
    ElMessage.error("关键词添加失败");
  }
};

const removeKeyword = async (id) => {
  try {
    const res = await removeVoiceKeyword(id);
    if (res.code === 200) {
      ElMessage.success("关键词已删除");
      await loadVoiceKeywords();
    } else {
      ElMessage.error(res.msg || "关键词删除失败");
    }
  } catch (error) {
    console.error("删除关键词失败:", error);
    ElMessage.error("关键词删除失败");
  }
};
</script>

<style lang="scss" scoped>
.system-config {
  padding: 30px;
  background-color: #f5f7fa;
  min-height: 100vh;
}

.config-card {
  border-radius: 16px;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.1);
  overflow: hidden;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.page-title {
  font-size: 28px;
  font-weight: bold;
  color: #303133;
  margin: 0;
}

/* 配置 section */
.config-section {
  margin-bottom: 40px;
  padding-bottom: 30px;
  border-bottom: 1px solid #e4e7ed;

  &:last-child {
    border-bottom: none;
    margin-bottom: 0;
    padding-bottom: 0;
  }
}

.section-title {
  font-size: 20px;
  font-weight: bold;
  color: #303133;
  margin: 0 0 25px;
  display: flex;
  align-items: center;
  gap: 10px;
}

/* 健康预警阈值配置 */
.health-form {
  max-width: 800px;
}

/* 数据备份 */
.backup-section {
  margin-top: 20px;
}

.backup-card {
  border-radius: 12px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.backup-content {
  display: grid;
  grid-template-columns: 2fr 1fr;
  gap: 40px;
  padding: 20px;
}

.backup-info {
  flex: 1;
}

.backup-title {
  font-size: 18px;
  font-weight: bold;
  color: #303133;
  margin: 0 0 10px;
}

.backup-desc {
  font-size: 14px;
  color: #606266;
  margin: 0 0 20px;
}

.backup-actions {
  display: flex;
  flex-direction: column;
  gap: 20px;
  justify-content: center;
}

/* 语音关键词维护 */
.voice-section {
  margin-top: 20px;
}

.voice-card {
  border-radius: 12px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.voice-content {
  padding: 20px;
}

.voice-input {
  margin-bottom: 30px;
}

.keyword-list {
  display: flex;
  flex-wrap: wrap;
  gap: 15px;
  margin-bottom: 30px;
}

.keyword-tag {
  font-size: 14px;
  padding: 8px 16px;
  border-radius: 20px;
  cursor: pointer;
  transition: all 0.3s ease;

  &:hover {
    transform: translateY(-2px);
  }
}

.voice-tips {
  padding: 20px;
  background-color: #f9fafc;
  border-radius: 8px;
  margin-top: 20px;
}

.voice-tips h4 {
  font-size: 16px;
  font-weight: bold;
  color: #303133;
  margin: 0 0 15px;
}

.voice-tips ul {
  padding-left: 20px;
}

.voice-tips li {
  font-size: 14px;
  color: #606266;
  margin-bottom: 8px;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .system-config {
    padding: 20px;
  }

  .page-title {
    font-size: 24px;
  }

  .backup-content {
    grid-template-columns: 1fr;
    gap: 20px;
  }

  .backup-actions {
    flex-direction: row;
    justify-content: flex-start;
  }
}
</style>

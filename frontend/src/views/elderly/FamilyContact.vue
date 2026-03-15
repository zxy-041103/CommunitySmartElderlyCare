<template>
  <div class="family-contact-page">
    <h1 class="page-title">联系家属</h1>
    
    <el-tabs v-model="activeTab" type="border-card" class="contact-tabs">
      <!-- 拨打电话标签页 -->
      <el-tab-pane label="拨打电话" name="phone">
        <div class="phone-tab-content">
          <h3 class="section-title">选择要联系的家属</h3>
          <div v-if="familyMembers.length === 0" class="empty-state">
            <el-empty description="暂无家属信息" />
          </div>
          <div v-else class="family-list">
            <div
              v-for="family in familyMembers"
              :key="family.id"
              class="family-card"
              @click="selectFamily(family)"
            >
              <div class="family-avatar">{{ family.familyName ? family.familyName.charAt(0) : '家' }}</div>
              <div class="family-info">
                <div class="family-name">{{ family.familyName }}</div>
                <div class="family-relation">{{ family.relationName }}</div>
                <div class="family-phone">{{ family.phone }}</div>
              </div>
              <el-button type="primary" size="large" class="call-btn" @click.stop="callFamily(family)">
                <el-icon><Phone /></el-icon>
                拨打电话
              </el-button>
            </div>
          </div>
        </div>
      </el-tab-pane>
      
      <!-- 短信聊天标签页 -->
      <el-tab-pane label="短信聊天" name="chat">
        <div class="chat-tab-content">
          <!-- 选择家属 -->
          <div v-if="!selectedChatFamily" class="select-family-section">
            <h3 class="section-title">选择要聊天的家属</h3>
            <div v-if="familyMembers.length === 0" class="empty-state">
              <el-empty description="暂无家属信息" />
            </div>
            <div v-else class="family-list">
              <div
                v-for="family in familyMembers"
                :key="family.id"
                class="family-card"
                @click="startChat(family)"
              >
                <div class="family-avatar">{{ family.familyName ? family.familyName.charAt(0) : '家' }}</div>
                <div class="family-info">
                  <div class="family-name">{{ family.familyName }}</div>
                  <div class="family-relation">{{ family.relationName }}</div>
                </div>
                <el-button type="primary" size="large" class="chat-btn">
                  <el-icon><ChatDotRound /></el-icon>
                  开始聊天
                </el-button>
              </div>
            </div>
          </div>
          
          <!-- 聊天界面 -->
          <div v-else class="chat-interface">
            <!-- 聊天头部 -->
            <div class="chat-header">
              <el-button size="large" @click="backToFamilyList">
                <el-icon><ArrowLeft /></el-icon>
                返回
              </el-button>
              <div class="chat-family-info">
                <div class="family-avatar-small">{{ selectedChatFamily.familyName ? selectedChatFamily.familyName.charAt(0) : '家' }}</div>
                <div class="family-name">{{ selectedChatFamily.familyName }}</div>
              </div>
              <el-button size="large" type="danger" @click="clearChatHistory">
                <el-icon><Delete /></el-icon>
                清空记录
              </el-button>
            </div>
            
            <!-- 聊天记录区域 -->
            <div ref="chatContainer" class="chat-messages">
              <div v-if="chatMessages.length === 0" class="empty-chat">
                <el-empty description="暂无聊天记录，开始和家人聊天吧" />
              </div>
              <div
                v-for="message in chatMessages"
                :key="message.id"
                class="message-item"
                :class="{ 'message-self': message.senderType === 'elderly' }"
              >
                <div class="message-avatar">{{ message.senderType === 'elderly' ? '我' : selectedChatFamily.familyName.charAt(0) }}</div>
                <div class="message-content">
                  <div class="message-text">{{ message.content }}</div>
                  <div class="message-time">{{ formatTime(message.sendTime) }}</div>
                </div>
              </div>
            </div>
            
            <!-- 快捷话术按钮 -->
            <div class="quick-phrases-section">
              <el-button size="large" type="info" class="quick-phrases-btn" @click="showQuickPhrases = true">
                <el-icon><ChatLineRound /></el-icon>
                快捷话术
              </el-button>
            </div>
            
            <!-- 输入区域 -->
            <div class="chat-input-area">
              <el-input
                v-model="messageInput"
                type="textarea"
                :rows="2"
                placeholder="输入消息..."
                size="large"
                class="message-input"
                @keyup.enter.ctrl="sendMessage"
              />
              <div class="input-actions">
                <el-button
                  size="large"
                  :type="isVoiceRecording ? 'danger' : 'info'"
                  class="voice-input-btn"
                  @click="toggleVoiceInput"
                >
                  <el-icon><Microphone /></el-icon>
                  {{ isVoiceRecording ? '录音中...' : '语音输入' }}
                </el-button>
                <el-button type="primary" size="large" class="send-btn" @click="sendMessage">
                  <el-icon><Promotion /></el-icon>
                  发送
                </el-button>
              </div>
            </div>
          </div>
        </div>
      </el-tab-pane>
    </el-tabs>
    
    <!-- 快捷话术弹窗 -->
    <el-dialog
      v-model="showQuickPhrases"
      title="选择快捷话术"
      width="80%"
      center
    >
      <div class="quick-phrases-dialog">
        <el-collapse v-model="activePhraseCategory">
          <el-collapse-item title="日常报平安" name="daily">
            <div class="phrase-list">
              <el-button
                v-for="(phrase, index) in quickPhrases.daily"
                :key="index"
                size="large"
                class="phrase-btn"
                @click="selectPhrase(phrase)"
              >
                {{ phrase }}
              </el-button>
            </div>
          </el-collapse-item>
          <el-collapse-item title="身体不适" name="sick">
            <div class="phrase-list">
              <el-button
                v-for="(phrase, index) in quickPhrases.sick"
                :key="index"
                size="large"
                class="phrase-btn"
                @click="selectPhrase(phrase)"
              >
                {{ phrase }}
              </el-button>
            </div>
          </el-collapse-item>
          <el-collapse-item title="生活需求" name="need">
            <div class="phrase-list">
              <el-button
                v-for="(phrase, index) in quickPhrases.need"
                :key="index"
                size="large"
                class="phrase-btn"
                @click="selectPhrase(phrase)"
              >
                {{ phrase }}
              </el-button>
            </div>
          </el-collapse-item>
          <el-collapse-item title="情感关怀" name="emotion">
            <div class="phrase-list">
              <el-button
                v-for="(phrase, index) in quickPhrases.emotion"
                :key="index"
                size="large"
                class="phrase-btn"
                @click="selectPhrase(phrase)"
              >
                {{ phrase }}
              </el-button>
            </div>
          </el-collapse-item>
          <el-collapse-item title="紧急情况" name="emergency">
            <div class="phrase-list">
              <el-button
                v-for="(phrase, index) in quickPhrases.emergency"
                :key="index"
                size="large"
                type="danger"
                class="phrase-btn"
                @click="selectPhrase(phrase)"
              >
                {{ phrase }}
              </el-button>
            </div>
          </el-collapse-item>
        </el-collapse>
      </div>
      <template #footer>
        <span class="dialog-footer">
          <el-button size="large" @click="showQuickPhrases = false">关闭</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { defineOptions, ref, onMounted, onUnmounted, nextTick } from "vue";
import { ElMessage, ElMessageBox } from "element-plus";
import {
  Phone,
  ChatDotRound,
  ArrowLeft,
  Delete,
  Microphone,
  Promotion,
  ChatLineRound,
} from "@element-plus/icons-vue";
import { getFamilyMembers } from "@/api/elderly/home";
import { getChatHistory, markAsRead, clearChatHistory, getAllQuickPhrases } from "@/api/elderly/chat";
import voiceAssistant from "@/utils/voiceAssistant";

// 页面名称
defineOptions({
  name: 'FamilyContact'
});

const activeTab = ref("phone");
const familyMembers = ref([]);
const selectedChatFamily = ref(null);
const chatMessages = ref([]);
const messageInput = ref("");
const chatContainer = ref(null);
const showQuickPhrases = ref(false);
const activePhraseCategory = ref(["daily"]);
const isVoiceRecording = ref(false);

// 快捷话术模板
const quickPhrases = {
  daily: [
    "我现在一切都好，在家休息呢，你不用惦记",
    "今天天气挺好，我出去走了走，身体挺舒服",
    "晚饭吃过啦，吃得很饱，你早点休息",
  ],
  sick: [
    "我今天有点头晕/胸闷，现在在家坐着，你方便的话赶紧回来看看我",
    "我感觉浑身不舒服，已经量过体温了，你给我回个电话吧",
    "我刚才不小心摔了一跤，现在坐在地上，没法起来，你快来帮我一下",
  ],
  need: [
    "家里的米/盐用完了，你下次回来帮我带一点哦",
    "我想明天去医院复查，能不能陪我一起去呀",
    "这个手机/电视我不会操作了，你有空的时候教我一下",
  ],
  emotion: [
    "今天看到你小时候的照片，特别想你，有空多回家看看",
    "外面降温了，你出门记得多穿件衣服，别感冒了",
    "谢谢你一直照顾我，有你在我特别安心",
  ],
  emergency: [
    "我现在特别不舒服，已经打了120，你赶紧来医院找我",
    "家里有陌生人敲门，我很害怕，你快给我回电话",
    "我忘带钥匙了，现在在门口，你赶紧送钥匙过来",
  ],
};

// WebSocket连接
let websocket = null;

onMounted(() => {
  fetchFamilyMembers();
  initVoiceAssistant();
});

onUnmounted(() => {
  if (websocket) {
    websocket.close();
  }
  voiceAssistant.destroy();
});

// 获取家属列表
const fetchFamilyMembers = async () => {
  try {
    const res = await getFamilyMembers();
    familyMembers.value = res.data || [];
  } catch (error) {
    console.error("获取家属列表失败", error);
    ElMessage.error("获取家属列表失败");
  }
};

// 拨打电话
const callFamily = (family) => {
  if (family.phone) {
    window.location.href = `tel:${family.phone}`;
    ElMessage.success(`正在拨打 ${family.familyName} 的电话`);
  } else {
    ElMessage.warning("该家属暂无电话号码");
  }
};

// 开始聊天
const startChat = (family) => {
  selectedChatFamily.value = family;
  loadChatHistory(family.id);
  initWebSocket(family.id);
};

// 返回家属列表
const backToFamilyList = () => {
  selectedChatFamily.value = null;
  if (websocket) {
    websocket.close();
    websocket = null;
  }
};

// 加载聊天记录
const loadChatHistory = async (familyId) => {
  // TODO: 调用后端接口加载聊天记录
  // 暂时使用模拟数据
  chatMessages.value = [
    {
      id: 1,
      senderType: "family",
      content: "爸，今天身体怎么样？",
      sendTime: new Date(Date.now() - 3600000).toISOString(),
    },
    {
      id: 2,
      senderType: "elderly",
      content: "我挺好的，不用担心",
      sendTime: new Date(Date.now() - 3000000).toISOString(),
    },
  ];
  scrollToBottom();
};

// 初始化WebSocket
const initWebSocket = (familyId) => {
  // TODO: 替换为实际的WebSocket地址
  const wsUrl = `ws://localhost:8080/ws/chat/${familyId}`;
  websocket = new WebSocket(wsUrl);

  websocket.onopen = () => {
    console.log("WebSocket连接成功");
  };

  websocket.onmessage = (event) => {
    const message = JSON.parse(event.data);
    chatMessages.value.push(message);
    scrollToBottom();
  };

  websocket.onerror = (error) => {
    console.error("WebSocket错误:", error);
    ElMessage.error("聊天连接出错");
  };

  websocket.onclose = () => {
    console.log("WebSocket连接关闭");
  };
};

// 发送消息
const sendMessage = () => {
  if (!messageInput.value.trim()) {
    ElMessage.warning("请输入消息内容");
    return;
  }

  const message = {
    id: Date.now(),
    senderType: "elderly",
    content: messageInput.value,
    sendTime: new Date().toISOString(),
  };

  // 发送到WebSocket
  if (websocket && websocket.readyState === WebSocket.OPEN) {
    websocket.send(JSON.stringify(message));
  }

  // 添加到本地列表
  chatMessages.value.push(message);
  messageInput.value = "";
  scrollToBottom();
};

// 滚动到底部
const scrollToBottom = () => {
  nextTick(() => {
    if (chatContainer.value) {
      chatContainer.value.scrollTop = chatContainer.value.scrollHeight;
    }
  });
};

// 清空聊天记录
const clearChatHistory = () => {
  ElMessageBox.confirm("确定要清空聊天记录吗？", "提示", {
    confirmButtonText: "确定",
    cancelButtonText: "取消",
    type: "warning",
  })
    .then(() => {
      chatMessages.value = [];
      ElMessage.success("聊天记录已清空");
    })
    .catch(() => {});
};

// 选择快捷话术
const selectPhrase = (phrase) => {
  messageInput.value = phrase;
  showQuickPhrases.value = false;
};

// 语音输入
const toggleVoiceInput = () => {
  if (isVoiceRecording.value) {
    voiceAssistant.stop();
    isVoiceRecording.value = false;
  } else {
    voiceAssistant.start();
    isVoiceRecording.value = true;
  }
};

// 初始化语音助手
const initVoiceAssistant = () => {
  voiceAssistant.on("result", (text) => {
    messageInput.value = text;
    isVoiceRecording.value = false;
  });

  voiceAssistant.on("error", () => {
    isVoiceRecording.value = false;
  });

  voiceAssistant.on("end", () => {
    isVoiceRecording.value = false;
  });
};

// 格式化时间
const formatTime = (time) => {
  if (!time) return "";
  const date = new Date(time);
  return date.toLocaleString("zh-CN", {
    month: "2-digit",
    day: "2-digit",
    hour: "2-digit",
    minute: "2-digit",
  });
};

const selectFamily = (family) => {
  console.log("选择家属:", family);
};
</script>

<style lang="scss" scoped>
.family-contact-page {
  padding: 30px;
  background-color: #f5f7fa;
  min-height: 100vh;
}

.page-title {
  font-size: 32px;
  font-weight: bold;
  color: #303133;
  margin-bottom: 30px;
  text-align: center;
}

.contact-tabs {
  background-color: #ffffff;
  border-radius: 16px;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.1);

  :deep(.el-tabs__header) {
    background-color: #f5f7fa;
    border-radius: 16px 16px 0 0;
    margin: 0;
  }

  :deep(.el-tabs__item) {
    font-size: 20px;
    padding: 20px 40px;
    height: auto;
  }

  :deep(.el-tabs__content) {
    padding: 30px;
  }
}

.section-title {
  font-size: 24px;
  font-weight: bold;
  color: #303133;
  margin-bottom: 20px;
}

.empty-state {
  padding: 60px 20px;
  text-align: center;
}

.family-list {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.family-card {
  display: flex;
  align-items: center;
  padding: 24px;
  background-color: #f5f7fa;
  border-radius: 12px;
  cursor: pointer;
  transition: all 0.3s ease;

  &:hover {
    background-color: #e6f7ff;
    transform: translateY(-2px);
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  }
}

.family-avatar {
  width: 64px;
  height: 64px;
  border-radius: 50%;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 28px;
  font-weight: bold;
  margin-right: 24px;
}

.family-info {
  flex: 1;
}

.family-name {
  font-size: 24px;
  font-weight: bold;
  color: #303133;
  margin-bottom: 8px;
}

.family-relation {
  font-size: 18px;
  color: #909399;
  margin-bottom: 4px;
}

.family-phone {
  font-size: 18px;
  color: #606266;
}

.call-btn,
.chat-btn {
  font-size: 18px;
  padding: 16px 32px;
  height: auto;
}

// 聊天界面样式
.chat-interface {
  display: flex;
  flex-direction: column;
  height: 600px;
}

.chat-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 20px;
  background-color: #f5f7fa;
  border-radius: 12px;
  margin-bottom: 20px;
}

.chat-family-info {
  display: flex;
  align-items: center;
  gap: 16px;
}

.family-avatar-small {
  width: 48px;
  height: 48px;
  border-radius: 50%;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 20px;
  font-weight: bold;
}

.chat-messages {
  flex: 1;
  overflow-y: auto;
  padding: 20px;
  background-color: #f5f7fa;
  border-radius: 12px;
  margin-bottom: 20px;
}

.empty-chat {
  padding: 60px 20px;
  text-align: center;
}

.message-item {
  display: flex;
  align-items: flex-start;
  margin-bottom: 20px;

  &.message-self {
    flex-direction: row-reverse;

    .message-content {
      align-items: flex-end;
      margin-left: 0;
      margin-right: 16px;
    }

    .message-text {
      background-color: #409eff;
      color: white;
    }
  }
}

.message-avatar {
  width: 48px;
  height: 48px;
  border-radius: 50%;
  background: linear-gradient(135deg, #67c23a 0%, #95d475 100%);
  color: white;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 18px;
  font-weight: bold;
  flex-shrink: 0;
}

.message-content {
  display: flex;
  flex-direction: column;
  margin-left: 16px;
  max-width: 70%;
}

.message-text {
  background-color: #ffffff;
  padding: 16px 20px;
  border-radius: 12px;
  font-size: 18px;
  color: #303133;
  line-height: 1.6;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.message-time {
  font-size: 14px;
  color: #909399;
  margin-top: 8px;
}

.quick-phrases-section {
  margin-bottom: 16px;
}

.quick-phrases-btn {
  font-size: 18px;
  padding: 16px 32px;
  height: auto;
}

.chat-input-area {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.message-input {
  :deep(.el-textarea__inner) {
    font-size: 18px;
    padding: 16px;
  }
}

.input-actions {
  display: flex;
  justify-content: space-between;
  gap: 16px;
}

.voice-input-btn,
.send-btn {
  font-size: 18px;
  padding: 16px 32px;
  height: auto;
}

.send-btn {
  flex: 1;
}

// 快捷话术弹窗样式
.quick-phrases-dialog {
  max-height: 500px;
  overflow-y: auto;
}

.phrase-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
  padding: 16px;
}

.phrase-btn {
  font-size: 18px;
  padding: 16px 24px;
  height: auto;
  text-align: left;
  white-space: normal;
  line-height: 1.5;
}

:deep(.el-collapse-item__header) {
  font-size: 20px;
  font-weight: bold;
  padding: 20px;
  height: auto;
}

:deep(.el-collapse-item__content) {
  padding: 0;
}
</style>

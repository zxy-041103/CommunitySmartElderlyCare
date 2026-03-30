<template>
  <div class="voice-container">
    <!-- 语音帮助面板 -->
    <transition name="slide-up">
      <div class="voice-help-panel" v-if="helpVisible">
        <div class="help-header">
          <h4>🎙️ 语音指令列表</h4>
          <el-button :icon="Close" circle size="small" @click="helpVisible=false" />
        </div>
        <div class="help-content">
          <p class="help-tip">点击麦克风按钮，说出以下关键词即可快速操作：</p>
          <div v-for="(cmds, group) in groupedCommands" :key="group" class="cmd-group">
            <div class="group-title">{{ group }}</div>
            <div class="cmd-list">
              <div class="cmd-item" v-for="cmd in cmds" :key="cmd.label" @click="navigateTo(cmd)">
                <span class="cmd-icon">{{ cmd.icon }}</span>
                <span class="cmd-label">{{ cmd.label }}</span>
              </div>
            </div>
          </div>
        </div>
      </div>
    </transition>

    <!-- 识别结果提示 -->
    <transition name="fade">
      <div class="voice-result" v-if="resultVisible">
        <div class="result-text">
          <el-icon v-if="recognizing" class="rotating"><Loading /></el-icon>
          <el-icon v-else-if="resultType==='success'" color="#67C23A"><CircleCheck /></el-icon>
          <el-icon v-else color="#F56C6C"><CircleClose /></el-icon>
          <span>{{ resultText }}</span>
        </div>
      </div>
    </transition>

    <!-- 悬浮按钮组 -->
    <div class="voice-fab-group">
      <el-tooltip content="语音指令帮助" placement="left">
        <button class="voice-help-btn" @click="helpVisible=!helpVisible">
          <span>?</span>
        </button>
      </el-tooltip>
      <el-tooltip :content="recognizing?'点击停止':'点击说话'" placement="left">
        <button :class="['voice-fab', { recording: recognizing }]" @click="toggleRecognition">
          <span class="fab-icon">🎙️</span>
          <div class="ripple" v-if="recognizing"></div>
          <div class="ripple delay" v-if="recognizing"></div>
        </button>
      </el-tooltip>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onBeforeUnmount } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '../store/user'
import { getGroupedCommands, matchVoiceCommand } from '../utils/voiceCommands'
import { ElMessage } from 'element-plus'
import { Close } from '@element-plus/icons-vue'

const router = useRouter()
const userStore = useUserStore()
const groupedCommands = computed(() => getGroupedCommands())

const helpVisible = ref(false)
const recognizing = ref(false)
const resultVisible = ref(false)
const resultText = ref('')
const resultType = ref('')

let recognition = null
let resultTimer = null

const initSpeechRecognition = () => {
  const SpeechRecognition = window.SpeechRecognition || window.webkitSpeechRecognition
  if (!SpeechRecognition) return null
  const rec = new SpeechRecognition()
  rec.lang = 'zh-CN'
  rec.continuous = false
  rec.interimResults = true
  rec.maxAlternatives = 3

  rec.onresult = (event) => {
    let text = ''
    for (let i = event.resultIndex; i < event.results.length; i++) {
      text += event.results[i][0].transcript
    }
    if (event.results[event.results.length - 1].isFinal) {
      handleRecognitionResult(text)
    } else {
      showResult('正在识别: ' + text, '', true)
    }
  }

  rec.onerror = (event) => {
    recognizing.value = false
    if (event.error === 'no-speech') showResult('未检测到语音，请重试', 'error')
    else if (event.error === 'not-allowed') showResult('请允许麦克风权限', 'error')
    else showResult('识别出错: ' + event.error, 'error')
  }

  rec.onend = () => { recognizing.value = false }
  return rec
}

const handleRecognitionResult = (text) => {
  recognizing.value = false
  const matched = matchVoiceCommand(text)
  if (matched) {
    showResult(`识别: "${text}" → ${matched.label}`, 'success')
    // 处理不同类型的指令
    if (matched.type === 'exec' && matched.execAction === 'logout') {
      userStore.logout()
      router.push('/login')
    } else if (matched.type === 'action' && matched.action === 'fontLarge') {
      userStore.setFontSize('large')
      showResult('已切换为大号字体', 'success')
    } else if (matched.type === 'action' && matched.action === 'fontNormal') {
      userStore.setFontSize('normal')
      showResult('已切换为标准字体', 'success')
    } else if (matched.path) {
      router.push(matched.path)
    }
  } else {
    showResult(`识别: "${text}" - 未匹配到指令`, 'error')
  }
}

const showResult = (text, type, keepShowing = false) => {
  resultText.value = text
  resultType.value = type
  resultVisible.value = true
  if (resultTimer) clearTimeout(resultTimer)
  if (!keepShowing) {
    resultTimer = setTimeout(() => { resultVisible.value = false }, 3000)
  }
}

const toggleRecognition = () => {
  if (recognizing.value) {
    if (recognition) recognition.stop()
    recognizing.value = false
    return
  }
  if (!recognition) {
    recognition = initSpeechRecognition()
    if (!recognition) {
      ElMessage.warning('当前浏览器不支持语音识别，建议使用Chrome浏览器')
      return
    }
  }
  try {
    recognizing.value = true
    showResult('正在聆听，请说出指令...', '', true)
    recognition.start()
  } catch (e) {
    recognizing.value = false
    showResult('语音识别启动失败', 'error')
  }
}

const navigateTo = (cmd) => {
  if (cmd.path) router.push(cmd.path)
  helpVisible.value = false
}

onBeforeUnmount(() => {
  if (recognition) recognition.stop()
  if (resultTimer) clearTimeout(resultTimer)
})
</script>

<style scoped>
.voice-container { position: fixed; bottom: 20px; right: 20px; z-index: 2000; }

.voice-fab-group { display: flex; flex-direction: column; align-items: center; gap: 10px; }
.voice-help-btn {
  width: 40px; height: 40px; border-radius: 50%; border: none;
  background: linear-gradient(135deg, #409EFF, #337ecc); color: #fff;
  font-size: 18px; font-weight: bold; cursor: pointer;
  box-shadow: 0 4px 12px rgba(64,158,255,0.4); transition: all 0.3s ease;
}
.voice-help-btn:hover { transform: scale(1.1); box-shadow: 0 6px 20px rgba(64,158,255,0.5); }

.voice-fab {
  width: 56px; height: 56px; border-radius: 50%; border: none;
  background: linear-gradient(135deg, #67C23A, #529b2e); color: #fff;
  font-size: 28px; cursor: pointer; position: relative;
  box-shadow: 0 4px 16px rgba(103,194,58,0.4); transition: all 0.3s ease;
  display: flex; align-items: center; justify-content: center;
}
.voice-fab:hover { transform: scale(1.08); box-shadow: 0 6px 24px rgba(103,194,58,0.5); }
.voice-fab.recording {
  background: linear-gradient(135deg, #F56C6C, #c45656);
  box-shadow: 0 4px 16px rgba(245,108,108,0.5); animation: pulse 1.5s infinite;
}
.fab-icon { font-size: 28px; z-index: 1; }

.ripple {
  position: absolute; width: 100%; height: 100%; border-radius: 50%;
  border: 2px solid rgba(245,108,108,0.6); animation: rippleEffect 1.5s infinite ease-out;
}
.ripple.delay { animation-delay: 0.5s; }

@keyframes rippleEffect { 0% { transform: scale(1); opacity: 1; } 100% { transform: scale(2.2); opacity: 0; } }
@keyframes pulse { 0%, 100% { transform: scale(1); } 50% { transform: scale(1.05); } }

/* 识别结果 */
.voice-result {
  position: fixed; bottom: 90px; right: 20px;
  background: rgba(0,0,0,0.8); color: #fff; padding: 12px 20px;
  border-radius: 12px; max-width: 320px; font-size: 14px;
  box-shadow: 0 4px 16px rgba(0,0,0,0.3); backdrop-filter: blur(10px);
}
.result-text { display: flex; align-items: center; gap: 8px; }
.rotating { animation: spin 1s linear infinite; }
@keyframes spin { from { transform: rotate(0deg); } to { transform: rotate(360deg); } }

/* 帮助面板 - 修复溢出 */
.voice-help-panel {
  position: fixed; bottom: 90px; right: 20px; width: 340px;
  background: #fff; border-radius: 16px;
  box-shadow: 0 8px 32px rgba(0,0,0,0.15);
  overflow: hidden; max-height: 70vh;
}
.help-header {
  display: flex; justify-content: space-between; align-items: center;
  padding: 14px 16px; background: linear-gradient(135deg, #1a3a5c, #4a90d9); color: #fff;
}
.help-header h4 { margin: 0; font-size: 15px; }
.help-content {
  padding: 12px 16px; overflow-y: auto; max-height: calc(70vh - 50px);
  box-sizing: border-box;
}
.help-tip { color: #909399; font-size: 12px; margin: 0 0 10px 0; }

/* 分组标题 */
.cmd-group { margin-bottom: 12px; }
.group-title {
  font-size: 12px; color: #909399; font-weight: 500;
  margin-bottom: 6px; padding-left: 2px;
}

/* 指令列表 - 紧凑单列布局 */
.cmd-list { display: flex; flex-wrap: wrap; gap: 6px; }
.cmd-item {
  display: inline-flex; align-items: center; gap: 6px;
  padding: 5px 10px; border-radius: 8px; background: #f5f7fa;
  cursor: pointer; transition: all 0.2s ease;
  border: 1px solid transparent; font-size: 13px;
}
.cmd-item:hover { background: #ecf5ff; border-color: #409EFF; }
.cmd-icon { font-size: 16px; flex-shrink: 0; }
.cmd-label { color: #303133; white-space: nowrap; }

/* 动画 */
.slide-up-enter-active, .slide-up-leave-active { transition: all 0.3s ease; }
.slide-up-enter-from, .slide-up-leave-to { opacity: 0; transform: translateY(20px); }
.fade-enter-active, .fade-leave-active { transition: opacity 0.3s ease; }
.fade-enter-from, .fade-leave-to { opacity: 0; }
</style>

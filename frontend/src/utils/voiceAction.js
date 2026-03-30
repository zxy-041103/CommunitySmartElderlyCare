import { ref, watch } from 'vue'

// 全局响应式动作信号，用于 VoiceButton 与页面组件通信
const _pendingAction = ref(null)

/**
 * 触发一个语音动作（由 VoiceButton 调用）
 * @param {string} action - 动作名称
 * @param {*} payload - 可选附加数据
 */
export function triggerVoiceAction(action, payload = null) {
  _pendingAction.value = { action, payload, ts: Date.now() }
}

/**
 * 在页面组件中监听语音动作
 * @param {string} actionName - 要监听的动作名称
 * @param {Function} handler - 动作触发时的回调 (payload) => void
 */
export function useVoiceAction(actionName, handler) {
  watch(_pendingAction, (val) => {
    if (val && val.action === actionName) {
      handler(val.payload)
    }
  })
}

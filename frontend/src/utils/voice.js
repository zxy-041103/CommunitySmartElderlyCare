import { ElMessage } from "element-plus";

class VoiceRecognition {
  constructor() {
    this.recognition = null;
    this.isListening = false;
    this.onResult = null;
    this.onError = null;
    this.onStart = null;
    this.onEnd = null;
    this.emergencyKeyword = "呼叫护工";
    this.continuousMode = false;
  }

  init() {
    if (
      !"webkitSpeechRecognition" in window &&
      !"SpeechRecognition" in window
    ) {
      console.error("浏览器不支持语音识别功能");
      ElMessage.error("您的浏览器不支持语音识别功能，请使用Chrome浏览器");
      return false;
    }

    const SpeechRecognition =
      window.SpeechRecognition || window.webkitSpeechRecognition;
    this.recognition = new SpeechRecognition();

    this.recognition.lang = "zh-CN";
    this.recognition.continuous = this.continuousMode;
    this.recognition.interimResults = false;
    this.recognition.maxAlternatives = 1;

    this.recognition.onresult = (event) => {
      const results = event.results;
      const lastResult = results[results.length - 1];
      const transcript = lastResult[0].transcript.trim();

      console.log("语音识别结果:", transcript);

      if (this.onResult) {
        this.onResult(transcript);
      }

      this.checkEmergencyKeyword(transcript);
    };

    this.recognition.onerror = (event) => {
      console.error("语音识别错误:", event.error);

      let errorMessage = "语音识别出错";
      switch (event.error) {
        case "no-speech":
          errorMessage = "未检测到语音，请重试";
          break;
        case "audio-capture":
          errorMessage = "无法访问麦克风，请检查权限设置";
          break;
        case "not-allowed":
          errorMessage = "麦克风权限被拒绝，请在浏览器设置中允许访问";
          break;
        case "network":
          errorMessage = "网络连接错误，请检查网络";
          break;
        default:
          errorMessage = `语音识别错误: ${event.error}`;
      }

      if (this.onError) {
        this.onError(event.error);
      }

      ElMessage.error(errorMessage);
      this.stop();
    };

    this.recognition.onstart = () => {
      this.isListening = true;
      console.log("语音识别已启动");

      if (this.onStart) {
        this.onStart();
      }
    };

    this.recognition.onend = () => {
      this.isListening = false;
      console.log("语音识别已停止");

      if (this.onEnd) {
        this.onEnd();
      }

      if (this.continuousMode && this.autoRestart) {
        this.start();
      }
    };

    return true;
  }

  start() {
    if (!this.recognition) {
      const initialized = this.init();
      if (!initialized) {
        return false;
      }
    }

    if (this.isListening) {
      console.warn("语音识别已在运行中");
      return true;
    }

    try {
      this.recognition.start();
      return true;
    } catch (error) {
      console.error("启动语音识别失败:", error);
      ElMessage.error("启动语音识别失败，请重试");
      return false;
    }
  }

  stop() {
    if (this.recognition && this.isListening) {
      this.recognition.stop();
      this.autoRestart = false;
    }
  }

  startContinuous() {
    this.continuousMode = true;
    this.autoRestart = true;
    if (this.recognition) {
      this.recognition.continuous = true;
    }
    return this.start();
  }

  stopContinuous() {
    this.continuousMode = false;
    this.autoRestart = false;
    if (this.recognition) {
      this.recognition.continuous = false;
    }
    this.stop();
  }

  checkEmergencyKeyword(text) {
    if (text.includes(this.emergencyKeyword)) {
      console.log("检测到紧急求助关键词:", this.emergencyKeyword);
      ElMessage.warning("检测到紧急求助指令，正在为您呼叫护工...");

      if (this.onEmergency) {
        this.onEmergency(text);
      }

      this.stop();
    }
  }

  on(event, callback) {
    switch (event) {
      case "result":
        this.onResult = callback;
        break;
      case "error":
        this.onError = callback;
        break;
      case "start":
        this.onStart = callback;
        break;
      case "end":
        this.onEnd = callback;
        break;
      case "emergency":
        this.onEmergency = callback;
        break;
      default:
        console.warn(`未知事件类型: ${event}`);
    }
  }

  off(event) {
    switch (event) {
      case "result":
        this.onResult = null;
        break;
      case "error":
        this.onError = null;
        break;
      case "start":
        this.onStart = null;
        break;
      case "end":
        this.onEnd = null;
        break;
      case "emergency":
        this.onEmergency = null;
        break;
      default:
        console.warn(`未知事件类型: ${event}`);
    }
  }

  setEmergencyKeyword(keyword) {
    this.emergencyKeyword = keyword;
  }

  isSupported() {
    return "webkitSpeechRecognition" in window || "SpeechRecognition" in window;
  }

  isActive() {
    return this.isListening;
  }

  destroy() {
    this.stop();
    this.recognition = null;
    this.onResult = null;
    this.onError = null;
    this.onStart = null;
    this.onEnd = null;
    this.onEmergency = null;
  }
}

export default new VoiceRecognition();
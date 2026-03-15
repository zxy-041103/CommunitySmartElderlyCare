/**
 * 语音助手工具类
 * 提供语音识别和指令处理功能
 */

class VoiceAssistant {
  constructor() {
    this.recognition = null;
    this.callbacks = {};
    this.isListening = false;
    this.init();
  }

  /**
   * 初始化语音识别
   */
  init() {
    if ('webkitSpeechRecognition' in window) {
      this.recognition = new window.webkitSpeechRecognition();
      this.recognition.continuous = false;
      this.recognition.interimResults = false;
      this.recognition.lang = 'zh-CN';

      this.recognition.onstart = () => {
        this.isListening = true;
        this.trigger('start');
      };

      this.recognition.onresult = (event) => {
        const transcript = event.results[0][0].transcript;
        this.trigger('result', transcript);
        this.processCommand(transcript);
      };

      this.recognition.onerror = (event) => {
        this.trigger('error', event.error);
      };

      this.recognition.onend = () => {
        this.isListening = false;
        this.trigger('end');
      };
    }
  }

  /**
   * 检查浏览器是否支持语音识别
   */
  isSupported() {
    return 'webkitSpeechRecognition' in window;
  }

  /**
   * 开始语音识别
   */
  start() {
    if (this.recognition && !this.isListening) {
      try {
        this.recognition.start();
      } catch (error) {
        this.trigger('error', error);
      }
    }
  }

  /**
   * 停止语音识别
   */
  stop() {
    if (this.recognition && this.isListening) {
      this.recognition.stop();
    }
  }

  /**
   * 处理语音指令
   */
  processCommand(text) {
    // 紧急求助指令
    if (text.includes('紧急') || text.includes('求助') || text.includes('救命') || text.includes('不舒服')) {
      this.trigger('emergency', text);
      return;
    }

    // 预约服务指令
    if (text.includes('预约') || text.includes('服务') || text.includes('家政') || text.includes('医疗')) {
      this.trigger('booking', text);
      return;
    }

    // 联系家属指令
    if (text.includes('联系') || text.includes('家属') || text.includes('孩子') || text.includes('家人') || text.includes('聊天')) {
      this.trigger('contactFamily', text);
      return;
    }
  }

  /**
   * 注册事件监听
   */
  on(event, callback) {
    if (!this.callbacks[event]) {
      this.callbacks[event] = [];
    }
    this.callbacks[event].push(callback);
  }

  /**
   * 移除事件监听
   */
  off(event) {
    if (this.callbacks[event]) {
      delete this.callbacks[event];
    }
  }

  /**
   * 触发事件
   */
  trigger(event, data) {
    if (this.callbacks[event]) {
      this.callbacks[event].forEach(callback => {
        callback(data);
      });
    }
  }

  /**
   * 销毁语音助手
   */
  destroy() {
    if (this.recognition) {
      this.recognition.stop();
      this.recognition = null;
    }
    this.callbacks = {};
    this.isListening = false;
  }
}

// 导出单例
const voiceAssistant = new VoiceAssistant();
export default voiceAssistant;
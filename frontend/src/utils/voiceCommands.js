/**
 * 语音指令注册表 - 老年用户专用（仅用户端）
 * type: 'navigate' 跳转页面
 * type: 'action'   跳转页面并触发页面内动作 (action: 动作名)
 * type: 'exec'     直接在本地执行 (execAction: 动作名)
 */

const userCommands = [
    // ===== 页面导航 =====
    { keywords: ['首页', '回到首页', '回首页', '主页', '去首页'], path: '/user/home', label: '返回首页', icon: '🏠', type: 'navigate', group: '页面导航' },
    { keywords: ['健康', '健康中心', '查看健康', '我的健康', '健康数据'], path: '/user/health', label: '健康中心', icon: '❤️', type: 'navigate', group: '页面导航' },
    { keywords: ['服务中心', '找服务', '服务列表', '查看服务'], path: '/user/service', label: '服务中心', icon: '🛎️', type: 'navigate', group: '页面导航' },
    { keywords: ['我的预约', '查看预约', '预约记录', '预约列表'], path: '/user/appointment', label: '我的预约', icon: '📋', type: 'navigate', group: '页面导航' },
    { keywords: ['紧急求助', '求助页面', '紧急页面'], path: '/user/emergency', label: '紧急求助', icon: '🆘', type: 'navigate', group: '页面导航' },
    { keywords: ['活动中心', '查看活动', '社区活动', '活动列表'], path: '/user/activity', label: '活动中心', icon: '🎯', type: 'navigate', group: '页面导航' },
    { keywords: ['意见反馈', '反馈列表', '我的反馈'], path: '/user/feedback', label: '意见反馈', icon: '💬', type: 'navigate', group: '页面导航' },
    { keywords: ['个人中心', '个人信息', '我的信息', '设置'], path: '/user/profile', label: '个人中心', icon: '👤', type: 'navigate', group: '页面导航' },

    // ===== 紧急操作 =====
    { keywords: ['一键求助', '发起求助', '救命', 'SOS', '求救', '帮帮我'], path: '/user/emergency', label: '一键求助', icon: '🚨', type: 'action', action: 'openSOS', group: '紧急操作' },
    { keywords: ['跌倒求助', '我跌倒了', '摔倒了', '我摔倒了'], path: '/user/emergency', label: '跌倒求助', icon: '🤕', type: 'action', action: 'openSOSFall', group: '紧急操作' },
    { keywords: ['突发疾病', '身体不舒服', '不舒服', '生病了', '发病了'], path: '/user/emergency', label: '疾病求助', icon: '🏥', type: 'action', action: 'openSOSIllness', group: '紧急操作' },
    { keywords: ['求助记录', '查看求助', '我的求助', '求救记录'], path: '/user/emergency', label: '求助记录', icon: '📜', type: 'navigate', group: '紧急操作' },

    // ===== 健康查看 =====
    { keywords: ['查看血压', '血压', '我的血压', '血压数据'], path: '/user/health', label: '查看血压', icon: '💓', type: 'navigate', group: '健康查看' },
    { keywords: ['查看心率', '心率', '我的心率', '心率数据'], path: '/user/health', label: '查看心率', icon: '💗', type: 'navigate', group: '健康查看' },
    { keywords: ['查看血糖', '血糖', '我的血糖', '血糖数据'], path: '/user/health', label: '查看血糖', icon: '🩸', type: 'navigate', group: '健康查看' },
    { keywords: ['查看血氧', '血氧', '我的血氧', '血氧数据'], path: '/user/health', label: '查看血氧', icon: '🫁', type: 'navigate', group: '健康查看' },
    { keywords: ['近七天', '近7天', '最近七天', '最近7天', '近一周'], path: '/user/health', label: '近7天数据', icon: '📅', type: 'action', action: 'healthPeriod7', group: '健康查看' },
    { keywords: ['近十四天', '近14天', '最近十四天', '最近14天', '近两周'], path: '/user/health', label: '近14天数据', icon: '📅', type: 'action', action: 'healthPeriod14', group: '健康查看' },
    { keywords: ['近三十天', '近30天', '最近三十天', '最近30天', '近一个月'], path: '/user/health', label: '近30天数据', icon: '📅', type: 'action', action: 'healthPeriod30', group: '健康查看' },
    { keywords: ['健康预警', '预警记录', '查看预警', '健康警告'], path: '/user/health', label: '健康预警', icon: '⚠️', type: 'navigate', group: '健康查看' },

    // ===== 服务预约 =====
    { keywords: ['预约服务', '我要预约', '服务预约', '预约'], path: '/user/service', label: '预约服务', icon: '📝', type: 'navigate', group: '服务预约' },
    { keywords: ['全部预约', '所有预约', '预约全部'], path: '/user/appointment', label: '全部预约', icon: '📋', type: 'action', action: 'filterAll', group: '服务预约' },
    { keywords: ['待确认预约', '查看待确认', '待处理预约', '待确认'], path: '/user/appointment', label: '待确认预约', icon: '⏳', type: 'action', action: 'filterPending', group: '服务预约' },
    { keywords: ['已确认预约', '查看已确认', '已确认'], path: '/user/appointment', label: '已确认预约', icon: '✅', type: 'action', action: 'filterConfirmed', group: '服务预约' },
    { keywords: ['已完成预约', '查看已完成', '完成的预约', '已完成'], path: '/user/appointment', label: '已完成预约', icon: '🏁', type: 'action', action: 'filterCompleted', group: '服务预约' },
    { keywords: ['已取消预约', '取消的预约', '查看已取消'], path: '/user/appointment', label: '已取消预约', icon: '🚫', type: 'action', action: 'filterCancelled', group: '服务预约' },
    { keywords: ['取消预约', '我要取消预约'], path: '/user/appointment', label: '取消预约', icon: '❌', type: 'navigate', group: '服务预约' },
    { keywords: ['评价服务', '服务评价', '写评价', '给服务打分'], path: '/user/appointment', label: '评价服务', icon: '⭐', type: 'action', action: 'filterCompleted', group: '服务预约' },

    // ===== 活动操作 =====
    { keywords: ['报名活动', '我要报名', '参加活动', '活动报名'], path: '/user/activity', label: '报名活动', icon: '✋', type: 'navigate', group: '活动操作' },
    { keywords: ['取消报名', '退出活动', '取消活动'], path: '/user/activity', label: '取消报名', icon: '🚫', type: 'navigate', group: '活动操作' },

    // ===== 反馈操作 =====
    { keywords: ['提交反馈', '写反馈', '提意见', '我要反馈', '提建议'], path: '/user/feedback', label: '提交建议', icon: '✏️', type: 'action', action: 'openFeedbackSuggestion', group: '反馈操作' },
    { keywords: ['投诉', '我要投诉', '提交投诉'], path: '/user/feedback', label: '提交投诉', icon: '😤', type: 'action', action: 'openFeedbackComplaint', group: '反馈操作' },
    { keywords: ['表扬', '我要表扬', '提交表扬', '夸一夸'], path: '/user/feedback', label: '提交表扬', icon: '👍', type: 'action', action: 'openFeedbackPraise', group: '反馈操作' },

    // ===== 个人操作 =====
    { keywords: ['修改密码', '改密码', '换密码', '更改密码'], path: '/user/profile', label: '修改密码', icon: '🔒', type: 'navigate', group: '个人操作' },
    { keywords: ['修改信息', '编辑资料', '改信息', '保存信息'], path: '/user/profile', label: '修改信息', icon: '📝', type: 'navigate', group: '个人操作' },
    { keywords: ['放大字体', '字体大', '大号字体'], label: '放大字体', icon: '🔤', type: 'exec', execAction: 'fontLarge', group: '个人操作' },
    { keywords: ['超大字体', '字体超大', '最大字体', '特大字体'], label: '超大字体', icon: '🔠', type: 'exec', execAction: 'fontXLarge', group: '个人操作' },
    { keywords: ['缩小字体', '字体小', '标准字体', '正常字体'], label: '标准字体', icon: '🔡', type: 'exec', execAction: 'fontNormal', group: '个人操作' },
    { keywords: ['退出登录', '退出', '注销', '登出', '退出系统'], label: '退出登录', icon: '🚪', type: 'exec', execAction: 'logout', group: '个人操作' },

    // ===== 信息查看 =====
    { keywords: ['查看公告', '公告', '通知', '最新公告'], path: '/user/home', label: '查看公告', icon: '📢', type: 'navigate', group: '信息查看' },
    { keywords: ['健康概览', '我的健康概览', '查看健康概览'], path: '/user/home', label: '健康概览', icon: '📊', type: 'navigate', group: '信息查看' },
    { keywords: ['近期活动', '最近活动', '查看近期活动'], path: '/user/home', label: '近期活动', icon: '📆', type: 'navigate', group: '信息查看' },
]

/**
 * 获取语音指令列表
 */
export function getVoiceCommands() {
    return userCommands
}

/**
 * 获取按分组整理的指令 { groupName: [cmd, ...] }
 */
export function getGroupedCommands() {
    const groups = {}
    for (const cmd of userCommands) {
        if (!groups[cmd.group]) groups[cmd.group] = []
        groups[cmd.group].push(cmd)
    }
    return groups
}

/**
 * 根据识别文本匹配最佳语音指令（最长关键词优先）
 */
export function matchVoiceCommand(text) {
    if (!text) return null
    const cleaned = text.replace(/[，。！？、\s,.!?]/g, '')
    let bestMatch = null
    let bestLen = 0
    for (const cmd of userCommands) {
        for (const kw of cmd.keywords) {
            if (cleaned.includes(kw) && kw.length > bestLen) {
                bestMatch = cmd
                bestLen = kw.length
            }
        }
    }
    return bestMatch
}

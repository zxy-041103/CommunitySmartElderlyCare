import { defineStore } from 'pinia'
import { ref } from 'vue'

export const useUserStore = defineStore('user', () => {
  const token = ref(localStorage.getItem('token') || '')
  const userInfo = ref(JSON.parse(localStorage.getItem('userInfo') || '{}'))
  const fontSize = ref(localStorage.getItem('fontSize') || 'normal')

  function setToken(t) {
    token.value = t
    localStorage.setItem('token', t)
  }

  function setUserInfo(info) {
    userInfo.value = info
    localStorage.setItem('userInfo', JSON.stringify(info))
  }

  function setFontSize(size) {
    fontSize.value = size
    localStorage.setItem('fontSize', size)
    document.body.classList.remove('font-large', 'font-xlarge')
    if (size === 'large') document.body.classList.add('font-large')
    if (size === 'xlarge') document.body.classList.add('font-xlarge')
  }

  function logout() {
    token.value = ''
    userInfo.value = {}
    localStorage.removeItem('token')
    localStorage.removeItem('userInfo')
  }

  function initFontSize() {
    const size = localStorage.getItem('fontSize') || 'normal'
    setFontSize(size)
  }

  return { token, userInfo, fontSize, setToken, setUserInfo, setFontSize, logout, initFontSize }
})

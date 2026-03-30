import axios from 'axios'
import { ElMessage } from 'element-plus'
import router from '../router'

const request = axios.create({
  baseURL: '',
  timeout: 15000
})

request.interceptors.request.use(config => {
  const token = localStorage.getItem('token')
  if (token) {
    config.headers['Authorization'] = 'Bearer ' + token
  }
  return config
}, error => Promise.reject(error))

request.interceptors.response.use(response => {
  const res = response.data
  if (res.code === 200) {
    return res
  } else {
    ElMessage.error(res.msg || '操作失败')
    return Promise.reject(new Error(res.msg))
  }
}, error => {
  if (error.response && error.response.status === 401) {
    ElMessage.error('登录已过期，请重新登录')
    localStorage.removeItem('token')
    localStorage.removeItem('userInfo')
    router.push('/login')
  } else {
    ElMessage.error(error.message || '网络请求失败')
  }
  return Promise.reject(error)
})

export default request

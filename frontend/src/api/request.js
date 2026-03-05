import axios from "axios";
import { ElMessage } from "element-plus";
import { useUserStore } from "@/store/user";
import router from "@/router";

const request = axios.create({
  baseURL: "/api",
  timeout: 30000,
  headers: {
    "Content-Type": "application/json;charset=UTF-8",
  },
});

request.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem("token");
    console.log('API请求拦截器执行，token:', token);
    console.log('API请求URL:', config.url);
    if (token) {
      config.headers["Authorization"] = `Bearer ${token}`;
      console.log('API请求添加Authorization头:', config.headers["Authorization"]);
    } else {
      console.log('API请求没有token');
    }
    return config;
  },
  (error) => {
    console.error("Request error:", error);
    return Promise.reject(error);
  },
);

request.interceptors.response.use(
  (response) => {
    const res = response.data;
    if (res.code !== 200) {
      ElMessage.error(res.message || "请求失败");

      if (res.code === 401) {
        const userStore = useUserStore();
        userStore.logout();
        router.push("/login");
      }

      return Promise.reject(new Error(res.message || "请求失败"));
    }
    return res;
  },
  (error) => {
    console.error("Response error:", error);

    if (error.response) {
      switch (error.response.status) {
        case 401: {
          ElMessage.error("未授权，请重新登录");
          const userStore = useUserStore();
          userStore.logout();
          router.push("/login");
          break;
        }
        case 403:
          ElMessage.error("拒绝访问");
          break;
        case 404:
          ElMessage.error("请求地址不存在");
          break;
        case 500:
          ElMessage.error("服务器错误");
          break;
        default:
          ElMessage.error(error.response.data.message || "请求失败");
      }
    } else if (error.message.includes("timeout")) {
      ElMessage.error("请求超时");
    } else {
      ElMessage.error("网络错误");
    }

    return Promise.reject(error);
  },
);

export default request;
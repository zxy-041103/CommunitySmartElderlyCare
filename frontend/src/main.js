import { createApp } from "vue";
import ElementPlus from "element-plus";
import "element-plus/dist/index.css";
import zhCn from "element-plus/es/locale/lang/zh-cn";
import App from "./App.vue";
import router from "./router";
import "./assets/styles/index.scss";
import pinia from "./store";
import { useUserStore } from "./store";

const app = createApp(App);

app.use(pinia);
app.use(router);
app.use(ElementPlus, {
  locale: zhCn,
  size: "large",
});

// 初始化用户状态（在应用挂载前）
const initApp = async () => {
  const userStore = useUserStore();
  await userStore.initUserState();
  app.mount("#app");
};

initApp();

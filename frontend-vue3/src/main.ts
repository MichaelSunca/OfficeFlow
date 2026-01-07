import { createApp } from 'vue'
// 1. 导入 Element Plus 核心库
import ElementPlus from 'element-plus'
// 2. 导入 Element Plus 的样式文件
import 'element-plus/dist/index.css'
// 3. 导入图标库（如果你想在按钮上显示小图标）
import * as ElementPlusIconsVue from '@element-plus/icons-vue'

import './style.css'
import App from './App.vue'

const app = createApp(App)

// 4. 全局注册所有图标
for (const [key, component] of Object.entries(ElementPlusIconsVue)) {
    app.component(key, component)
}

// 5. 使用 Element Plus 插件
app.use(ElementPlus)

app.mount('#app')
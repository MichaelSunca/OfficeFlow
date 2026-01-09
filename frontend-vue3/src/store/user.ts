// src/store/user.ts
import { defineStore } from 'pinia'
import { ref } from 'vue'
import { jwtDecode } from "jwt-decode";

interface MyJwtPayload {
    role: string;   // 对应 claims.put("role", ...)
    userId: number; // 对应 claims.put("userId", ...)
    sub: string;    // JWT 默认的主题，通常是用户名
}

export const useUserStore = defineStore('user', () => {
    const userRole = ref('');
    const userId = ref<number | null>(null);

    const loginSuccess = (token: string) => {
        // 💡 解析加密的 Token 字符串
        const decoded = jwtDecode<MyJwtPayload>(token);

        // 💡 同步到响应式变量中
        userRole.value = decoded.role;      // 拿到 "ADMIN"
        userId.value = decoded.userId;      // 拿到 1

        // 持久化到本地，防止刷新页面丢失
        localStorage.setItem('token', token);
        localStorage.setItem('userRole', decoded.role);
    };

    // 💡 退出登录方法
    const logout = () => {
        // 1. 重置响应式状态（这样侧边栏会立即根据 v-if 隐藏）
        userRole.value = '';
        userId.value = null;

        // 2. 清除本地持久化缓存
        localStorage.removeItem('token');
        localStorage.removeItem('userRole');
        localStorage.removeItem('userId');
        // 或者直接 localStorage.clear(); 但 removeItem 更精确
    };

    return { userRole, userId, loginSuccess, logout };
});

// export const useUserStore = defineStore('user', () => {
//     // 1. 状态：从缓存初始化角色
//     const role = ref<string>(localStorage.getItem('userRole') || '')
//     const token = ref<string>(localStorage.getItem('token') || '')
//
//     // 2. 计算属性：判断是否是管理员
//     const isAdmin = computed(() => role.value === 'ADMIN')
//
//     // 3. 动作：保存登录信息
//     const saveLoginInfo = (newToken: string, newRole: string) => {
//         token.value = newToken
//         role.value = newRole
//         localStorage.setItem('token', newToken)
//         localStorage.setItem('userRole', newRole)
//     }
//
//     // 4. 动作：退出登录
//     const logout = () => {
//         token.value = ''
//         role.value = ''
//         localStorage.clear()
//     }
//
//     return { role, token, isAdmin, saveLoginInfo, logout }
// })
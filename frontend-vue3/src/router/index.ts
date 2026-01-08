import { createRouter, createWebHistory, type RouteRecordRaw } from 'vue-router'

// 1. 定义路由映射
const routes: Array<RouteRecordRaw> = [
    {
        path: '/login',
        name: 'Login',
        component: () => import('@/views/login/index.vue')
    },
    {
        path: '/',
        component: () => import('@/layout/index.vue'),
        redirect: '/assets',
        children: [
            {
                path: 'assets',
                name: 'Assets',
                component: () => import('@/views/assets/index.vue'),
                meta: { title: '资产台账' }
            },
            {
                path: 'user-search',
                name: 'UserSearch',
                component: () => import('@/views/user/UserSearch.vue'),
                meta: { title: '职员查询' }
            }
        ]
    }
]

const router = createRouter({
    history: createWebHistory(),
    routes
})

// 3. 路由守卫：实现登录拦截逻辑
router.beforeEach((to, _from, next) => {
    const token = localStorage.getItem('token')

    // 如果目标路由需要登录
    if (to.meta.requiresAuth) {
        if (token) {
            // 有 token，放行
            next()
        } else {
            // 没 token，弹回登录页
            next('/login')
        }
    } else {
        // 如果已登录还想去登录页，直接送回首页
        if (to.path === '/login' && token) {
            next('/assets')
        } else {
            next()
        }
    }
})

export default router
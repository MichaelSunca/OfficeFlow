import axios from 'axios';
import type { AxiosInstance, AxiosResponse, InternalAxiosRequestConfig } from 'axios';
import { ElMessage } from 'element-plus';
import router from "@/router";

const service: AxiosInstance = axios.create({
    baseURL: import.meta.env.VITE_API_URL || '/api', // 建议配合 .env 文件
    timeout: 10000,
});

// 请求拦截器
service.interceptors.request.use(
    (config: InternalAxiosRequestConfig) => {
        // 比如这里可以从 localStorage 获取 token
        const token = localStorage.getItem('token');
        if (token) {
            config.headers.Authorization = `Bearer ${token}`;
        }
        return config;
    },
    (error) => {
        return Promise.reject(error);
    }
);

// 响应拦截器
service.interceptors.response.use(
    (response: AxiosResponse) => {
        // 如果后端返回 code 401，说明 token 失效
        if (response.data.code === 401) {
            localStorage.removeItem('token');
            router.push('/login'); // 这样就能跳回登录了
            ElMessage.error('登录失效，请重新登录');
            return Promise.reject(new Error('Unauthorized'));
        }

        const { code, message, data } = response.data;
        if (code === 200) {
            return data;
        } else {
            ElMessage.error(message || '系统出错');
            return Promise.reject(new Error(message || 'Error'));
        }
    },
    (error) => {
        // 如果 HTTP 状态码是 401 (后端没封装 code，直接报 HTTP 401)
        if (error.response?.status === 401) {
            localStorage.removeItem('token');
            router.push('/login');
        }
        const msg = error.response?.data?.message || '网络连接异常';
        ElMessage.error(msg);
        return Promise.reject(error);
    }
);

export default service;


export const http = {
    get<T = any>(url: string, params?: object): Promise<T> {
        return service.get(url, { params }) as unknown as Promise<T>;
    },
    post<T = any>(url: string, data?: object): Promise<T> {
        return service.post(url, data) as unknown as Promise<T>;
    },
};
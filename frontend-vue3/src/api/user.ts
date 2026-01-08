import { http } from '@/utils/request';

// 复用之前定义的接口
export interface UserVO {
    id: number;
    username: string;
    nickname: string;
    avatar: string;
}

/**
 * 根据 ID 查询单个用户
 * @param id 用户 ID
 */
export const getUserByIdApi = (id: number | string) => {
    // 使用反引号 ` 拼接路径参数
    return http.get<UserVO>(`/users/${id}`);
};

/**
 * Get all users
 */
export const getUserListApi = () => {
    return http.get<UserVO[]>('/users/list');
};
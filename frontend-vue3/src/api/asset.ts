import { http } from '@/utils/request';

// 定义请求参数的类型，与后端 DTO 对应
export interface AssetOperateDTO {
    assetId: number
    remark: string
}

export interface AssetVO {
    id: number
    assetName: string
    assetSn: string
    category: string
    status: number // 0: 闲置, 1: 领用中, 2: 维修
    location: string
    userNickname: string | null
    createTime: string
}

export interface PageResult<T> {
    records: T[]
    total: number
    size: number
    current: number
}

/**
 * 资产领用
 */
export const claimAssetApi = (data: AssetOperateDTO) => {
    return http.post<string>('/assets/claim', data)
}

/**
 * 资产退库
 */
export const returnAssetApi = (data: AssetOperateDTO) => {
    return http.post<string>('/assets/return', data)
}

/**
 * 获取资产分页列表
 */
export const getAssetListApi = (params: { current: number; size: number }) => {
    return http.get<PageResult<AssetVO>>('/assets/list', params)
}
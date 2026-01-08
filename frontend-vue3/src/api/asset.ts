import { http } from '@/utils/request';

// --- 1. DTO (请求对象) 定义 ---

/**
 * 资产领用/退库 DTO
 */
export interface AssetOperateDTO {
    assetId: number
    remark: string
}

/**
 * 资产新增/编辑 DTO (对应后端 AssetFormDTO)
 */
export interface AssetFormDTO {
    id?: number          // 编辑时必传，新增时可选/不传
    assetName: string
    assetSn: string
    category: string
    price: number
    location: string
    purchaseDate: string
}

/**
 * 资产分页查询参数
 */
export interface AssetQueryDTO {
    current: number
    size: number
    assetName?: string   // 搜索条件：名称
    status?: number | null // 搜索条件：状态
}

// --- 2. VO (响应对象) 定义 ---

export interface AssetVO {
    id: number
    assetName: string
    assetSn: string
    category: string
    status: number
    price: number
    location: string
    purchaseDate: string
    userNickname: string | null
    createTime: string
}

export interface PageResult<T> {
    records: T[]
    total: number
    size: number
    current: number
}

// --- 3. API 接口方法 ---

/**
 * 获取资产分页列表 (支持搜索)
 */
export const getAssetListApi = (params: AssetQueryDTO) => {
    return http.get<PageResult<AssetVO>>('/assets/list', params)
}

/**
 * 资产新增
 */
export const addAssetApi = (data: AssetFormDTO) => {
    return http.post<string>('/assets/add', data) // 依然是 POST
}

/**
 * 资产更新
 */
export const updateAssetApi = (data: AssetFormDTO) => {
    return http.post<string>('/assets/update', data) // 改为 POST
}

/**
 * 资产删除 (逻辑删除)
 */
export const deleteAssetApi = (id: number) => {
    // 即使是删除，我们也改用 POST，通过 URL 传参
    return http.post<string>(`/assets/delete?id=${id}`)
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
 * 获取资产流转历史
 */
export const getAssetRecordsApi = (assetId: number) => {
    return http.get<any[]>(`/assets/records/${assetId}`)
}
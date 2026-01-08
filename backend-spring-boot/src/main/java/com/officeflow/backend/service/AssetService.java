package com.officeflow.backend.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.officeflow.backend.dto.AssetFormDTO;
import com.officeflow.backend.dto.AssetOperateDTO;
import com.officeflow.backend.entity.Asset;
import com.officeflow.backend.vo.AssetVO;

/**
 * 资产业务逻辑层接口
 * 继承 IService 可以获得 MyBatis-Plus 提供的更强大的批量操作方法
 */
public interface AssetService extends IService<Asset> {
    /**
     * 领用资产
     */
    void claimAsset(AssetOperateDTO claimDTO, Long userId);

    void returnAsset(AssetOperateDTO returnDTO, Long userId);

    /**
     * 新增资产
     */
    void saveAsset(AssetFormDTO dto);

    /**
     * 更新资产信息
     */
    void updateAsset(AssetFormDTO dto);

    Page<AssetVO> getAssetListPage(int current, int size);
}
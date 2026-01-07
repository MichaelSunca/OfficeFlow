package com.officeflow.backend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.officeflow.backend.dto.AssetClaimDTO;
import com.officeflow.backend.dto.AssetReturnDTO;
import com.officeflow.backend.entity.Asset;
import com.officeflow.backend.entity.AssetRecord;
import com.officeflow.backend.exception.BusinessException;
import com.officeflow.backend.mapper.AssetMapper;
import com.officeflow.backend.mapper.AssetRecordMapper;
import com.officeflow.backend.service.AssetService;
import com.officeflow.backend.vo.AssetVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

/**
 * 资产业务逻辑实现类
 */
@Service
@RequiredArgsConstructor // 依赖注入
public class AssetServiceImpl extends ServiceImpl<AssetMapper, Asset> implements AssetService {

    private final AssetRecordMapper recordMapper;

    /**
     * 重写 save 方法，加入业务校验
     */
    @Override
    public boolean save(Asset entity) {
        // 1. 严格非空校验
        if (!StringUtils.hasText(entity.getAssetSn())) {
            throw new BusinessException("操作失败：资产序列号(SN)不能为空");
        }

        // 2. 校验 SN 是否已存在
        LambdaQueryWrapper<Asset> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Asset::getAssetSn, entity.getAssetSn());

        long count = this.count(wrapper);
        if (count > 0) {
            // 发现重复，抛出自定义异常（稍后我们在全局异常处理器中捕获）
            // 或者简单处理：这里我们抛出一个运行时异常
            throw new RuntimeException("资产序列号 [" + entity.getAssetSn() + "] 已存在，请勿重复添加");
        }

        // 3. 校验通过，执行真正的保存
        return super.save(entity);
    }

    @Override
    @Transactional(rollbackFor = Exception.class) // 开启事务，任何异常都回滚
    public void claimAsset(AssetClaimDTO claimDTO, Long userId) {
        // 1. 检查资产是否存在
        Asset asset = this.getById(claimDTO.getAssetId());
        if (asset == null) {
            throw new BusinessException("操作失败：目标资产不存在");
        }

        // 2. 检查资产状态：只有“闲置(0)”的资产才能被领用
        if (asset.getStatus() != 0) {
            throw new BusinessException("操作失败：该资产当前状态无法领用（可能已被领用或维修中）");
        }

        // 3. 更新资产状态
        asset.setStatus(1); // 1 = 领用中
        asset.setUserId(userId);
        this.updateById(asset);

        // 4. 记录流转日志（写入 bus_record 表）
        AssetRecord record = new AssetRecord();
        record.setAssetId(asset.getId());
        record.setUserId(userId);
        record.setActionType("APPLY");
        record.setRemark(claimDTO.getRemark());
        record.setAuditStatus(1); // 简单起见，这里设置为直接通过

        recordMapper.insert(record);

        // 如果上面 recordMapper 插入报错，事务会保证 asset 的状态也会变回 0
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void returnAsset(AssetReturnDTO returnDTO, Long userId) {
        // 1. 获取并校验是否存在
        Asset asset = this.getById(returnDTO.getAssetId());
        if (asset == null) throw new BusinessException("资产不存在");

        // 2. 状态校验
        if (asset.getStatus() != 1) throw new BusinessException("该资产不在领用状态");

        // 3. 归属校验
        if (!userId.equals(asset.getUserId())) throw new BusinessException("你不是当前领用人");

        // 4. 执行更新（显式清空用户ID）
        this.update(new LambdaUpdateWrapper<Asset>()
                .eq(Asset::getId, asset.getId())
                .set(Asset::getStatus, 0)
                .set(Asset::getUserId, null)
        );

        // 5. 记录流转日志
        AssetRecord record = new AssetRecord();
        record.setAssetId(asset.getId());
        record.setUserId(userId);
        record.setActionType("RETURN");
        record.setRemark(returnDTO.getRemark());
        record.setAuditStatus(1);
        recordMapper.insert(record);
    }

    @Override
    public Page<AssetVO> getAssetListPage(int current, int size) {
        Page<AssetVO> page = new Page<>(current, size);
        return baseMapper.selectAssetPage(page);
    }
}
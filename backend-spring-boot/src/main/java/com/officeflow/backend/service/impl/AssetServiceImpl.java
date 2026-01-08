package com.officeflow.backend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.officeflow.backend.common.enums.AssetStatusEnum;
import com.officeflow.backend.dto.AssetFormDTO;
import com.officeflow.backend.dto.AssetOperateDTO;
import com.officeflow.backend.entity.Asset;
import com.officeflow.backend.entity.AssetRecord;
import com.officeflow.backend.exception.BusinessException;
import com.officeflow.backend.mapper.AssetMapper;
import com.officeflow.backend.mapper.AssetRecordMapper;
import com.officeflow.backend.service.AssetService;
import com.officeflow.backend.vo.AssetRecordVO;
import com.officeflow.backend.vo.AssetVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * 资产业务逻辑实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor // 依赖注入
public class AssetServiceImpl extends ServiceImpl<AssetMapper, Asset> implements AssetService {

    private final AssetRecordMapper assetRecordMapper;

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
    public void claimAsset(AssetOperateDTO claimDTO, Long userId) {
        Asset asset = this.getById(claimDTO.getAssetId());
        if (asset == null) throw new BusinessException("操作失败：目标资产不存在");
        if (!asset.getStatus().equals(AssetStatusEnum.IDLE.getCode())) {
            throw new BusinessException("操作失败：该资产当前状态为[" +
                    AssetStatusEnum.getDescriptionByCode(asset.getStatus()) + "]，无法领用");
        }

        Integer oldStatus = asset.getStatus();
        Integer newStatus = 1; // 领用中

        // 更新资产状态
        asset.setStatus(1); // 1 = 领用中
        asset.setUserId(userId);
        this.updateById(asset);

        // 4. 记录流转日志（写入 bus_record 表）
        AssetRecord record = new AssetRecord();
        record.setAssetId(asset.getId());
        record.setUserId(userId);
        record.setActionType("APPLY");
        record.setOldStatus(oldStatus); // 赋值旧状态 (0)
        record.setNewStatus(newStatus); // 赋值新状态 (1)
        record.setRemark(claimDTO.getRemark());
        record.setAuditStatus(1); // 简单起见，这里设置为直接通过

        assetRecordMapper.insert(record);

        // 如果上面 recordMapper 插入报错，事务会保证 asset 的状态也会变回 0
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void returnAsset(AssetOperateDTO returnDTO, Long userId) {
        Asset asset = this.getById(returnDTO.getAssetId());
        if (asset == null) throw new BusinessException("资产不存在");
        if (asset.getStatus() != 1) throw new BusinessException("该资产不在领用状态");
        if (!userId.equals(asset.getUserId())) throw new BusinessException("你不是当前领用人");

        Integer oldStatus = asset.getStatus();
        Integer newStatus = 0; // 闲置

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
        record.setOldStatus(oldStatus); // 赋值旧状态 (1)
        record.setNewStatus(newStatus); // 赋值新状态 (0)
        record.setRemark(returnDTO.getRemark());
        record.setAuditStatus(1);
        assetRecordMapper.insert(record);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveAsset(AssetFormDTO dto) {
        // 1. 唯一性检查：SN 码不能重复
        Long count = this.lambdaQuery()
                .eq(Asset::getAssetSn, dto.getAssetSn())
                .count();
        if (count > 0) {
            throw new BusinessException("资产序列号(SN)已存在，请勿重复录入");
        }

        // 2. DTO 转 Entity
        Asset asset = new Asset();
        BeanUtils.copyProperties(dto, asset);

        // 3. 设置初始状态
        asset.setStatus(0); // 默认为闲置

        // 4. 执行保存
        this.save(asset);
        log.info("新增资产成功，SN: {}", dto.getAssetSn());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateAsset(AssetFormDTO dto) {
        // 1. 检查目标资产是否存在
        Asset existingAsset = this.getById(dto.getId());
        if (existingAsset == null) {
            throw new BusinessException("未找到待修改的资产");
        }

        // 2. 如果修改了 SN 码，需要校验新 SN 是否被别人占用
        if (!existingAsset.getAssetSn().equals(dto.getAssetSn())) {
            Long count = this.lambdaQuery()
                    .eq(Asset::getAssetSn, dto.getAssetSn())
                    .ne(Asset::getId, dto.getId())
                    .count();
            if (count > 0) {
                throw new BusinessException("新的序列号(SN)已被其他资产使用");
            }
        }

        // 3. 属性更新
        BeanUtils.copyProperties(dto, existingAsset);

        // 4. 执行更新
        this.updateById(existingAsset);
        log.info("更新资产成功，ID: {}", dto.getId());
    }

    @Override
    public Page<AssetVO> getAssetListPage(int current, int size, String assetName, Integer status) {
        // 1. 创建分页对象
        Page<AssetVO> page = new Page<>(current, size);

        // 2. 调用 Mapper 执行关联查询
        // 这里建议去 XML 或使用 MyBatis-Plus 的自定义查询
        return baseMapper.selectAssetPage(page, assetName, status);
    }

    @Override
    public List<AssetRecordVO> getAssetRecords(Long assetId) {
        // 建议在 AssetRecordMapper 中写一个专门的 SQL 关联查询
        return assetRecordMapper.selectRecordListWithUserInfo(assetId);
    }
}
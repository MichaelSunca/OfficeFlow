package com.officeflow.backend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.officeflow.backend.common.enums.AssetStatusEnum;
import com.officeflow.backend.common.enums.AuditStatusEnum;
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

    /**
     * 领用申请 (进入审批流)
     * 逻辑：校验资产状态 -> 校验是否重复申请 -> 插入待审批记录
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void claimAsset(AssetOperateDTO claimDTO, Long userId) {
        // 1. 获取资产信息并校验是否存在
        // 💡 这里可以使用 baseMapper (因为继承了 ServiceImpl) 或显式注入的 assetMapper
        Asset asset = this.getById(claimDTO.getAssetId());
        if (asset == null) {
            throw new BusinessException("操作失败：目标资产不存在");
        }

        // 2. 状态校验：只有“闲置”状态的资产可以发起领用申请
        if (!AssetStatusEnum.IDLE.getCode().equals(asset.getStatus())) {
            throw new BusinessException("操作失败：该资产当前处于 [" +
                    AssetStatusEnum.getDescriptionByCode(asset.getStatus()) + "] 状态，无法申请领用");
        }

        // 3. 并发安全校验：检查数据库中是否已经存在该资产的“待审批”记录
        // 防止多个人同时针对同一个闲置资产点击“领用”
        Long pendingCount = assetRecordMapper.selectCount(new LambdaQueryWrapper<AssetRecord>()
                .eq(AssetRecord::getAssetId, asset.getId())
                .eq(AssetRecord::getAuditStatus, AuditStatusEnum.PENDING.getCode()));

        if (pendingCount > 0) {
            throw new BusinessException("操作失败：该资产已有正在处理中的领用申请，请勿重复提交");
        }

        // 4. 记录流转日志（核心：设置审核状态为 PENDING）
        AssetRecord record = new AssetRecord();
        record.setAssetId(asset.getId());
        record.setUserId(userId); // 申请人 ID
        record.setActionType("CLAIM"); // 动作类型：领用

        // 状态变迁描述：从 闲置(0) 变为 领用中(1)
        record.setOldStatus(AssetStatusEnum.IDLE.getCode());
        record.setNewStatus(AssetStatusEnum.USING.getCode());

        // 💡 审批流关键点：
        record.setAuditStatus(AuditStatusEnum.PENDING.getCode()); // 设为 0 (待审批)
        record.setRemark(claimDTO.getRemark());

        assetRecordMapper.insert(record);

        // 💡 注意：此时【不更新】bus_asset 表的状态。
        // 资产依然维持 status = 0，直到管理员在 auditClaim 方法中点下“通过”。
        log.info("用户 {} 提交了资产 {} 的领用申请，等待管理员审批", userId, asset.getAssetName());
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

    /**
     * 审批领用申请
     * 逻辑：校验记录状态 -> 处理审批结果 -> 更新资产权属 -> 追加审批备注
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void auditClaim(Long recordId, Integer auditResult, String auditRemark) {
        // 1. 获取申请记录并校验
        AssetRecord record = assetRecordMapper.selectById(recordId);
        if (record == null) {
            throw new BusinessException("操作失败：申请记录不存在");
        }

        // 💡 核心校验：只有“待审批”状态的记录才能进行审批操作
        if (!AuditStatusEnum.PENDING.getCode().equals(record.getAuditStatus())) {
            throw new BusinessException("操作失败：该申请已被处理（当前状态：" +
                    AuditStatusEnum.getDescriptionByCode(record.getAuditStatus()) + "）");
        }

        // 2. 获取关联的资产信息
        Asset asset = baseMapper.selectById(record.getAssetId());
        if (asset == null) {
            throw new BusinessException("操作失败：关联资产已不存在");
        }

        // 3. 处理审批分歧
        if (AuditStatusEnum.PASSED.getCode().equals(auditResult)) {
            // --- 情况 A: 审批通过 ---
            log.info("资产领用申请通过：记录ID {}, 资产ID {}, 领用人ID {}", recordId, asset.getId(), record.getUserId());

            // 更新资产主表状态和权属
            asset.setStatus(AssetStatusEnum.USING.getCode()); // 设为 1 (领用中)
            asset.setUserId(record.getUserId());             // 将资产归属给当时的申请人
            baseMapper.updateById(asset);

            // 更新记录状态为已通过
            record.setAuditStatus(AuditStatusEnum.PASSED.getCode());

        } else if (AuditStatusEnum.REJECTED.getCode().equals(auditResult)) {
            // --- 情况 B: 审批驳回 ---
            log.info("资产领用申请被驳回：记录ID {}, 原因: {}", recordId, auditRemark);

            // 更新记录状态为已驳回
            record.setAuditStatus(AuditStatusEnum.REJECTED.getCode());

            // 💡 驳回逻辑：资产表(bus_asset)保持原样，依然是闲置(0)且无领用人
        } else {
            throw new BusinessException("操作失败：非法的审批操作类型");
        }

        // 4. 完善审批轨迹信息
        // 将管理员的审批意见追加到备注中，方便在 Timeline (时间轴) 中查看
        String originalRemark = StringUtils.hasText(record.getRemark()) ? record.getRemark() : "无申请备注";
        String adminNote = StringUtils.hasText(auditRemark) ? auditRemark : "管理员未填写意见";

        record.setRemark(originalRemark + " | [审批意见]: " + adminNote);

        // 5. 保存记录更新
        assetRecordMapper.updateById(record);
    }

    @Override
    public Page<AssetRecordVO> getPendingAuditPage(int current, int size) {
        Page<AssetRecordVO> page = new Page<>(current, size);
        // 💡 这里的逻辑是：只查 audit_status = 0 (PENDING) 的记录
        // 并且需要关联查询资产名和申请人昵称
        return assetRecordMapper.selectPendingAuditPage(page);
    }
}
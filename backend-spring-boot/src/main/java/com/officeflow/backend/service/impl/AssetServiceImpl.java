package com.officeflow.backend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.officeflow.backend.entity.Asset;
import com.officeflow.backend.exception.BusinessException;
import com.officeflow.backend.mapper.AssetMapper;
import com.officeflow.backend.service.AssetService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * 资产业务逻辑实现类
 */
@Service
public class AssetServiceImpl extends ServiceImpl<AssetMapper, Asset> implements AssetService {

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
}
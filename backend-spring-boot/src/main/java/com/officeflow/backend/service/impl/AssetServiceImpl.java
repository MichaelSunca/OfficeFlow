package com.officeflow.backend.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.officeflow.backend.entity.Asset;
import com.officeflow.backend.mapper.AssetMapper;
import com.officeflow.backend.service.AssetService;
import org.springframework.stereotype.Service;

/**
 * 资产业务逻辑实现类
 */
@Service
public class AssetServiceImpl extends ServiceImpl<AssetMapper, Asset> implements AssetService {
}
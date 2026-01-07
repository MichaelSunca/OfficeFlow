package com.officeflow.backend.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.officeflow.backend.entity.Asset;

/**
 * 资产业务逻辑层接口
 * 继承 IService 可以获得 MyBatis-Plus 提供的更强大的批量操作方法
 */
public interface AssetService extends IService<Asset> {
}
package com.officeflow.backend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.officeflow.backend.entity.Asset;
import org.apache.ibatis.annotations.Mapper;

/**
 * 资产模块持久层接口
 * 继承 MyBatis-Plus 的 BaseMapper，自动获得标准的增删改查方法
 */
@Mapper
public interface AssetMapper extends BaseMapper<Asset> {
    // 后续如果有复杂的联表统计查询，可以在此定义自定义 SQL 方法
}
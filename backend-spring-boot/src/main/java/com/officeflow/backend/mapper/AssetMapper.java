package com.officeflow.backend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.officeflow.backend.entity.Asset;
import com.officeflow.backend.vo.AssetVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * 资产模块持久层接口
 * 继承 MyBatis-Plus 的 BaseMapper，自动获得标准的增删改查方法
 */
@Mapper
public interface AssetMapper extends BaseMapper<Asset> {
    // 后续如果有复杂的联表统计查询，可以在此定义自定义 SQL 方法

    /**
     * 分页查询资产及用户信息
     */
    Page<AssetVO> selectAssetPage(
            @Param("page") Page<AssetVO> page,
            @Param("assetName") String assetName,
            @Param("status") Integer status
    );
}
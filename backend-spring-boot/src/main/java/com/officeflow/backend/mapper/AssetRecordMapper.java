package com.officeflow.backend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.officeflow.backend.entity.AssetRecord;
import com.officeflow.backend.vo.AssetRecordVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface AssetRecordMapper extends BaseMapper<AssetRecord> {
    /**
     * 查询资产的流转履历，关联查询用户信息和资产信息
     */
    List<AssetRecordVO> selectRecordListWithUserInfo(@Param("assetId") Long assetId);

    /**
     * 分页查询待审批的申请记录
     * 关联 bus_asset 拿资产名称，关联 sys_user 拿申请人昵称
     */
    Page<AssetRecordVO> selectPendingAuditPage(Page<AssetRecordVO> page);
}

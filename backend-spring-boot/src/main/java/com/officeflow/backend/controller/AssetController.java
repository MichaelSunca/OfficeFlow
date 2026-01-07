package com.officeflow.backend.controller;

import com.officeflow.backend.common.Result;
import com.officeflow.backend.entity.Asset;
import com.officeflow.backend.mapper.AssetMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 资产管理相关的 REST 接口
 */
@RestController
@RequestMapping("/api/assets")
@RequiredArgsConstructor
public class AssetController {

    private final AssetMapper assetMapper;

    /**
     * 获取系统中所有资产的列表
     * @return 统一封装的 Result 对象，包含资产列表数据
     */
    @GetMapping("/list")
    public Result<List<Asset>> list() {
        // selectList(null) 代表无条件查询，即查询 bus_asset 表所有数据
        List<Asset> list = assetMapper.selectList(null);
        return Result.success(list);
    }
}
package com.officeflow.backend.controller;

import com.officeflow.backend.common.Result;
import com.officeflow.backend.entity.Asset;
import com.officeflow.backend.service.AssetService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 资产管理相关的 REST 接口
 */
@RestController
@RequestMapping("/api/assets")
@RequiredArgsConstructor
public class AssetController {

    private final AssetService assetService;

    /**
     * 获取所有资产列表
     */
    @GetMapping("/list")
    public Result<List<Asset>> list() {
        return Result.success(assetService.list());
    }

    /**
     * 新增资产
     * @param asset 前端传来的资产 JSON 对象
     */
    @PostMapping("/add")
    public Result<String> add(@RequestBody Asset asset) {
        // 设置初始状态为 0 (闲置)
        asset.setStatus(0);

        boolean saved = assetService.save(asset);
        if (saved) {
            return Result.success("资产添加成功");
        } else {
            return Result.error(500, "资产添加失败");
        }
    }
}
package com.officeflow.backend.controller;

import com.officeflow.backend.common.Result;
import com.officeflow.backend.dto.AssetAddDTO;
import com.officeflow.backend.dto.AssetClaimDTO;
import com.officeflow.backend.entity.Asset;
import com.officeflow.backend.service.AssetService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
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
     * @param assetAddDTO 前端传来的资产 JSON 对象
     */
    @PostMapping("/add")
    public Result<String> add(@Valid @RequestBody AssetAddDTO assetAddDTO) {
        Asset asset = new Asset();
        // 将 DTO 的属性拷贝到 Entity 对象中
        BeanUtils.copyProperties(assetAddDTO, asset);

        // 强制设置状态，不受前端传参影响，保证业务安全
        asset.setStatus(0);

        assetService.save(asset);
        return Result.success("资产添加成功");
    }

    /**
     * 领用资产
     */
    @PostMapping("/claim")
    public Result<String> claim(@Valid @RequestBody AssetClaimDTO claimDTO) {
        // 假设你已经能从 SecurityContextHolder 拿到当前登录用户的 ID
        // 临时测试可以直接传 admin 的 ID: 1L
        Long currentUserId = 1L;

        assetService.claimAsset(claimDTO, currentUserId);
        return Result.success("领用成功");
    }
}
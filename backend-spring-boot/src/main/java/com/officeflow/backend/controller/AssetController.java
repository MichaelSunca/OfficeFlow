package com.officeflow.backend.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.officeflow.backend.common.Result;
import com.officeflow.backend.dto.AssetFormDTO;
import com.officeflow.backend.dto.AssetOperateDTO;
import com.officeflow.backend.service.AssetService;
import com.officeflow.backend.utils.SecurityUtils;
import com.officeflow.backend.vo.AssetVO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 资产管理相关的 REST 接口
 */
@RestController
@RequestMapping("/api/assets")
@RequiredArgsConstructor
public class AssetController {

    private final AssetService assetService;

    @GetMapping("/list")
    public Result<Page<AssetVO>> list(
            @RequestParam(defaultValue = "1") int current,
            @RequestParam(defaultValue = "10") int size) {
        return Result.success(assetService.getAssetListPage(current, size));
    }

    /**
     * 领用资产
     */
    @PostMapping("/claim")
    public Result<String> claim(@Valid @RequestBody AssetOperateDTO claimDTO) {
        // 从 SecurityContextHolder 拿到当前登录用户的 ID
        Long currentUserId = SecurityUtils.getUserId();

        assetService.claimAsset(claimDTO, currentUserId);
        return Result.success("领用成功");
    }

    /**
     * 资产退库
     */
    @PostMapping("/return")
    public Result<String> returnAsset(@Valid @RequestBody AssetOperateDTO returnDTO) {
        Long currentUserId = SecurityUtils.getUserId();
        assetService.returnAsset(returnDTO, currentUserId);
        return Result.success("退库成功，资产已入库");
    }

    /**
     * 新增资产
     * @param dto
     * @return
     */
    @PostMapping("/add")
    public Result<String> add(@Validated(AssetFormDTO.CreateGroup.class) @RequestBody AssetFormDTO dto) {
        assetService.saveAsset(dto);
        return Result.success("新增资产成功");
    }

    /**
     * 编辑资产
     * @param dto
     * @return
     */
    @PutMapping("/update")
    public Result<String> update(@Validated(AssetFormDTO.UpdateGroup.class) @RequestBody AssetFormDTO dto) {
        assetService.updateAsset(dto);
        return Result.success("更新资产成功");
    }
}
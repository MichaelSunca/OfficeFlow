package com.officeflow.backend.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.officeflow.backend.common.Result;
import com.officeflow.backend.dto.AssetFormDTO;
import com.officeflow.backend.dto.AssetOperateDTO;
import com.officeflow.backend.service.AssetService;
import com.officeflow.backend.utils.SecurityUtils;
import com.officeflow.backend.vo.AssetRecordVO;
import com.officeflow.backend.vo.AssetVO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
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

    @GetMapping("/list")
    public Result<Page<AssetVO>> list(
            @RequestParam(defaultValue = "1") int current,
            @RequestParam(defaultValue = "10") int size,
            String assetName,  // 💡 接收前端传来的搜索词
            Integer status     // 💡 接收前端传来的状态过滤
    ) {
        // 这里的 getAssetListPage 需要传搜索参数进去
        return Result.success(assetService.getAssetListPage(current, size, assetName, status));
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
    @PostMapping("/update")
    public Result<String> update(@Validated(AssetFormDTO.UpdateGroup.class) @RequestBody AssetFormDTO dto) {
        assetService.updateAsset(dto);
        return Result.success("更新资产成功");
    }

    /**
     * 删除
     * @param id
     * @return
     */
    @PostMapping("/delete")
    public Result<String> delete(@RequestParam Long id) {
        assetService.removeById(id); // MyBatis-Plus 开启逻辑删除后，这行就是逻辑删除
        return Result.success("删除成功");
    }

    @GetMapping("/records/{assetId}")
    public Result<List<AssetRecordVO>> getRecords(@PathVariable Long assetId) {
        // 💡 这里不再直接调用 Service.list()，而是调用一个专门查询 VO 的方法
        return Result.success(assetService.getAssetRecords(assetId));
    }
}
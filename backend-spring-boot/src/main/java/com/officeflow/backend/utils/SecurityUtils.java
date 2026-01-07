package com.officeflow.backend.utils;

import com.officeflow.backend.exception.BusinessException;
import com.officeflow.backend.security.LoginUser;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * 安全服务工具类
 */
public class SecurityUtils {

    /**
     * 获取当前登录用户 ID
     * 注意：这取决于你在 Login 时存入 Principal 的对象是什么
     */
    public static Long getUserId() {
        try {
            // 这里强制转换的前提是你在自定义 UserDetailsService 中返回的是包含 ID 的对象
            // 如果你目前 Principal 只是存了 Username，那么这里需要做调整
            // 假设你自定义了一个包含 ID 的 LoginUser 类
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null || !authentication.isAuthenticated()) {
                throw new BusinessException(401, "登录状态已失效");
            }

            // 如果你目前只是简单实现，Principal 通常是 UserDetails 对象
            // 我们通常建议在自定义 UserDetails 中加入 userId 字段
            return ((LoginUser) authentication.getPrincipal()).getUserId();
        } catch (Exception e) {
            throw new BusinessException(401, "获取用户信息失败，请重新登录");
        }
    }
}
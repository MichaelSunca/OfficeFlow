package com.officeflow.backend.controller;

import com.officeflow.backend.common.Result;
import com.officeflow.backend.dto.LoginRequest;
import com.officeflow.backend.utils.JwtUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;

    @PostMapping("/login")
    public Result<Map<String, Object>> login(@RequestBody LoginRequest loginRequest) {
        // 1. 将前端传来的用户名密码封装成 Spring Security 认的 Token 对象
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword());

        // 2. 这一步会触发 CustomUserDetailsService.loadUserByUsername()
        // 并且内部会自动比对数据库里的加密密码
        Authentication authentication = authenticationManager.authenticate(authenticationToken);

        // 3. 如果运行到这里没报错，说明验证通过，生成 JWT
        String token = jwtUtils.createToken(loginRequest.getUsername());

        // 4. 返回给前端
        Map<String, Object> data = new HashMap<>();
        data.put("token", token);
        data.put("username", loginRequest.getUsername());
        return Result.success(data);
    }
}
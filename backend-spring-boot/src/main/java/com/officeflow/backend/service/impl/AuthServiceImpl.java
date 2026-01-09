package com.officeflow.backend.service.impl;

import com.officeflow.backend.dto.LoginDTO;
import com.officeflow.backend.mapper.UserMapper;
import com.officeflow.backend.service.AuthService;
import com.officeflow.backend.utils.JwtUtils;
import com.officeflow.backend.vo.UserVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor // 依赖注入
public class AuthServiceImpl implements AuthService {

    private final UserMapper userMapper;
    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;

    public String login(LoginDTO loginDTO) {

        // 将前端传来的用户名密码封装成 Spring Security 认的 Token 对象
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(loginDTO.getUsername(), loginDTO.getPassword());

        // 这一步会触发 CustomUserDetailsService.loadUserByUsername()
        // 并且内部会自动比对数据库里的加密密码
        authenticationManager.authenticate(authenticationToken);

        // 调用 selectUserByName 查出用户信息
        UserVO user = userMapper.selectUserByName(loginDTO.getUsername());

        // 将 role_key 塞进 JWT Token
        // 这样前端解析 Token 就能拿到 "ADMIN"，后端拦截器也能拿到
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", user.getRoleKey());
        claims.put("userId", user.getId());
        claims.put("username", user.getUsername());

        return jwtUtils.createToken(claims);
    }
}
